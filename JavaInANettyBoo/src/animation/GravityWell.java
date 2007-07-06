package animation;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

public class GravityWell extends Ball{
    private static final double T = 200;
    private int radius = 50;

    public GravityWell(Color color, Point speed, Point position) {
        super(color, speed, position, 50);

    }

    public int getRadius() {
        return radius;
    }

    public void prepare(List<Ball> balls) {

        for (Ball b : balls) {
            if (b == this || b instanceof Sink || b.isDead()) continue;
            Point p = b.getPosition();
            double d = position.distance(p);
            /*if (d < T / 2){
                b.speed.x = (int) ((position.x - p.x)/20);/// (T - (d/T) * 20.0));
                b.speed.y = (int) ((position.y - p.y)/20);// / (T - (d/T) * 20.0 ));
            } else*/
            if (d < T) {
                int dx = (position.x - p.x) / 100;
                int dy = (position.y - p.y) / 100;
                b.speed.x += dx;/// (T - (d/T) * 20.0));
                b.speed.y += dy;// / (T - (d/T) * 20.0 ));
                if (dx != 0 || dy != 0) {
                    radius++;
                }
            }
        }
        radius = Math.max(Math.min(radius - 5, 300), 50);
    }
}
}
