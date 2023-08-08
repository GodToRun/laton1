package laton.engine;
import static org.lwjgl.opengl.GL11.*;
public class WorldLight {
	public static boolean light = false;
	public static void init() {
			light = true;
			glEnable(GL_COLOR_MATERIAL);
	}
}
