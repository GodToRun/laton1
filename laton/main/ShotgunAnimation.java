package laton.main;

import laton.engine.Animation;
import laton.engine.GameObject;
import laton.engine.OBJLoader;
import laton.engine.Sector;
import vecutils.Vector3;

public class ShotgunAnimation extends GunAnimation {
	GameObject shotgun;
	int tick = 0;
	int current_keyframe = 0;
	float amount = 0f;
	Sector fire;
	public ShotgunAnimation(GameObject shotgun, float amount) {
		this.amount = amount;
		ended = true;
		keyframes = 6;
		this.shotgun = shotgun;
		OBJLoader loader = (OBJLoader)shotgun.mesh;
		fire = loader.findSector("fire");
		if (fire != null) {
			for (Vector3 v : fire.vecs) {
				v.x -= 100;
				v.z -= 100;
			}
		}
		
		shotgun.create();
	}
	@Override
	public void init() {
		shotgun.getPosition().z += this.amount;
		shotgun.getRotation().x += 21f * this.amount;
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
			if (fire != null)
				for (Vector3 v : fire.vecs) {
					v.x += 100;
					v.z += 100;
				}
			this.shotgun.create();
		}
		else if (keyframe >= 1) {
			if (keyframe == 1) {
				if (fire != null)
					for (Vector3 v : fire.vecs) {
						v.x -= 100;
						v.z -= 100;
					}
				this.shotgun.create();
			}
			shotgun.getRotation().x -= 21 * this.amount / (keyframes-1);
			this.shotgun.getPosition().z -= this.amount / (keyframes-1);
		}
	}

}
