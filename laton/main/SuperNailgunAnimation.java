package laton.main;

import laton.engine.Animation;
import laton.engine.GameObject;
import laton.engine.OBJLoader;
import laton.engine.Sector;
import vecutils.Vector3;
import vecutils.Vector4;

public class SuperNailgunAnimation extends GunAnimation {
	GameObject shotgun;
	int tick = 0;
	int current_keyframe = 0;
	public SuperNailgunAnimation(GameObject shotgun) {
		ended = true;
		keyframes = 2;
		this.shotgun = shotgun;
		this.shotgun.setRotation(new Vector4(0, 0, 1, 0));
	}
	@Override
	public void init() {
		//shotgun.getPosition().z += 0.7f;
	}
	@Override
	public void update() {
		if (!ended) {
			tick++;
			if (tick > 5) {
				onAnimation(current_keyframe);
				current_keyframe++;
				tick = 0;
				if (current_keyframe >= keyframes) {
					current_keyframe = 0;
					ended = true;
					return;
				}
			}
		}
		else {
			current_keyframe = 0;
			tick = 0;
		}
	}
	@Override
	public void onAnimation(int keyframe) {
		if (ended) return;
		if (keyframe == 0) {
			this.shotgun.getRotation().z = 45;
		}
		else if (keyframe == 1) {
			this.shotgun.getRotation().z = 0;
		}
	}

}
