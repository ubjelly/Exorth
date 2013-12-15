package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.WorldObject;
import org.hyperion.rs2.model.WorldObjectManager;

public class WorldObjectRemovalEvent extends Event {

	public WorldObjectRemovalEvent() {
		super(600);
	}

	@Override
	public void execute() {
		for (final WorldObject o : WorldObjectManager.objects) {
			if (o.ticks > 0) {
				o.ticks--;
			} else {
				WorldObjectManager.updateWorldObject(o);
				WorldObjectManager.remove.add(o);
			}
		}
		for (final WorldObject o : WorldObjectManager.remove) {
			WorldObjectManager.objects.remove(o);
		}
		WorldObjectManager.remove.clear();		
	}
}