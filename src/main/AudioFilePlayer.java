package main;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javazoom.spi.vorbis.sampled.file.VorbisAudioFileReader;

public class AudioFilePlayer {

	private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
		byte[] buffer = new byte[64];
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			line.write(buffer, 0, n);
		}
	}

	public void play() {
		Thread thread = new Thread() {
			public void run() {
				for (;;) {
					playOgg("/assets/music1.ogg", 30);
					playOgg("/assets/music2.ogg", 30);
				}
			}
		};
		thread.start();
	}

	public void playSound(String name) {
		Thread thread = new Thread() {
			public void run() {
				playOgg(name, 100);
			}
		};
		thread.start();
	}

	public void playOgg(String name, int volume) {
		try {
			AudioInputStream in = null;
			VorbisAudioFileReader mp = new VorbisAudioFileReader();
			in = mp.getAudioInputStream(ImageLoader.class.getResourceAsStream(name));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
					baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			Info info = new Info(SourceDataLine.class, targetFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(targetFormat);
			line.start();
			FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(20.0f * (float) Math.log10(volume / 100.0));
			stream(AudioSystem.getAudioInputStream(targetFormat, in), line);
			line.drain();
			line.stop();
		} catch (Exception ue) {
			ue.printStackTrace();
		}
	}
}