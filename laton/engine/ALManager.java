package laton.engine;
import static org.lwjgl.openal.ALC10.*;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCcontext;
import org.lwjgl.openal.ALCdevice;

import static org.lwjgl.openal.AL10.*;
public class ALManager {
	private ALCcontext cont;
	private ALCdevice device;
	public void create() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		cont = alcGetCurrentContext();
		device = alcGetContextsDevice(cont);
		//cont = alcCreateContext(device, b);
		//alcMakeContextCurrent(cont);
	}
	public void destroy() {
		//alcDestroyContext(cont);
		//alcCloseDevice(device);
		AL.destroy();
	}
}
