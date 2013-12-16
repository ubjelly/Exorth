package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * A packet handler which takes no action i.e. it ignores the packet.
 * @author Graham Edgecombe
 *
 */
public class QuietPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		/*if(packet.getOpcode() == 121) {
			for(FloorItem i : player.getRegion().getRegionItems()) {
				if(!i.isTaken() && i.isGlobal() || i.getDroppedFor() == player && !i.isTaken() ||
						i.getOwner() instanceof Player && i.getOwner() == player && !i.isTaken()) {
					player.getActionSender().sendRegionalItem(i);
				}
			}
		}*/
		if (packet.getOpcode() == 121){
			player.getActionSender().sendCloseInterface();
		}
	}

}
