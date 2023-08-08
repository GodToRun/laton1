package laton.main;

import laton.engine.Mesh;
import laton.engine.Textures;
import vecutils.Vector3;

public class SilverKey extends Pickable {
	Game laton;
	public SilverKey(Game laton, Mesh mesh, float rotate, String tex) {
		super(mesh, rotate);
		setScale(new Vector3(0.6f, 0.6f, 0.6f));
		this.texture = Textures.loadTexture(tex, 9728);
		this.laton = laton;
	}
	@Override
	public void onPick() {
		this.laton.silverkey = true;
		this.laton.playerSource.setBuffer(this.laton.ammoClip.getBufferId());
		this.laton.playerSource.play();
		setActive(false);
	}
	@Override
	public void update() {
		super.update();
		getRotation().y += 7;
	}

}
