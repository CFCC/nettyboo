package animation;

import java.awt.*;

public class Ball {
    private  Point position;
    private  Point speed;
    private Color color;
    private  int radius;

    private String text;

    public Ball(Color color, Point speed, Point position, int radius) {
        this.color = color;
        this.speed = speed;
        this.position = position;
        this.radius = radius;
        this.text= "";
    }

    public Point getSpeed() {
        return speed;
    }

    public void setSpeed(Point speed) {
        this.speed = speed;
    }

    public int getRadius() {
        return radius;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Point getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
