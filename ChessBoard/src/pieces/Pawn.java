package pieces;

import Global.Global;
import board.ChessBoard;
import javax.swing.*;

public class Pawn extends Piece{

    private boolean enPassantDangerFromLeft = false;// if this pawn can be taken using en passant from opponent pawn in the left
    private boolean enPassantDangerFromRight = false;// if this pawn can be taken using en passant from opponent pawn in the right
    private boolean enPassantAllowed = false; //if this pawn is allowed to en passant an opponent pawn
    private boolean check = false; // not decided how to implement check yet, this is just a placeholder

    public Pawn(Character chessCol, int chessRow, boolean white){
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white)
            setIdentification(PieceIdentification.W_PAWN);
        else
            setIdentification(PieceIdentification.B_PAWN);

        ChessBoard.insertPiece(chessCol,chessRow,this);

        validMoveSet = new boolean[6];
    }


    @Override
    public void moveCheck() {

        for (int i = 0; i<6; i++){ //resetting the valid moveset
            validMoveSet[i] = false;
        }
        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = ChessBoard.getBoard();

        //4 possible moves for pawn
        //direction is different for white and black so moveset has to be initiated differently
        if (getIdentification().isBlack()) {

            moveSet = new int[][]{
                    //one square forward, 2 square forward, left capture, right capture, en Passant to the left, en Passant to the right
                    {row + 1, col}, {row + 2, col}, {row + 1, col - 1}, {row + 1, col + 1}, {-1, -1}, {-1, -1}
            };
        } else {

            moveSet = new int[][]{
                    //one square forward, 2 square forward, left capture, right capture, en Passant to the left, en Passant to the right
                    {row - 1, col}, {row - 2, col}, {row - 1, col - 1}, {row - 1, col + 1}, {-1, -1}, {-1, -1}
            };

        }

        //crude way to check if general the moves exist on the board, en passant moves are calculated later in the code
        for (int i = 0; i < 4; i++) {
            if (moveSet[i][1] < 8 && moveSet[i][1] >= 0 && moveSet[i][0] < 8 && moveSet[i][0] >= 0) {
                validMoveSet[i] = true;
            }
        }

        //one square move logic
        if (validMoveSet[0]) {
            int toRow = moveSet[0][0];
            int toCol = moveSet[0][1];

            if (refBoard[toRow][toCol] != null) { //because the validMoveSet can be assumed to be filled with trues, we just flip it to false again if the move is blocked by another piece
                validMoveSet[0] = false;
                validMoveSet[1] = false;//2 square move also gets blocked
            }

        }

        // 2 square move logic
        if (validMoveSet[1] && validMoveSet[0]) { //for 2 square to be valid, 1 square move must be valid from the before if check
            int toRow = moveSet[1][0];
            int toCol = moveSet[1][1];

            if (isOnStartingRow()) { //uses a method to see if the pawn is in the starting row
                if (refBoard[toRow][toCol] != null) {
                    validMoveSet[1] = false; //flip the validMoveSet to false because it is as assumed the array is filled with trues
                }
            } else {
                validMoveSet[1] = false; //the 2 square move not valid if the pawn is not in the starting row
            }
        }

        //capturing a piece logic
        if (validMoveSet[2] || validMoveSet[3]) {
            int toRow2 = moveSet[2][0]; // capture to the left
            int toCol2 = moveSet[2][1];

            int toRow3 = moveSet[3][0]; // capture to the right
            int toCol3 = moveSet[3][1];

            if (refBoard[toRow2][toCol2] == null) { //if there is no piece is present in the left immediate diagonal pawn cannot move there
                validMoveSet[2] = false;
            } else {

                if (getIdentification().isWhite() == refBoard[toRow2][toCol2].getIdentification().isWhite()) { // must be an opponent piece
                    validMoveSet[2] = false;
                }
            }

            if (refBoard[toRow3][toCol3] == null) { //if there is no piece is present in the right immediate diagonal pawn cannot move there
                validMoveSet[3] = false;
            } else {

                if (getIdentification().isWhite() == refBoard[toRow3][toCol3].getIdentification().isWhite()) { // must be an opponent piece
                    validMoveSet[3] = false;
                }
            }

        }

        //en passant to the left logic
        if ((col - 1) >= 0 && col - 1 < 8){ //toColumn must be valid
            if (PieceIdentification.isPawn(refBoard[row][col - 1])) {//only a pawn can be taken using en passant

                Pawn p = (Pawn) refBoard[row][col - 1];


                    //checking if this pawn has the ability to en passant
                    //only allowed to en passant if this pawn does it immediately after the opponent moves a pawn 2 squares
                    //if not done on the immediate turn the privilege is withdrawn

                p.enPassantDangerCheck(); //check if the opponent pawn can be en passant'ed


                //for white and black the coordinate system is different because of directions
                if (getIdentification().isWhite()) {

                    if (p.enPassantDangerFromRight) {// we are to p's right, so this is the correct flag

                        moveSet[4][0] = row - 1; //white moves form 7 -> 0 so the row number reduces
                        moveSet[4][1] = chessColToIndex(p.getChessCol());// in en passant the piece moves to the same column as the opponent piece, same as capturing normally
                        validMoveSet[4] = true;
                        System.out.println("can enpassant to " + rowToChessRow(moveSet[4][0]) + colToChessCol(moveSet[4][1]));
                        System.out.println("valid: " + validMoveSet[4]);

                    }


                } else {

                    if (p.enPassantDangerFromRight) {
                        moveSet[4][0] = row + 1; //black moves form 0 -> 7 so the row number increases
                        moveSet[4][1] = chessColToIndex(p.getChessCol()); // in en passant the piece moves to the same column as the opponent piece, same as capturing normally
                        validMoveSet[4] = true;

                        System.out.println("can enpassant to " + rowToChessRow(moveSet[4][0]) + colToChessCol(moveSet[4][1]));
                        System.out.println("valid: " + validMoveSet[4]);
                    }

                }


            }

        }

        //en passant logic to the right
        if (col + 1 >= 0 && col + 1 < 8) {
            if (PieceIdentification.isPawn(refBoard[row][col + 1])) {
                Pawn p = (Pawn) refBoard[row][col + 1];
                p.enPassantDangerCheck();


                if (getIdentification().isWhite()) {

                    if (p.enPassantDangerFromLeft) {// we are to p's left, so this is the correct flag
                        moveSet[5][0] = row - 1; //white moves form 7 -> 0 so the row number reduces
                        moveSet[5][1] = chessColToIndex(p.getChessCol());// in en passant the piece moves to the same column as the opponent piece, same as capturing normally
                        validMoveSet[5] = true;
                        System.out.println("can enpassant to " + rowToChessRow(moveSet[5][0]) + colToChessCol(moveSet[5][1]));
                        System.out.println("valid: " + validMoveSet[5]);
                    }


                } else {

                    if (p.enPassantDangerFromLeft) {

                        moveSet[5][0] = row + 1; //black moves form 0 -> 7 so the row number increases
                        moveSet[5][1] = chessColToIndex(p.getChessCol());// in en passant the piece moves to the same column as the opponent piece, same as capturing normally
                        validMoveSet[5] = true;
                        System.out.println("can enpassant to " + rowToChessRow(moveSet[5][0]) + colToChessCol(moveSet[5][1]));
                        System.out.println("valid: " + validMoveSet[5]);
                    }

                }
            }
        }

        Global.print1D(validMoveSet);

    }

    @Override
    public void movePiece() {

    }

    @Override
    public String toString(){
        return "Pawn";
    }

    //a method used to check if the pawn is in the starting row
    public boolean isOnStartingRow(){

        if (getIdentification().isWhite() && chessRowToIndex(getChessRow()) == 6 ){
            return true;
        }
        if (getIdentification().isBlack() && chessRowToIndex(getChessRow()) == 1 ){
            return true;
        }

        return false;
    }

    // a method used to check if a pawn can be taken using en passant
    public boolean[] enPassantDangerCheck(){

        int dangerRow ; //the row where an en passant could happen, different for white and black pieces
        Piece[][] refBoard = ChessBoard.getBoard();
        int col = chessColToIndex(getChessCol());

        if(getIdentification().isWhite()){ // if white
            dangerRow = 4;

            if (col+1 >= 0 && col+1 <8){ // column must be valid
                if (PieceIdentification.isPawn(refBoard[dangerRow][col+1]) && refBoard[dangerRow][col+1].getIdentification().isBlack()){ //if there is another black pawn to the right
                    enPassantDangerFromRight = true;
                    System.out.println("this piece could be enPassant'ed next move : "+refBoard[dangerRow][col].getChessCol()+refBoard[dangerRow][col].getChessRow());
                    ((Pawn) refBoard[dangerRow][col+1]).enPassantAllowed = true; //gives said pawn en passant privileges

                }
            }

            if (col-1 >= 0 && col-1 <8){//column must be valid
                if (PieceIdentification.isPawn(refBoard[dangerRow][col-1]) && refBoard[dangerRow][col-1].getIdentification().isBlack()){//if there is another black pawn to the left
                    enPassantDangerFromLeft = true;
                    System.out.println("this piece could be enPassant'ed next move : "+refBoard[dangerRow][col].getChessCol()+refBoard[dangerRow][col].getChessRow());
                    ((Pawn) refBoard[dangerRow][col-1]).enPassantAllowed = true; //gives said pawn en passant privileges

                }
            }



        }  else { //if black
            dangerRow = 3;

            if (col+1 >= 0 && col+1 <8){//column must be valid
                if (PieceIdentification.isPawn(refBoard[dangerRow][col+1]) && refBoard[dangerRow][col+1].getIdentification().isWhite()){//if there is another white pawn to the right
                    enPassantDangerFromRight = true;
                    System.out.println("this piece could be enPassant'ed next move : "+refBoard[dangerRow][col].getChessCol()+refBoard[dangerRow][col].getChessRow());
                    ((Pawn) refBoard[dangerRow][col+1]).enPassantAllowed = true; //gives said pawn the en passant privileges

                }
            }

            if (col-1 >= 0 && col-1 <8){
                if (PieceIdentification.isPawn(refBoard[dangerRow][col-1]) && refBoard[dangerRow][col-1].getIdentification().isWhite()){//if there is another white pawn to the left
                    enPassantDangerFromLeft = true;
                    System.out.println("this piece could be enPassant'ed next move : "+refBoard[dangerRow][col].getChessCol()+refBoard[dangerRow][col].getChessRow());
                    ((Pawn) refBoard[dangerRow][col-1]).enPassantAllowed = true;//gives said pawn the en passant privileges

                }
            }
        }
        return new boolean[]{enPassantDangerFromLeft,enPassantDangerFromRight};
    }

    // a method used to promote a pawn
    public void promote(){

        int lastRow ;//last row differs for white and black
        boolean isWhite = false;//to check what color the pawn is
        Piece[][] refBoard = ChessBoard.getBoard();

        if(getIdentification().isWhite()){
            lastRow = 0;//for white last row is 0 as white moves 7 -> 0
            isWhite = true;
        }  else {
            lastRow = 7;//for black the last row is 7 as black moves 0 -> 7
        }


        for (int i = 0; i < 8; i++){ //checks the whole last row to see if there is a pawn present
            if(PieceIdentification.isPawn(refBoard[lastRow][i])){
                refBoard[lastRow][i] = null; //sets the pawn piece square to null

                String[] options = { //promotion options
                        "Knight",
                        "Bishop",
                        "Rook",
                        "Queen",
                };

                int choice = JOptionPane.showOptionDialog(//the message box to choose which promotion happens
                        null,
                        "Choose the promotion:",
                        "Promotion",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[3]//default is queen
                );

                switch (choice){ //the promotions
                    case 0 :
                        refBoard[lastRow][i] = new Knight(colToChessCol(i),rowToChessRow(lastRow),isWhite);
                        break;
                    case 1 :
                        refBoard[lastRow][i] = new Bishop(colToChessCol(i),rowToChessRow(lastRow),isWhite);
                        break;
                    case 2 :
                        refBoard[lastRow][i] = new Rook(colToChessCol(i),rowToChessRow(lastRow),isWhite);
                        break;
                    case 3 :
                        refBoard[lastRow][i] = new Queen(colToChessCol(i),rowToChessRow(lastRow),isWhite);
                        break;
                    default :
                        refBoard[lastRow][i] = new Queen(colToChessCol(i),rowToChessRow(lastRow),isWhite);
                }

                ChessBoard.setBoard(refBoard);//the board is updated

            }
        }



    }
    //getters
    public boolean getEnPassantAllowed(){
        return enPassantAllowed;
    }

    //a method used to release a pawn from en passant danger if the opponent doesn't use en passant in his immediate turn
    public void releaseEnPassantDanger(){
        enPassantDangerFromLeft = false;
        enPassantDangerFromRight = false;
    }

    //a method used to revoke en passant privileges from a pawn if the player doesn't use en passant in his immediate turn
    public void revokeEnPassantAllowed(){
        enPassantAllowed = false;
    }
}
