package pieces;

import Global.Global;
import board.ChessBoard;

public class Rook extends Piece {

    private boolean check = false;

    public Rook(Character col, int row, boolean white) {
        setCol(col);
        setRow(row);

        if (white) {
            setIdentification(PieceIdentification.W_ROOK);
        } else {
            setIdentification(PieceIdentification.B_ROOK);
        }
        ChessBoard.insertPiece(row, turnColToIndex(col),this);
    }

    @Override
    public void moveCheck() {
        int col = turnColToIndex(getCol());
        int row = getRow();

        Piece[][] refBoard = ChessBoard.getBoard();

        // A rook can move a maximum of 14 squares (7 in each of its two dimensions)
        int[][] tempMoveSet = new int[14][2];
        boolean[] tempValidMoveSet = new boolean[14];
        int count = 0;

        // Directions: {row_change, col_change}
        // Up, Down, Right, Left
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : dirs) {
            int toRow = row + dir[0];
            int toCol = col + dir[1];

            while (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
                tempMoveSet[count][0] = toRow;
                tempMoveSet[count][1] = toCol;

                if (refBoard[toRow][toCol] == null) {
                    tempValidMoveSet[count] = true;
                    count++;
                } else {
                    // Check if it's an opponent piece
                    if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                        if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                            refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                            check = true;
                            tempValidMoveSet[count] = false; // King check logic as used in Knight
                        } else {
                            tempValidMoveSet[count] = true;
                        }
                    } else {
                        tempValidMoveSet[count] = false; // Blocked by own piece
                    }
                    count++;
                    break; // Blocked from going further in this direction
                }

                toRow += dir[0];
                toCol += dir[1];
            }
        }

        // Initialize and correctly size the moveSet and validMoveSet arrays
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

    }

    @Override
    public String toString() {
        return "Rook";
    }
}
