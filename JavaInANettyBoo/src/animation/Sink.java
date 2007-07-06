package animation;

import java.awt.Point;
import java.awt.Color;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Cosmotic
 * Date: Jul 6, 2007
 * Time: 11:55:12 AM
 */
public class Sink extends Ball{
    public Sink(Color color, Point speed, Point position) {
        super(color, speed, position,50);

    }

    public int getRadius() {
        return 50;
    }

    public void prepare(List<Ball> balls) {
        for(Ball b: balls){
            if (position.distance(b.getPosition()) < 200){
                b.speed.x = 0;
                b.speed.y = 0;
            }
        }
    }
}
