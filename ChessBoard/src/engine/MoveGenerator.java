package engine;

import board.ChessBoard;
import pieces.King;
import pieces.Pawn;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MoveGenerator {

    boolean originalTurnState ;

    public ArrayList<Move> generateMoves(ChessBoard chessBoard){
        ArrayList<Move> moves = new ArrayList<>();
        Piece[][] refBoard = chessBoard.getBoard();

        for (int row = 0; row<8; row++){

            for (int col = 0; col<8; col++){

                Piece p = refBoard[row][col];

                if (p == null) continue;

                //skips pieces that doesn't belong to the moving side
                if (p.getIdentification().isWhite() != chessBoard.isWhiteToMove()) continue;

                p.moveCheck(chessBoard);

                ArrayList<int[]> moveSet = p.getValidMoveList(chessBoard);

                /*
                if (validMoveSet == null || moveSet == null) continue;

                if (validMoveSet.length != moveSet.length) {
                    throw new IllegalArgumentException("The moveSet and validMoveSet arrays are not parallel !" +
                            "\nmoveSet length : "+moveSet.length+" validMoveSet length : "+validMoveSet.length+
                            "\nCrashed Piece type:"+p);
                }


                 */
                for (int i = 0; i < moveSet.size(); i++){

                    //if (!validMoveSet[i]) continue;
                    int[] square = moveSet.get(i);
                    int r = square[0];
                    int c = square[1];

                    char fromChessCol = p.getChessCol();
                    int fromChessRow = p.getChessRow();

                    char toChessCol = Piece.colToChessCol(c);
                    int toChessRow = Piece.rowToChessRow(r);

                    Piece capturedPiece = refBoard[r][c];

                    if (p instanceof Pawn && c != col && capturedPiece == null) {
                        capturedPiece = refBoard[row][c];
                    }

                    //board is indexed [row][col]
                    Move m = new Move(fromChessCol,fromChessRow,toChessCol,toChessRow,p,capturedPiece); //board is indexed with row-col format

                    //if the pawn was pushed 2 squares
                    if (p instanceof Pawn && Math.abs(r - row) == 2) {
                        m.isDoublePawnPush = true;
                    }

                    moves.add(m);
                }


            }
        }
        return moves;


    }

    // simulateMoves() recursively counts the number of leaf positions reachable
    // at the given depth (standard perft logic).
    public int simulateMoves(ChessBoard chessBoard,int depth, boolean whiteToMove){

        if (depth==0) return 1;

        //saves the board's turn state so we can restore it later
        boolean originalTurnState = chessBoard.isWhiteToMove();//stores the original turn state
        chessBoard.setWhiteToMove(whiteToMove);//flips the turn

        List<Move> moves = generateMoves(chessBoard);
        int numPositions = 0;

        for (Move move : moves){
            makeMove(chessBoard,move);

            if (chessBoard.isKingInCheck(whiteToMove)) {
                undoMove(chessBoard, move);
                continue;
            }

            numPositions += simulateMoves(chessBoard,depth-1,!whiteToMove);
            undoMove(chessBoard,move);
        }

        //restore the turn state
        chessBoard.setWhiteToMove(originalTurnState);

        return numPositions;
    }

    //applies the move to the simulation purposes
    public void makeMove(ChessBoard chessBoard, Move move){
        Piece[][] refBoard = chessBoard.getBoard();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece pp = refBoard[r][c];
                if (pp instanceof Pawn && pp.getIdentification().isWhite() != chessBoard.isWhiteToMove()) {
                    Pawn pawn = (Pawn) pp;
                    if (pawn.isEnPassantVulnerable()) {
                        pawn.setEnPassantVulnerable(false);
                        // Save this pawn to our move object so we can restore it during undoMove
                        move.resetEnPassantPawns.add(pawn);
                    }
                }
            }
        }

        refBoard[move.fromRow][move.fromCol] = null; //simulates a move
        refBoard[move.toRow][move.toCol] = move.movedPiece;

        //keep the pieces internal coordinates in sync with the board
        move.movedPiece.updateCoords(Piece.colToChessCol(move.toCol), Piece.rowToChessRow(move.toRow));

        //stops the calculation of false castling moves
        move.movedPiece.setHasMoved(true);

        //enPassant move logic, the captured piece must be cleared correctly
        if (move.movedPiece instanceof Pawn && move.fromCol != move.toCol && refBoard[move.fromRow][move.toCol] == move.capturedPiece) {
            refBoard[move.fromRow][move.toCol] = null;
        }

        //castling logic, the rook must move in tandem with the king
        if (move.movedPiece instanceof King && Math.abs(move.toCol-move.fromCol) == 2){
            boolean isKingside = move.toCol > move.fromCol;
            int rookFromCol = isKingside ? 7 : 0;
            int rookToCol = isKingside ? move.toCol - 1 : move.toCol + 1;
            Piece rook = refBoard[move.fromRow][rookFromCol];

            refBoard[move.fromRow][rookFromCol] = null; // Clears original square
            refBoard[move.fromRow][rookToCol] = rook;   // Places on new square
            if (rook != null) {
                rook.updateCoords(Piece.colToChessCol(rookToCol), Piece.rowToChessRow(move.fromRow));
                rook.setHasMoved(true); // Must be true when making the move
            }
        }

        //the en passant vulnerability must be set for it to be available in the simulation tree
        if (move.isDoublePawnPush){
            ((Pawn)move.movedPiece).setEnPassantVulnerable(true);

        }

        chessBoard.setBoard(refBoard);
        //chessBoard.repaint();
    }

    public void undoMove(ChessBoard chessBoard, Move move){
        Piece[][] refBoard = chessBoard.getBoard();

        //put the moved piece back at its original square
        refBoard[move.fromRow][move.fromCol] = move.movedPiece;
        move.movedPiece.updateCoords(
                Piece.colToChessCol(move.fromCol),
                Piece.rowToChessRow(move.fromRow));

        //put the captured piece back correctly (Handles En Passant geometry)
        if (move.movedPiece instanceof Pawn && move.fromCol != move.toCol &&
                move.capturedPiece != null ) {


            int capRow = Piece.chessRowToIndex(move.capturedPiece.getChessRow());

            if (capRow != move.toRow) { // Confirmed En Passant
                refBoard[move.toRow][move.toCol] = null;
                refBoard[move.fromRow][move.toCol] = move.capturedPiece;
            } else { // Standard diagonal capture
                refBoard[move.toRow][move.toCol] = move.capturedPiece;
            }

        } else {
            refBoard[move.toRow][move.toCol] = move.capturedPiece; // Standard capture restore
        }

        if (move.capturedPiece != null) {
            move.capturedPiece.updateCoords(
                    move.capturedPiece.getChessCol(),
                    move.capturedPiece.getChessRow());
        }

        //UNDO CASTLING (Missing from your code)
        if (move.movedPiece instanceof King && Math.abs(move.toCol - move.fromCol) == 2) {
            boolean isKingside = move.toCol > move.fromCol;
            int rookFromCol = isKingside ? 7 : 0;
            int rookToCol = isKingside ? move.toCol - 1 : move.toCol + 1;
            Piece rook = refBoard[move.fromRow][rookToCol];

            refBoard[move.fromRow][rookToCol] = null;
            refBoard[move.fromRow][rookFromCol] = rook;
            if (rook != null) {
                rook.updateCoords(Piece.colToChessCol(rookFromCol), Piece.rowToChessRow(move.fromRow));
                rook.setHasMoved(false);
            }
        }

        //restore the original hasMoved State using the hadMoved state stored in the Move class
        //restores castling rights, after a move is made and undone in the simulation tree
        move.movedPiece.setHasMoved(move.pieceHadMoved);

        //resets the en passant vulnerability to the original state
        if (move.isDoublePawnPush){
            ((Pawn) move.movedPiece).setEnPassantVulnerable(false);
        }

        for (Piece p : move.resetEnPassantPawns) {
            ((Pawn) p).setEnPassantVulnerable(true);
        }

        chessBoard.setBoard(refBoard);
        //chessBoard.repaint();
    }

    //convenient method to print perft(performance test) results
    //from depth 1 up to max depth
    public static void runPerftUpToDepth(ChessBoard chessBoard,int maxDepth){
        //position enumeration by depth

        MoveGenerator mg = new MoveGenerator();

        for (int i = 1; i<=maxDepth; i++){

            long start = System.nanoTime();//to measure the amount of time it to for a simulation

            long numPositions = mg.simulateMoves(chessBoard,i, chessBoard.isWhiteToMove());

            long end = System.nanoTime();
            long elapsedTimeNano = end - start;
            long elapsedTimeMilli = TimeUnit.NANOSECONDS.toMillis(elapsedTimeNano);


            System.out.println("Depth : "+i+"   Number of positions : "+numPositions+"  Time(ms) : "+elapsedTimeMilli);


        }

    }
}
