package animation;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.font.TextLayout;
import java.util.List;

public class N00bPwner {
    private static final String START_PLAYING_STRING = "Right-click to play";

    private final GameScreen gameScreen;
    private static final Color DARK_GRAY = new Color(30, 30, 30);

    public N00bPwner(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void pwn(Graphics2D g) {
        List<ScreenObject> objects = gameScreen.getScreenObjects();
        if (objects.isEmpty()) {
			Font oldFont = g.getFont();
			Font font = findLargestFont(g, START_PLAYING_STRING, (int) (gameScreen.screen.getWidth() * .9));
            g.setColor(DARK_GRAY);
            g.setFont(font);
            g.drawString(START_PLAYING_STRING,
                    (float) (gameScreen.screen.getWidth() * .05), (float) (gameScreen.screen.getHeight()*.75));
			g.setFont(oldFont);
		} else {
            drawClickAndDragHints(g);
        }
    }

    private void drawClickAndDragHints(Graphics2D g) {
        List<Ball> balls = gameScreen.getBalls();
        if (!areSomeObjectsMoving(balls)) {
            for (Ball object : balls) {
                if (object.getSpeed().distance(0, 0) == 0) {
                    g.setColor(Color.GRAY);
                    g.setFont(new Font("blah", Font.BOLD, 20));
                    Point pos = object.getPosition();
                    double radius = object.getRadius() - 5;
                    int tx = (int) (pos.x + radius);
                    int ty = (int) (pos.y + radius);
                    g.drawString("Click and drag to throw", tx, ty);
                    g.setStroke(new BasicStroke(1));
                    g.drawLine(tx-8, ty-g.getFont().getSize()/2, pos.x, pos.y);
                }
            }
        }
    }

    private boolean areSomeObjectsMoving(List<? extends ScreenObject> objects) {
        boolean someMoving = false;
        for (ScreenObject object : objects) {
            if (object.getSpeed().distance(0, 0) != 0) {
                someMoving = true;
                break;
            }
        }
        return someMoving;
    }

    private Font findLargestFont(Graphics2D g, String string, int maxWidth) {
        Font lastFont;
        Font font = null;
        for (int size = 1; size < 200; size++) {
            lastFont = font;
            font = new Font("blah", Font.BOLD, size);
            TextLayout layout = new TextLayout(string, font, g.getFontRenderContext());
            if (layout.getBounds().getWidth() > maxWidth) {
                // the previous font was cool
                font = lastFont;
                break;
            }
        }
        return font;
    }
}
