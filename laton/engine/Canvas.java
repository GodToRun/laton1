package laton.engine;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
public class Canvas {
	public ArrayList<UIObject> uis = new ArrayList<UIObject>();
	public void render(Camera cam) {
		for (UIObject ui : uis) {
			if (!ui.renderd) continue;
			GL11.glLoadIdentity();
			ui.render();
			
		}
	}
}
