package pieces;

import Global.Global;
import board.ChessBoard;

import java.util.Arrays;

public class Knight extends Piece{

    private boolean check = false; //not decided how to implement checking yet this is just a place holder


    public Knight(Character col, int row, boolean white){
        setCol(col);
        setRow(row);

        if (white){
            setIdentification(PieceIdentification.W_KNIGHT);
        } else {
            setIdentification(PieceIdentification.B_KNIGHT);
        }
        ChessBoard.insertPiece(turnColToIndex(col), row, this);
    }

    @Override
    public void moveCheck() {
        int col = turnColToIndex(getCol());
        int row = getRow();

        Piece[][] refBoard = ChessBoard.getBoard();

        //8 possible moves for a knight
        moveSet = new int[][]{
                  {col + 2, row + 1}, {col + 2, row - 1}
                , {col - 2, row + 1}, {col - 2, row - 1}
                , {col + 1, row + 2}, {col + 1, row - 2}
                , {col - 1, row + 2}, {col - 1, row - 2}
        };

        //8 possible moves for a knight
        int[] move1 = {col+2,row+1};
        int[] move2 = {col+2,row-1};

        int[] move3 = {col-2,row+1};
        int[] move4 = {col-2,row-1};

        int[] move5 = {col+1,row+2};
        int[] move6 = {col+1,row-2};

        int[] move7 = {col-1,row+2};
        int[] move8 = {col-1,row+2};

        for(int i = 0 ; i < 8 ; i++  ){

            int toCol = moveSet[i][0];
            int toRow = moveSet[i][1];

            if (toCol < 8 && toCol >=0 && toRow < 8 && toRow >= 0 ){

                //if empty square
                if (refBoard[toCol][toRow] == null){
                    validMoveSet[i] = true;
                    continue;
                }
                //if piece colours are not the same, if it is a king of the different it is not allowed
                if(refBoard[toCol][toRow].getIdentification().isWhite() && getIdentification().isBlack() &&
                        refBoard[toCol][toRow].getIdentification() != PieceIdentification.B_KING && refBoard[toCol][toRow].getIdentification() != PieceIdentification.W_KING){

                    validMoveSet[i] = true;
                    continue;
                }
                if(refBoard[toCol][toRow].getIdentification().isBlack() && getIdentification().isWhite() &&
                        refBoard[toCol][toRow].getIdentification() != PieceIdentification.B_KING && refBoard[toCol][toRow].getIdentification() != PieceIdentification.W_KING){

                    validMoveSet[i] = true;
                    continue;
                }
                //if it is a king of a different team
                if (refBoard[toCol][toRow].getIdentification() == PieceIdentification.B_KING && getIdentification().isWhite()) {
                    check = true;
                    validMoveSet[i] = false; // special case
                    continue;
                }
                if (refBoard[toCol][toRow].getIdentification() == PieceIdentification.W_KING && getIdentification().isBlack()) {
                    check = true;
                    validMoveSet[i] = false; // special case
                    continue;

                }
                validMoveSet[i] = false;

            }

        }
        Global.print1D(validMoveSet);
    }

    @Override
    public void movePiece() {

    }

    @Override
    public String toString(){
        return "Knight";
    }

}
