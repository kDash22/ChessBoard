package pieces;


import board.ChessBoard;

public abstract  class Piece {

    protected boolean[] validMoveSet; //array to hold the valid moves
    protected int[][] moveSet; //all moves of a piece
    private Character col; //column letter in the chessboard
    private int row; //row digit in the chessboard
    private PieceIdentification identification;//what piece it is and what team does it belong to

    //setters
    public void setCol(Character col) {
        if (ChessBoard.COLUMN_LETTERS.contains(col)){
            this.col = col;
        } else {
            throw new IllegalArgumentException(" COLUMN LETTER NOT VALID ! ");
        }
    }

    public void setRow(int row) {
        if (row < 8 && row >= 0){
            this.row = row;
        } else {
            throw new IllegalArgumentException(" ROW NUMBER CANNOT BE BIGGER THAN 8 OR SMALLER THAN 0 ! ");
        }
    }
    public void setIdentification(PieceIdentification identification) {
        this.identification = identification;
    }

    //getters
    public Character getCol() {
        return col;
    }

    public int getChessRow() {
        return row;
    }

    public PieceIdentification getIdentification() {
        return identification;
    }

    //a method used to convert column letter into int to be used in arrays
    public int turnColToIndex(Character x){
        return switch (x) {
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

    public boolean[] getValidMoveSet() {
        return validMoveSet;
    }

    public int[][] getMoveSet() {
        return moveSet;
    }

    //checking the available moves
    public abstract void moveCheck();

    public abstract void movePiece();


}
