package org.hyperion.rs2.packet;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.combat.CombatCheck;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.Packet;

public class AttackPacketHandler implements PacketHandler {

	final int NPC = 72;
	final int PLAYER = 128;
	
	@Override
	public void handle(Player player, Packet packet) {
		// TODO Auto-generated method stub
		int index = -1;
		
		switch(packet.getOpcode()) {
		
		case NPC:
			index = packet.getShortA();
			if(index < 0 || index > Constants.MAX_NPCS) return;
			NPC n = (NPC) World.getWorld().getNPCs().get(index);
			player.setInteractingEntity(n);
			player.setCurrentTarget(n);
			break;
			
		case PLAYER:
			index = packet.getUnsignedShort();
			System.out.println("initialized: "+ index);
			if(index < 0 || index > Constants.MAX_PLAYERS) return;
			
			Player p = (Player) World.getWorld().getPlayers().get(index);
			player.setInteractingEntity(p);
			player.setCurrentTarget(p);
			break;
			
		}
	}

}
