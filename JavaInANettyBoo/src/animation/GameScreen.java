package animation;

import interaction.Interaction;
import network.Network;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameScreen extends JFrame {
    private List<ScreenObject> screenObjects = new CopyOnWriteArrayList<ScreenObject>();
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
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (Ball b : getBalls()) {
                Point position = b.getPosition();
                int radius = b.getRadius();
                Point speed = b.getSpeed();

                g.setColor(b.getColor());
                g.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);

                int x1;
                int y1;

                // Get new X
                if (position.x + speed.x - radius < 0 && speed.x < 0) {
                    x1 = -(position.x + speed.x) + (2 * radius);
                    if (network.isLeftConnected()) {
                        network.sendToLeftScreen(b);
//                        screenObjects.remove(b);
                    } else {
                        speed.x = -speed.x;
                    }
                } else {
                    int screenWidth = getWidth();
                    if (position.x + speed.x + radius > screenWidth && speed.x > 0) {
                        x1 = screenWidth - 2 * radius - (speed.x - (screenWidth - position.x));
                        if (network.isRightConnected()) {
                            network.sendToRightScreen(b);
                            screenObjects.remove(b);
                        } else {
                            speed.x = -speed.x;
                        }

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
                DefaultListModel data = new DefaultListModel();
                JList ipSelector = new JList(data);
                ipSelector.setSize(300, 300);
                ipSelector.setMinimumSize(new Dimension(300, 300));
                ipSelector.setPreferredSize(new Dimension(300, 300));
                network.nettyBooFinder.findMoreNettyBoos(data);
                JOptionPane.showInternalMessageDialog(getContentPane(),
                        ipSelector);
                network.connectToServer("left", (String) ipSelector.getSelectedValue());
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
        gameScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new Interaction(gameScreen);

        gameScreen.setSize(1280, 800);
        gameScreen.screen.setBackground(Color.black);
        gameScreen.setVisible(true);
    }

    public void addBallFromLeft(Ball recievedBall) {
        recievedBall.getPosition().x = 0;
        addBall(recievedBall);

    }

    public void addBallFromRight(Ball recievedBall) {

        recievedBall.getPosition().x = getWidth();
        addBall(recievedBall);
    }
}