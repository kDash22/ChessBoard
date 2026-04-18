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

        // A King can have up to 8 moves + 2 for castling
        int[][] tempMoveSet = new int[10][2];
        boolean[] tempValidMoveSet = new boolean[10];
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

                // --- King Proximity Rule ---
                // If the target square is reachable, check if it's adjacent to the enemy King
                if (tempValidMoveSet[count]) {
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int adjRow = toRow + dr;
                            int adjCol = toCol + dc;
                            if (adjRow >= 0 && adjRow < 8 && adjCol >= 0 && adjCol < 8) {
                                Piece adjPiece = refBoard[adjRow][adjCol];
                                if (adjPiece instanceof King && adjPiece.getIdentification().isWhite() != getIdentification().isWhite()) {
                                    tempValidMoveSet[count] = false;
                                    break;
                                }
                            }
                        }
                        if (!tempValidMoveSet[count]) break;
                    }
                }
                count++;
            }
        }


        // --- Castling Logic ---
        if (!this.hasMoved()) {
            // King Side Castling
            if (refBoard[row][7] instanceof Rook rook && !rook.hasMoved()) {
                if (refBoard[row][5] == null && refBoard[row][6] == null) {
                    tempMoveSet[count][0] = row;
                    tempMoveSet[count][1] = 6;
                    tempValidMoveSet[count] = true;
                    count++;
                }
            }
            // Queen Side Castling
            if (refBoard[row][0] instanceof Rook rook && !rook.hasMoved()) {
                if (refBoard[row][1] == null && refBoard[row][2] == null && refBoard[row][3] == null) {
                    tempMoveSet[count][0] = row;
                    tempMoveSet[count][1] = 2;
                    tempValidMoveSet[count] = true;
                    count++;
                }
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
