package laton.main;

import laton.engine.AABB;
import laton.engine.GameObject;
import laton.engine.OBJLoader;
import laton.engine.Texture;
import laton.engine.Textures;
import vecutils.Vector3;
import vecutils.Vector4;

public class Gib {
	GibObj[] gibs = new GibObj[5];
	Game laton;
	ParticleSystem particle;
	float vel;
	public Gib(Game laton, Vector3 pos, float vel, int num) {
		gibs = new GibObj[num];
		this.laton = laton;
		particle = new ParticleSystem(new Vector4(0.55f, 0, 0, 0.95f));
		particle.destroyWhenEnded = true;
		particle.create();
		Texture gibTex = Textures.loadTexture("ltex/gib.png", 9728);
		OBJLoader loader = new OBJLoader("lmodels/gib.obj");
		for (int i = 0; i < gibs.length; i++) {
			gibs[i] = new GibObj(particle, loader);
			gibs[i].setPosition(pos);
			gibs[i].create();
			gibs[i].texture = gibTex;
			gibs[i].velocity.y = 0.2f + ((float)Math.random() * (vel-0.7f));
			gibs[i].velocity.x = 0.5f - ((float)Math.random() * vel);
			gibs[i].velocity.z = 0.5f - ((float)Math.random() * vel);
			gibs[i].gravity = -0.025f;
			gibs[i].sizeAABB = new Vector3(1, 1, 1);
			gibs[i].compareAABB = laton.mapAABB;
			gibs[i].inGround = false;
		}
	}
	public Gib(Game laton, Vector3 pos) {
		this(laton, pos, 1.2f, 5);
	}
}
