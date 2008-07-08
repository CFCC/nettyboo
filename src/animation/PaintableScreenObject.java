package animation;

import java.awt.Point;
import java.awt.Graphics2D;

/**
 * Created by IntelliJ IDEA.
 * User: cosmotic
 * Date: Jul 1, 2008
 * Time: 4:16:11 PM
 */
public abstract class PaintableScreenObject extends ScreenObject {
	public PaintableScreenObject(Point speed, Point position) {super(speed, position);}
	protected void paint(Graphics2D g){
		g.translate(getPosition().x, getPosition().y);
	}
}
