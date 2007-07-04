package animation;

import interaction.Interaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends JFrame {
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
            for (Ball b : getBalls()) {
                g.setColor(b.getColor());
                g.fillOval(b.getPosition().x - b.getRadius(), b.getPosition().y - b.getRadius(), b.getRadius()*2, b.getRadius()*2);
                if (b.getPosition().y + b.getSpeed().y -b.getRadius() < 0) {
                    int x1 = b.getPosition().x + b.getSpeed().x;
                    int y1 = -b.getSpeed().y - b.getPosition().y + 2*b.getRadius() ;
                    b.getPosition().x = x1;
                    b.getPosition().y = y1;
                    b.getSpeed().y = -b.getSpeed().y;
                } else if (b.getPosition().y  + b.getSpeed().y + b.getRadius()> getHeight()) {
                    int x1 = b.getPosition().x + b.getSpeed().x;
                    int y1 = getHeight() - 2* b.getRadius() - (b.getSpeed().y - (getHeight() - b.getPosition().y)) ;
                    b.getPosition().x = x1;
                    b.getPosition().y = y1;
                    b.getSpeed().y = -b.getSpeed().y;
                } else {
                    int x1 = b.getPosition().x + b.getSpeed().x;
                    int y1 = b.getPosition().y + b.getSpeed().y;
                    b.setPosition(new Point(x1, y1));

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

        new Interaction(gameScreen);
        
        gameScreen.setSize(1280, 800);
        gameScreen.screen.setBackground(Color.black);
        gameScreen.setVisible(true);

//        Network network = new Network(gameScreen);
    }
}