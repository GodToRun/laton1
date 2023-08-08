package laton.engine;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import vecutils.Vector2;
import vecutils.Vector3;
import vecutils.Vector4;

public class GameObject {
	public int vbo_vertex_handle;
	public int vbo_color_handle;
	public int vbo_tex_handle;
	public int vertices = 3;
	public GameObject parent = null;
	public int vertex_size = 3; // X, Y, Z
	public int color_size = 4; // R, G, B, A
	public int tex_size = 2; // X, Y
	protected Vector3 position = Vector3.ZERO();
	public Vector3 offset = Vector3.ZERO();
	protected Vector4 rotation = new Vector4(0.0f, 0.0f, 0.0f, 0.0f);
	protected Vector3 scale = new Vector3(1, 1, 1);
	public Texture texture;
	public static ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public static ArrayList<GameObject> deletePool = new ArrayList<GameObject>();
	public Mesh mesh;
	public Vector3 velocity = Vector3.ZERO();
	public float gravity = 0;
	private boolean isActive = true;
	public Vector3 minAABB;
	public Vector3 sizeAABB;
	public ArrayList<AABB> compareAABB;
	public ForceMode forceMode = ForceMode.Acceleration;
	public float bounce = 0f;
	
	public Animation animation;
	public int aniTick = 0;
	
	public Vector3 forward = Vector3.ONE();
	public Vector3 right = Vector3.ONE();
	
	public boolean inGround = true, isSideCollisioned = false;
	private AABB aabb;
	public GameObject(Mesh mesh) {
		this.mesh = mesh;
		objects.add(this);
		minAABB=Vector3.ZERO();
	}
	public static void processDeletePool() {
		for (GameObject go : deletePool) {
			objects.remove(go);
		}
		deletePool.clear();
	}
	public final AABB getAABB() {
		return aabb;
	}
	public void addToDeletePool() {
		deletePool.add(this);
	}
	// Physics Engine
	public void aabbUpdate(AABB camAABB, ArrayList<AABB> mapAABB) {
		GameObject cam = this;
		boolean inGround = false;
		for (AABB aabb : mapAABB) {
			if (camAABB.intersects(aabb)) {
				/* Y-Axis Collision Check */
				camAABB.move(new Vector3(0, -this.velocity.y, 0));
				if (!camAABB.intersects(aabb) || this.inGround) {
					if (camAABB.max.y > aabb.min.y && camAABB.min.y < aabb.max.y && camAABB.min.y+4f > aabb.max.y) {
						this.getPosition().y += aabb.max.y - camAABB.min.y;
						//camAABB.min.y = aabb.max.y;
					}
					inGround = true;
				}
				camAABB.move(new Vector3(0, this.velocity.y, 0));
				/* (X,Z)-Axis Collision Check */
				camAABB.move(new Vector3(-this.velocity.x, 0, 0));
				boolean x = false, z = false;
				if (!camAABB.intersects(aabb) && camAABB.min.y < aabb.max.y) {
					x = true;
					this.move(new Vector3(-this.velocity.x, 0, 0));
				}
				else {
					x = false;
					camAABB.move(new Vector3(this.velocity.x, 0, 0));
				}
				
				camAABB.move(new Vector3(0, 0, -this.velocity.z));
				if (!camAABB.intersects(aabb) && camAABB.min.y < aabb.max.y) {
					z = true;
					cam.move(new Vector3(0, 0, -this.velocity.z));
				}
				else {
					z = false;
					camAABB.move(new Vector3(0, 0, this.velocity.z));
				}
				if (!isSideCollisioned)
					isSideCollisioned = !x || !z;
			}
		}
		this.inGround = inGround;
		if (this.inGround && bounce > 0) {
			this.inGround = false;
			this.velocity.y += bounce;
			bounce /= 1.8f;
			if (bounce < 0.3f) {
				bounce = 0;
			}
		}
		/*if (!inGround && isSideCollisioned && bounce >= 0.3f) {
			bounce /= 1.8f;
			System.out.println("OMG");
			this.velocity = this.velocity.mul(-1);
		}*/
	}
	public boolean active() {
		return isActive;
	}
	public void setActive(boolean active) {
		this.isActive = active;
	}
	public void lookAt(GameObject go, Vector3 direction) {
		
	}
	public void create() {
		vertices = mesh.vertices.length;
		//vertex_size = 
		vbo();
		
	}
	protected void vbo() {
		FloatBuffer vertex_data = BufferUtils.createFloatBuffer(vertices * vertex_size);
		for (Vector3 vertex : mesh.vertices) {
			if (vertex != null)
				vertex_data.put(new float[] { vertex.x, vertex.y, vertex.z });
		}
		vertex_data.flip();

		FloatBuffer color_data = BufferUtils.createFloatBuffer(vertices * color_size);
		if (mesh.colors.length == 1) {
			for (int i = 0; i < vertices; i++) {
				color_data.put(new float[] { mesh.colors[0].x, mesh.colors[0].y, mesh.colors[0].z, mesh.colors[0].t });
			}
		}
		else {
			for (Vector4 color : mesh.colors) {
				color_data.put(new float[] { color.x, color.y, color.z, color.t });
			}
		}
		FloatBuffer tex_data = BufferUtils.createFloatBuffer(vertices * tex_size);
		for (Vector2 uv : mesh.uvs) {
			if (uv != null)
				tex_data.put(new float[] { uv.x, uv.y });
		}
		tex_data.flip();
		color_data.flip();

		vbo_vertex_handle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_vertex_handle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_data, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		vbo_color_handle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_color_handle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, color_data, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		vbo_tex_handle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_tex_handle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tex_data, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	public void move(Vector3 vec) {
		this.position = this.position.add(vec);
	}
	public void setPosition(Vector3 vec) {
		this.position = vec;
	}
	public Vector3 getPosition() {
		return this.position;
	}
	public void setScale(Vector3 vec) {
		this.scale = vec;
	}
	public Vector3 getScale() {
		return this.scale;
	}
	public void setRotation(Vector4 roc) {
		this.rotation = roc;
	}
	public Vector4 getRotation() {
		return this.rotation;
	}
	public void update() {
		if (!active()) return;
		forward = new Vector3(-(float)Math.sin(Math.toRadians(rotation.y)), (float)Math.sin(Math.toRadians(rotation.x)), (float)Math.cos(Math.toRadians(rotation.y)));
		right = new Vector3(-(float)Math.sin(Math.toRadians(rotation.y+90)), 0, (float)Math.cos(Math.toRadians(rotation.y+90)));
		if (forceMode == ForceMode.Acceleration) {
			float acc = 1.08f;
			velocity.x /= acc;
			velocity.y /= acc;
			velocity.z /= acc;
		}
		move(velocity);
		if (!inGround) {
			// Add Gravity And Y-Axis Collision
			velocity.y += gravity;
		}
		else if (gravity != 0) {
			velocity.y = 0;
		}
		if (animation != null) {
			aniTick++;
			if (aniTick > 60/animation.keyframes) {
				animation.onAnimation(animation.current_keyframe);
				animation.current_keyframe++;
				if (animation.current_keyframe >= animation.keyframes) {
					animation.current_keyframe = 0;
				}
				aniTick = 0;
			}
		}
		if (sizeAABB != null && compareAABB != null) {
			aabb = new AABB(getPosition().add(minAABB), getPosition().add(sizeAABB));
			aabbUpdate(aabb, compareAABB);
		}
	}
}