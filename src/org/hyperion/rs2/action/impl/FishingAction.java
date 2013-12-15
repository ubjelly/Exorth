package org.hyperion.rs2.action.impl;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.combat.CombatCheck;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Definitions;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * Based off HarvestingAction, but modified because it uses a NPC instead of Object.
 * @author phil
 * @date 2/4/2011
 */
public class FishingAction extends Action {

	public FishingAction(Player player, Location location, FISH f) {
		super(player, 600);
		// TODO Auto-generated constructor stub
		this.fishingSpot = location;
		this.fish = f;
	}
	
	private Location fishingSpot;
	
	private FISH fish;

	public enum SPOTS {
		LURE_BAIT(315, FISH.TROUT, FISH.SARDINES),
		NET_BAIT(320, FISH.SHRIMP, FISH.HERRING),
		LURE_BAIT2(311, FISH.SALMON, FISH.COD),
		CAGE_HARPOON(312, FISH.LOBSTER, FISH.TUNA),
		NET_HARPOON(313, FISH.MONKFISH, FISH.SHARK);
		
		short id;
		public FISH[] options = new FISH[2];
		SPOTS(int id, FISH a, FISH b) {
			this.id = (short) id;
			options[0] = a;
			options[1] = b;
		}
		static Map<Short, SPOTS> spot = new HashMap<Short, SPOTS>();
		
		public static SPOTS getSpot(int id) {
			return spot.get(id);
		}
		
		static {
			for(SPOTS s : SPOTS.values())
				spot.put(s.id, s);
		}
	}
	
	public enum FISH {
		SHRIMP(317, 1, 10, 0.50, new short[] {303}, 620),
		SARDINES(327, 5, 20, 0.46, new short[] {307, 313}, 622),
		HERRING(345, 10, 30, 0.43, new short[] {307, 313}, 622),
		ANCHOVIES(321, 15, 40, 0.41, new short[] {303}, 620),
		COD(341, 23, 45, 0.40, new short[] {305}, 620),
		SALMON(331, 30, 70, 0.35, new short[] {309, 314}, 622),
		TROUT(335, 20, 50, 0.44, new short[] {309, 314}, 622),
		LOBSTER(377, 40, 90, 0.33, new short[] {301}, 619),
		SHARK(383, 76, 110, 0.23, new short[] {311}, 618),
		SWORDFISH(371, 50, 100, 0.27, new short[] {311}, 618),
		MONKFISH(7944, 62, 120, 0.35, new short[] {303}, 620),
		TUNA(359, 55, 80, 0.35, new short[] {311}, 618),
		SEA_TURTLE(395, 79, 38, 0.20, new short[] {303}, 620),
		MANTA(389, 81, 46, 0.15, new short[] {303}, 620);
		
		short fish, emote;
		byte level;
		double xp, factor;
		short[] materials;
		FISH(int id, int lv, double xp, double catchChance, short[] materials, int anim) {
			fish = (short) id;
			level = (byte) lv;
			this.xp = xp;
			factor = catchChance;
			emote = (short) anim;
			this.materials = materials;
		}
	}
	
	/**
	 * Determines if it's applicable for fishing
	 * @return
	 */
	public boolean startUpCheck() {
		// TODO Auto-generated method stub
		Player p = getPlayer();
		if(p.getSkills().getLevel(Skills.FISHING) < fish.level) {
			p.getActionSender().sendMessage("You need a fishing level of at least "+fish.level+".");
			return false;
		}
		for(int item : fish.materials) {
			if(!p.getInventory().contains(item)) {
				p.getActionSender().sendMessage("You do not have a "+Definitions.forId(item).getName()+".");
				return false;
			}
		}
		return true;
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		// TODO Auto-generated method stub
		return QueuePolicy.ALWAYS;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		// TODO Auto-generated method stub
		return WalkablePolicy.NON_WALKABLE;
	}
	
	/**
	 * Gloves to increase xp and rate of catch.
	 * @return
	 */
	public boolean hasGloves() {
		return (fish == FISH.SHARK && getPlayer().getEquipment().contains(12861));
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		if(!getPlayer().getLocation().withinRange(fishingSpot, 1)) return;
		if(!startUpCheck()) {
			this.stop();
			return;
		}
		Player p = getPlayer();
		if(!(p.getInventory().freeSlots() > 0)) {
			p.getActionSender().sendMessage("You do have run out of inventory space.");
			p.playAnimation(Animation.create(-1));
			this.stop();
			return;
		}
		p.face(fishingSpot);
		if(p.getCurrentAnimation() == null)
			p.playAnimation(Animation.create(fish.emote));
		if(CombatCheck.random(((fish.level * (hasGloves() ? 12 : 10)) * fish.factor)) <= p.getSkills().getLevel(Skills.FISHING) * (fish.factor / 2)) {
			p.getInventory().add(new Item(fish.fish));
			if(fish.materials.length > 1)
				p.getInventory().remove(new Item(fish.materials[1]));
			p.getActionSender().sendMessage("You catch a "+Definitions.forId(fish.fish).getName().replace("Raw ", "")+".");
			p.getSkills().addExperience(Skills.FISHING, hasGloves() ? 1.10 * fish.xp * Constants.SKILL_EXPERIENCE : fish.xp * Constants.SKILL_EXPERIENCE);
		}
	}

}
