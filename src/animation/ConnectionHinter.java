package animation;

import network.NettyBooFinder;
import network.FinderListener;
import network.NettyBoo;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.AbstractMap;

public class ConnectionHinter {
	private GameScreen gameScreen;
	private NettyBooFinder nettyBooFinder;

	private Map<NettyBoo, ConnectionHint> connectionHints = new HashMap<NettyBoo, ConnectionHint>();
	private Map<ConnectionHint, NettyBoo> nettyBoos = new HashMap<ConnectionHint, NettyBoo>();


	private Map<ConnectionHint, Integer> leftYMap = new HashMap<ConnectionHint, Integer>();
	private Map<ConnectionHint, Integer> rightYMap = new HashMap<ConnectionHint, Integer>();
	private Map<ConnectionHint, Integer> leftSpeedMap = new HashMap<ConnectionHint, Integer>();
	private Map<ConnectionHint, Integer> rightSpeedMap = new HashMap<ConnectionHint, Integer>();


	private ConnectionHint closeConnectionHint = new ConnectionHint("Disconnect", Color.BLUE);

	private boolean showHints = true;
	private Random r = new Random();
	private Map.Entry<ConnectionHint, Boolean> mousedOverHint = null;
	private static final Color CONNECTION_COLOR = new Color(150, 90, 0);

	public ConnectionHinter(final GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		this.nettyBooFinder = gameScreen.getNetwork().getNettyBooFinder();

		this.nettyBooFinder.addFinderListener(new FinderListener() {
			public void NettyBooFound(NettyBoo nettyBoo) {
				final ConnectionHint hint = new ConnectionHint(nettyBoo.getHostName(), CONNECTION_COLOR);
				connectionHints.put(nettyBoo, hint);
				nettyBoos.put(hint, nettyBoo);
				leftYMap.put(hint, r.nextInt(Math.max(1, gameScreen.getHeight())));
				rightYMap.put(hint, r.nextInt(Math.max(1, gameScreen.getHeight())));
				leftSpeedMap.put(hint, (1 + r.nextInt(2)) * (r.nextBoolean() ? -1 : 1));
				rightSpeedMap.put(hint, (1 + r.nextInt(2)) * (r.nextBoolean() ? -1 : 1));
			}
			public void NettyBooLost(NettyBoo nettyBoo) {
				ConnectionHint hint = connectionHints.remove(nettyBoo);
				nettyBoos.remove(hint);

				leftYMap.remove(hint);
				rightYMap.remove(hint);
				leftSpeedMap.remove(hint);
				rightSpeedMap.remove(hint);
			}
			public void NettyBooUpdated(NettyBoo nettyBoo) {
				final ConnectionHint hint = connectionHints.get(nettyBoo);

				if (hint == null)
					NettyBooFound(nettyBoo);
				else
					hint.setText(nettyBoo.getHostName());
			}
		});
	}

	public void setShowHints(boolean showHints) {
		this.showHints = showHints;
	}


	public void mouseClicked(Point point) {
		if (mousedOverHint != null) {
			if (mousedOverHint.getKey() == closeConnectionHint) {
				gameScreen.getNetwork().disconnect(mousedOverHint.getValue());
			} else {
				final NettyBoo nettyBoo = nettyBoos.get(mousedOverHint.getKey());
				gameScreen.getNetwork().connectToServer(mousedOverHint.getValue(), nettyBoo.getAddress().getHostAddress());
			}
		}
	}

	public void hint(Graphics2D g, Point currentMouseLocation) {
		if (!showHints)
			return;

		closeConnectionHint.updateShapeIfNeeded(g);
		for (ConnectionHint hint : connectionHints.values()) {
			hint.updateShapeIfNeeded(g);
		}

		findMouseOverConnectionHint(currentMouseLocation);

		drawConnectionHints(g, true);
		drawConnectionHints(g, false);
	}
	public boolean isMouseOverHint(){
		return mousedOverHint != null;
	}
	private void findMouseOverConnectionHint(Point currentMouseLocation) {
		mousedOverHint = findConnectionHintUnderMouse(currentMouseLocation, true);
		if (mousedOverHint == null) {
			mousedOverHint = findConnectionHintUnderMouse(currentMouseLocation, false);
		}
	}
	private void drawConnectionHints(Graphics2D g, boolean isLeft) {
		Map<ConnectionHint, Integer> yMap = isLeft ? leftYMap : rightYMap;
		Map<ConnectionHint, Integer> speedMap = isLeft ? leftSpeedMap : rightSpeedMap;

		boolean isConnected = isLeft ? gameScreen.getNetwork().isLeftConnected() : gameScreen.getNetwork().isRightConnected();

		final int x = isLeft ? 0 : gameScreen.getWidth();
		final int maxY = gameScreen.getHeight() - 50 - 2 * ConnectionHint.BUBBLE_SIZE;
		if (isConnected) {
			g.translate(x, maxY);
			paintConnectionHint(closeConnectionHint, g, isLeft);
			g.translate(-x, -maxY);
		} else {
			for (ConnectionHint connectionHint : connectionHints.values()) {
				final int y = yMap.get(connectionHint);
				g.translate(x, y);
				paintConnectionHint(connectionHint, g, isLeft);
				g.translate(-x, -y);
				final Integer speed = speedMap.get(connectionHint);
				int newY = y + speed;
				if (newY < ConnectionHint.BUBBLE_SIZE) {
					newY = ConnectionHint.BUBBLE_SIZE;
					speedMap.put(connectionHint, Math.abs(speed));
				} else if (newY >= maxY) {
					newY = maxY;
					speedMap.put(connectionHint, Math.abs(speed) * -1);
				}

				yMap.put(connectionHint, newY);
			}
		}
	}
	private void paintConnectionHint(ConnectionHint connectionHint, Graphics2D g, boolean isLeft) {
		boolean isMousedOverHint;
		isMousedOverHint = mousedOverHint != null && mousedOverHint.getKey() == connectionHint && mousedOverHint.getValue() == isLeft;

		connectionHint.paint(g, isLeft, isMousedOverHint);
	}
	private Map.Entry<ConnectionHint, Boolean> findConnectionHintUnderMouse(Point p, boolean isLeft) {
		Map<ConnectionHint, Integer> yMap = isLeft ? leftYMap : rightYMap;
		boolean isConnected = isLeft ? gameScreen.getNetwork().isLeftConnected() : gameScreen.getNetwork().isRightConnected();

		final int x = isLeft ? 0 : gameScreen.getWidth();
		final int maxY = gameScreen.getHeight() - 50 - 2 * ConnectionHint.BUBBLE_SIZE;
		Point translatedP = new Point(p);

		if (isConnected) {
			translatedP.translate(-x, -maxY);
			if (closeConnectionHint.getShape(isLeft).contains(translatedP))
				return new AbstractMap.SimpleEntry<ConnectionHint, Boolean>(closeConnectionHint, isLeft);
		} else {
			for (ConnectionHint connectionHint : connectionHints.values()) {
				translatedP = new Point(p);
				translatedP.translate(-x, -yMap.get(connectionHint));
				if (connectionHint.getShape(isLeft).contains(translatedP))
					return new AbstractMap.SimpleEntry<ConnectionHint, Boolean>(connectionHint, isLeft);
			}
		}
		return null;
	}
}
