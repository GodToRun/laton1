package laton.engine;
import static org.lwjgl.openal.AL10.*;

import java.io.InputStream;
import java.nio.ShortBuffer;

import org.lwjgl.openal.Util;
public class AudioBuffer {
	private final int bufferId;

    public AudioBuffer(InputStream is) {
        this.bufferId = alGenBuffers();
        WaveData wave = WaveData.create(is);
        alBufferData(bufferId, wave.format, wave.data, wave.samplerate);
        wave.dispose();
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public void cleanup() {
        alDeleteBuffers(this.bufferId);
    }
}
