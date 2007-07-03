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
                for (Ball ball : gamescreen.getBalls()) {

                    double x = ball.getPosition().getX() - e.getPoint().getX();
                    double y = ball.getPosition().getX() - e.getPoint().getY();
                    double distance = Math.sqrt(x * x + y * y);
                    if(distance<=ball.getRadius()){
                       ball.setText(JOptionPane.showInputDialog("Enter new text for ball:"));
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    downPoint = e.getPoint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    upPoint = e.getPoint();
                    int xMoved = upPoint.x - downPoint.x;
                    int yMoved = upPoint.y - downPoint.y;
                    gamescreen.addBall(new Ball(Color.blue, new Point(xMoved, yMoved),downPoint,10));
                }
            }
        });
    }
}
