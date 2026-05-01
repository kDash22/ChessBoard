package pieces;

import board.ChessBoard;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{

    private boolean check = false;//not decided how to implement checking yet this is just a placeholder
    private static final int PIECE_VALUE = 3;

    public Knight(Character chessCol, int chessRow, boolean white,ChessBoard chessBoard){
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white){
            setIdentification(PieceIdentification.W_KNIGHT);
        } else {
            setIdentification(PieceIdentification.B_KNIGHT);
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

        //int validCount = 0;//used to get valid move validCount
        //8 possible moves for a knight
        int[][] tempMoveSet = new int[][]{
                {row + 2, col + 1}, {row + 2, col - 1},
                {row - 2, col + 1}, {row - 2, col - 1},
                {row + 1, col + 2}, {row + 1, col - 2},
                {row - 1, col + 2}, {row - 1, col - 2}
        };


        for(int i = 0 ; i < 8 ; i++  ){

            int toCol = tempMoveSet[i][1];
            int toRow = tempMoveSet[i][0];

            if (toCol < 8 && toCol >=0 && toRow < 8 && toRow >= 0 ){

                Piece target = refBoard[toRow][toCol];

                //if empty square
                if (target == null){
                    validMoveList.add(new int[]{toRow,toCol});
                    //tempValidMoveSet[i] = true;
                    //validCount++;
                    continue;
                }

                //if it is an enemy piece, but if it is the enemy king the move is not allowed as king cannot be taken
                if (target.getIdentification().isWhite() != getIdentification().isWhite()) {

                    if (target.getIdentification() == PieceIdentification.W_KING ||
                            target.getIdentification() == PieceIdentification.B_KING) {
                        check = true;
                        //tempValidMoveSet[i] = true; // Essential for check detection
                        validMoveList.add(new int[]{toRow,toCol});
                        //validCount++;
                    } else {
                        //tempValidMoveSet[i] = true;
                        //validCount++;
                        validMoveList.add(new int[]{toRow,toCol});
                    }
                }
            }

        }
        //moveSet = new int[validCount][2];
        //validMoveSet = new boolean[validCount];

        //System.out.print("Knight : ");
        //Global.print1D(tempValidMoveSet);

    }

    @Override
    public void movePiece() {

    }

    @Override
    public String toString(){
        if (getIdentification().isWhite())
            return " wN ";
        else
            return " bN ";


    }

}
