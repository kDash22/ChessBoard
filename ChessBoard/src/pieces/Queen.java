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
        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = ChessBoard.getBoard();

        // A Queen can have up to 27 moves (14 straight + 13 diagonal)
        int[][] tempMoveSet = new int[28][2];
        boolean[] tempValidMoveSet = new boolean[28];
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
                tempMoveSet[count][0] = toRow;
                tempMoveSet[count][1] = toCol;

                if (refBoard[toRow][toCol] == null) {
                    tempValidMoveSet[count] = true;
                    count++;
                } else {
                    // Hit a piece - check if it's an enemy
                    if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                        if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                                refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                            check = true;
                            tempValidMoveSet[count] = false; // Special case: King cannot be taken
                        } else {
                            tempValidMoveSet[count] = true;
                        }
                    } else {
                        tempValidMoveSet[count] = false; // Blocked by own piece
                    }
                    count++;
                    break; // Stop sliding in this direction
                }

                toRow += direction[0];
                toCol += direction[1];
            }
        }

        // Trim arrays to the actual number of moves found
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
        // To be implemented
    }

    @Override
    public String toString() {
        return "Queen";
    }
}
