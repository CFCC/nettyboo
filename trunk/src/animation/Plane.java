package animation;

import animation.Ball;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Zak
 * Date: Jul 1, 2008
 * Time: 11:11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class Plane extends Ball {
	private static BufferedImage planeImage = null;

	static {
		try {
			InputStream inputStream;
			ImageInputStream imageInputStream;
			ImageReader imageReader;

			inputStream = Plane.class.getResourceAsStream("/animation/f15.png");
			imageInputStream = ImageIO.createImageInputStream(inputStream);
			imageReader = ImageIO.getImageReaders(imageInputStream).next();
			imageReader.setInput(imageInputStream, false);
			planeImage = imageReader.read(0);
		} catch (IOException e) {
			planeImage = null;
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public Plane(Point speed, Point downPoint) {
		super(speed, downPoint, 200);
	}

	public void paintBall(Graphics2D g) {
		g.drawImage(planeImage, null, position.x - planeImage.getWidth() / 2, position.y - planeImage.getHeight() / 2);
	}
	public boolean doesBounce() {
		return false;
	}
}
