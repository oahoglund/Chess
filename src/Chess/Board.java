package Chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    Tile[][] board;
    boolean playingColor;
    boolean checkMate;
    int[] wKingpos;
    int[] bKingpos;
    int[] selectedPos;
    BufferedImage[] images;
    boolean wKmoved = false;
    boolean wKRmoved = false;
    boolean wQRmoved = false;
    boolean bKmoved = false;
    boolean bKRmoved = false;
    boolean bQRmoved = false;
    int[] recentMoved2Pawn;


    public Board(){
        // Constructor
        board = new Tile[8][8];
        images = new BufferedImage[13];
        try {
            // Put all images in an array
            for (int i = 0; i < 13; i++){
                images[i] = ImageIO.read(getClass().getResourceAsStream("/pieces/sprite_" + i + ".png"));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        this.initialize();
    }

    public void initialize() {
        // Sets the starting location of the pieces
        // color: true = white, false = black
        // piece: 0 = blank, 1 = king, 2 = queen, 3 = rook, 4 = bishop, 5 = horse, 6 = pawn

        // Initialize white major pieces
        board[0][0] = new Tile(true, 3, 0, 0); // White Rook
        board[0][1] = new Tile(true, 5, 0, 1); // White Knight
        board[0][2] = new Tile(true, 4, 0, 2); // White Bishop
        board[0][3] = new Tile(true, 2, 0, 3); // White Queen
        board[0][4] = new Tile(true, 1, 0, 4); // White King
        wKingpos = new int[]{0,4};
        board[0][5] = new Tile(true, 4, 0, 5); // White Bishop
        board[0][6] = new Tile(true, 5, 0, 6); // White Knight
        board[0][7] = new Tile(true, 3, 0, 7); // White Rook

        // Initialize white pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Tile(true, 6, 1, i); // White Pawns
        }

        // Initialize black major pieces
        board[7][0] = new Tile(false, 3, 7, 0); // Black Rook
        board[7][1] = new Tile(false, 5, 7, 1); // Black Knight
        board[7][2] = new Tile(false, 4, 7, 2); // Black Bishop
        board[7][3] = new Tile(false, 2, 7, 3); // Black Queen
        board[7][4] = new Tile(false, 1, 7, 4); // Black King
        bKingpos = new int[]{7,4};
        board[7][5] = new Tile(false, 4, 7, 5); // Black Bishop
        board[7][6] = new Tile(false, 5, 7, 6); // Black Knight
        board[7][7] = new Tile(false, 3, 7, 7); // Black Rook

        // Initialize black pawns
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Tile(false, 6, 6, i); // Black Pawns
        }

        // Initialize empty tiles for the rest of the board
        for (int i = 2; i < 6; i++) {  // Rows 2 to 5
            for (int j = 0; j < 8; j++) {  // All columns
                board[i][j] = new Tile(false, 0, i, j);  // Blank
            }
        }

        // Update moves
        playingColor = true;
        checkMate = false;
        updateMoves();
    }

    public boolean canSelectPiece(int row, int col){
        //Can only select your pieces that has moves
        return (board[row][col].color == playingColor && board[row][col].piece != 0 && !board[row][col].moves.isEmpty());
    }

    public void update(int row, int col){
        if (selectedPos == null){
            if (canSelectPiece(row,col)){
                selectedPos = new int[]{row,col};
            }
        } else if (board[selectedPos[0]][selectedPos[1]].contains(new int[]{row,col})) {
            // Checks if you are doing a move
            movePiece(selectedPos[0],selectedPos[1],row,col);

            // Have to manually make sure the empty tile doesnt have any moves
            board[selectedPos[0]][selectedPos[1]].moves = null;
            // Promotion
            if ((row == 7 || row == 0) && board[row][col].piece == 6){
                board[row][col].piece = 2; // I dont want to implement the other promotions
                // but I should at least implement knight promotion, but dont wanna
            }
            // Casteling and en passant
            switch (board[row][col].piece){
                //Casteling
                case 1: {
                    // Check if doing a castling move and if it does, moves the rook
                    // Also checks what king is being moved
                    if (playingColor){
                        if (!wKmoved && row == 0 && col == 2 && !wQRmoved){
                            movePiece(0,0,0,3);
                        } else if (!wKmoved && row == 0 && col == 6 && !wKRmoved) {
                            movePiece(0,7,0,5);
                        }
                        wKmoved = true;
                    } else {
                        if (!bKmoved && row == 7 && col == 2 && !bQRmoved){
                            movePiece(7,0,7,3);
                        } else if (!bKmoved && row == 7 && col == 6 && !bKRmoved) {
                            movePiece(7,7,7,5);
                        }
                        bKmoved = true;
                    }
                    recentMoved2Pawn = null;
                    break;
                }
                case 3: {
                    // Checks what rook is being moved
                    if (playingColor){
                        if (selectedPos[0] == 0 && selectedPos[1] == 0) {
                            wQRmoved = true;
                        } else if (selectedPos[0] == 0 && selectedPos[1] == 7 ) {
                            wKRmoved = true;
                        }
                    } else {
                        if (selectedPos[0] == 7 && selectedPos[1] == 0) {
                            bQRmoved = true;
                        } else if (selectedPos[0] == 7 && selectedPos[1] == 7 ) {
                            bKRmoved = true;
                        }
                    }
                    recentMoved2Pawn = null;
                    break;
                }
                // En passant
                case 6: {
                    int pawnDirection = playingColor ? 1 : -1;
                    if (recentMoved2Pawn != null && row == (recentMoved2Pawn[0]+pawnDirection)
                            && col == recentMoved2Pawn[1]){
                        // Kill en passented pawn
                        board[recentMoved2Pawn[0]][recentMoved2Pawn[1]].piece = 0;
                        board[recentMoved2Pawn[0]][recentMoved2Pawn[1]].moves = null;
                    }
                    if ((selectedPos[0] == 1 && row == 3) || (selectedPos[0] == 6 && row == 4)){
                        // Update tracker
                        recentMoved2Pawn = new int[]{row, col};
                    } else {
                        recentMoved2Pawn = null;
                    }
                    break;
                }
                default:{
                    // All of these are to reset so that we are only tracking recent moves
                    recentMoved2Pawn = null;
                }
            }
            selectedPos = null;
            playingColor = !playingColor;
            updateMoves();
            System.out.println("You playing as " + (playingColor ? "white" : "black"));
        } else {
            // Makes it so that you can select another piece or click of the one you are on
            if (canSelectPiece(row,col)){
                selectedPos = new int[]{row,col};
            }else {
                selectedPos = null;
            }
        }
        System.out.println("Updated");
    }

    public void updateMoves(){
        boolean hasMoves = false;
        for (int i = 0 ; i < 8 ; i++){
            for (int j = 0 ; j < 8 ; j++){
                if (board[i][j].color == playingColor && board[i][j].piece != 0){ //Updates only your pieces
                    updateMove(i,j);
                    // This is quite obvoius
                    if (!board[i][j].moves.isEmpty()) {
                        hasMoves = true;
                    }
                }
            }
        }
        this.checkMate = !hasMoves;
    }

    public void draw(Graphics2D g2, int tileSize){
        for (int i = 7 ; i >= 0 ; i--){
            for (int j = 0 ; j < 8 ; j++){
                // Draw the checkerboard grid
                if ((i+j) % 2 == 1) {
                    g2.setColor(Color.DARK_GRAY);
                }
                else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRect(j*tileSize,i*tileSize,tileSize,tileSize);

                // Draw the pieces
                if (board[i][j].piece != 0){
                    int color = board[i][j].color ? 0 : 1;
                    g2.drawImage(images[10 - (board[i][j].piece-1)*2 + color],j*tileSize,i*tileSize,
                            tileSize,tileSize,null);
                }
            }
        }
        // Draw possible moves
        if (selectedPos != null){
            for (int[] array : board[selectedPos[0]][selectedPos[1]].moves) {
                g2.setColor(Color.GREEN);
                g2.drawOval(array[1]*tileSize,array[0]*tileSize,tileSize,tileSize);
            }
        }
        // Draw check mate message
        if (checkMate){
            g2.drawImage(images[12],2*tileSize,3*tileSize,4*tileSize,2*tileSize,null);
        }
    }

    public void updateMove(int row, int col){
        List<int[]> moves = new ArrayList<>();
        switch(board[row][col].piece){
            case 0:
                break;
            case 1:
                moves = getKingMoves(row, col);
                break;
            case 2:
                moves = getQueenMoves(row, col);
                break;
            case 3:
                moves = getRookMoves(row, col);
                break;
            case 4:
                moves = getBishopMoves(row, col);
                break;
            case 5:
                moves = getKnightMoves(row, col);
                break;
            case 6:
                moves = getPawnMoves(row, col);
                break;
        }
        board[row][col].moves = moves;
    }

    public List<int[]> getKingMoves(int row, int col){
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1},{0,1}};
        for (int[] direction : directions){
            int newRow = board[row][col].row + direction[0];
            int newCol = board[row][col].col + direction[1];
            if (isValidMove(row, col, newRow,newCol)){
                moves.add(new int[]{newRow,newCol});
            }
        }
        // Casteling
        if ((playingColor && !wKmoved) || (!playingColor && !bKmoved)){ // Check if a king has been moved
            int i = playingColor ? 0 : 7; // Backrow
            if (((playingColor && !wKRmoved) || (!playingColor && !bKRmoved)) && board[i][5].piece == 0  && board[i][6].piece == 0
                    && !isCurrentlyInCheck() && !isCheck(i,4,i,5) && !isCheck(i,4,i,6)){
                // Check if king side rook has not been moved
                moves.add(new int[]{i,6});
            }

            if (((playingColor && !wQRmoved) || (!playingColor && !bQRmoved)) && board[i][3].piece == 0  && board[i][2].piece == 0 && board[i][1].piece == 0
                    && !isCurrentlyInCheck() && !isCheck(i,4,i,3) && !isCheck(i,4,i,2)){
                // Queenside if king side rook has not been moved
                moves.add(new int[]{i,2});
            }
        }
        return moves;
    }

    public List<int[]> getQueenMoves(int row, int col){
        List<int[]> moves = new ArrayList<>();
        moves.addAll(getRookMoves(row,col));
        moves.addAll(getBishopMoves(row,col));
        return moves;
    }

    public List<int[]> getRookMoves(int row, int col){
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{1,0},{0,-1},{-1,0},{0,1}};
        for (int[] direction : directions){
            int newRow = board[row][col].row;
            int newCol = board[row][col].col;
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                if (isValidMove(row, col, newRow, newCol)){
                    moves.add(new int[]{newRow, newCol});
                    if (board[newRow][newCol].piece != 0) break; // Stop if there is a piece
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    public List<int[]> getBishopMoves(int row, int col){
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{1,1},{1,-1},{-1,-1},{-1,1}};
        for (int[] direction : directions){
            int newRow = board[row][col].row;
            int newCol = board[row][col].col;
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                if (isValidMove(row, col, newRow, newCol)) {
                    moves.add(new int[]{newRow, newCol});
                    if (board[newRow][newCol].piece != 0) break; // Stop if there is a piece
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    public List<int[]> getKnightMoves(int row, int col){
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};
        for (int[] direction : directions){
            int newRow = board[row][col].row + direction[0];
            int newCol = board[row][col].col + direction[1];
            if (isValidMove(row, col, newRow, newCol)){
                moves.add(new int[]{newRow,newCol});
            }
        }
        return moves;
    }

    public List<int[]> getPawnMoves(int row, int col){
        List<int[]> moves = new ArrayList<>();
        int startingRow = board[row][col].color ? 1 : 6;
        int direction = board[row][col].color ? 1 : -1;
        int thisRow = board[row][col].row;
        int thisCol = board[row][col].col;
        if (isValidMove(row,col,thisRow+direction,thisCol)
                && board[thisRow+direction][thisCol].piece == 0){
            moves.add(new int[]{thisRow+direction, thisCol});
            if (isValidMove(row,col,thisRow+2*direction,thisCol) && thisRow==startingRow
                    && board[thisRow+2*direction][thisCol].piece == 0){
                moves.add(new int[]{thisRow+2*direction, thisCol});
            }
        }
        // Check for diagonal moves and en passent, the long bit after || is checking for en passent
        if (isValidMove(row,col,thisRow+direction,thisCol+1)
                && ((board[thisRow+direction][thisCol+1].piece != 0) || (recentMoved2Pawn != null && row == recentMoved2Pawn[0]
                && isInRange(row,recentMoved2Pawn[1]-1) && col == (recentMoved2Pawn[1]-1))
                && board[thisRow+direction][thisCol+1].piece == 0) ){
            moves.add(new int[]{thisRow+direction, thisCol+1});
        }
        if (isValidMove(row,col,thisRow+direction,thisCol-1)
                && ((board[thisRow+direction][thisCol-1].piece != 0) || (recentMoved2Pawn != null && row == recentMoved2Pawn[0]
                && isInRange(row,recentMoved2Pawn[1]+1) && col == (recentMoved2Pawn[1]+1)
                && board[thisRow+direction][thisCol-1].piece == 0))){
            moves.add(new int[]{thisRow+direction, thisCol-1});
        }
        return moves;
    }

    public boolean isValidMove(int row, int col, int newRow, int newCol){
        if (isValidMovement(newRow,newCol)) {
            return !(isCheck(row, col, newRow, newCol));
        }
        else {
            return false;
        }
    }

    public boolean isValidMovement(int newRow, int newCol){
        return isInRange(newRow,newCol) && (board[newRow][newCol].piece == 0 || board[newRow][newCol].color != playingColor);
    }

    public boolean isInRange(int row, int col){
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isCheck(int row, int col, int toRow, int toCol){
        int oldPiece = board[toRow][toCol].piece;
        boolean oldColor = board[toRow][toCol].color;
        movePiece(row,col,toRow,toCol);
        if (isCurrentlyInCheck()){
            retract(toRow, toCol, row, col, oldPiece, oldColor);
            return true;
        } else {
            retract(toRow, toCol, row, col, oldPiece, oldColor);
            return false;
        }

    }

    public boolean isCurrentlyInCheck(){
        int[] kingPos = playingColor ? wKingpos : bKingpos;
        int kingRow = kingPos[0];
        int kingCol = kingPos[1];

        // Check if enemy pawn is attacking
        // Might break program if it tries to take values outside of board
        int pawnDirection = playingColor ? 1 : -1;
        if (isInRange(kingRow + pawnDirection, kingCol + 1) && board[kingRow + pawnDirection][kingCol + 1].color != playingColor
                && board[kingRow + pawnDirection][kingCol + 1].piece == 6) {
            return true;
        }
        if (isInRange(kingRow + pawnDirection, kingCol - 1) && board[kingRow + pawnDirection][kingCol - 1].color != playingColor
                && board[kingRow + pawnDirection][kingCol - 1].piece == 6) {
            return true;
        }
        // Check if enemy king is attacking
        int[][] directions = {{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            if (isInRange(newRow, newCol) && board[newRow][newCol].color != playingColor && board[newRow][newCol].piece == 1) {
                return true;
            }
        }

        // Check if enemy knight is attacking
        directions = new int[][]{{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            if (isInRange(newRow, newCol) && board[newRow][newCol].color != playingColor && board[newRow][newCol].piece == 5) {
                return true;
            }
        }
        // Check if enemy Rook or Queen are attacking straight
        directions = new int[][]{{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
        for (int[] direction : directions) {
            int newRow = kingRow;
            int newCol = kingCol;
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                if (isInRange(newRow, newCol) &&
                        (board[newRow][newCol].color != playingColor || board[newRow][newCol].piece == 0)) {
                    if (board[newRow][newCol].piece == 2 || board[newRow][newCol].piece == 3) {
                        return true;
                    }
                    if (board[newRow][newCol].color != playingColor && board[newRow][newCol].piece != 0){
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        // Check if enemy bishop or Queen are attacking diagonaly
        directions = new int[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        for (int[] direction : directions) {
            int newRow = kingRow;
            int newCol = kingCol;
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                if (isInRange(newRow, newCol) &&
                        (board[newRow][newCol].color != playingColor || board[newRow][newCol].piece == 0)) {
                    if (board[newRow][newCol].piece == 2 || board[newRow][newCol].piece == 4) {
                        return true;
                    }
                    if (board[newRow][newCol].color != playingColor && board[newRow][newCol].piece != 0){
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return false; // If no one is attacking then return that it is not a check
    }

    public void movePiece(int row, int col, int toRow, int toCol){
        if (board[row][col].piece == 1){ // If the king is moved it updates the tracking
            if (board[row][col].color) {
                this.wKingpos = new int[]{toRow,toCol};
            } else{
                this.bKingpos = new int[]{toRow,toCol};
            }
        }
        board[toRow][toCol].piece = board[row][col].piece;
        board[toRow][toCol].color = board[row][col].color;
        board[row][col].piece = 0;
        // Does not change the color so that for the check test I can still use this.color
        // to know what color the king is and so that the isValid move would work properly
    }

    public void retract(int fromRow, int fromCol, int oldRow, int oldCol, int oldPiece, boolean oldColor){
        movePiece(fromRow,fromCol,oldRow,oldCol);
        board[fromRow][fromCol].piece = oldPiece;
        board[fromRow][fromCol].color = oldColor;
    }

}
