package animation;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

public class Sink extends Ball {
    public Sink(Color color, Point speed, Point position, int radius) {
        super(color, speed, position, radius);
    }

    public void prepare(List<Ball> balls) {
        if (!wasCreated()) return;
        for (Ball b : balls) {
            if (b == this || b instanceof Sink || !b.isAlive()) continue;
            Point p = b.getPosition();
            double distance = position.distance(p);
            double ballRadius = b.getRadius();
            double myRadius = getRadius();
            if (distance - ballRadius < Math.max(50, myRadius)){
                b.speed.x = (position.x - p.x)/20;/// (T - (distance/T) * 20.0));
                b.speed.y = (position.y - p.y)/20;// / (T - (distance/T) * 20.0 ));
                if (b.speed.x == 0 && b.speed.y == 0) {
                    if (ballRadius <= 0) {
                        getGameScreen().removeBall(b);
                    } else {
                        double ballArea = area(ballRadius);
                        double newBallRadius = ballRadius - 1;
                        double newBallArea = area(newBallRadius);
                        double diffArea = ballArea - newBallArea;
                        double myArea = area(myRadius);
                        double newArea = myArea - diffArea;
                        b.setRadius(newBallRadius);
                        double myNewRadius = Math.sqrt(newArea / Math.PI);
                        setRadius(Math.min(myRadius-1, myNewRadius));
                        if (myNewRadius < 0) {
                            getGameScreen().removeBall(this);
                        }
                    }
                }
            }
        }
    }

    private double area(double radius) {
        return Math.PI * radius * radius;
    }
}
