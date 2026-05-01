package pieces;

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


        if (chessBoard.isWhiteToMove() != getIdentification().isWhite()){
            moveSet = null;
            validMoveSet = null;
            return;
        }


        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = chessBoard.getBoard();

        // A Queen can have up to 28 moves (14 straight + 14 diagonal)
        int[][] tempMoveSet = new int[28][2];
        boolean[] tempValidMoveSet = new boolean[28];
        int count = 0;
        int validMoveCount = 0;//to keep track of valid moves for resizing the moveSet and validMoveSet arrays later

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
                    validMoveCount++;
                } else {
                    // Hit a piece - check if it's an enemy
                    if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                        if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                                refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                            check = true;
                            tempValidMoveSet[count] = true; // Essential for check detection
                            validMoveCount++;
                        } else {
                            tempValidMoveSet[count] = true;
                            validMoveCount++;   
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
        moveSet = new int[validMoveCount][2];
        validMoveSet = new boolean[validMoveCount];
        int j = 0;

        for (int i = 0; i < count; i++) {
            if (tempValidMoveSet[i]) {
                moveSet[j][0] = tempMoveSet[i][0];
                moveSet[j][1] = tempMoveSet[i][1];
                validMoveSet[j] = true;
                j++;
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
