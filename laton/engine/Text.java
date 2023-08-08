package laton.engine;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import vecutils.Vector4;

public class Text extends UIObject {
	public String text;
	TrueTypeFont font;
	public Color col;
	public Text(Canvas canvas, int x, int y, String text, float size) {
		super(canvas, x, y, size);
		col = new Color(1, 1, 1, 1);
		this.size = size;
		this.text = text;
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24); //name, style (PLAIN, BOLD, or ITALIC), size
		font = new TrueTypeFont(awtFont, false); //base Font, anti-aliasing true/false
	}
	@Override
	public void render() {
		GL11.glLoadIdentity();
		//Color.black.bind();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef(0, 0, -1);
		GL11.glScalef(0.01f * size, -0.01f * size, 0.01f * size);
		font.drawString(x, y, text, col);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
