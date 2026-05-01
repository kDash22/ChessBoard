package engine;

import pieces.Pawn;
import pieces.Piece;

public class Move {

    int fromCol, fromRow;
    int toCol, toRow;

    Piece movedPiece, capturedPiece;

    //this is to ensure when a king or a rook is moved, the castling option stays unaccessible
    boolean pieceHadMoved;

    //to enable en passant if a pawn is pushed 2 squares
    boolean isDoublePawnPush;

    public Move(char fromChessCol,int fromChessRow, char toChessCol,int toChessRow, Piece moved, Piece captured) {

        fromRow = Piece.chessRowToIndex(fromChessRow);
        fromCol = Piece.chessColToIndex(fromChessCol);
        toRow = Piece.chessRowToIndex(toChessRow);
        toCol = Piece.chessColToIndex(toChessCol);
        movedPiece = moved;
        capturedPiece = captured;

        pieceHadMoved = moved.hasMoved();

        isDoublePawnPush = (moved instanceof Pawn) && (Math.abs(toRow - fromRow) == 2);
    }

    public String toString(){
        return (/* "\n"+ */ movedPiece+" : "+ Piece.colToChessCol(fromCol)+Piece.rowToChessRow(fromRow)+
                " -> "+Piece.colToChessCol(toCol)+Piece.rowToChessRow(toRow));
    }
}
