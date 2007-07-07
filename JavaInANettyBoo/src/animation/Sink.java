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
            if (distance + b.getRadius() < Math.max(50, getRadius())){
                b.speed.x = (position.x - p.x)/20;/// (T - (distance/T) * 20.0));
                b.speed.y = (position.y - p.y)/20;// / (T - (distance/T) * 20.0 ));
                if (b.speed.x == 0 && b.speed.y == 0) {
                    int newRadius;
                    if (b.getRadius() <= 0) {
                        getGameScreen().removeBall(b);
                        newRadius = getRadius() - (3+b.getRadius()/10);
                    } else {
                        b.setRadius(b.getRadius()-1);
                        newRadius = getRadius() - 1;
                    }
                    setRadius(newRadius);
                    if (newRadius < 0) {
                        getGameScreen().removeBall(this);
                    }
                }
            }
        }
    }
}
