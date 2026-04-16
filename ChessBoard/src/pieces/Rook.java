package pieces;

import Global.Global;
import board.ChessBoard;

public class Rook extends Piece {

    private boolean check = false;

    public Rook(Character col, int chessRow, boolean white) {
        setCol(col);
        setRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_ROOK);
        } else {
            setIdentification(PieceIdentification.B_ROOK);
        }
        ChessBoard.insertPiece(turnColToIndex(col), chessRow, this);
    }

    @Override
    public void moveCheck() {
        int col = turnColToIndex(getCol());
        int row = Global.chessRowToIndex(getChessRow());

        Piece[][] refBoard = ChessBoard.getBoard();

        // up to 14 straight moves
        int[][] tempMoveSet = new int[14][2];
        boolean[] tempValidMoveSet = new boolean[14];
        int count = 0;

        // 4 diagonal directions
        int[][] dirs = { { -1, 1 }, { 1, 1 }, { 1, -1 }, { -1, -1 } };

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
                    // hit an opponent piece
                    if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                        if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                                refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                            check = true;
                            tempValidMoveSet[count] = false; // special check case
                        } else {
                            tempValidMoveSet[count] = true;
                        }
                    } else {
                        tempValidMoveSet[count] = false; // blocked by our piece
                    }
                    count++;
                    break; // stop sliding this way
                }

                toRow += dir[0];
                toCol += dir[1];
            }
        }

        // trim arrays to exactly what we found
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
