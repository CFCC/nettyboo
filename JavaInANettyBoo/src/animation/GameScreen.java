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
    //the list of balls on screen, each ball is assigned a specific number
    public void addBall(){}
    public static void main(String[] args) {
        GameScreen gameScreen = new GameScreen();
        gameScreen.setVisible(true);
        gameScreen.setSize(1280,800);
        gameScreen.setBackground(Color.black);



    }
}

