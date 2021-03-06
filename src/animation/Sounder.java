package animation;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.Point;
import java.awt.Color;
import java.util.Arrays;
import java.io.ObjectInputStream;

public class Sounder extends GenericBall {
    public Sounder(Color color, Point speed, Point position, int radius) {
        super(color, speed, position, radius);

        startThread();
    }

    private void startThread() {
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
                    if (isDead()) {
                        break;
                    }
                    fillData(data);
                    line.write(data, 0, data.length);
                }
            }
        }).start();
    }

    private void readObject(ObjectInputStream in) {
        startThread();
    }

    private void fillData(byte[] data) {
        double speedMax300 =Math.min(10000, getSpeed().distance(0, 0));
        if (speedMax300 == 0) {
            Arrays.fill(data, (byte) 0);
            return;
        }
        int screenWidth = getScreenWidth();
        double radiusMax15 = Math.min(15, Math.sqrt(getRadius()));
        for (int i = 0; i < data.length; i += 2) {
            int freq = (int) (50 + speedMax300*2);//(int) (2000 + 1000*(Math.sin(Math.PI * 2 * i / (data.length / 10))));
            byte blog = square(freq, i);
            data[i] = (byte) (blog * (screenWidth -getPosition().x) / screenWidth * radiusMax15 / 15);
            data[i+1] = (byte) (blog * getPosition().x / screenWidth * radiusMax15 / 15);
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
