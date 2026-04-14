package pieces;

import Global.Global;
import board.ChessBoard;

import java.util.Arrays;

public class Knight extends Piece{

    private boolean check = false;
    public Knight(Character col, int row, boolean white){
        setCol(col);
        setRow(row);

        if (white){
            setIdentification(PieceIdentification.W_KNIGHT);
            ChessBoard.insertPiece(turnColToIndex(col), row, PieceIdentification.W_KNIGHT );
        } else {
            setIdentification(PieceIdentification.B_KNIGHT);
            ChessBoard.insertPiece(turnColToIndex(col), row, PieceIdentification.B_KNIGHT);
        }

    }

    @Override
    public void moveCheck() {
        int col = turnColToIndex(getCol());
        int row = getRow();

        PieceIdentification[][] refBoard = ChessBoard.getBoard();

        int[][] moveSet = {
                        {col+2,row+1},{col+2,row-1}
                        ,{col-2,row+1},{col-2,row-1}
                        ,{col+1,row+2},{col+1,row-2}
                        ,{col-1,row+2},{col-1,row-2}
                        };
        boolean[] validMoveSet = new boolean[8];

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

                if (refBoard[toCol][toRow] == null){
                    validMoveSet[i] = true;
                    continue;
                }
                if(refBoard[toCol][toRow].isWhite() && getIdentification().isBlack() &&
                        refBoard[toCol][toRow] != PieceIdentification.B_KING && refBoard[toCol][toRow] != PieceIdentification.W_KING ){

                    validMoveSet[i] = true;
                    continue;
                }
                if(refBoard[toCol][toRow].isBlack() && getIdentification().isWhite() &&
                        refBoard[toCol][toRow] != PieceIdentification.B_KING && refBoard[toCol][toRow] != PieceIdentification.W_KING){
                    validMoveSet[i] = true;
                    continue;
                }
                if (refBoard[toCol][toRow] == PieceIdentification.B_KING && getIdentification().isWhite()) {
                    check = true;
                    validMoveSet[i] = false; // special case
                    continue;
                }
                if (refBoard[toCol][toRow] == PieceIdentification.W_KING && getIdentification().isBlack()) {
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
