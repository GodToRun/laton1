package laton.main;

import java.util.ArrayList;

import laton.engine.GameObject;
import laton.engine.Mesh;
import laton.engine.Quad;
import vecutils.Vector3;
import vecutils.Vector4;

public class ParticleSystem extends GameObject {
	int i = 0;
	public float des = 8f;
	float dt = 0f;
	float sd = 0;
	int di = 0;
	public boolean destroyWhenEnded = false;
	public ParticleSystem(Vector4 col) {
		super(new Quad());
		mesh.colors[0] = col;
		mesh.vertices = new Vector3[1600];
	}
	public void emit(Vector3 pos) {
		float s = 0.18f;
		if (i >= mesh.vertices.length-4) return;
		mesh.vertices[i] = pos;
		mesh.vertices[i+1] = pos.add(new Vector3(s, 0, 0));
		mesh.vertices[i+2] = pos.add(new Vector3(s, s, 0));
		mesh.vertices[i+3] = pos.add(new Vector3(0, s, 0));
		i += 4;
	}
	@Override
	public void create() {
		vertices = i;
		//vertex_size = 
		if (vertices > 0)
			vbo();
	}
	@Override
	public void update() {
		if (sd > 50 && i > 0)
			dt++;
		sd++;
		if (dt > des) {
			if (di+7 >= i) {
				i = 0;
				di = 0;
				dt = 0;
				if (destroyWhenEnded) {
					setActive(false);
					addToDeletePool();
				}
				return;
			}
			for (int j = 0; j < 2; j++) {
				mesh.vertices[di+0] = null;
				mesh.vertices[di+1] = null;
				mesh.vertices[di+2] = null;
				mesh.vertices[di+3] = null;
				di += 4;
				i -= 4;
				
			}
			dt = 0;
			if (i > 0)
				create();
		}
	}
}
