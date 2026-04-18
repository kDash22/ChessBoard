package pieces;


import board.ChessBoard;

public abstract  class Piece {

    protected boolean[] validMoveSet; //array to hold the valid moves
    protected int[][] moveSet; //all moves of a piece
    private Character chessCol; //column letter in the chessboard
    private int chessRow; //chessRow digit in the chessboard
    private PieceIdentification identification;//what piece it is and what team does it belong to
    private boolean hasMoved = false;

    //setters
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setChessCol(Character chessCol) {
        if (ChessBoard.COLUMN_LETTERS.contains(chessCol)){
            this.chessCol = chessCol;
        } else {
            throw new IllegalArgumentException(" COLUMN LETTER NOT VALID ! : "+chessCol);
        }
    }

    public void setChessRow(int chessRow) {
        if (chessRow <= 8 && chessRow > 0){
            this.chessRow = chessRow;
        } else {
            throw new IllegalArgumentException(" ROW NUMBER MUST BE BETWEEN 1 AND 8 ! : "+ chessRow);
        }
    }
    public void setIdentification(PieceIdentification identification) {
        this.identification = identification;
    }

    //getters
    public Character getChessCol() {
        return chessCol;
    }

    public int getChessRow() {
        return chessRow;
    }

    public PieceIdentification getIdentification() {
        return identification;
    }

    //a method used to convert column letter into int to be used in arrays
    public static int chessColToIndex(Character chessCol){
        if (!ChessBoard.COLUMN_LETTERS.contains(chessCol)){
           throw new IllegalArgumentException(" COLUMN LETTER NOT VALID ! : " + chessCol);
        }
        return switch (chessCol) {
            case 'a' -> 0;
            case 'b' -> 1;
            case 'c' -> 2;
            case 'd' -> 3;
            case 'e' -> 4;
            case 'f' -> 5;
            case 'g' -> 6;
            case 'h' -> 7;
            default -> -1;
        };
    }

    //a method used to convert array col number to chess column number
    public static char colToChessCol(int col){
        if (col > 7 || col < 0) throw new IllegalArgumentException(" Array Column number must be between 0 and 7 ! :"+col);
        return switch (col) {
            case 0 -> 'a';
            case 1 -> 'b';
            case 2 -> 'c';
            case 3 -> 'd';
            case 4 -> 'e';
            case 5 -> 'f';
            case 6 -> 'g';
            case 7 -> 'h';
            default -> 'x';
        };
    }

    //a method used to convert chess rows into int to be used in arrays
    public static int chessRowToIndex(int chessRow) {
        if (chessRow < 1 || chessRow > 8) {
            throw new IllegalArgumentException("chessRow must be between 1 and 8: " + chessRow);
        }
        return 8 - chessRow;
    }
    //a method used to convert chess rows into int to be used in arrays
    public static int rowToChessRow(int row) {
        if (row < 0 || row > 7) {
            throw new IllegalArgumentException("Array row number must be between 0 and 7: " + row);
        }
        return 8-row;
    }

    public boolean[] getValidMoveSet() {
        moveCheck();
        return validMoveSet;
    }

    public int[][] getMoveSet() {
        moveCheck();
        return moveSet;
    }

    //checking the available moves
    public abstract void moveCheck();

    public abstract void movePiece();


}
