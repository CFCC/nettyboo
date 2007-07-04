package interaction;

import animation.Ball;
import animation.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class Creation {
    GameScreen gamescreen;

    public Creation(GameScreen screen) {
        this.gamescreen = screen;

        gamescreen.addMouseListener(new MouseAdapter() {
            private Point downPoint;
            private Point upPoint;

            public void mouseClicked(MouseEvent e) {
                if (e.getButton()==e.BUTTON1 && e.getClickCount()==2) {

                    for (Ball ball : gamescreen.getBalls()) {
                        double x = ball.getPosition().getX() - e.getPoint().getX();
                        double y = ball.getPosition().getY() - e.getPoint().getY();
                        double distance = Math.sqrt(x * x + y * y);
                        System.out.println(distance);
                        if (distance <= ball.getRadius()) {
                            ball.setText(JOptionPane.showInputDialog("Enter new text for ball:"));

                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                for(Ball ball:gamescreen.getBalls()){
                    double x = ball.getPosition().getX() - e.getPoint().getX();
                    double y = ball.getPosition().getY() - e.getPoint().getY();
                    double distance = Math.sqrt(x * x + y * y);
                    System.out.println(distance);
                    if(distance<=ball.getRadius()){
                       ball.setSpeed(new Point(0,0));
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    downPoint = e.getPoint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    upPoint = e.getPoint();
                    int xMoved = upPoint.x - downPoint.x;
                    int yMoved = upPoint.y - downPoint.y;
                    gamescreen.addBall(new Ball(Color.yellow, new Point(xMoved, yMoved), downPoint, 100));
                }
            }
        });
    }
}
