package pieces;

import Global.Global;
import board.ChessBoard;

public class Knight extends Piece{

    private boolean check = false; //not decided how to implement checking yet this is just a placeholder

    public Knight(Character col, int chessRow, boolean white){
        setCol(col);
        setRow(chessRow);

        if (white){
            setIdentification(PieceIdentification.W_KNIGHT);
        } else {
            setIdentification(PieceIdentification.B_KNIGHT);
        }
        ChessBoard.insertPiece(turnColToIndex(col), chessRow, this);

        validMoveSet = new boolean[8];
    }

    @Override
    public void moveCheck() {
        int col = turnColToIndex(getCol());
        int row = Global.chessRowtoIndex(getChessRow());

        Piece[][] refBoard = ChessBoard.getBoard();

        //8 possible moves for a knight
        moveSet = new int[][]{
                {row + 2, col + 1}, {row + 2, col - 1},
                {row - 2, col + 1}, {row - 2, col - 1},
                {row + 1, col + 2}, {row + 1, col - 2},
                {row - 1, col + 2}, {row - 1, col - 2}
        };

        //8 possible moves for a knight
        int[] move1 = {row + 2, col + 1};
        int[] move2 = {row + 2, col - 1};

        int[] move3 = {row - 2, col + 1};
        int[] move4 = {row - 2, col - 1};

        int[] move5 = {row + 1, col + 2};
        int[] move6 = {row + 1, col - 2};

        int[] move7 = {row - 1, col + 2};
        int[] move8 = {row - 1, col - 2};

        for(int i = 0 ; i < 8 ; i++  ){

            int toCol = moveSet[i][1];
            int toRow = moveSet[i][0];

            if (toCol < 8 && toCol >=0 && toRow < 8 && toRow >= 0 ){

                //if empty square
                if (refBoard[toRow][toCol] == null){
                    validMoveSet[i] = true;
                    continue;
                }
                //if piece colours are not the same, if it is a king of the different it is not allowed
                if(refBoard[toRow][toCol].getIdentification().isWhite() && getIdentification().isBlack() &&
                        refBoard[toRow][toCol].getIdentification() != PieceIdentification.B_KING && refBoard[toRow][toCol].getIdentification() != PieceIdentification.W_KING){

                    validMoveSet[i] = true;
                    continue;
                }
                if(refBoard[toRow][toCol].getIdentification().isBlack() && getIdentification().isWhite() &&
                        refBoard[toRow][toCol].getIdentification() != PieceIdentification.B_KING && refBoard[toRow][toCol].getIdentification() != PieceIdentification.W_KING){

                    validMoveSet[i] = true;
                    continue;
                }
                //if it is a king of a different team
                if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING && getIdentification().isWhite()) {
                    check = true;
                    validMoveSet[i] = false; // special case
                    continue;
                }
                if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING && getIdentification().isBlack()) {
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
