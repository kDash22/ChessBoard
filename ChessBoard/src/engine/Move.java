package engine;

import Global.Global;
import board.ChessBoard;
import pieces.Piece;
import pieces.PieceIdentification;

import java.util.ArrayList;

public class Move {

    int fromCol, fromRow;
    int toCol, toRow;

    Piece movedPiece, capturedPiece;

    public Move(char fromChessCol,int fromChessRow, char toChessCol,int toChessRow, Piece moved, Piece captured) {

        fromRow = Piece.chessRowToIndex(fromChessRow);
        fromCol = Piece.chessColToIndex(fromChessCol);
        toRow = Piece.chessRowToIndex(toChessRow);
        toCol = Piece.chessColToIndex(toChessCol);
        movedPiece = moved;
        capturedPiece = captured;
    }

    public String toString(){
        return (/* "\n"+ */ movedPiece+" : "+ Piece.colToChessCol(fromCol)+Piece.rowToChessRow(fromRow)+
                " -> "+Piece.colToChessCol(toCol)+Piece.rowToChessRow(toRow));
    }
}
