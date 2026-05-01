package pieces;

import java.util.ArrayList;

import board.ChessBoard;

public class Bishop extends Piece {

    private boolean check = false;
    private static final int PIECE_VALUE = 3;

    public Bishop(Character chessCol, int chessRow, boolean white,ChessBoard chessBoard) {
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_BISHOP);
        } else {
            setIdentification(PieceIdentification.B_BISHOP);
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

        // up to 13 moves on the longest diagonal
        int count = 0;
        //int validMoveCount = 0;//to keep track of valid moves for resizing the moveSet and validMoveSet arrays later

        // Directions: {row_change, col_change}
        // Top-Right, Top-Left, Bottom-Right, Bottom-Left
        int[][] directions = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

        for (int[] direction : directions) {
            int toRow = row + direction[0];
            int toCol = col + direction[1];

            while (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {

                if (refBoard[toRow][toCol] == null) {
                    count++;
                    validMoveList.add(new int[]{toRow,toCol});
                } else {
                    // Check if it's an opponent piece
                    if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {
                        if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                                refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                            check = true;
                            validMoveList.add(new int[]{toRow,toCol});
                            //validMoveCount++;
                        } else {
                            validMoveList.add(new int[]{toRow,toCol});
                            //validMoveCount++; 
                        }
                    }
                    count++;
                    break; // Blocked from going further in this direction
                }

                toRow += direction[0];
                toCol += direction[1];
            }
        }

        //System.out.println("\nBishop valid move count : "+count+"\n");
        /*
        int j = 0;

        
        for (int i = 0; i < count; i++) {
            if (tempValidMoveSet[i]) {
                moveSet[j][0] = tempMoveSet[i][0];
                moveSet[j][1] = tempMoveSet[i][1];
                validMoveSet[j] = true;
                j++;
            }
        }
         */

        //System.out.print("Bishop : ");
        //Global.print1D(validMoveSet);
        //Global.print1D(tempValidMoveSet);

    }

    @Override
    public void movePiece() {

    }

    @Override
    public String toString(){
        if (getIdentification().isWhite())
            return " wB ";
        else
            return " bB ";
    }
}
