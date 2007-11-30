package animation;

import interaction.Interaction;
import network.NettyBooFinder;
import network.Network;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class dictates the animation of objects such as what happens when an object hits a
 * vertical wall when another computer is or is not present. When another computer is present,
 * it will pass the object to it, and when another computer isn't present, it will simply make
 * object bounce. When an object moves, it either glides accross the screen with or without
 * sound, depending on the object. This class works directly with other classes within the
 * JavaInANettyBoo project.
 *
 * @author Colin C. Gerwitz
 */
public class GameScreen extends JFrame {
    private List<ScreenObject> screenObjects = new CopyOnWriteArrayList<ScreenObject>();
    private JButton leftComputerLinkButton;
    private JButton rightComputerLinkButton;
    private JPanel mainPanel;
    private JPanel gameScreenHolder;
    private JToolBar toolbar;
    private JButton ballButton;
    private JButton soundButton;
    private JButton sinkButton;
    private JButton pauseButton;
    private JButton gravityWellButton;
    private Network network;
    private Interaction interaction;
    private ClickMode clickMode;

    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    private N00bPwner n00bpwner = new N00bPwner(this);

    boolean paused = false;

    private final Object renderedLock = new Object();
    private boolean rendered = false;

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
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            List<Ball> balls = getBalls();
            Collections.sort(balls, new Comparator<Ball>() {
                public int compare(Ball o1, Ball o2) {
                    return o1.getRadius() > o2.getRadius() ? -1 : o1.getRadius() < o2.getRadius() ? 1 : 0;
                }
            });
            for (Ball b : balls) {
                Point position = b.getPosition();
                double radius = b.getRadius();

                g.setColor(b.getColor());
                g.fillOval((int) (position.x - radius), (int) (position.y - radius),
                        (int) radius * 2, (int) radius * 2);
                drawHalo(g, interaction.getCurrentMouseLocation(), b);
            }

            interaction.drawSlingshots(g);
            n00bpwner.pwn(g);
            showIPIfMulticastReceived(g);
            synchronized(renderedLock) {
                rendered = true;
                renderedLock.notifyAll();
            }
        }
    };

    private void animateBalls(List<Ball> balls) {
        for (Ball b : balls) {
            double x1;
            double y1;

            // Get new X
            if (!paused) {
                b.prepare(balls);
                Point position = b.getPosition();
                double radius = b.getRadius();
                Point speed = b.getSpeed();

                if (position.x + 3 * radius < 0 || position.x > getWidth() + radius){
                    screenObjects.remove(b);
                    continue;
                }
                if (position.x + speed.x - radius < 0 && speed.x < 0 && !b.isDead()) {
                    x1 = -(position.x + speed.x) + (2 * radius);
                    if (network.isLeftConnected()) {
                        network.sendToLeftScreen(b);
                        b.setDead(true);
                    } else {
                        speed.x = -speed.x;
                    }
                } else {
                    int screenWidth = screen.getWidth();
                    if (position.x + speed.x + radius > screenWidth && speed.x > 0 && !b.isDead()) {
                        x1 = screenWidth - 2 * radius - (speed.x - (screenWidth - position.x));
                        if (network.isRightConnected()) {
                            network.sendToRightScreen(b);
                            b.setDead(true);
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
                    int screenHeight = screen.getHeight();
                    if (position.y + speed.y + radius > screenHeight) {
                        y1 = screenHeight - 2 * radius - (speed.y - (screenHeight - position.y));
                        speed.y = -speed.y;
                    } else {
                        y1 = position.y + speed.y;
                    }
                }

                // Update position with new values
                position.x = (int) x1;
                position.y = (int) y1;
            }
        }
    }


    private void showIPIfMulticastReceived(Graphics2D g) {
        if (System.currentTimeMillis() - network.nettyBooFinder.getTimeOfLastMulticastReception() < 8000) {
            InetAddress lastSender = network.nettyBooFinder.getLastMulticastSender();
            Collection<InetAddress> ips = NettyBooFinder.getLocalIPAddresses();
            if (lastSender != null) {
                String sender = lastSender.getHostAddress();
                String prefix = sender.substring(0, sender.lastIndexOf('.') + 1);
                Collection<InetAddress> ipsWithPrefix = findIpsWithPrefix(ips, prefix);
                if (!ipsWithPrefix.isEmpty()) {
                    ips = ipsWithPrefix;
                }
            }
            String str = buildIpString(ips);
            Font font = new Font("blah", Font.BOLD, 200);
            TextLayout layout = new TextLayout(str, font, g.getFontRenderContext());

            g.setFont(font);
            g.setColor(new Color(200, 100, 0));
            g.drawString(str, (float) ((screen.getWidth() - layout.getBounds().getWidth()) / 2),
                    (float) (screen.getHeight() / 2 + layout.getBounds().getHeight() / 2));
        }
    }

    private List<InetAddress> findIpsWithPrefix(Collection<InetAddress> ips, String prefix) {
        List<InetAddress> bestIps = new ArrayList<InetAddress>();
        for (InetAddress address : ips) {
            if (address.getHostAddress().startsWith(prefix)) {
                bestIps.add(address);
            }
        }
        return bestIps;
    }

    private String buildIpString(Collection<InetAddress> ips) {
        String str = "";
        for (InetAddress address : ips) {
            if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                if (!str.equals("")) {
                    str += "\n";
                }
                str += address.getHostAddress();
            }
        }
        return str;
    }

    private void updateCursor() {
        Point p = interaction.getCurrentMouseLocation();
        if (p == null) {
            this.setCursor(defaultCursor);
            return;
        }

        Cursor cursor = defaultCursor;
        for (Ball ball : getBalls()) {
            if (ball.getPosition().distance(p) < ball.getRadius()) {
                cursor = handCursor;
            }
        }
        // none of the balls are under the cursor
        this.setCursor(cursor);
    }

    private void drawHalo(Graphics2D g, Point cursorPosition, Ball ball) {
        double radius = ball.getRadius();
        Point pos = ball.getPosition();
        if (pos.distance(cursorPosition) < radius) {
            radius += 5;
            g.setStroke(new BasicStroke(10));

            g.setColor(Color.WHITE);
            g.drawOval((int) (pos.x - radius + 1), (int) (pos.y - radius + 1),
                    (int) (radius * 2 - 2), (int) (radius * 2 - 2));

//            radius += 5;
//            g.setStroke(new BasicStroke(6));
//            g.setColor(Color.WHITE);
//            g.drawOval(pos.x - radius, pos.y - radius, radius*2, radius*2);
        }
    }

    public GameScreen() {
        this.network = new Network(this);
        this.interaction = new Interaction(this);
        gameScreenHolder.add(screen);
        add(mainPanel);
        new Timer(1000 / 60, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        }).start();
//        java.util.Timer timer = new java.util.Timer(false);
//        timer.schedule(new TimerTask() {
        Thread animationThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    waitForNextRender();
                    updateCursor();
                    animateBalls(getBalls());
                }
            }
        });
        animationThread.setPriority(Thread.MIN_PRIORITY);
        animationThread.start();
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
                //network.connectToServer("left", JOptionPane.showInternalInputDialog(getContentPane(), "IP address for left computer?"));
            }
        });
        rightComputerLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel data = new DefaultListModel();
                JList ipSelector = new JList(data);
                ipSelector.setSize(300, 300);
                ipSelector.setMinimumSize(new Dimension(300, 300));
                ipSelector.setPreferredSize(new Dimension(300, 300));
                network.nettyBooFinder.findMoreNettyBoos(data);
                JOptionPane.showInternalMessageDialog(getContentPane(),
                        ipSelector);
                network.connectToServer("right", (String) ipSelector.getSelectedValue());
                //network.connectToServer("right", JOptionPane.showInternalInputDialog(getContentPane(), "IP address for right computer?"));
            }
        });
        setMode(ClickMode.BALL);
        ballButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode(ClickMode.BALL);
            }
        });
        soundButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode(ClickMode.SOUND);
            }
        });
        sinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode(ClickMode.SINK);
            }
        });
        gravityWellButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode(ClickMode.GRAVITY_WELL);
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
    }

    private void waitForNextRender() {
        synchronized(renderedLock) {
            while (!rendered) {
                try {
                    renderedLock.wait();
                } catch (InterruptedException e) {
                    break;
                }
            }
            rendered = false;
        }
    }

    private void setMode(ClickMode mode) {
        this.clickMode = mode;
        ballButton.setSelected(mode == ClickMode.BALL);
        soundButton.setSelected(mode == ClickMode.SOUND);
        sinkButton.setSelected((mode == ClickMode.SINK));
        gravityWellButton.setSelected((mode == ClickMode.GRAVITY_WELL));
    }

    //the list of balls on screen, each ball is assigned a specific number
    public void addBall(ScreenObject fart) {
        screenObjects.add(fart);
        fart.setGameScreen(this);
    }

    public static void main(String[] args) {
        GameScreen gameScreen = new GameScreen();
        gameScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameScreen.setSize(1280, 800);
        gameScreen.screen.setBackground(Color.black);
        gameScreen.setVisible(true);
    }

    public void addBallFromLeft(Ball recievedBall) {
        recievedBall.getPosition().x = (int) -recievedBall.getRadius();
        addBall(recievedBall);

    }

    public void addBallFromRight(Ball recievedBall) {
        recievedBall.getPosition().x = (int) (getWidth() + recievedBall.getRadius());
        addBall(recievedBall);
    }

    public ClickMode getClickMode() {
        return clickMode;
    }

    public void removeBall(ScreenObject object) {
        screenObjects.remove(object);
    }

    public static enum ClickMode {
        BALL, SINK, GRAVITY_WELL, SOUND
    }

}