package animation;

import java.awt.*;

public class Ball extends ScreenObject {
    private Color color;
    private int radius;

    private String text;

    public Ball(Color color, Point speed, Point position, int radius) {
        super(speed, position);
        this.color = color;
        this.radius = radius;
        this.text = null;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
