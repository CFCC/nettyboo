package animation;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: cosmotic
 * Date: Jul 1, 2008
 * Time: 3:38:13 PM
 */
public class ConnectionHint {

	private String text;

	public static final int BUBBLE_SIZE = 20;
	private Color color;

	private Shape leftShape = null;
	private Shape rightShape = null;
	private static final Font FONT = new Font("Helvetica", Font.BOLD, (int) (BUBBLE_SIZE * 1.5));


	public ConnectionHint(String text, Color color) {
		this.text = text;
		this.color = color;
	}
	public void updateShapeIfNeeded(Graphics2D g) {
		if (leftShape == null) {
			GeneralPath path = new GeneralPath();

			g.setFont(FONT);
			double stringWidth = g.getFontMetrics().getStringBounds(text, g).getWidth();

			path.moveTo(0, 0);
			path.lineTo(BUBBLE_SIZE, -BUBBLE_SIZE);
			path.lineTo(BUBBLE_SIZE * .8 + stringWidth, -BUBBLE_SIZE);
			path.curveTo(BUBBLE_SIZE * .8 + stringWidth + BUBBLE_SIZE * 1.25, -BUBBLE_SIZE, BUBBLE_SIZE * .8 + stringWidth + BUBBLE_SIZE * 1.25, BUBBLE_SIZE, BUBBLE_SIZE * .8 + stringWidth, BUBBLE_SIZE);
			path.lineTo(BUBBLE_SIZE, BUBBLE_SIZE);
			path.closePath();
			leftShape = path;
		}
		if (rightShape == null) {
			GeneralPath path = new GeneralPath();

			g.setFont(FONT);
			double stringWidth = g.getFontMetrics().getStringBounds(text, g).getWidth();

			path.moveTo(0, 0);
			path.lineTo(-BUBBLE_SIZE, -BUBBLE_SIZE);
			path.lineTo(-BUBBLE_SIZE * .8 - stringWidth, -BUBBLE_SIZE);
			path.curveTo(-BUBBLE_SIZE * .8 - stringWidth - BUBBLE_SIZE * 1.25, -BUBBLE_SIZE, -BUBBLE_SIZE * .8 - stringWidth - BUBBLE_SIZE * 1.25, BUBBLE_SIZE, -BUBBLE_SIZE * .8 - stringWidth, BUBBLE_SIZE);
			path.lineTo(-BUBBLE_SIZE, BUBBLE_SIZE);
			path.closePath();
			rightShape = path;
		}
	}


	protected void paint(Graphics2D g, boolean isLeft, boolean mousedOverHint) {
		final Shape shape = isLeft ? leftShape : rightShape;

		if (shape == null)
			return;
		
		Composite oldComposite;
		oldComposite = g.getComposite();

		if (!mousedOverHint) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		}

		g.setColor(this.color);
		g.fill(shape);

//		if (mousedOverHint) {
//			g.setStroke(GameScreen.HALO_STROKE);
//			g.setColor(GameScreen.HALO_COLOR);
//			g.draw(shape);
//		}


//		if ((color.getRed() + color.getGreen() + color.getBlue()) / 3 < 255 / 2)
//			g.setColor(Color.white);
//		else

		g.setComposite(oldComposite);

		g.setColor(Color.black);

		double stringWidth = g.getFontMetrics().getStringBounds(text, g).getWidth();

		g.setFont(FONT);
		g.drawString(text, (isLeft ? BUBBLE_SIZE : (-BUBBLE_SIZE - (int) stringWidth)), BUBBLE_SIZE / 2);

	}
	public void setText(String text) {
		this.text = text;
		leftShape = null;
		rightShape = null;
	}
	public Shape getShape(boolean isLeft) {

		return isLeft ? leftShape : rightShape;
	}
}
