package Chess;

import java.awt.*;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
    // Screen settings
    final int originalTileSize = 16; // 16 x 16 tile
    final int scale = 3;

    public int tileSize = originalTileSize * scale; // 48 x 48 tile
    final int maxScreenCol = 8;
    final int maxScreenRow = 8;
    final int screenWidth = tileSize * maxScreenCol; // 368 pixels
    final int screenHeight = tileSize * maxScreenRow; // 368 pizels

    MouseHandler mouseH = new MouseHandler();
    Board chessBoard = new Board();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.addMouseListener(mouseH);
        this.setFocusable(true);

    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        chessBoard.update(mouseH.row,mouseH.col);

        chessBoard.draw(g2,tileSize);

        g2.dispose();
    }


}
