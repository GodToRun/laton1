package laton.main;

import laton.engine.Camera;
import laton.engine.GameObject;
import laton.engine.Mesh;
import vecutils.Vector3;
import vecutils.Vector4;

public class Monster extends GameObject {
	Game laton;
	public Camera cam;
	public boolean died = false;
	public int hp = 35;
	int rotTick = 0;
	public boolean isInWater = false;
	public boolean haveAI = true;
	Vector3 dir;
	public Monster(Game laton, Camera cam, Mesh mesh) {
		super(mesh);
		this.laton = laton;
		this.cam = cam;
		this.rotation = new Vector4(0, 1, 0, 0);
		compareAABB = laton.mapAABB;
		minAABB = new Vector3(0, -6.2f, 0);
		sizeAABB = new Vector3(1, 1, 1);
		gravity = -0.025f;
	}
	@Override
	public void update() {
		super.update();
		if (!died && haveAI)
			onAI();
	}
	public void moveToPlayer(GameObject target, float speed) {
		Vector3 dir = target.getPosition().sub(getPosition()).normalized();
		dir.y = 0;
		velocity = dir.mul(1f * speed);
	}
	public void lookPlayer() {
		dir = cam.getPosition().sub(getPosition()).normalized();
		dir.y = 0;
		float a = (float)Math.toDegrees(Math.atan2(dir.x, dir.z))+90;
		this.rotation.y = a;
	}
	public void onAI() {
		rotTick++;
		if (rotTick > 100) {
			if (Math.random() < 0.5 || rotation.t < 42) {
				rotation.t += 42;
				rotation.t %= 360;
			}
			else{
				rotation.t -= 42;
			}
			rotTick = 0;
		}
	}
	public void damage(int hp) {
		this.hp -= hp;
		if (this.hp <= 0) {
			onDeath();
		}
	}
	public void onDeath() {
		died = true;
		aniTick = 0;
	}
}
