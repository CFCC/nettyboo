package animation;

import interaction.Creation;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class GameScreen extends JFrame {
    
    private int width;
    private int height;
    private Color background;
    private List <Ball> balls =new ArrayList<Ball>();

    public List<Ball> getBalls() {
        return balls;
    }

    public JPanel screen;

    {
        screen = new JPanel() {
            protected void paintComponent(Graphics gg) {
                super.paintComponent(gg);

                Graphics2D g = (Graphics2D) gg;

            }
        };
    }

    public GameScreen() {
        add(screen);
    }

    //the list of balls on screen, each ball is assigned a specific number
    public void addBall(Ball ball){}
    public static void main(String[] args) {
        GameScreen gameScreen = new GameScreen();
        gameScreen.setSize(1280,800);
        gameScreen.setVisible(true);
        gameScreen.screen.setBackground(Color.black);

        new Creation(gameScreen);
    }

}

