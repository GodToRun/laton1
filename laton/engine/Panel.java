package laton.engine;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import vecutils.Vector4;

public class Panel extends UIObject {
	public Color c;
	public Panel(Canvas canvas, int x, int y, Color c, float size) {
		super(canvas, x, y, size);
		this.c = c;
		this.size = 1;
	}
	@Override
	public void render() {
		if (1==1)return;
		// ugly. but it does not matter to performance!
		glLoadIdentity();
		//GL11.glEnable(GL11.GL_BLEND);
		float z = -2f;
		glTranslatef(0, 0, z);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(c.r, c.g, c.b, c.a);          // Set The Color To Red
		glBegin(GL_QUADS);
		glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Front)
		glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Front)
		glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Front)
		glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Front)
		glEnd();
		glDisable(GL_BLEND);
		//GL11.glDisable(GL11.GL_BLEND);
	}
}
