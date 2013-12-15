package org.hyperion.rs2.content;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.npcs.npc;

/**
 * This will handle spawning mob's.
 * @author phil
 *
 */
public class MobSpawn {
	
	/**
	 * Logging class.
	 */
	private static final Logger logger = Logger.getLogger(MobSpawn.class.getName());

	/**
	 * Creates a NPC aka 'Mob' at the represented spot.
	 * @param id The 'Mob' id.
	 * @param spawnSpot The location to be spawned at.
	 * @param summoner The Summoner of the 'Mob'
	 */
	public static void create(int id, Location spawnSpot, Entity summoner, Entity target) {
		
		NPC n = new npc(id, spawnSpot);
		
		n.setMaster(summoner);
		
		n.setCurrentTarget(target);
		
		World.getWorld().register(n);
	}
	
	/**
	 * Loads the spawn file into the NPC mapping.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 * @throws NumberFormatException 
	 */
	public static void init() throws IOException, ClassNotFoundException, NumberFormatException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {//Were going to read a cfg to make things easier for now.
		short amount = 0;
		BufferedReader in = new BufferedReader(new FileReader("./data/spawn.cfg"));
		String line = null;
		in.readLine();
		while((line = in.readLine()) != null) {
			line = line.trim();
			String type = line.substring(0, line.indexOf("=")-1);
			line = line.substring(line.indexOf("=") + 1);
			line.trim();
			line = line.replaceAll(" ", ":");
			line = line.substring(1, line.length());
			String[] spawn = line.split(":");
			int walk = spawn.length < 5 ? 4 : Integer.parseInt(spawn[4]);
			Location loc = Location.create(Integer.parseInt(spawn[1]), Integer.parseInt(spawn[2]), Integer.parseInt(spawn[3]));
			NPC npc = (NPC) Class.forName("org.hyperion.rs2.model.npcs."+type).getConstructor(int.class, Location.class, int.class).newInstance(Integer.parseInt(spawn[0]), loc, walk);
			World.getWorld().register(npc);
			amount++;
		}
		logger.info("Successfully spawned "+amount+" mobs.");
	}
}
