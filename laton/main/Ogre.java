package laton.main;

import org.lwjgl.opengl.GL11;

import laton.engine.Camera;
import laton.engine.Mesh;
import laton.engine.OBJLoader;
import laton.engine.Textures;
import vecutils.Vector3;
import vecutils.Vector4;

public class Ogre extends Monster {
	
	int tick = 0;
	int damTick = 0;
	int greTick = 0;
	public Ogre(Game laton, Camera cam, Mesh mesh) {
		super(laton, cam, mesh);
		this.hp = 200;
		texture = Textures.loadTexture("ltex/ogre.png", 9728);
		GruntAnimation ani = new GruntAnimation(this, (OBJLoader)this.mesh);
		this.animation = ani;
		this.rotation = new Vector4(0, 1, 0, 0);
	}
	@Override
	public void update() {
		super.update();
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
				animation = new KnightActionAnimation(this, (OBJLoader)this.mesh);
			}
			damTick++;
			if (damTick > 20) {
				damTick = 0;
				laton.playerHP -= 8;
			}
		}
		else if (dis.x < 28f && dis.y < 5.8f && dis.z < 28f) {
			if (animation.getClass() != GruntAnimation.class) {
				GruntAnimation ani = new GruntAnimation(this, (OBJLoader)this.mesh);
				this.animation = ani;
			}
			moveToPlayer(cam, 0.14f);
			greTick++;
			if (greTick > 60) {
				Grenade grenade = new Grenade(dir.mul(-1), laton, cam, laton.grenadeMesh, 2f, 90, true);
				grenade.texture = Textures.loadTexture("ltex/rocket.png", 9728);
				grenade.setScale(new Vector3(0.4f, 0.4f, 0.4f));
				grenade.setPosition(getPosition().add(dir.mul(6)).add(new Vector3(0, -0.15f, 0)));
				grenade.create();
				greTick = 0;
			}
		}
	}
	@Override
	public void onDeath() {
		super.onDeath();
		if (hp < -28) { // Gibbed
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
