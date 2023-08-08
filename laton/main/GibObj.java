package laton.main;

import laton.engine.GameObject;
import laton.engine.Mesh;
import vecutils.Vector3;
import vecutils.Vector4;

public class GibObj extends GameObject {

	int i;
	int ci = 0;
	int d = 0;
	ParticleSystem ps;
	public GibObj(ParticleSystem ps, Mesh mesh) {
		super(mesh);
		this.ps = ps;
		bounce = 0.2f + (float)Math.random() / 1.2f;
	}
	public void update() {
		super.update();
		d++;
		if (d > 25) {
			ps.setPosition(ps.getPosition().sub(new Vector3(0, 0.004f, 0)));
		}
		if (!isSideCollisioned && !inGround) {
			i++;
			ci++;
			if (i > 2) {
				ps.emit(position);
				ps.emit(position.add(new Vector3(0.3f, 0.05f, 0.2f)));
				i = 0;
			}
			if (ci > 8) {
				ci = 0;
				ps.create();
			}
			this.rotation.z += 14f;
		}
	}
}
