package animation;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.Point;
import java.awt.Color;

public class Sounder extends Ball {
    public Sounder(Color color, Point speed, Point position, int radius) {
        super(color, speed, position, radius);

        new Thread(new Runnable() {
            public void run() {
                byte[] data = new byte[4410];
                fillData(data);
                AudioFormat format = new AudioFormat(44100, 8, 2, true, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line;
                try {
                    line = (SourceDataLine) AudioSystem.getLine(info);
                } catch (LineUnavailableException e) {
                    return;
                }
                try {
                    line.open(format, data.length);
                } catch (LineUnavailableException e) {
                    return;
                }
                line.start();
                while (true) {
                    fillData(data);
                    line.write(data, 0, data.length);
                }
            }
        }).start();
    }

    private void fillData(byte[] data) {
        int screenWidth = getScreenWidth();
        for (int i = 0; i < data.length; i += 2) {
            int freq = 50 + (300 - Math.min(300, getRadius())) * 5;//(int) (2000 + 1000*(Math.sin(Math.PI * 2 * i / (data.length / 10))));
            byte blog = square(freq, i);
            data[i] = (byte) (blog * (screenWidth -getPosition().x) / screenWidth);
            data[i+1] = (byte) (blog * getPosition().x / screenWidth);
        }
    }

    private int getScreenWidth() {
        GameScreen screen = getGameScreen();
        if (screen == null) {
            return 1024;
        } else {
            return screen.getWidth();
        }
    }

    private static byte sine(int freq, int x) {
        return (byte) (Math.sin(x * (Math.PI * 2) / (44100 / freq)) * 127);
    }

    private static byte saw(int freq, int i) {
        return (byte) (i % (44100 / freq));
    }

    private static byte blade(int freq, int i) {
        int samples = 44100 / freq;
        return (byte) ((i % (samples * 2)) - samples);
    }

    private static byte square(int freq, int i) {
        int samples = 44100 / freq;
        return (byte) (i % (samples*2) > samples ? 127 : -128);
    }

}
