package laton.main;

import vecutils.Vector3;

public class Teleporter {
	Vector3 pos, telpos;
	Game laton;
	public Teleporter(Game laton, Vector3 pos, Vector3 telpos) {
		this.pos = pos;
		this.laton = laton;
		this.telpos = telpos;
	}
	public void update() {
		if (laton.cam.getPosition().distance(pos).length() < 5f) {
			laton.cam.setPosition(telpos);
			for (Monster m : laton.monsters) {
				if (m.getPosition().distance(telpos).length() < 20) {
					m.damage(2147483647);
				}
			}
		}
	}
}
