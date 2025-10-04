package Chess;

import java.util.Arrays;
import java.util.List;

public class Tile {
    boolean color;
    int piece;
    int row;
    int col;
    List<int[]> moves;

    public Tile(boolean color, int piece, int row, int col){
        this.color = color;
        this.piece = piece;
        this.row = row;
        this.col = col;
    }

    public List<int[]> getMoves(){
        return moves;
    }

    public boolean contains(int[] A){
        for (int[] move : moves) {
            if (Arrays.equals(move, A)) {
                return true;
            }
        }
        return false;
    }
}
