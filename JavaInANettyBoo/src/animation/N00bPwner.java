package animation;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.font.TextLayout;

public class N00bPwner {
    private static final String START_PLAYING_STRING = "Right-click the field to start playing";

    private final GameScreen gameScreen;

    public N00bPwner(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void pwn(Graphics2D g) {
        if (gameScreen.getScreenObjects().isEmpty()) {
            Font font = findLargestFont(g, START_PLAYING_STRING, (int) (gameScreen.screen.getWidth() * .9));
            g.setColor(Color.GRAY);
            g.setFont(font);
            g.drawString(START_PLAYING_STRING, (float) (gameScreen.screen.getWidth() * .05), gameScreen.screen.getHeight()/2);
        }
    }

    private Font findLargestFont(Graphics2D g, String string, int maxWidth) {
        Font lastFont;
        Font font = null;
        for (int size = 1; size < 100; size++) {
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
