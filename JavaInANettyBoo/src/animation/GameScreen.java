package animation;

import javax.swing.*;
import java.util.List;
import java.awt.*;

public class GameScreen extends JFrame {
    
    public int width;
    //the width of the game window
    public int height;
    //the height of the game window
    public Color background;
    //controls the color of the background of the GameScreen
    public List <Ball> balls;

    public JPanel screen = new JPanel() {
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);

            Graphics2D g = (Graphics2D) gg;

            // draw the screen
            g.setColor(Color.green);
            g.setStroke(new BasicStroke(30));
            g.drawOval(400,500,250,200);
            g.setColor(Color.blue);
            g.drawOval(350,200,200,250);
            g.setColor(Color.blue);
            g.fillOval(500,400,250,200);
            g.fillOval(500,400,200,250);
        }
    };

    public GameScreen() {
        add(screen);
    }

    //the list of balls on screen, each ball is assigned a specific number
    public void addBall(){}
    public static void main(String[] args) {
        GameScreen gameScreen = new GameScreen();
        gameScreen.setSize(1280,800);
        gameScreen.setVisible(true);
        gameScreen.screen.setBackground(Color.black);
    }

}

