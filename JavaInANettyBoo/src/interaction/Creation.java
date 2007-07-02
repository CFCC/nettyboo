package interaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Jul 2, 2007
 * Time: 11:18:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class Creation {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(600, 500);
        Container sl = frame.getContentPane();
        sl.setBackground(Color.black);
        sl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("ello' govna");
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }
        });
    }
}
