package animation;

import javax.swing.*;
import java.awt.*;


public class Canvas extends JComponent{
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red);
        g2.drawLine(0,0,getWidth(),0);
        g2.setColor(Color.red);
        g2.drawLine(0,getHeight(),0,0);


    }

    public static void main(String[] args) {

        JFrame window = new JFrame();
        window.add(new Canvas());

        window.setSize(1280,720);
        window.setBackground(Color.red);
        window.setVisible(true);
    }
}