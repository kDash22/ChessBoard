package pieces;

import java.util.ArrayList;

import board.ChessBoard;

public class King extends Piece {

    public King(Character chessCol, int chessRow, boolean white,ChessBoard chessBoard) {
        setChessCol(chessCol);
        setChessRow(chessRow);

        if (white) {
            setIdentification(PieceIdentification.W_KING);
        } else {
            setIdentification(PieceIdentification.B_KING);
        }
        chessBoard.insertPiece(chessCol, chessRow, this);
    }

    @Override
    public void moveCheck(ChessBoard chessBoard) {

        // ALWAYS clear the list first to prevent duplicating old moves
        if (validMoveList != null) {
            validMoveList.clear();
        }


        int col = chessColToIndex(getChessCol());
        int row = Piece.chessRowToIndex(getChessRow());

        Piece[][] refBoard = chessBoard.getBoard();

        // A King can have up to 8 moves + 2 for castling
        // All 8 directions
        int[][] directions = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, // Straight
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } // Diagonal
        };

        for (int[] direction : directions) {
            int toRow = row + direction[0];
            int toCol = col + direction[1];

            // Check if the square is available
            if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
                Piece targetPiece = refBoard[toRow][toCol];

                if (targetPiece != null && targetPiece.getIdentification().isWhite() == getIdentification().isWhite()) {
                    continue; 
                }

                if (targetPiece == null) {
                    // Empty square
                    validMoveList.add(new int[]{toRow,toCol});

                } else if (targetPiece.getIdentification().isWhite() != getIdentification().isWhite()) {
                    // Enemy piece 
                    validMoveList.add(new int[]{toRow,toCol});
                } 
                

                // King Proximity Rule
                // Check if adjacent squares contain an enemy King
                boolean remove = false;
                boolean tooCloseToEnemyKing = false;

                outer:
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {

                        int adjRow = toRow + dr;
                        int adjCol = toCol + dc;

                        if (adjRow < 0 || adjRow >= 8 || adjCol < 0 || adjCol >= 8) continue;

                        Piece adjPiece = refBoard[adjRow][adjCol];

                        if (adjPiece instanceof King
                                && adjPiece.getIdentification().isWhite() != getIdentification().isWhite()) {

                            remove = true;
                            break outer;// no need to check further once we found the enemy King
                        }

                    }
                        
                }
                if (!tooCloseToEnemyKing) {
                    validMoveList.add(new int[]{toRow, toCol});
                }
            
            }

            // --- Castling Logic ---
            if (!this.hasMoved() && !chessBoard.isKingInCheck(getIdentification().isWhite())) {

                // King Side Castling
                if (refBoard[row][7] instanceof Rook rook && !rook.hasMoved()) {
                    if (refBoard[row][5] == null && refBoard[row][6] == null) {
                        validMoveList.add(new int[]{row, 6});
                    }
                }
                // Queen Side Castling
                if (refBoard[row][0] instanceof Rook rook && !rook.hasMoved()) {
                    if (refBoard[row][1] == null && refBoard[row][2] == null && refBoard[row][3] == null) {
                        validMoveList.add(new int[]{row, 2});
                    }
                }
            }
        }

    
    }

    @Override
    public void movePiece() {
        // Implementation for special moves like castling can go here
    }

    @Override
    public String toString(){
        if (getIdentification().isWhite())
            return " wK ";
        else
            return " bK ";


    }
}
