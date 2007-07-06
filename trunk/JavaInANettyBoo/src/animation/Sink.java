package animation;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Cosmotic
 * Date: Jul 6, 2007
 * Time: 11:55:12 AM
 */
public class Sink extends Ball{
    private static final double T = 600.0;

    public Sink(Color color, Point speed, Point position) {
        super(color, speed, position,50);

    }

    public int getRadius() {
        return 50;
    }

    public void prepare(List<Ball> balls) {

        for(Ball b: balls){
            if (b == this || b instanceof Sink || b.isDead()) continue;
            Point p = b.getPosition();
            double d = position.distance(p);
            /*if (d < T / 2){
                b.speed.x = (int) ((position.x - p.x)/20);/// (T - (d/T) * 20.0));
                b.speed.y = (int) ((position.y - p.y)/20);// / (T - (d/T) * 20.0 ));
            } else*/ if (d < T) {
                b.speed.x += (int) ((position.x - p.x)/100);/// (T - (d/T) * 20.0));
                b.speed.y += (int) ((position.y - p.y)/100);// / (T - (d/T) * 20.0 ));
            }
        }
    }
}
