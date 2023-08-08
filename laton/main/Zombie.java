package laton.main;

import laton.engine.Camera;
import laton.engine.Mesh;
import laton.engine.OBJLoader;
import laton.engine.Textures;
import vecutils.Vector3;
import vecutils.Vector4;

public class Zombie extends Monster {
	int tick = 0;
	int damTick = 0;
	int resurrTick = 0;
	public Zombie(Game laton, Camera cam, Mesh mesh) {
		super(laton, cam, mesh);
		this.hp = 60;
		texture = Textures.loadTexture("ltex/zombie.png", 9728);
		GruntAnimation ani = new GruntAnimation(this, (OBJLoader)this.mesh);
		this.animation = ani;
		this.rotation = new Vector4(0, 1, 0, 0);
	}
	@Override
	public void update() {
		super.update();
		if (died && active()) resurrTick++;
		if (resurrTick > 250) {
			this.hp = 60;
			died = false;
			resurrTick = 0;
			mesh = new OBJLoader("lmodels/zombie.obj");
			create();
		}
	}
	@Override
	public void onAI() {
		tick++;
		if (tick > 18) {
			lookPlayer();
			tick = 0;
		}
		Vector3 dis = getPosition().distance(cam.getPosition());
		if (dis.x < 6f && dis.y < 5f && dis.z < 6f) {
			if (animation.getClass() != KnightActionAnimation.class) {
				//animation = new KnightActionAnimation(this, (OBJLoader)this.mesh);
			}
			damTick++;
			if (damTick > 20) {
				damTick = 0;
				laton.playerHP -= 7;
			}
		}
		else if (dis.x < 28f && dis.y < 6f && dis.z < 28f) {
			if (animation.getClass() != GruntAnimation.class) {
				GruntAnimation ani = new GruntAnimation(this, (OBJLoader)this.mesh);
				this.animation = ani;
			}
			moveToPlayer(cam, 0.15f);
		}
	}
	@Override
	public void onDeath() {
		super.onDeath();
		if (hp < -25) { // Gibbed
			Gib gib = new Gib(laton, getPosition().copy());
			animation = null;
			setActive(false);
			addToDeletePool();
		}
		else {
			animation = new GruntDeathAnimation(this, (OBJLoader)this.mesh);
		}
	}
}
