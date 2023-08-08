package laton.main;

import java.util.ArrayList;

import laton.engine.GameObject;
import laton.engine.IEvent;
import laton.engine.OBJLoader;
import laton.engine.Sector;
import vecutils.Vector3;

public class LSInterpreter {
	ArrayList<IEvent> events = new ArrayList<IEvent>();
	OBJLoader map;
	public LSInterpreter(Game laton, GameObject mapObj, OBJLoader map, String[] lines) {
		this.map = map;
		for (String line : lines) {
			String[] arg = line.split(" ");
			if (arg[0].equals("set") || arg[0].equals("skset")) {
				Sector eventInfo = map.findSector(arg[1]);
				Sector eventObject = map.findSector(arg[2]);
				String eventType = arg[3];
				if (eventType.equals("mvt")) {
					Vector3 per = new Vector3(
							Float.parseFloat(arg[4]),
							Float.parseFloat(arg[5]),
							Float.parseFloat(arg[6]));
					int amountTick = Integer.parseInt(arg[7]);
					IEvent mvtEvent = new MovementEvent(laton, mapObj, eventObject, per, amountTick);
					if (arg[0].equals("skset")) mvtEvent.needSilverKey = true;
					mvtEvent.origin = eventInfo.vecs.get(0).copy();
					events.add(mvtEvent);
				}
				else if (eventType.equals("ext")) {
					IEvent mvtEvent = new ExitEvent(laton);
					if (arg[0].equals("skset")) mvtEvent.needSilverKey = true;
					mvtEvent.origin = eventInfo.vecs.get(0).copy();
					events.add(mvtEvent);
				}
			}
		}
	}
	ArrayList<IEvent> expandEvents() {
		return events;
	}
}
