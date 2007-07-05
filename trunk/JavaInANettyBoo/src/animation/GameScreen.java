package animation;

import interaction.Interaction;
import network.Network;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends JFrame {
    private List<ScreenObject> screenObjects = new ArrayList<ScreenObject>();
    private JButton leftComputerLinkButton;
    private JButton rightComputerLinkButton;
    private JPanel mainPanel;
    private JPanel gameScreenHolder;
    private Network network;

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
                Point position = b.getPosition();
                int radius = b.getRadius();
                Point speed = b.getSpeed();

                g.setColor(b.getColor());
                g.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);

                int x1;
                int y1;

                // Get new X
                if (position.x + speed.x - radius < 0) {
                    x1 = -(position.x + speed.x) + (2 * radius);
                    if (network.isLeftConnected()) {
                        network.sendToLeftScreen(b);
                    } else {
                        speed.x = -speed.x;
                    }
                } else {
                    int screenWidth = getWidth();
                    if (position.x + speed.x + radius > screenWidth) {
                        x1 = screenWidth - 2 * radius - (speed.x - (screenWidth - position.x));
                        speed.x = -speed.x;
                    } else {
                        x1 = position.x + speed.x;
                    }
                }

                // Get New Y
                if (position.y + speed.y - radius < 0) {
                    y1 = -(position.y + speed.y) + (2 * radius);
                    speed.y = -speed.y;
                } else {
                    int screenHeight = getHeight();
                    if (position.y + speed.y + radius > screenHeight) {
                        y1 = screenHeight - 2 * radius - (speed.y - (screenHeight - position.y));
                        speed.y = -speed.y;
                    } else {
                        y1 = position.y + speed.y;
                    }
                }

                // Update position with new values
                position.x = x1;
                // position is where the ball is
                // position.y is where the ball is on the y plane
                // this line means "change position.y to y1"
                // change where the ball is on the y plane to y1
                // move the ball to whatever number y1 is
                position.y = y1;
            }
        }
    };

    public GameScreen() {
        this.network = new Network(this);
        gameScreenHolder.add(screen);
        add(mainPanel);
        new Timer(1000 / 60, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        }).start();
        leftComputerLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                network.connectToServer("left", JOptionPane.showInternalInputDialog(getContentPane(), "IP address for left computer?"));
            }
        });
        rightComputerLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                network.connectToServer("right", JOptionPane.showInternalInputDialog(getContentPane(), "IP address for right computer?"));
            }
        });
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
    }
}