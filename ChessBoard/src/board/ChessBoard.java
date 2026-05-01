package board;

import Global.Global;
import engine.MoveGenerator;
import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;



public class ChessBoard extends JPanel {

    private boolean isGameOver = false;

    private boolean immediateAction = false;//to check if the player has a special move in his immediate turn

    private boolean selection = false;
    private int selectedRow = -1; //to get the row of the selected piece
    private int selectedCol = -1; //to get the column of the selected piece

    private List<Piece> capturedByWhite = new ArrayList<>();
    private List<Piece> capturedByBlack= new ArrayList<>();

    private int selectedToRow = -1; //to get the row of the square that the piece is going to move to
    private int selectedToCol = -1; //to get the column of the square that the piece is going to move to

    private boolean rotating = false; //if board is rotating
    private final int TILE_SIZE = 80;
    private final static int pieceBarWidth = 240;
    private final static int pieceBarLength = 640;

    private boolean flipped = false;//board gui state, white to move is false
    private boolean whiteToMove = true;//white's turn to move


    private double angle = 0;
    private double targetAngle = 0;

    private Timer timer;

    private Image wPawn, wKnight, wBishop, wRook, wQueen, wKing,
            bPawn, bKnight, bBishop, bRook, bQueen, bKing;

    private Piece[][] board = new Piece[8][8]; //[row][col]

    public static final List<Character> COLUMN_LETTERS = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');

    public  Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public void insertPiece(Character chessCol, int chessRow, Piece piece) {
        int row = Piece.chessRowToIndex(chessRow);
        int col = Piece.chessColToIndex(chessCol);
        board[row][col] = piece;
        
    }


    private Image getPieceImage(Piece piece) {
        return switch (piece) {
            case Pawn pawn -> piece.getIdentification().isWhite() ? wPawn : bPawn;
            case Knight knight -> piece.getIdentification().isWhite() ? wKnight : bKnight;
            case Bishop bishop -> piece.getIdentification().isWhite() ? wBishop : bBishop;
            case Rook rook -> piece.getIdentification().isWhite() ? wRook : bRook;
            case Queen queen -> piece.getIdentification().isWhite() ? wQueen : bQueen;
            case King king -> piece.getIdentification().isWhite() ? wKing : bKing;
            case null, default -> null;
        };

    }

    public void loadPieces() {
        try {
            //white
            wPawn = new ImageIcon(getClass().getResource("/pieceImages/white/white-pawn.png")).getImage();
            wKnight = new ImageIcon(getClass().getResource("/pieceImages/white/white-knight.png")).getImage();
            wBishop = new ImageIcon(getClass().getResource("/pieceImages/white/white-bishop.png")).getImage();
            wRook = new ImageIcon(getClass().getResource("/pieceImages/white/white-rook.png")).getImage();
            wQueen = new ImageIcon(getClass().getResource("/pieceImages/white/white-queen.png")).getImage();
            wKing = new ImageIcon(getClass().getResource("/pieceImages/white/white-king.png")).getImage();

            //black
            bPawn = new ImageIcon(getClass().getResource("/pieceImages/black/black-pawn.png")).getImage();
            bKnight = new ImageIcon(getClass().getResource("/pieceImages/black/black-knight.png")).getImage();
            bBishop = new ImageIcon(getClass().getResource("/pieceImages/black/black-bishop.png")).getImage();
            bRook = new ImageIcon(getClass().getResource("/pieceImages/black/black-rook.png")).getImage();
            bQueen = new ImageIcon(getClass().getResource("/pieceImages/black/black-queen.png")).getImage();
            bKing = new ImageIcon(getClass().getResource("/pieceImages/black/black-king.png")).getImage();
        } catch (Exception e) {
            throw new RuntimeException("Image loading failed", e);
        }
    }

    public static Image flipPiece(Image piece) {
        if (piece != null) {
            int w = piece.getWidth(null);
            int h = piece.getHeight(null);

            BufferedImage flippedPiece = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = flippedPiece.createGraphics();

            g2d.drawImage(piece, w, h, -w, -h, null);
            g2d.dispose();

            return flippedPiece;
        }
        return null;
    }

    public ChessBoard() {

        makeNewBoard();
        loadPieces();
        setPreferredSize(new Dimension(8 * TILE_SIZE, 8 * TILE_SIZE));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (isGameOver) return; //stops mouse function if the game is over

                selectPiece(e);
                movePiece(e);


            }
        });
    }


    // animation-based flip
    private void flipBoard() {

        if (rotating)
            return;

        rotating = true; // rotation lock, blocks any new rotations

        flipped = !flipped;
        whiteToMove = !whiteToMove;

        targetAngle = flipped ? Math.PI : 0.0;

        timer = new Timer(25, e -> {

            double speed = 0.08;

            if (Math.abs(targetAngle - angle) < speed) {
                angle = targetAngle;
                timer.stop();
                rotating = false; //returns rotation lock
            } else {
                angle += Math.signum(targetAngle - angle) * speed;
            }

            repaint();
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Piece[][] refBoard = getBoard();


        int boardSize = 8 * TILE_SIZE;

        // center board inside panel
        int offsetX = (getWidth() - boardSize) / 2;
        int offsetY = (getHeight() - boardSize) / 2;

        // move origin to board position
        g2d.translate(offsetX, offsetY);

        // rotate around board center
        g2d.rotate(angle, boardSize / 2.0, boardSize / 2.0);

        //draw the chess board squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                if ((row + col) % 2 == 0)
                    g2d.setColor(Color.WHITE);
                else
                    g2d.setColor(Color.GRAY);

                /*if (row + col == 0)
                    g2d.setColor(Color.RED);

                 */

                g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        if (selectedRow != -1 && selectedCol != -1) {
            Piece selected = refBoard[selectedRow][selectedCol];
            if (selected != null) {
                highlightValidSquare(g2d, selected);

                g2d.setColor(new Color(148,224,224,90));
                g2d.fillRect(selectedCol * TILE_SIZE, selectedRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (!flipped) {
            //draw the pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = board[row][col];
                    Image pieceIcon = getPieceImage(piece);

                    if (pieceIcon != null) {

                        g2d.drawImage(
                                pieceIcon,
                                col * TILE_SIZE,
                                row * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE,
                                null
                        );
                    }
                }

            }
        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = board[row][col];
                    Image flippedPiece = flipPiece(getPieceImage(piece));

                    if (flippedPiece != null) {

                        g2d.drawImage(
                                flippedPiece,
                                col * TILE_SIZE,
                                row * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE,
                                null
                        );
                    }
                }

            }
        }
        highlightCheckedKing(g2d);

        //highlightValidSquare(g2d, new Knight('d',5,true));
        //highlightValidSquare(g2d, new Knight('d',4,false));
        //highlightValidSquare(g2d, new Rook('b',5,false));
        //highlightValidSquare(g2d,new Rook('c',7,true));

    }

    private void highlightValidSquare(Graphics2D g2d, Piece piece) {

        java.util.ArrayList<int[]> moveList = piece.getValidMoveList(this);

        for (int i = 0; i < moveList.size(); i++) {

            int[] square = moveList.get(i);
            int row = square[0];
            int col = square[1];

            // bounds check
            if (row < 0 || row >= 8 || col < 0 || col >= 8)
                continue;
            if (board[row][col] != null) {
                g2d.setColor(new Color(255, 0, 0, 60));
            } else {
                g2d.setColor(new Color(0, 255, 0, 60));
            }

            g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);



        }

    }

    public void selectPiece(MouseEvent e) {

        //to ensure the coordinate system works properly in case of a flip or a window resize
        int[] screenCoordinates = getScreenTileCoordinates(e);

        int[] boardCoordinates = toBoardCoordinates(screenCoordinates[0], screenCoordinates[1]);
        int row = boardCoordinates[0];
        int col = boardCoordinates[1];

        if (col < 0 || col > 7 || row < 0 || row > 7  ){
            return;
        }

        Piece[][] refBoard = getBoard();

        //  If a piece is already selected and destination is empty, the movePiece handles the next click
        if (selection && refBoard[row][col] == null) {
            return;
        }

        if (refBoard[row][col] != null) {

            if (refBoard[row][col].getIdentification().isWhite() && whiteToMove) {
                selection = true;
                selectedCol = col;
                selectedRow = row;
                repaint();
            }

            if (refBoard[row][col].getIdentification().isBlack() && !whiteToMove){
                selection = true;
                selectedCol = col;
                selectedRow = row;
                repaint();
            }
        } else {
            selection = false;
            selectedCol = -1;
            selectedRow = -1;
            repaint();
        }


    }

    private void movePiece(MouseEvent e){

        if (selection){
            //to ensure the coordinate system works properly in case of a flip or a window resize
            int[] screenCoordinates = getScreenTileCoordinates(e);

            int[] boardCoordinates = toBoardCoordinates(screenCoordinates[0], screenCoordinates[1]);
            int row = boardCoordinates[0];
            int col = boardCoordinates[1];

            if (col < 0 || col > 7 || row < 0 || row > 7  ){
                return;
            }
            selectedToRow = row;
            selectedToCol = col;

            Piece[][] refBoard = getBoard();
            Piece movingPiece = refBoard[selectedRow][selectedCol];

            java.util.ArrayList<int[]> moveList = movingPiece.getValidMoveList(this);


            for (int i = 0; i < moveList.size(); i++){

                int[] square = moveList.get(i);
                int r = square[0];
                int c = square[1];

                if (r == selectedToRow && c == selectedToCol){

                    // enPassantVulnerable was only cleared inside the immediateAction block, which only ran
                    // if opponent pawns were already adjacent during the double-step — leaving the flag stuck
                    // as true forever in all other cases. Clearing it here on every move guarantees the
                    // en passant window always expires after exactly one turn.

                    boolean movingIsWhite = movingPiece.getIdentification().isWhite();
                    for (int r2 = 0; r2 < 8; r2++) {
                        for (int c2 = 0; c2 < 8; c2++) {
                            Piece pp = refBoard[r2][c2];
                            if (pp instanceof Pawn && pp.getIdentification().isWhite() != movingIsWhite) {
                                ((Pawn) pp).setEnPassantVulnerable(false);
                            }
                        }
                    }


                    //en passant logic
                    if (immediateAction){//the player must make the en passant on his immediate turn

                        int enPassantRow ; //the row en passant happens differs for white and black

                        if (whiteToMove){
                           enPassantRow = 3;
                        } else {
                            enPassantRow = 4;
                        }

                        for (int j = 0; j<8 ; j++){ //checks the whole en passant row

                            if (PieceIdentification.isPawn(refBoard[enPassantRow][j])){//if a piece in that row is a pawn
                                Pawn p = (Pawn) refBoard[enPassantRow][j];

                                if (p.getEnPassantAllowed()){//if en passant allowed for a pawn
                                    p.revokeEnPassantAllowed();//revoked
                                }

                                p.releaseEnPassantDanger();//release the en passant danger from the endangered pawns
                                p.setEnPassantVulnerable(false);
                            }
                        }
                        immediateAction = false;//sets the special move check to false
                    }

                    Piece target = refBoard[selectedToRow][selectedToCol];//moves the piece

                    //if en passant happens, the logic for capturing the piece
                    if (PieceIdentification.isPawn(movingPiece)) {
                        boolean isSingleFileStep = Math.abs(selectedToCol - selectedCol) == 1; // en passant must move exactly one file
                        boolean isSingleRankStep = Math.abs(selectedToRow - selectedRow) == 1; // en passant must move exactly one rank
                        boolean isDiagonal = isSingleFileStep && isSingleRankStep; // enPassant move must be a single-step diagonal
                        boolean landingEmpty = refBoard[selectedToRow][selectedToCol] == null; //en passant'ing pawn must land on an empty square

                        if (isDiagonal && landingEmpty ) { // only true for en passant

                            Piece captured = refBoard[selectedRow][selectedToCol];
                            if (captured instanceof Pawn) {
                                if (captured.getIdentification().isWhite())
                                    capturedByBlack.add(captured);
                                else
                                    capturedByWhite.add(captured);

                                refBoard[selectedRow][selectedToCol] = null;
                            } else {
                                throw new IllegalArgumentException(" En Passant logic error in movePiece method ! ");

                            }
                        }
                    }



                    if (target != null) { //adding the captured pieces
                        if (target.getIdentification().isWhite()){
                            capturedByBlack.add(target);
                        } else {
                            capturedByWhite.add(target);
                        }
                    }



                    // Castling Logic Execution
                    if (movingPiece instanceof King && Math.abs(selectedCol - selectedToCol) == 2) {
                        int rookOriginalCol = (selectedToCol == 6) ? 7 : 0;
                        int rookTargetCol = (selectedToCol == 6) ? 5 : 3;

                        Piece rook = refBoard[selectedRow][rookOriginalCol];
                        if (rook instanceof Rook) {
                            rook.setChessCol(Piece.colToChessCol(rookTargetCol));
                            rook.setHasMoved(true);
                            refBoard[selectedRow][rookTargetCol] = rook;
                            refBoard[selectedRow][rookOriginalCol] = null;
                        }
                    }

                    movingPiece.setChessCol(Piece.colToChessCol(selectedToCol));//updates the piece column
                    movingPiece.setChessRow(Piece.rowToChessRow(selectedToRow));//updates the piece row
                    movingPiece.setHasMoved(true);

                    refBoard[selectedToRow][selectedToCol] = movingPiece;//updates the location of the moves piece
                    refBoard[selectedRow][selectedCol] = null;

                    setBoard(refBoard);//updates the board array

                    //if a pawn is getting promoted check
                    if (PieceIdentification.isPawn(movingPiece)  ){
                        Pawn p = (Pawn) movingPiece;
                        p.promote(this);//automatically sets the board in the method

                        //checks if a pawn can be in en passant danger
                        if ((p.getIdentification().isWhite() && selectedToRow == selectedRow-2)
                                || (p.getIdentification().isBlack() && selectedToRow == selectedRow+2)){

                            p.setEnPassantVulnerable(true); //flag set only on actual double-step

                            boolean[] enPassantDanger = p.enPassantDangerCheck(this); //checking if there is opponent pawns that can jump to the oppotunity
                            if (enPassantDanger[0] || enPassantDanger[1]){
                                immediateAction = true;//sets the special move check for next turn to be true
                            }
                        }
                    }


                    selectedRow = selectedCol = selectedToRow = selectedToCol = -1;//wipes the variables
                    selection = false;//wipes the selection


                    repaint();//board refresh
                    flipBoard();//flips board after a successful move
                    checkGameOver(whiteToMove);
                    break;
                }

            }





        }
        /*
        System.out.println("\nflipped : "+flipped);
        System.out.println("is white to move : "+isWhiteToMove()); */


    }

    private int[] getScreenTileCoordinates(MouseEvent event){
        //this ensures the coordinate system works in case of a window resize
        int boardSize = 8 * TILE_SIZE;

        int offsetX = (getWidth() - boardSize) / 2;
        int offsetY = (getHeight() - boardSize) / 2;

        int adjustedX = event.getX() - offsetX;
        int adjustedY = event.getY() - offsetY;

        // Reject any mouse click that falls outside the visual board area.
        // Without this, negative coordinates (or overflow past board size)
        // can be incorrectly mapped to valid tiles due to integer division behavior.
        if (adjustedX < 0 || adjustedY < 0 || adjustedX >= boardSize || adjustedY >= boardSize) {
            return new int[]{-1, -1};
        }

        // Convert pixel coordinates to board indices.
        // Math.floorDiv is used instead of normal division to ensure correct handling
        // of negative values (it rounds toward negative infinity, not toward zero).
        // This prevents misclassification of out-of-bounds clicks as tile (0,0).
        int screenCol = Math.floorDiv(adjustedX, TILE_SIZE);
        int screenRow = Math.floorDiv(adjustedY, TILE_SIZE);

        return new int[]{screenRow,screenCol};

    }



    // Translates raw screen tile coordinates to board array indices,
    // accounting for the flipped state. The board array itself never changes.
    private int[] toBoardCoordinates(int screenRow, int screenCol) {
        if (flipped) {
            return new int[]{ 7 - screenRow, 7 - screenCol };
        }
        return new int[]{ screenRow, screenCol };
    }

    // a method which makes a new board with all the pieces
    public void makeNewBoard(){

        board = new Piece[8][8];
 
        // ----- White pieces -----
        insertPiece('a', 1, new Rook('a', 1, true, this));
        insertPiece('b', 1, new Knight('b', 1, true, this));
        insertPiece('c', 1, new Bishop('c', 1, true, this));
        insertPiece('d', 1, new Queen('d', 1, true, this));
        insertPiece('e', 1, new King('e', 1, true, this));
        insertPiece('f', 1, new Bishop('f', 1, true, this));
        insertPiece('g', 1, new Knight('g', 1, true, this));
        insertPiece('h', 1, new Rook('h', 1, true, this));
 
        insertPiece('a', 2, new Pawn('a', 2, true, this));
        insertPiece('b', 2, new Pawn('b', 2, true, this));
        insertPiece('c', 2, new Pawn('c', 2, true, this));
        insertPiece('d', 2, new Pawn('d', 2, true, this));
        insertPiece('e', 2, new Pawn('e', 2, true, this));
        insertPiece('f', 2, new Pawn('f', 2, true, this));
        insertPiece('g', 2, new Pawn('g', 2, true, this));
        insertPiece('h', 2, new Pawn('h', 2, true, this));
 
        // ----- Black pieces -----
        insertPiece('a', 8, new Rook('a', 8, false, this));
        insertPiece('b', 8, new Knight('b', 8, false, this));
        insertPiece('c', 8, new Bishop('c', 8, false, this));
        insertPiece('d', 8, new Queen('d', 8, false, this));
        insertPiece('e', 8, new King('e', 8, false, this));
        insertPiece('f', 8, new Bishop('f', 8, false, this));
        insertPiece('g', 8, new Knight('g', 8, false, this));
        insertPiece('h', 8, new Rook('h', 8, false, this));
 
        insertPiece('a', 7, new Pawn('a', 7, false, this));
        insertPiece('b', 7, new Pawn('b', 7, false, this));
        insertPiece('c', 7, new Pawn('c', 7, false, this));
        insertPiece('d', 7, new Pawn('d', 7, false, this));
        insertPiece('e', 7, new Pawn('e', 7, false, this));
        insertPiece('f', 7, new Pawn('f', 7, false, this));
        insertPiece('g', 7, new Pawn('g', 7, false, this));
        insertPiece('h', 7, new Pawn('h', 7, false, this));


    }
    // Finds the current position of the King of the specified color
    public int[] findKing(boolean white) {

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p instanceof King && p.getIdentification().isWhite() == white) {
                    return new int[]{r, c};
                }
            }
        }
        return null; // Should never happen in a real game
    }

    // Checks if a square is under attack by any piece of the specified color
    public boolean isSquareAttacked(int row, int col, boolean attackedByWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getIdentification().isWhite() == attackedByWhite) {
                    // Pawns capture differently than they move
                    if (p instanceof Pawn) {
                        int pRow = Piece.chessRowToIndex(p.getChessRow());
                        int pCol = Piece.chessColToIndex(p.getChessCol());
                        int direction = attackedByWhite ? -1 : 1;
                        if (pRow + direction == row && Math.abs(pCol - col) == 1) {
                            return true;
                        }
                    } else if (p instanceof King) {
                        // King proximity check to avoid recursive calls
                        int pRow = Piece.chessRowToIndex(p.getChessRow());
                        int pCol = Piece.chessColToIndex(p.getChessCol());
                        if (Math.abs(pRow - row) <= 1 && Math.abs(pCol - col) <= 1) {
                            return true;
                        }
                    } else {

                        ArrayList<int[]> moveSet = p.getValidMoveListRaw(this);

                        if (moveSet == null) continue;



                        if (!moveSet.isEmpty()){
                            for (int i = 0; i < moveSet.size(); i++) {

                                int[] square = moveSet.get(i);

                                if (square == null) continue;;

                                // NEW: Guard against malformed arrays and print the culprit
                                if (square.length < 2) {
                                    System.out.println("CRITICAL ERROR: Array too short from piece at " + p.getChessCol() + p.getChessRow());
                                    continue;
                                }

                                int r1 = square[0];
                                int c1 = square[1];

                                if (r1 == row && c1 == col) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isKingInCheck(boolean white) {
        int[] kingPos = findKing(white);
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos[0], kingPos[1], !white);
    }

    private void highlightCheckedKing(Graphics2D g2d){
        int row, col;
        if (isKingInCheck(true)){
            int[] kingPos = findKing(true);
            row = kingPos[0];
            col = kingPos[1];

            g2d.setColor(new Color(255,0,0,180));
            g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        if (isKingInCheck(false)){
            int[] kingPos = findKing(false);
            row = kingPos[0];
            col = kingPos[1];

            g2d.setColor(new Color(255,0,0,180));
            g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }


    }

    // Checks if the specified color player has any legal moves left
    public boolean hasLegalMoves(boolean white) {
        Piece[][] refBoard = getBoard();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                Piece p = refBoard[r][c];
                if (p != null && p.getIdentification().isWhite() == white) {

                    java.util.ArrayList<int []> moves = p.getValidMoveList(this);
                    Global.printMoveList(moves);
                    if (!moves.isEmpty()) return true;
                }
            }
        }
        return false;
    }

    // Checks if the game is over and displays a message if so
    public void checkGameOver(boolean whiteTurn) {

        String color = whiteTurn ? "White" : "Black";
        System.out.println("Checking game over state for " + color + "...");
        
        if (!hasLegalMoves(whiteTurn)) {
            System.out.println("No legal moves found for " + color);

            if (isKingInCheck(whiteTurn)) {

                String winner = whiteTurn ? "Black" : "White";
                System.out.println("CHECKMATE! " + winner + " wins!");
                JOptionPane.showMessageDialog(this, "CHECKMATE! " + winner + " wins!");

                isGameOver = true;
            } else {
                System.out.println("STALEMATE!");
                JOptionPane.showMessageDialog(this, "STALEMATE! It's a draw.");
                isGameOver = true;
            }
        } else if (isKingInCheck(whiteTurn)) {
             System.out.println(color + " is in Check!");
        }
    }

    public List<Piece> getCapturedByBlack() {
        return capturedByBlack;
    }

    public List<Piece> getCapturedByWhite() {
        return capturedByWhite;
    }

    public void setCapturedByBlack(List<Piece> capturedByBlack1) {
        capturedByBlack = capturedByBlack1;
    }

    public void setCapturedByWhite(List<Piece> capturedByWhite1) {
        capturedByWhite = capturedByWhite1;
    }

    public boolean isWhiteToMove(){
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove){
        this.whiteToMove = whiteToMove;
    }

    public static void main (String[]args){

        JFrame frame = new JFrame("Chess Board");
        frame.setLayout(new BorderLayout());

        ChessBoard chessBoard = new ChessBoard();

        frame.add(chessBoard, BorderLayout.CENTER);
        frame.setResizable(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //Global.printAllValidMoves(chessBoard);

        MoveGenerator.runPerftUpToDepth(chessBoard,6);

        /*
        MoveGenerator mg = new MoveGenerator();
        System.out.println("Number of positions : "+mg.simulateMoves(chessBoard,2,true));

        Global.printAllValidMoves(chessBoard);

        */

        /*

        Global.printArrayList(mg.generateMoves(chessBoard,true));

        System.out.println("----------------------------------------------------------------");
        Global.printArrayList(mg.generateMoves(chessBoard,false));

        Global.printMoveList(getBoard()[7][4].getValidMoveList());
        Global.printValidMoveSet(getBoard()[7][4].getValidMoveSet());

         */



    }


}

