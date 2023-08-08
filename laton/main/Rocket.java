package laton.main;

import laton.engine.AABB;
import laton.engine.Camera;
import laton.engine.ForceMode;
import laton.engine.GameObject;
import laton.engine.Mesh;
import vecutils.Vector3;
import vecutils.Vector4;

public class Rocket extends GameObject {
	Vector3 forward;
	float speed;
	Game laton;
	AABB rocketAABB = new AABB(Vector3.ZERO(), Vector3.ZERO());
	Camera cam;
	int damage;
	boolean isExplode = false;
	public Rocket(Game laton, Camera cam, Mesh mesh, float speed, int damage, boolean isExplode) {
		super(mesh);
		this.cam = cam;
		this.damage = damage;
		this.laton = laton;
		this.speed = speed;
		this.rotation = new Vector4(cam.getRotation().x, -cam.getRotation().y, cam.getRotation().z, 0);
		forceMode = ForceMode.Immediate;
		forward = cam.forward.copy();
		velocity = velocity.sub(forward.mul(speed));
		this.isExplode = isExplode;
	}
	void explode() {
		ExplosionObject obj = new ExplosionObject(laton);
		obj.setPosition(getPosition());
		obj.create();
		if (isExplode) {
			Vector3 l = cam.getPosition();
			Vector3 r = getPosition();
			float xi = (l.x - r.x) / 1;
			float yi = (l.y - r.y) / 1;
			float zi = (l.z - r.z) / 1;
			Vector3 dir = new Vector3(xi, yi, zi).normalized();
			Vector3 dis = new Vector3(xi, yi, zi);
			Vector3 vel = new Vector3(dir.x * (1/dis.x), dir.y * (1/dis.y), dir.z * (1/dis.z));
			float limit = 3f;
			if (Math.abs(dis.x) > limit) dis.x = limit;
			if (Math.abs(dis.y) > limit) dis.y = limit;
			if (Math.abs(dis.z) > limit) dis.z = limit;
			cam.velocity = cam.velocity.add(vel);
			
		}
	}
	@Override
	public void update() {
		super.update();
		if (!active()) return;
		rocketAABB.min = getPosition();
		rocketAABB.max = getPosition().add(new Vector3(0.5f, 0.5f, 0.5f));
		aabbUpdate(rocketAABB, laton.mapAABB);
		if (isSideCollisioned || inGround) {
			explode();
			setActive(false);
			return;
		}
		boolean isDestroy = false;
		for (Monster mon : laton.monsters) {
			if (!mon.active() || mon.died) continue;
			Vector3 dis = getPosition().distance(mon.getPosition());
			if (dis.x < 11f && dis.y < 6f && dis.z < 10f) {
				isDestroy = true;
				if (mon.getClass() == Shambler.class)
					mon.damage(damage/2);
				else
					mon.damage(damage);
			}
		}
		if (isDestroy) {
			explode();
			setActive(false);
			return;
		}
	}

}
