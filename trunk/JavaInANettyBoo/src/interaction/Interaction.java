package interaction;

import animation.Ball;
import animation.GameScreen;
import animation.Sounder;
import animation.Sink;
import animation.ScreenObject;
import animation.GravityWell;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class Interaction {
    private Point downPoint;
    private List<Ball> list = new ArrayList<Ball>();
    private ScreenObject newBall = null;

    private GameScreen gamescreen;
    private Point currentMouseLocation = new Point(0, 0);

    public Interaction(GameScreen screen) {
        this.gamescreen = screen;

        gamescreen.screen.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                currentMouseLocation = e.getPoint();
            }

            public void mouseMoved(MouseEvent e) {
                currentMouseLocation = e.getPoint();
            }
        });
        gamescreen.screen.addMouseListener(new MouseAdapter() {
            private long timePress;
            private Timer timer;

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {

                    for (Ball ball : gamescreen.getBalls()) {
                        if (ball.getPosition().distance(e.getPoint()) <= ball.getRadius()) {
                            if (ball.getText() == null) {
                                ball.setText(JOptionPane.showInternalInputDialog(
                                        gamescreen.getContentPane(), "Enter new text for ball:"));
                            } else {
                                if (ball.getText().equalsIgnoreCase("Poopenheimer")
                                        || ball.getText().equalsIgnoreCase("David")) {
                                    JOptionPane.showInternalMessageDialog(gamescreen.getContentPane(),
                                            "LOL ^___________________^");
                                    break;
                                }
                                JOptionPane.showInternalMessageDialog(
                                        gamescreen.getContentPane(), "The message for this ball is:\n"
                                        + ball.getText());
                            }
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                timePress = System.currentTimeMillis();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    list.clear();
                    for (Ball ball : gamescreen.getBalls()) {
                        if (ball.getPosition().distance(e.getPoint()) <= ball.getRadius() && ball.isDead()==false) {
                            ball.setSpeed(new Point(0, 0));
                            list.add(ball);
                        }
                    }
                }
                downPoint = e.getPoint();
                if (isRightButton(e)) {
                    Point speed = new Point(0, 0);
                    GameScreen.ClickMode clickMode = gamescreen.getClickMode();
                    switch (clickMode) {
                        case BALL:
                            newBall = new Ball(Color.YELLOW, speed, downPoint, 1);
                            break;
                        case SOUND:
                            newBall = new Sounder(Color.RED, speed, downPoint, 1);
                            break;
                        case SINK:
                            newBall = new Sink(Color.BLUE, speed, downPoint);
                            break;
                        case GRAVITY_WELL:
                            newBall = new GravityWell(Color.GREEN,speed,downPoint);
                            break;
                        
                    }
                    gamescreen.addBall(newBall);
                    timer = new Timer(1000 / 30, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (newBall instanceof Ball)
                                ((Ball)newBall).setRadius((int) (.1 * (System.currentTimeMillis() - timePress)));
                        }
                    });
                    timer.start();
                }
            }

            public void mouseReleased(MouseEvent e) {
                long timeRelease = System.currentTimeMillis();
                long elapsedTime = timeRelease - timePress;
                Point upPoint = e.getPoint();
                int xMoved = upPoint.x - downPoint.x;
                int yMoved = upPoint.y - downPoint.y;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    for (Ball ball : list) {
                        ball.setSpeed(new Point(xMoved, yMoved));
                    }
                    list.clear();
                }
                if (newBall != null && isRightButton(e)) {
                    if (newBall instanceof Ball)
                        ((Ball) newBall).setRadius((int) (.1 * elapsedTime));
                    newBall.setSpeed(new Point(xMoved, yMoved));
                    if (timer != null) {
                        timer.stop();
                        timer = null;
                    }
                    newBall = null;
                }
            }
        });
    }

    private boolean isRightButton(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON3 || (e.getModifiers() & KeyEvent.CTRL_MASK) != 0;
    }

    public Point getCurrentMouseLocation() {
        return currentMouseLocation;
    }

    public void drawSlingshots(Graphics2D g) {
        if (newBall != null || !list.isEmpty()) {
            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(downPoint.x, downPoint.y, currentMouseLocation.x, currentMouseLocation.y);
        }
    }
}
