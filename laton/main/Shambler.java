package laton.main;

import laton.engine.AABB;
import laton.engine.Camera;
import laton.engine.GameObject;
import laton.engine.Mesh;
import laton.engine.OBJLoader;
import laton.engine.Textures;
import vecutils.Vector3;
import vecutils.Vector4;

public class Shambler extends Monster {
	GameObject lit;
	int tick = 0;
	int damTick = 0;
	GameObject target;
	boolean toTarget = false;
	OBJLoader lightning;
	public Shambler(Game laton, Camera cam, Mesh mesh) {
		super(laton, cam, mesh);
		this.lightning = new OBJLoader("lmodels/lightning.obj");
		this.hp = 600;
		texture = Textures.loadTexture("ltex/shambler.png", 9728);
		ShamblerAnimation ani = new ShamblerAnimation(this, (OBJLoader)this.mesh);
		this.animation = ani;
		this.rotation = new Vector4(0, 1, 0, 0);
		target = cam;
	}
	void doLightning() {
		lit = new GameObject(lightning);
		lit.texture = Textures.loadTexture("ltex/lightning.png", 9728);
		lit.setPosition(getPosition().sub(new Vector3(0, 2, 0)));
		lit.setRotation(new Vector4(0, 1, 0, this.rotation.t));
		lit.create();
	}
	@Override
	public void update() {
		super.update();
		if (!died) {
			Vector3 dis = getPosition().distance(target.getPosition());
			if (toTarget) {
				damTick++;
				if (damTick == 100) {
					doLightning();
					boolean s = false;
					int i;
					raycasting:
					for (i = 0; i < 12; i++) {
						move(dir.mul(2));
						if (getPosition().distance(laton.cam.getPosition()).length() < 3f) {
							i++;
							break raycasting;
						}
						for (AABB aabb : laton.mapAABB) {
							if (new AABB(getPosition(), getPosition().add(new Vector3(1, 1, 1))).intersects(aabb)) {
								i++;
								s = true;
								break raycasting;
							}
						}
					}
					move(dir.mul(-2*i));
					if (!s)
						laton.playerHP -= 20;
				}
				if (damTick > 100) {
					lit.getRotation().x = (float) Math.sin(System.currentTimeMillis()/10);
					if (lit.getRotation().x < 0) lit.getRotation().x = 0.1f;
				}
				if (damTick > 120) {
					lit.setActive(false);
					damTick = 0;
				}
			}
			if (dis.x < 30f && dis.y < 10f && dis.z < 30f) {
				toTarget = true;
				if (dis.x >= 14f || dis.z >= 14f)
					moveToPlayer(target, 0.1f);
			}
			else {
				toTarget = false;
			}
		}
		
	}
	@Override
	public void onAI() {
		if (!toTarget) super.onAI();
		else {
			tick++;
			if (tick > 18) {
				lookPlayer();
				tick = 0;
			}
		}
	}
	@Override
	public void onDeath() {
		super.onDeath();
		if (lit != null)
			lit.setActive(false);
		if (hp < -50) { // Gibbed
			Gib gib = new Gib(laton, getPosition().copy());
			animation = null;
			setActive(false);
			addToDeletePool();
		}
		else {
			animation = new GruntDeathAnimation(this, (OBJLoader)this.mesh);
			((GruntDeathAnimation)animation).legMin = -7.6f;
		}
	}
}
