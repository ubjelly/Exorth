package org.hyperion.rs2.model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

public class NPCDefinition
{
	
	/**
	 * max Definitions to be Loaded.
	 */
	public static final int MAX_DEFINITIONS = 8000;
	
	/**
	 * Logger for this region.
	 */
	private static final Logger logger = Logger.getLogger(NPCDefinition.class.getName());

	
	/**
	 * Constructor for setting the NPCDefinition.
	 * @param name The NPC name.
	 * @param level The combatLevel.
	 * @param size The NPC size.
	 */
	public NPCDefinition(String n, int cl, int ts, boolean i, int m, int s) {
        this.name = n;
        this.combatLevel = (short) cl;
        this.tileSize = (byte) ts;
        this.maxHealth = (short) m;
        this.immuneToPoison = (byte) (i ? 1 : 0);
        this.spawnTimer = (short) s;
    }

	/**
	 * The NPC name.
	 */
	private String name;
	
	/**
	 * Gets the NPC name.
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	public void setName(String s) {
		this.name = s;
	}
	
	/**
	 * The NPC id.
	 */
	private short maxHealth;
	
	/**
	 * Gets the NPC id.
	 * @return The id.
	 */
	public int getMaxHealth()
	{
		return maxHealth;
	}
	
	/**
	 * SpawnTimer.
	 */
	private short spawnTimer;
	
	/**
	 * Immune to pioson.
	 */
	private byte immuneToPoison;
	
	/**
	 * The NPC combatLevel.
	 */
	private short combatLevel;
	
	/**
	 * Gets the NPC combatLevel.
	 * @return The combatLevel.
	 */
	public int getCombatLevel()
	{
		return combatLevel;
	}
	
	
	public void setCombatLevel(int i) {
		this.combatLevel = (short) i;
	}
	/**
	 * The NPC size.
	 */
	private byte tileSize;
	
	/**
	 * Gets the NPC size.
	 * @return The size.
	 */
	public int getSize()
	{
		return tileSize;
	}
	
	public void setTileSize(int i) {
		this.tileSize = (byte) i;
	}
	
	/**
     * Loads the attacks into the Map.
     * @throws FileNotFoundException
     */
    public static void load() throws FileNotFoundException {
		try {
			int i = 0;
			File f = new File("./data/NPC.dat");
			DataInputStream is = new DataInputStream(new FileInputStream(f));
			while(is.available() != 0) {
                            int i2 = is.readShort();
                            String n = is.readUTF();
                            //String b = readRS2String(buf);
                            boolean immune = is.readByte() == 1;
                            int cb = is.readShort();
                            int si = is.readByte();
                            int health = is.readShort();
                            int timer =  is.readShort();
                            i++;
                            if(i >= MAX_DEFINITIONS) continue;
                            Definitions.addDefinition(i2, new NPCDefinition(n, cb, si, immune, health, timer));
                        }
	        logger.info("Loaded "+i +" NPC definitions.");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }

	/**
	 * @return the immuneToPoison
	 */
	public boolean isImmuneToPoison() {
		return immuneToPoison == 1;
	}

	/**
	 * @return the spawnTimer
	 */
	public int getSpawnTimer() {
		return spawnTimer;
	}
	
}
