package animation;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.util.List;

public class GenericBall extends Ball {
	private Color color;

	public GenericBall(Color color, Point speed, Point position, int radius) {
		super(speed, position, radius);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void paintBall(Graphics2D g) {
		double radius = getRadius();

		g.setColor(getColor());
		g.fillOval((int) (position.x - radius), (int) (position.y - radius),
				   (int) radius * 2, (int) radius * 2);
	}
	public boolean doesBounce() {
		return true;
	}
}
