package interaction;

import animation.Ball;
import animation.GameScreen;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

public class Interaction {
    GameScreen gamescreen;

    public Interaction(GameScreen screen) {
        this.gamescreen = screen;

        gamescreen.screen.addMouseListener(new MouseAdapter() {
            private Point downPoint;
            private Point upPoint;
            private List<Ball> list = new ArrayList<Ball>();
            private long timePress;
            private long timeRelease;

            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) {

                    for (final Ball ball : gamescreen.getBalls()) {
                        double x = ball.getPosition().getX() - e.getPoint().getX();
                        double y = ball.getPosition().getY() - e.getPoint().getY();
                        double distance = Math.sqrt(x * x + y * y);
                        System.out.println(distance);
                        if (distance <= ball.getRadius()&&ball.getText()==null) {
                            ball.setText(JOptionPane.showInternalInputDialog(
                                    gamescreen.getContentPane(), "Enter new text for ball:"));
                        }
                        if(ball.getText()!=null&&distance <= ball.getRadius()){
                            System.out.println(ball.getText());
                            JOptionPane.showInternalMessageDialog(
                                    gamescreen.getContentPane(), "The message for this ball is:       "+ball.getText());
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                timePress = System.currentTimeMillis();
                if (e.getButton() == e.BUTTON1) {
                    list.clear();
                    for (Ball ball : gamescreen.getBalls()) {
                        double x = ball.getPosition().getX() - e.getPoint().getX();
                        double y = ball.getPosition().getY() - e.getPoint().getY();
                        double distance = Math.sqrt(x * x + y * y);
                        System.out.println(distance);
                        if (distance <= ball.getRadius()) {
                            ball.setSpeed(new Point(0, 0));
                            list.add(ball);
                        }
                    }
                }
                downPoint = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                timeRelease = System.currentTimeMillis();
                long elapsedTime = timeRelease - timePress;
                upPoint = e.getPoint();
                int xMoved = upPoint.x - downPoint.x;
                int yMoved = upPoint.y - downPoint.y;
                if (e.getButton() == e.BUTTON1) {
                    for (Ball ball : list) {
                        ball.setSpeed(new Point(xMoved, yMoved));
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int alg = (int) (.1 * elapsedTime);
                    System.out.println(alg);
                    gamescreen.addBall(new Ball(Color.yellow, new Point(xMoved, yMoved), downPoint, alg));
                }
            }
        });
    }
}
