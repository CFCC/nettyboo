package animation;

import java.awt.Point;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Zak
 * Date: Jul 1, 2008
 * Time: 11:16:15 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Ball extends ScreenObject {
    protected String text;
    private boolean created = false;
    private boolean dead = false;
    protected double radius;

    public Ball(Point speed, Point position, int radius) {
        super(speed, position);
        text = null;
        this.radius = radius;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void prepare(List<Ball> balls) {

    }

    public boolean wasCreated() {
        return created;
    }

    public void setCreated() {
        this.created = true;
    }

    public boolean isAlive() {
        return wasCreated() && !isDead();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = Math.min(radius,500);
    }

    public abstract void  paintBall(Graphics2D g);

	public abstract boolean doesBounce();

}
