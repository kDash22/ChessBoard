package pieces;


import board.ChessBoard;

public abstract  class Piece {


    private boolean isWhite;
    private Character col;
    private int row;
    private PieceIdentification identification;

    //setters
    public void setWhite(boolean white) {
        this.isWhite = white;
    }

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

    //getters
    public Character getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public PieceIdentification getIdentification() {
        return identification;
    }

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
    public abstract void moveCheck();

    public abstract void movePiece();

    public void setIdentification(PieceIdentification identification) {
        this.identification = identification;
    }
}
