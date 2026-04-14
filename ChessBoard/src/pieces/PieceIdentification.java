package pieces;

public enum PieceIdentification {

    B_PAWN(false), W_PAWN(true),
    B_KNIGHT(false), W_KNIGHT(true),
    B_BISHOP(false), W_BISHOP(true),
    B_ROOK(false), W_ROOK(true),
    B_QUEEN(false), W_QUEEN(true),
    B_KING(false), W_KING(true);

    private final boolean isWhite;

    PieceIdentification(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isBlack() {
        return !isWhite;
    }
}
