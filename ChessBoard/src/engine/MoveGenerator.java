package engine;

import board.ChessBoard;
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

                if (p.getIdentification().isWhite() != chessBoard.isWhiteToMove()) continue;

                p.moveCheck(chessBoard);

                int[][] moveSet = p.getMoveSet(chessBoard);
                boolean[] validMoveSet = p.getValidMoveSet(chessBoard);

                if (validMoveSet == null || moveSet == null) continue;

                if (validMoveSet.length != moveSet.length) {
                    throw new IllegalArgumentException("The moveSet and validMoveSet arrays are not parallel !" +
                            "\nmoveSet length : "+moveSet.length+" validMoveSet length : "+validMoveSet.length+
                            "\nCrashed Piece type:"+p);
                }

                for (int i = 0; i < moveSet.length; i++){

                    if (!validMoveSet[i]) continue;

                    char fromChessCol = p.getChessCol();
                    int fromChessRow = p.getChessRow();

                    char toChessCol = Piece.colToChessCol(moveSet[i][1]);
                    int toChessRow = Piece.rowToChessRow(moveSet[i][0]);



                    Move m = new Move(fromChessCol,fromChessRow,toChessCol,toChessRow,p,refBoard[ moveSet[i][0] ][ moveSet[i][1] ]); //board is indexed with row-col format
                    moves.add(m);
                }


            }
        }
        return moves;


    }

    public int simulateMoves(ChessBoard chessBoard,int depth, boolean whiteToMove){

        if (depth==0) return 1;

        boolean originalTurnState = chessBoard.isWhiteToMove();//stores the original turn state
        chessBoard.setWhiteToMove(whiteToMove);//flips the turn

        List<Move> moves = generateMoves(chessBoard);
        int numPositions = 0;

        for (Move move : moves){
            makeMove(chessBoard,move);
            numPositions += simulateMoves(chessBoard,depth-1,!whiteToMove);
            undoMove(chessBoard,move);
        }

        chessBoard.setWhiteToMove(originalTurnState);

        return numPositions;
    }

    public void makeMove(ChessBoard chessBoard, Move move){
        Piece[][] refBoard = chessBoard.getBoard();

        /*
        if (refBoard[move.toRow][move.toCol]!= null){

            if (refBoard[move.toRow][move.toCol].getIdentification().isWhite()){
                List<Piece> capturedByBlack= chessBoard.getCapturedByBlack();
                capturedByBlack.add(refBoard[move.toRow][move.toCol]);
                chessBoard.setCapturedByBlack(capturedByBlack);
            } else {
                List<Piece> capturedByWhite = chessBoard.getCapturedByWhite();
                capturedByWhite.add(refBoard[move.toRow][move.toCol]);
                chessBoard.setCapturedByWhite(capturedByWhite);
            }
        }
        */
        refBoard[move.fromRow][move.fromCol] = null; //simulates a move
        refBoard[move.toRow][move.toCol] = move.movedPiece;
        move.movedPiece.updateCoords(Piece.colToChessCol(move.toCol), Piece.rowToChessRow(move.toRow));

        chessBoard.setBoard(refBoard);
        chessBoard.repaint();
    }

    public void undoMove(ChessBoard chessBoard, Move move){
        Piece[][] refBoard = chessBoard.getBoard();

        refBoard[move.toRow][move.toCol] = move.capturedPiece; //undoes a move
        if (move.capturedPiece != null)
            move.capturedPiece.updateCoords(Piece.colToChessCol(move.toCol), Piece.rowToChessRow(move.toRow));

        refBoard[move.fromRow][move.fromCol] = move.movedPiece;
        move.movedPiece.updateCoords(Piece.colToChessCol(move.fromCol), Piece.rowToChessRow(move.fromRow));

        chessBoard.setBoard(refBoard);
        chessBoard.repaint();
    }

    public static void runPerftUpToDepth(ChessBoard chessBoard,int maxDepth){
        //perft = position enumeration by depth

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
