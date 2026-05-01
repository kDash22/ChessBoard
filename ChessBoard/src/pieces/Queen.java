package pieces;

import java.lang.reflect.Array;
import java.util.ArrayList;

import board.ChessBoard;

public class Queen extends Piece {

    private static final int PIECE_VALUE = 9;
    private boolean check = false;

    public Queen(Character chessCol, int chessRow, boolean white,ChessBoard chessBoard) {
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_QUEEN);
        } else {
            setIdentification(PieceIdentification.B_QUEEN);
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

        // Combined 8 directions: Straight (Rook) + Diagonal (Bishop)
        int[][] directions = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, // Straight
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } // Diagonal
        };

        for (int[] direction : directions) {
            int toRow = row + direction[0];
            int toCol = col + direction[1];

            while (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {

                if (refBoard[toRow][toCol] == null) {

                    validMoveList.add(new int[]{toRow,toCol});
                    count++;
                    
                } else {
                    // Hit a piece - check if it's an enemy
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
                    break; // Stop sliding in this direction
                }

                toRow += direction[0];
                toCol += direction[1];
            }
        }

    }

    @Override
    public void movePiece() {
        // To be implemented
    }

    @Override
    public String toString(){
        if (getIdentification().isWhite())
            return " wQ ";
        else
            return " bQ ";


    }
}
