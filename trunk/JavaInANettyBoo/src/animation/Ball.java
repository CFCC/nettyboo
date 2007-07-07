package animation;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

public class Ball extends ScreenObject {
    private Color color;
    private double radius;

    private String text;
    private boolean created = false;
    private boolean dead = false;

    public Ball(Color color, Point speed, Point position, int radius) {
        super(speed, position);
        this.color = color;
        this.radius = radius;
        this.text = null;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public double getRadius() {
        return radius;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setRadius(double radius) {
        this.radius = radius;
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
}
