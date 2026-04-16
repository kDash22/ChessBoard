package pieces;

import Global.Global;
import board.ChessBoard;

public class King extends Piece {

    public King(Character chessCol, int chessRow, boolean white) {
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_KING);
        } else {
            setIdentification(PieceIdentification.B_KING);
        }
        ChessBoard.insertPiece(chessCol, chessRow, this);
    }

    @Override
    public void moveCheck() {
        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = ChessBoard.getBoard();

        // A King can have up to 8 moves
        int[][] tempMoveSet = new int[8][2];
        boolean[] tempValidMoveSet = new boolean[8];
        int count = 0;

        // All 8 directions
        int[][] directions = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, // Straight
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } // Diagonal
        };

        for (int[] direction : directions) {
            int toRow = row + direction[0];
            int toCol = col + direction[1];

            // Check if the square is available
            if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
                tempMoveSet[count][0] = toRow;
                tempMoveSet[count][1] = toCol;

                if (refBoard[toRow][toCol] == null) {
                    // Empty square
                    tempValidMoveSet[count] = true;
                } else if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                    // Enemy piece
                    tempValidMoveSet[count] = true;
                } else {
                    // Friendly piece
                    tempValidMoveSet[count] = false;
                }
                count++;
            }
        }

        // Trim the arrays
        moveSet = new int[count][2];
        validMoveSet = new boolean[count];

        for (int i = 0; i < count; i++) {
            moveSet[i][0] = tempMoveSet[i][0];
            moveSet[i][1] = tempMoveSet[i][1];
            validMoveSet[i] = tempValidMoveSet[i];
        }

        Global.print1D(validMoveSet);
    }

    @Override
    public void movePiece() {
        // Implementation for special moves like castling can go here
    }

    @Override
    public String toString() {
        return "King";
    }
}
