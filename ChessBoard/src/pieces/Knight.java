package pieces;

import Global.Global;
import board.ChessBoard;

public class Knight extends Piece{

    private boolean check = false;//not decided how to implement checking yet this is just a placeholder
    private static final int PIECE_VALUE = 3;

    public Knight(Character chessCol, int chessRow, boolean white){
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white){
            setIdentification(PieceIdentification.W_KNIGHT);
        } else {
            setIdentification(PieceIdentification.B_KNIGHT);
        }
        ChessBoard.insertPiece(chessCol, chessRow, this);

    }

    @Override
    public void moveCheck() {
        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = ChessBoard.getBoard();

        int count = 0;//used to get valid move count
        //8 possible moves for a knight
        int[][] tempMoveSet = new int[][]{
                {row + 2, col + 1}, {row + 2, col - 1},
                {row - 2, col + 1}, {row - 2, col - 1},
                {row + 1, col + 2}, {row + 1, col - 2},
                {row - 1, col + 2}, {row - 1, col - 2}
        };
        boolean[] tempValidMoveSet = new boolean[8];

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

            int toCol = tempMoveSet[i][1];
            int toRow = tempMoveSet[i][0];

            if (toCol < 8 && toCol >=0 && toRow < 8 && toRow >= 0 ){

                //if empty square
                if (refBoard[toRow][toCol] == null){
                    tempValidMoveSet[i] = true;
                    count++;
                    continue;
                }
                //if it is an enemy piece, but if it is the enemy king the move is not allowed as king cannot be taken
                if (refBoard[toRow][toCol].getIdentification().isWhite() != getIdentification().isWhite()) {

                    if (refBoard[toRow][toCol].getIdentification() == PieceIdentification.W_KING ||
                            refBoard[toRow][toCol].getIdentification() == PieceIdentification.B_KING) {
                        check = true;
                        tempValidMoveSet[i] = true; // Essential for check detection
                        count++;
                    } else {
                        tempValidMoveSet[i] = true;
                        count++;
                    }
                } else {
                    tempValidMoveSet[i] = false; // Blocked by own piece
                }
            }

        }
        moveSet = new int[count][2];
        validMoveSet = new boolean[count];

        System.out.println("\nKnight valid move count : "+count+"\n");
        int j = 0;

        for (int i = 0; i < 8; i++) {
            if (tempValidMoveSet[i]) {
                moveSet[j][0] = tempMoveSet[i][0];
                moveSet[j][1] = tempMoveSet[i][1];
                validMoveSet[j] = true;
                j++;
            }
        }


        System.out.print("Knight : ");
        Global.print1D(tempValidMoveSet);
    }

    @Override
    public void movePiece() {

    }

    @Override
    public String toString(){
        return "Knight";
    }

}
