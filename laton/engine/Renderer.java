package laton.engine;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import vecutils.*;
public class Renderer {
	public GameObject skybox;
	public Vector3 offset = new Vector3(0, 0, 0);
	TrueTypeFont font = null;
	public void init(Camera cam) {
		skybox = new GameObject(new Cube());
		skybox.texture = Textures.loadTexture("ltex/sky.jpg", 9728);
		skybox.create();
		skybox.setScale(new Vector3(200, 200, 200));
		skybox.setPosition(cam.position);
		
		try {
		    InputStream inputStream = new FileInputStream("ltex/quake/quake2.ttf");
		    
		    Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		    awtFont = awtFont.deriveFont(16f); // set font size
		    font = new TrueTypeFont(awtFont, false);
		    
		        
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	public void render(Camera cam) {
		skybox.setPosition(cam.position);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		for (GameObject obj : GameObject.objects) {
			if (!obj.active()) continue;
			if (obj.mesh != null) {
				GL11.glLoadIdentity();
				Vector3 pos = obj.getPosition().copy().add(obj.offset);
				if (obj.mesh.renderInSpace) {
					pos = pos.add(offset);
				}
				Vector4 rot = obj.getRotation().copy();
				if (obj.parent != null) {
					pos = pos.add(obj.parent.getPosition().mul(-1));
					//rot = obj.parent.getRotation().mul(-1);
				}
				if (obj.vertices > 0)
					obj.mesh.renderVBO(cam, pos, obj.getScale(), rot, obj.vertices, obj.vbo_vertex_handle, obj.vbo_color_handle, obj.vbo_tex_handle, obj.vertex_size, obj.color_size, obj.tex_size, obj.texture);
			}
		}
	}		
}
