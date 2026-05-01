package pieces;

import java.lang.reflect.Array;
import java.util.ArrayList;

import board.ChessBoard;

public class Rook extends Piece {

    private static final int PIECE_VALUE = 5;

    private boolean check = false;

    public Rook(Character chessCol, int chessRow, boolean white,ChessBoard chessBoard) {
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_ROOK);
        } else {
            setIdentification(PieceIdentification.B_ROOK);
        }
        chessBoard.insertPiece(chessCol, chessRow, this);
    }

    @Override
    public void moveCheck(ChessBoard chessBoard) {


        // ALWAYS clear the list first to prevent duplicating old moves
        if (validMoveList != null) {
            validMoveList.clear();
        }


        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = chessBoard.getBoard();

        int count = 0;
        

        // Directions: {row_change, col_change}
        // Up, Down, Right, Left
        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        for (int[] direction : directions) {
            int toRow = row + direction[0];
            int toCol = col + direction[1];

            while (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {

                if (refBoard[toRow][toCol] == null) {
                    validMoveList.add(new int[]{toRow,toCol});
                    count++;

                } else {
                    // hit an opponent piece
                    if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                        if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                                refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                            check = true;
                            validMoveList.add(new int[]{toRow,toCol});
                        } else {
                            validMoveList.add(new int[]{toRow,toCol});
                        }
                    }
                    count++;
                    break; // stop sliding this way
                }

                toRow += direction[0];
                toCol += direction[1];
            }
        }
        //Global.print1D(validMoveSet);
    }

    @Override
    public void movePiece() {

    }

    @Override
    public String toString(){
        if (getIdentification().isWhite())
            return " wR ";
        else
            return " bR ";


    }
}
