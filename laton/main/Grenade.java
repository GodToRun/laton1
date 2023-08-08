package laton.main;

import laton.engine.AABB;
import laton.engine.Camera;
import laton.engine.ForceMode;
import laton.engine.GameObject;
import laton.engine.Mesh;
import vecutils.Vector3;
import vecutils.Vector4;

public class Grenade extends GameObject {
	Vector3 forward;
	float speed;
	Game laton;
	AABB rocketAABB = new AABB(Vector3.ZERO(), Vector3.ZERO());
	Camera cam;
	int damage;
	int tick = 0;
	boolean isExplode = false;
	int emitTick = 0;
	ParticleSystem ps;
	public Grenade(Vector3 forward, Game laton, Camera cam, Mesh mesh, float speed, int damage, boolean isExplode) {
		super(mesh);
		ps = new ParticleSystem(new Vector4(0.8f, 0.8f, 0.8f, 0.7f));
		ps.destroyWhenEnded = true;
		ps.create();
		this.cam = cam;
		this.damage = damage;
		this.laton = laton;
		this.speed = speed;
		this.rotation = new Vector4(0, 1, 0, -cam.getRotation().y);
		this.gravity = -0.025f;
		this.bounce = 0.8f;
		//forceMode = ForceMode.Immediate;
		velocity = velocity.sub(forward.mul(speed));
		this.isExplode = isExplode;
	}
	void explode() {
		ExplosionObject obj = new ExplosionObject(laton);
		obj.setPosition(getPosition());
		obj.create();
		/*if (isExplode) {
			Vector3 l = cam.getPosition();
			Vector3 r = getPosition();
			float xi = (l.x - r.x);
			float yi = (l.y - r.y);
			float zi = (l.z - r.z);
			Vector3 dis = new Vector3(xi, yi, zi).normalized();
			if (dis.x != 0)
				dis.x /= Math.abs(xi);
			if (dis.y != 0)
				dis.y /= Math.abs(yi);
			if (dis.z != 0)
				dis.z /= Math.abs(zi);
			float limit = 1f;
			if (Math.abs(dis.x) > limit) dis.x = limit;
			if (Math.abs(dis.y) > limit) dis.y = limit;
			if (Math.abs(dis.z) > limit) dis.z = limit;
			cam.velocity = cam.velocity.add(dis.mul(f));
		}*/
	}
	@Override
	public void update() {
		super.update();
		if (!active()) return;
		rocketAABB.min = getPosition();
		rocketAABB.max = getPosition().add(new Vector3(0.5f, 0.5f, 0.5f));
		aabbUpdate(rocketAABB, laton.mapAABB);
		/*if (isSideCollisioned || inGround) {
			explode();
			setActive(false);
			
			return;
		}*/
		emitTick++;
		if (emitTick > 3) {
			emitTick = 0;
			ps.emit(position);
			ps.emit(position.add(new Vector3(0.28f, 0.08f, 0.32f)));
			ps.create();
		}
		tick++;
		if (tick > 100) {
			tick = 0;
			explode();
			setActive(false);
			return;
			
		}
		boolean isDestroy = false;
		for (Monster mon : laton.monsters) {
			if (!mon.active() || mon.died) continue;
			Vector3 dis = getPosition().distance(mon.getPosition());
			if (dis.x < 3.5f && dis.y < 4.5f && dis.z < 2.7f) {
				isDestroy = true;
				if (mon.getClass() == Shambler.class)
					mon.damage(damage/2);
				else
					mon.damage(damage);
			}
		}
		Vector3 dis = getPosition().distance(laton.cam.getPosition());
		if (dis.x < 2f && dis.y < 4f && dis.z < 1.3f) {
			isDestroy = true;
			laton.playerHP -= damage / 3f;
		}
		if (isDestroy) {
			explode();
			setActive(false);
			return;
		}
	}

}
