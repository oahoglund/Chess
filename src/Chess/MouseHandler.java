package Chess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {
    public int row = 0, col = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
        GamePanel panel = (GamePanel) e.getSource();
        row = e.getY() / panel.tileSize;
        col = e.getX() / panel.tileSize;
        panel.repaint();
    }
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
