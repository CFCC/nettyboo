package interaction;

import animation.Ball;
import animation.GameScreen;
import animation.Sounder;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Interaction {
    GameScreen gamescreen;
    private Point currentMouseLocation = new Point(0, 0);

    public Interaction(GameScreen screen) {
        this.gamescreen = screen;

        gamescreen.screen.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                currentMouseLocation = e.getPoint();
            }
        });
        gamescreen.screen.addMouseListener(new MouseAdapter() {
            private Point downPoint;
            private Point upPoint;
            private List<Ball> list = new ArrayList<Ball>();
            private long timePress;
            private long timeRelease;
            public Ball newBall = null;
            private Timer timer;

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {

                    for (Ball ball : gamescreen.getBalls()) {
                        if (ball.getPosition().distance(e.getPoint()) <= ball.getRadius()) {
                            if (ball.getText() == null) {
                                ball.setText(JOptionPane.showInternalInputDialog(
                                        gamescreen.getContentPane(), "Enter new text for ball:"));
                            } else {
                                if(ball.getText().equalsIgnoreCase("Poopenheimer")
                                        ||ball.getText().equalsIgnoreCase("David")){
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
                        if (ball.getPosition().distance(e.getPoint()) <= ball.getRadius()) {
                            ball.setSpeed(new Point(0, 0));
                            list.add(ball);
                        }
                    }
                }
                downPoint = e.getPoint();
                if (isRightButton(e)) {
                    Point speed = new Point(0, 0);
                    GameScreen.ClickMode clickMode = gamescreen.getClickMode();
                    if (clickMode == GameScreen.ClickMode.BALL) {
                        newBall = new Ball(Color.yellow, speed, downPoint, 1);
                    } else if (clickMode == GameScreen.ClickMode.SOUND) {
                        newBall = new Sounder(Color.red, speed, downPoint, 1);
                    }
                    gamescreen.addBall(newBall);
                    timer = new Timer(1000 / 30, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            newBall.setRadius((int) (.1 * (System.currentTimeMillis() - timePress)));
                        }
                    });
                    timer.start();
                }
            }

            public void mouseReleased(MouseEvent e) {
                timeRelease = System.currentTimeMillis();
                long elapsedTime = timeRelease - timePress;
                upPoint = e.getPoint();
                int xMoved = upPoint.x - downPoint.x;
                int yMoved = upPoint.y - downPoint.y;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    for (Ball ball : list) {
                        ball.setSpeed(new Point(xMoved, yMoved));
                    }
                }
                if (newBall != null && isRightButton(e)) {
                    newBall.setRadius((int) (.1 * elapsedTime));
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
}
