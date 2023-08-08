package laton.main;

import laton.engine.GameObject;
import laton.engine.IEvent;
import laton.engine.Sector;
import vecutils.Vector3;
import vecutils.Vector4;

public class ExitEvent extends IEvent {
	Game laton;
	int tick = 0;
	public ExitEvent(Game laton) {
		this.laton = laton;
	}
	@Override
	public void onActive() {
		started = true;
	}

	@Override
	public void update() {
		if (ended || !started) return;
		laton.titleText.text = "LEVEL COMPLETED";
		laton.cam.setPosition(new Vector3(50, 70, 50));
		laton.cam.setRotation(new Vector4(40, -0.4f, 0.2f, 0f));
		tick++;
		if (tick > 200) {
			laton.titleText.text = "";
			laton.level++;
			laton.loadLevel("lmodels/m" + laton.level + ".obj", laton.codes[laton.level-1]);
		}
			
	}

}
