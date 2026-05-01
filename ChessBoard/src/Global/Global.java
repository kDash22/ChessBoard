package Global;

import board.ChessBoard;
import pieces.Piece;

import java.util.ArrayList;

public class Global {

    public static void print2D(Object[][] arr) {
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {

                if (arr[r][c] == null) {
                    System.out.print("null ");
                } else {
                    System.out.print(arr[r][c] + " ");
                }

            }
            System.out.println();
        }
    }

    public static void print1D(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                System.out.print("null ");
            } else {
                System.out.print(arr[i] + " ");
            }
        }
        System.out.println();
    }

    public static void print2D(boolean[][] arr) {
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                System.out.print(arr[r][c] + " ");
            }
            System.out.println();
        }
    }

    public static void print1D(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] ? "1 " : "0 ");
        }
        System.out.println();
    }

    public static void printMoveList(ArrayList<int[]> moveList){

        if (moveList.isEmpty()){
            System.out.println("There are no valid moves ! ");
            return;
        }

        for (int[] move : moveList) {
            char chessCol = Piece.colToChessCol(move[1]);
            int chessRow = Piece.rowToChessRow(move[0]);
            System.out.print(chessCol +" "+ chessRow + ", ");
        }
        System.out.println("number of moves : "+moveList.size());
    }

    public static void printValidMoveSet(boolean[] validMoveSet){
        for (boolean isValid : validMoveSet) {
            System.out.print(isValid ? "Valid, " : "Not Valid,");
        }
        System.out.println("number of moves : "+validMoveSet.length);
    }


    public static <T> void printArrayList(java.util.ArrayList<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
        System.out.println("items in List : "+list.size());
    }

    public static void printAllValidMoves(ChessBoard chessBoard){

        Piece[][] refBoard = chessBoard.getBoard();

        for (int i =0; i<8; i++){
            for (int j= 0; j<8; j++){
                if (refBoard[i][j]!=null){

                    if (refBoard[i][j].getIdentification().isWhite() != chessBoard.isWhiteToMove()) continue;

                    refBoard[i][j].moveCheck(chessBoard);
                    System.out.print(refBoard[i][j]);
                    Global.printMoveList(refBoard[i][j].getValidMoveList(chessBoard));
                }
            }
        }

    }

}


