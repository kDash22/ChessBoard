package pieces;

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


        if (chessBoard.isWhiteToMove() != getIdentification().isWhite()){
            moveSet = null;
            validMoveSet = null;
            return;
        }


        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = chessBoard.getBoard();

        // up to 14 straight moves
        int[][] tempMoveSet = new int[14][2];
        boolean[] tempValidMoveSet = new boolean[14];
        int count = 0;
        int validMoveCount = 0;//to keep track of valid moves for resizing the moveSet and validMoveSet arrays later

        // Directions: {row_change, col_change}
        // Up, Down, Right, Left
        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        for (int[] direction : directions) {
            int toRow = row + direction[0];
            int toCol = col + direction[1];

            while (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
                tempMoveSet[count][0] = toRow;
                tempMoveSet[count][1] = toCol;

                if (refBoard[toRow][toCol] == null) {
                    tempValidMoveSet[count] = true;
                    validMoveCount++;
                    count++;

                } else {
                    // hit an opponent piece
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
                        tempValidMoveSet[count] = false; // blocked by our piece
                    }
                    count++;
                    break; // stop sliding this way
                }

                toRow += direction[0];
                toCol += direction[1];
            }
        }

        // trim arrays to exactly what we found
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
