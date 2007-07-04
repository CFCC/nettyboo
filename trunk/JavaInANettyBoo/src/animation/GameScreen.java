package animation;

import interaction.Creation;
import network.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends JFrame {

    private int width;
    private int height;
    private Color background;
    private List<ScreenObject> screenObjects = new ArrayList<ScreenObject>();

    public List<Ball> getBalls() {
        List<Ball> list = new ArrayList<Ball>();
        for (ScreenObject ball : screenObjects) {
            if (ball instanceof Ball) {
                list.add((Ball) ball);
            }
        }
        return list;
    }

    public List<ScreenObject> getScreenObjects() {
        return screenObjects;
    }

    public JPanel screen = new JPanel() {
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            Graphics2D g = (Graphics2D) gg;
            for (Ball fart : getBalls()) {
                g.setColor(fart.getColor());
                g.fillOval(fart.getPosition().x, fart.getPosition().y, 100, 100);
                fart.setPosition(new Point(fart.getPosition().x + fart.getSpeed().x, fart.getPosition().y + fart.getSpeed().y));
                if (fart.getPosition().y < 0) {
                    fart.getSpeed().y = -fart.getSpeed().y;
                }
                if (fart.getPosition().y > 645) {
                    fart.getSpeed().y = -fart.getSpeed().y;
                }
            }
        }
    };

    public GameScreen() {
        new Timer(1000 / 30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        }).start();
        add(screen);
    }

    //the list of balls on screen, each ball is assigned a specific number
    public void addBall(ScreenObject fart) {
        screenObjects.add(fart);
    }

    public static void main(String[] args) {
        GameScreen gameScreen = new GameScreen();

        new Creation(gameScreen);
        
        gameScreen.setSize(1280, 800);
        gameScreen.screen.setBackground(Color.black);
        gameScreen.setVisible(true);

//        Network network = new Network(gameScreen);
    }
}