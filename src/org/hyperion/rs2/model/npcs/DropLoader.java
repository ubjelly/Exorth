package org.hyperion.rs2.model.npcs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.util.JSonParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * Loads the npc drops.
 * @author Stephen
 */
public class DropLoader {

	/**
	 * The drop map.
	 */
	private Map<Integer, Integer[]> drops = new HashMap<Integer, Integer[]>();
	
	public void loadDrops() 
	{
		JsonElement baseDoc = null;
		try {
			baseDoc = JSonParser.fromFile(new FileInputStream("C://Users//Stephen//Desktop//NpcDrops.json"));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot load npc drops!");
		}
		if(baseDoc != null) {
			JsonObject doc = baseDoc.getAsJsonObject();
			JsonArray items = doc.get("items").getAsJsonArray();
			for(JsonElement item : items) {
				drops.put(item.getAsInt(), null);
			}
		}
	}
	
}
