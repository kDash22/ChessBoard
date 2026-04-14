package pieces;

import board.ChessBoard;

public class Bishop extends Piece {

    private boolean check = false;

    public Bishop(Character col, int row, boolean white) {
        setCol(col);
        setRow(row);

        if (white) {
            setIdentification(PieceIdentification.W_BISHOP);
        } else {
            setIdentification(PieceIdentification.B_BISHOP);
        }
        ChessBoard.insertPiece(row, turnColToIndex(col), this);
    }

    @Override
    public void moveCheck() {

    }

    @Override
    public void movePiece() {

    }
}
