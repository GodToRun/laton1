package laton.main;

import laton.engine.Camera;
import laton.engine.GameObject;
import laton.engine.Mesh;
import laton.engine.OBJLoader;
import laton.engine.Textures;
import vecutils.Vector3;
import vecutils.Vector4;

public class ShubNiggurath extends Monster {
	public GameObject spike;
	int st = 0;
	public ShubNiggurath(Game laton, Camera cam, Mesh mesh) {
		super(laton, cam, mesh);
		spike = new GameObject(new OBJLoader("lmodels/shubspike.obj"));
		spike.create();
		texture = Textures.loadTexture("ltex/shub.png", 9728);
		this.hp = 2147483647;
		gravity = 0f;
	}
	@Override
	public void update() {
		super.update();
		st++;
		float val = (float)Math.sin((double)st / 135.0) * 115;
		spike.getPosition().x = getPosition().x;
		spike.getPosition().y = getPosition().y;
		spike.getPosition().z = val+30;
	}
	@Override
	public void onAI() {
	}
	@Override
	public void onDeath() {
		super.onDeath();
		spike.setActive(false);
		Gib gib = new Gib(laton, getPosition().copy(), 6f, 58);
		animation = null;
		laton.doEnding();
		setActive(false);
		addToDeletePool();
	}
}
