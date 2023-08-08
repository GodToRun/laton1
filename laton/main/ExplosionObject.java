package laton.main;

import laton.engine.AudioSource;
import laton.engine.GameObject;
import laton.engine.Mesh;
import laton.engine.Quad;
import laton.engine.Textures;
import vecutils.Vector3;

public class ExplosionObject extends GameObject {
	AudioSource expSource;
	public ExplosionObject(Game laton) {
		super(new Quad());
		setScale(new Vector3(2, 2, 2));
		expSource = new AudioSource(false, true);
		expSource.setBuffer(laton.expClip.getBufferId());
		expSource.play();
		mesh.isBillboard = true;
		texture = Textures.loadTexture("ltex/exp1.png", 9728);
		animation = new ExplosionAnimation(this);
	}

}
