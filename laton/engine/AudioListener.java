package laton.engine;
import static org.lwjgl.openal.AL10.*;

import java.nio.FloatBuffer;

import vecutils.Vector3;
public class AudioListener {
	public AudioListener() {
        this(new Vector3(0, 0, 0));
    }

    public AudioListener(Vector3 position) {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);

    }

    public void setSpeed(Vector3 speed) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setPosition(Vector3 position) {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
    }

    public void setOrientation(Vector3 at, Vector3 up) {
        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        FloatBuffer b = FloatBuffer.allocate(6);
        b.put(data);
        alListener(AL_ORIENTATION, b);
    }
}
