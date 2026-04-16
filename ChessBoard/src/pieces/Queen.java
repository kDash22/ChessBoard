package pieces;

import Global.Global;
import board.ChessBoard;

public class Queen extends Piece {

    private boolean check = false;

    public Queen(Character chessCol, int chessRow, boolean white) {
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_QUEEN);
        } else {
            setIdentification(PieceIdentification.B_QUEEN);
        }
        ChessBoard.insertPiece(chessCol, chessRow, this);
    }

    @Override
    public void moveCheck() {
    }

    @Override
    public void movePiece() {
    }
}
