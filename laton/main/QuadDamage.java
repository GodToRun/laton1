package laton.main;

import laton.engine.Mesh;
import laton.engine.Textures;
import vecutils.Vector3;

public class QuadDamage extends Pickable {
	Game laton;
	public QuadDamage(Game laton, Mesh mesh, float rotate, String tex) {
		super(mesh, rotate);
		setScale(new Vector3(0.6f, 0.6f, 0.6f));
		this.texture = Textures.loadTexture(tex, 9728);
		this.laton = laton;
	}
	@Override
	public void onPick() {
		this.laton.damageMultipler = 4f;
		this.laton.damMultipleTick = 0;
		this.laton.panelCol.r = 0.12f;
		this.laton.panelCol.g = 0.24f;
		this.laton.panelCol.b = 0.68f;
		this.laton.panelCol.a = 0.3f;
		this.laton.gamePanel.renderd = true;
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
