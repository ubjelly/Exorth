package org.hyperion.rs2.net;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.FloorItem;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Player.Rights;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.WorldObject;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.impl.EquipmentContainerListener;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.container.impl.WeaponContainerListener;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.region.RegionManager;
import org.hyperion.rs2.net.Packet.Type;

/**
 * A utility class for sending packets.
 * @author Graham Edgecombe
 *
 */
public class ActionSender {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates an action sender for the specified player.
	 * @param player The player to create the action sender for.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}
	
	/**
	 * Sends an inventory interface.
	 * @param interfaceId The interface id.
	 * @param inventoryInterfaceId The inventory interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterfaceInventory(int interfaceId, int inventoryInterfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(248).putShortA(interfaceId).putShort(inventoryInterfaceId).toPacket());
		return this;
	}
	
	/**
	 * Sends all the login packets.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogin() {
		player.setActive(true);
		sendPrivateMessageConfig(World.getWorld().getLoginServerConnector() == null && Constants.CONNNECT_TO_LOGIN_SERVER ? 0 : 2);
		sendDetails();
		if(player.getFightStyle() == -1)
			player.setFightStyle((byte) 0);
		sendMessage("Welcome to " + Constants.SERVER_NAME + ".");
		player.setRights(Rights.PLAYER);
		sendMapRegion();
		Prayer.resetPrayers(player, true);
		sendSidebarInterfaces();
		sendEnergy(player.getWalkingQueue().getRunEnergy());
		sendWeight(player.getEquipment().getWeight() + (player.getInventory().getWeight() < 0 ? 0 : player.getInventory().getWeight()));
		if(Constants.PVP_WORLD)
			sendPlayerOption("Attack",1,1);
		else
			sendPlayerOption("null",1,0);
		//sendPlayerOption("Challenge",2,0);
		sendSkills();
		sendPlayerOption("Trade With",4,0);
		sendPlayerOption("Follow",3,0);
		
		InterfaceContainerListener inventoryListener = new InterfaceContainerListener(player, Inventory.INTERFACE);
		player.getInventory().addListener(inventoryListener);
		
		InterfaceContainerListener equipmentListener = new InterfaceContainerListener(player, Equipment.INTERFACE);
		player.getEquipment().addListener(equipmentListener);
		player.getEquipment().addListener(new EquipmentContainerListener(player));
		player.getEquipment().addListener(new WeaponContainerListener(player));
		sendConfig(107, player.getFightStyle());
		return this;
	}
	
	/**
	 * Sends weight on the interface.
	 * @param i
	 * @return
	 */
	public ActionSender sendWeight(int i) {
		// TODO Auto-generated method stub
		PacketBuilder bldr = new PacketBuilder(240);
		bldr.putShort((short) i);
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Sends Chat Info for the Interface. Not Dialogue, Infopain.
	 * @param strings Lines of text.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendChatInfoInterface(String...strings) {
		int inter = 354;
		for(int i = 0; i < strings.length; i++)
			inter = (inter + i + 2);
		PacketBuilder bldr = new PacketBuilder(164);
		bldr.putLEShort(inter);
		player.write(bldr.toPacket());
		for(int i = 0; i < strings.length; i++)
			sendString(inter+1 + i, strings[i]);
		return this;
	}

	/**
	 * Sends a config to the client.
	 * @param id Config id.
	 * @param state Config value.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendConfig(int id, int state)
	{
		if(state < 255) {
			PacketBuilder bldr = new PacketBuilder(36);
			bldr.putLEShort(id);
			bldr.put((byte)state);
			player.write(bldr.toPacket());
		} else {
			PacketBuilder bldr = new PacketBuilder(87);
			bldr.putLEShort(id);
			bldr.putInt1(state);
	                player.write(bldr.toPacket());
		}
		return this;
	}
	
	/**
	 * Sends the player option.
	 * @param message The option.
	 * @param slot The index.
	 * @param priority Above the rest, or below.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendPlayerOption(String message,int slot,int priority) {
		player.write(new PacketBuilder(104, Type.VARIABLE).putByteC(slot).putByteA(priority).putRS2String(message).toPacket());
		return this;
	}

	/**
	 * Sends the packet to construct a map region.
	 * @param palette The palette of map regions.
	 * @return The action sender instance, for chaining.
	 *
	public ActionSender sendConstructMapRegion(Palette palette) {
		player.setLastKnownRegion(player.getLocation());
		PacketBuilder bldr = new PacketBuilder(241, Type.VARIABLE_SHORT);
		bldr.putShortA(player.getLocation().getRegionY() + 6);
		bldr.startBitAccess();
		for(int z = 0; z < 4; z++) {
			for(int x = 0; x < 13; x++) {
				for(int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					bldr.putBits(1, tile != null ? 1 : 0);
					if(tile != null) {
						bldr.putBits(26, tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1);
					}
				}
			}
		}
		bldr.finishBitAccess();
		bldr.putShort(player.getLocation().getRegionX() + 6);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the initial login packet (e.g. members, player id).
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendDetails() {
		player.write(new PacketBuilder(249).putByteA(player.isMembers() ? 1 : 0).putLEShortA(player.getIndex()).toPacket());
		player.write(new PacketBuilder(107).toPacket());
		return this;
	}
	
	/**
	 * Sends the player's skills.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkills() {
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			sendSkill(i);
		}
		return this;
	}
	
	/**
	 * Sends a specific skill.
	 * @param skill The skill to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkill(int skill) {
		PacketBuilder bldr = new PacketBuilder(134);
		bldr.put((byte) skill);
		bldr.putInt1((int) player.getSkills().getExperience(skill));
		bldr.put((byte) player.getSkills().getLevel(skill));
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends all the sidebar interfaces.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterfaces() {
		final int[] icons = Constants.SIDEBAR_INTERFACES[0];
		final int[] interfaces = Constants.SIDEBAR_INTERFACES[1];
		for(int i = 0; i < icons.length; i++) {
			sendSidebarInterface(icons[i], interfaces[i]);
		}
		return this;
	}
	
	/**
	 * Sends a specific sidebar interface.
	 * @param icon The sidebar icon.
	 * @param interfaceId The interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterface(int icon, int interfaceId) {
		player.write(new PacketBuilder(71).putShort(interfaceId).putByteA(icon).toPacket());
		return this;
	}
	
	/**
	 * Sends a message.
	 * @param message The message to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMessage(Object message) {
		player.write(new PacketBuilder(253, Type.VARIABLE).putRS2String((String)message).toPacket());
		return this;
	}
	
	/**
	 * Sends a world message.
	 * @param message The message to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendWorldMessage(Object message) {
		for (Player pz: World.getWorld().getPlayers()){
		pz.write(new PacketBuilder(253, Type.VARIABLE).putRS2String((String)message).toPacket());
		}
		return this;
	}
	
	/**
	 * Sends the map region load command.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMapRegion() {
		player.setLastKnownRegion(player.getLocation());
		player.write(new PacketBuilder(73).putShortA(player.getLocation().getRegionX()+6).putShort(player.getLocation().getRegionY()+6).toPacket());
		for(Region r : RegionManager.getSurroundingRegions(player.getLocation())) {//Read surrounding area's.
			for(FloorItem i : r.getRegionItems()) {
				if(!i.isTaken() && i.isGlobal() || i.getDroppedFor() == player && !i.isTaken() ||
						i.getOwner() instanceof Player && i.getOwner() == player && !i.isTaken()) {
					player.getActionSender().sendRegionalItem(i);
				}
			}
		}
		System.out.println("New Region Loaded.");
		return this;
	}
	
	/**
	 * Sends the logout packet.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogout() {
		player.getSession().write(new PacketBuilder(109).toPacket()).addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture future)
			{
				future.getSession().close(false);
			}
		});
		return this;
	}
	
	/**
	 * Creates a new world object.
	 * @param o The object
	 * @return The ActionSender instance
	 */
	
	public ActionSender sendWorldObjectCreation(WorldObject o, final boolean replacement) {
		int x = o.objectX - (this.player.getLastKnownRegion().getRegionX() * 8);
		int y = o.objectY - (this.player.getLastKnownRegion().getRegionY() * 8);
		this.player.write(new PacketBuilder(85).putByteC(y).putByteC(x).toPacket());
		this.player.write(new PacketBuilder(151).putByteS((byte) 0).putLEShort(replacement ? o.replacementId : o.objectId).putByteS((byte) ((o.objectType << 2) + (o.objectDirection & 3))).toPacket());
		return this;
	}
	
	/**
	 * Removes an existing world object.
	 * @param o The object
	 * @return The ActionSender instance
	 */
	
	public ActionSender sendWorldObjectRemoval(WorldObject o) {
		int x = o.objectX - (this.player.getLastKnownRegion().getRegionX() * 8);
		int y = o.objectY - (this.player.getLastKnownRegion().getRegionY() * 8);
		this.player.write(new PacketBuilder(85).putByteC(y).putByteC(x).toPacket());
		this.player.write(new PacketBuilder(101).putByteC((o.objectType << 2) + (o.objectDirection & 3)).put((byte) 0).toPacket());
		return this;
	}
	
	/**
	 * Sends a packet to update a group of items.
	 * @param interfaceId The interface id.
	 * @param items The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(items.length);
		for(Item item : items) {
			if(item != null) {
				int count = item.getCount();
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt2(count);
				} else {
					bldr.put((byte) count);
				}
				bldr.putLEShortA(item.getId() + 1);
			} else {
				bldr.put((byte) 0);
				bldr.putLEShortA(0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Sends a packet to update a group of items.
	 * @param interfaceId The interface id.
	 * @param items The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, int[] item, int[] amount) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(item.length);
		for(int i = 0; i < item.length; i++) {
			if(item[i] > 0) {
				int count = amount[i];
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt2(count);
				} else {
					bldr.put((byte) count);
				}
				bldr.putLEShortA(item[i] + 1);
			} else {
				bldr.put((byte) 0);
				bldr.putLEShortA(0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a packet to update a single item.
	 * @param interfaceId The interface id.
	 * @param slot The slot.
	 * @param item The item.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItem(int interfaceId, int slot, Item item) {
		PacketBuilder bldr = new PacketBuilder(34, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId).putSmart(slot);
		if(item != null) {
			bldr.putShort(item.getId() + 1);
			int count = item.getCount();
			if(count > 254) {
				bldr.put((byte) 255);
				bldr.putInt(count);
			} else {
				bldr.put((byte) count);
			}
		} else {
			bldr.putShort(0);
			bldr.put((byte) 0);
		}
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Sends a packet to update multiple (but not all) items.
	 * @param interfaceId The interface id.
	 * @param slots The slots.
	 * @param items The item array.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, int[] slots, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(34, Type.VARIABLE_SHORT).putShort(interfaceId);
		for(int i = 0; i < slots.length; i++) {
			Item item = items[slots[i]];
			bldr.putSmart(slots[i]);
			if(item != null) {
				bldr.putShort(item.getId() + 1);
				int count = item.getCount();
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt(count);
				} else {
					bldr.put((byte) count);
				}
			} else {
				bldr.putShort(0);
				bldr.put((byte) 0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the enter amount interface.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendEnterAmountInterface() {
		player.write(new PacketBuilder(27).toPacket());
		return this;
	}
	
	/**
	 * Sends the player an option.
	 * @param slot The slot to place the option in the menu.
	 * @param top Flag which indicates the item should be placed at the top.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInteractionOption(String option, int slot, boolean top) {
		PacketBuilder bldr = new PacketBuilder(104, Type.VARIABLE);
		bldr.put((byte) -slot);
		bldr.putByteA(top ? (byte) 0 : (byte) 1);
		bldr.putRS2String(option);
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Removes all interfaces.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendRemoveAllInterfaces() {
		PacketBuilder bldr = new PacketBuilder(219);
		player.write(bldr.toPacket());
		player.getInterfaceState().interfaceClosed();
		return this;
	}

	/**
	 * Sends a string.
	 * @param id The interface id.
	 * @param string The string.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendString(int id, String string) {
		PacketBuilder bldr = new PacketBuilder(126, Type.VARIABLE_SHORT);
		bldr.putRS2String(string);
		bldr.putShortA(id);
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Sends a model in an interface.
	 * @param id The interface id.
	 * @param zoom The zoom.
	 * @param model The model id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterfaceModel(int id, int zoom, int model) {
		PacketBuilder bldr = new PacketBuilder(246);
		bldr.putLEShort(id).putShort(zoom).putShort(model);
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Sends the private messaging status.
	 * @param status Current status.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendPrivateMessageConfig(int status) {
		PacketBuilder bldr = new PacketBuilder(221);
		bldr.put((byte) status);
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Sends the firends onlne status.
	 * @param name Friends name.
	 * @param world Offline, or worldId.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendPrivateMessageStatus(long name, int world) {
		PacketBuilder bldr = new PacketBuilder(50);
		bldr.putLong(name);
		bldr.put((byte) (world + 9));
		player.write(bldr.toPacket());
		return this;
	}
	
	/**
	 * Message counter, not sure why this shit is needed.
	 */
	private transient int messageCount = 1;
	
	/**
	 * Sends a private message to a player.
	 * @param name Player sending message.
	 * @param rights Rights of player sending message.
	 * @param chatMessage Message sent.
	 * @param size Size of message.
	 * @return The action sender isntance, for chaining.
	 */
	//that looks to be a syncing error to me, try moding your client to print out messages from private chat
	public ActionSender sendPrivateMessage(long from, int rights, byte[] chatText) {
		//chatText = "Hello".getBytes();
		//chatTextSize = chatText.length-1;
		player.write(new PacketBuilder(196, Type.VARIABLE)
		.putLong(from)//this
		.putInt(messageCount++)
		.put((byte)rights)//its not, i was sending messages before but they were blank just was like Test: 
		.put(chatText).toPacket());//packet problem now least the message is gettin across
		return this;//have you got it synced correctly? only login is synched, the checking of friends no that could be why, so try it a few times
	}
	
	/**
	 * Creates a new projectile.
	 * @param src The source location.
	 * @param dest The target location.
	 * @param index The index of the target to follow.
	 * <ul>
	 * <li>Mob/NPC: The server-index.</li>
	 * <li>Player: The -server-index.</li>
	 * <li>No Target: <i>0</i></li>
	 * </ul>
	 * @param gfx The id of the graphic to send.
	 * @param initHeight The initial height level of the graphic.
	 * @param endHeight The ending height level of the graphic.
	 * @param delay The delay before the graphic is created (So an animation can run, for example).
	 * @param speed The speed of the projectile (before it reaches its target tile).
	 * @param curvature The angle of the projectile's arc.
	 * @param offset The point where the projectile spawns. Should be between <i>0</i> and <i>50</i>.
	 * @return The ActionSender instance, for chaining.
	 */
	public ActionSender sendProjectile(Location src, Location dest, Entity target, int gfx, int initHeight, int endHeight, int delay, int slowness, int curvature, int offset) {
		//sendLocation(src); //This is packet 85 btw.
		if(player.getLocation().getZ() != src.getZ()) return this;
		player.write(new PacketBuilder(85).putByteC(src.getY() - (player.getLastKnownRegion().getRegionY()) * 8).putByteC(src.getX() - (player.getLastKnownRegion().getRegionX()) * 8).toPacket());
                int distance = (int) src.getDistanceFromLocation(dest);
            	int duration = delay + slowness + (distance * 5);

            player.write(new PacketBuilder(117).
				put((byte)dest.getZ()).
				put((byte)(dest.getX() - src.getX())).
				put((byte)(dest.getY() - src.getY())).
				putShort(target == null ? 0 : target instanceof Player ? -(target.getIndex()+1) : target.getIndex()+1).
				putShort(gfx).
				put((byte)initHeight).
				put((byte)endHeight).
				putShort(delay).
				putShort(duration).
				put((byte)curvature).
				put((byte) (offset * 64)).toPacket());
		return this;
	}
	
	/**
	 * Sends the item to be shown in the region.
	 * @param i The item.
	 */
	public ActionSender sendRegionalItem(FloorItem i){
		if(player.getLocation().getZ() != i.getLoc().getZ()) return this;
		Location loc = i.getLoc();
		player.write(new PacketBuilder(85, Type.FIXED).putByteC(loc.getY() - (player.getLastKnownRegion().getRegionY()) * 8).putByteC(loc.getX() - (player.getLastKnownRegion().getRegionX()) * 8).toPacket());
		
		PacketBuilder globalDrop2 = new PacketBuilder(44);
		globalDrop2.putLEShortA(i.getItem());
		globalDrop2.putShort(i.getAmount());
		globalDrop2.put((byte)i.getLoc().getZ());
		player.write(globalDrop2.toPacket());
		return this;
	}

	/**
	 * Sends a packet to remove a item in a region.
	 * @param i The item.
	 */
	public ActionSender sendRemoveRegionalItem(FloorItem i){
		if(player.getLocation().getZ() != i.getLoc().getZ()) return this;
		Location loc = i.getLoc();
		player.write(new PacketBuilder(85, Type.FIXED).putByteC(loc.getY() - (player.getLastKnownRegion().getRegionY()) * 8).putByteC(loc.getX() - (player.getLastKnownRegion().getRegionX()) * 8).toPacket());
		
		PacketBuilder globalDrop2 = new PacketBuilder(156);
		globalDrop2.putByteS((byte)i.getLoc().getZ());
		globalDrop2.putShort(i.getItem());
		player.write(globalDrop2.toPacket());
               // player.get
		return this;
	}
	
	/**
	 * Sends a packet to change the regional items amount.
	 * @param i The item.
	 * @return the action sender instance, for chaining.
	 */
	public ActionSender sendNewRegionalItemAmount(int oldAmount, FloorItem i) {
		Location loc = i.getLoc();
		player.write(new PacketBuilder(85, Type.FIXED).putByteC(loc.getY() - (player.getLastKnownRegion().getRegionY()) * 8).putByteC(loc.getX() - (player.getLastKnownRegion().getRegionX()) * 8).toPacket());
		PacketBuilder globalDrop2 = new PacketBuilder(84);
		globalDrop2.put((byte)i.getLoc().getZ());
		globalDrop2.putShort(i.getItem());
		globalDrop2.putShort(oldAmount);
		globalDrop2.putShort(i.getAmount());
		player.write(globalDrop2.toPacket());
		return this;
	}
	
	/**
	 * Sends the characters energy level
	 * @param energy The current energy level
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendEnergy(int energy) {
		energy = (int) Math.floor(energy / 100);
		player.write(new PacketBuilder(110).put((byte) (energy & 0xff)).toPacket());
		return this;
	}
	
	/**
	 * sends Chat settings/
	 * @param p Public chat setting/
	 * @param pChat Private chat setting.
	 * @param trade Trade setting.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendChatSettings(int p, int pChat, int trade) {
		player.write(new PacketBuilder(206)
		.put((byte) p)
		.put((byte) pChat)
		.put((byte)trade).toPacket());
		return this;
	}
	
	public ActionSender sendCloseInterface() {
		player.write(new PacketBuilder(219).toPacket());
		return this;
	}
	
	public ActionSender sendChatInterface(int id) {
		player.write(new PacketBuilder(164)
		.putLEShort(id).toPacket());
		return this;
	}
	
	
	
}
