package animation;

import interaction.Creation;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameScreen extends JFrame {
    
    private int width;
    private int height;
    private Color background;
    private List <Ball> balls =new ArrayList<Ball>();

    public List<Ball> getBalls() {
        return balls;
    }

    public JPanel screen = new JPanel() {
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            Graphics2D g = (Graphics2D) gg;
            for (Ball fart : balls) {
                g.fillOval(fart.getPosition().x,fart.getPosition().y,60,60);
            }
        }
    };
    public GameScreen() {
        new Timer(1000/30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        }).start();
        add(screen);
    }

    //the list of balls on screen, each ball is assigned a specific number
    public void addBall(Ball fart){
        balls.add(fart);
    }

    public static void main(String[] args) {
        GameScreen gameScreen = new GameScreen();
        gameScreen.setSize(1280,800);
        gameScreen.setVisible(true);
        gameScreen.screen.setBackground(Color.black);

        new Creation(gameScreen);
    }

}

