package animation;

import java.io.Serializable;
import java.awt.*;

/**
 * No.
 */
public class ScreenObject implements Serializable {
    protected Point position;
    protected Point speed;
    private transient GameScreen gameScreen;

    public ScreenObject(Point speed, Point position) {
        this.speed = speed;
        this.position = position;
    }

    public Point getSpeed() {
        return speed;
    }

    public void setSpeed(Point speed) {
        this.speed = speed;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
