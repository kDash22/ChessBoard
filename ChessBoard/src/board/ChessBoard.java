package board;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;



public class ChessBoard extends JPanel {

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

    private boolean flipped = false;

    private double angle = 0;
    private double targetAngle = 0;

    private Timer timer;

    private Image wPawn, wKnight, wBishop, wRook, wQueen, wKing,
            bPawn, bKnight, bBishop, bRook, bQueen, bKing;

    private static Piece[][] board = new Piece[8][8]; //[row][col]

    public static final List<Character> COLUMN_LETTERS = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');

    public static Piece[][] getBoard() {
        return board;
    }

    public static void setBoard(Piece[][] board) {
        ChessBoard.board = board;
    }

    public static void insertPiece(Character chessCol, int chessRow, Piece piece) {
        int row = Piece.chessRowToIndex(chessRow);
        int col = Piece.chessColToIndex(chessCol);
        Piece[][] board = getBoard();
        board[row][col] = piece;
        setBoard(board);
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

            BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = flipped.createGraphics();

            g2d.drawImage(piece, w, h, -w, -h, null);
            g2d.dispose();

            return flipped;
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

                if (row + col == 0)
                    g2d.setColor(Color.RED);

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
        //highlightValidSquare(g2d, new Knight('d',5,true));
        //highlightValidSquare(g2d, new Knight('d',4,false));
        //highlightValidSquare(g2d, new Rook('b',5,false));
        //highlightValidSquare(g2d,new Rook('c',7,true));

    }

    private void highlightValidSquare(Graphics2D g2d, Piece piece) {

        piece.moveCheck();

        boolean[] validMoveSet = piece.getValidMoveSet();
        int[][] moveSet = piece.getMoveSet();

        for (int i = 0; i < moveSet.length; i++) {

            if (validMoveSet[i]) {
                int row = moveSet[i][0];
                int col = moveSet[i][1];

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

            if (refBoard[row][col].getIdentification().isWhite() && !flipped) {
                selection = true;
                selectedCol = col;
                selectedRow = row;
                repaint();
            }

            if (refBoard[row][col].getIdentification().isBlack() && flipped){
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
            int[][] moveSet = movingPiece.getMoveSet();
            boolean[] validMoveset = movingPiece.getValidMoveSet();


            for (int i = 0; i < moveSet.length; i++){

                if (!validMoveset[i]) continue;

                int r = moveSet[i][0];
                int c = moveSet[i][1];

                if (r == selectedToRow && c == selectedToCol){

                    //en passant logic
                    if (immediateAction){//the player must make the en passant on his immediate turn

                        int enPassantRow ; //the row en passant happens differs for white and black

                        if (flipped){ //flipped = black's turn
                           enPassantRow = 4;
                        } else {
                            enPassantRow = 3;
                        }

                        for (int j = 0; j<8 ; j++){ //checks the whole en passant row

                            if (PieceIdentification.isPawn(refBoard[enPassantRow][j])){//if a piece in that row is a pawn
                                Pawn p = (Pawn) refBoard[enPassantRow][j];

                                if (p.getEnPassantAllowed()){//if en passant allowed for a pawn
                                    p.revokeEnPassantAllowed();//revoked
                                }

                                p.releaseEnPassantDanger();//release the en passant danger from the endangered pawns
                            }
                        }
                        immediateAction = false;//sets the special move check to false
                    }

                    Piece target = refBoard[selectedToRow][selectedToCol];//moves the piece

                    //if en passant happens, the logic for capturing the piece
                    if (PieceIdentification.isPawn(movingPiece)){
                        for (int k = 4; k < 6; k++ ){//en passant moves are 4 and 5
                            if (validMoveset[k]){

                                if (selectedToRow== moveSet[k][0]
                                        && selectedToCol == moveSet[k][1] ){//checking if the move was an en passant

                                    Pawn enPassantCapturedPawn = (Pawn) refBoard[selectedRow][selectedCol] ; //selectedRow because the pawn that gets captured is in the row as the pawn that does the en passant

                                    if (enPassantCapturedPawn.getIdentification().isWhite()){ //adding the captured pice
                                        capturedByBlack.add(enPassantCapturedPawn);
                                    } else {
                                        capturedByWhite.add(enPassantCapturedPawn);
                                    }

                                    refBoard[selectedRow][selectedToCol] = null;//selectedRow because the pawn that gets captured is in the row as the pawn that does the en passant


                                }

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
                        p.promote();//automatically sets the board in the method

                        //checks if a pawn can be in en passant danger
                        if ((p.getIdentification().isWhite() && selectedToRow == selectedRow-2)
                                || (p.getIdentification().isBlack() && selectedToRow == selectedRow+2)){

                            boolean[] enPassantDanger = p.enPassantDangerCheck(); //checking if there is opponent pawns that can jump to the oppotunity
                            if (enPassantDanger[0] || enPassantDanger[1]){
                                immediateAction = true;//sets the special move check for next turn to be true
                            }
                        }
                    }


                    selectedRow = selectedCol = selectedToRow = selectedToCol = -1;//wipes the variables
                    selection = false;//wipes the selection


                    repaint();//board refresh
                    flipBoard();//flips board after a successful move
                    break;
                }

            }





        }

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

        Piece[][] emptyBoard = new Piece[8][8];
        setBoard(emptyBoard);

        //white pieces
        Rook wR1 = new Rook('a',1,true);
        Knight wN1 = new Knight('b',1,true);
        Bishop wB1 = new Bishop('c',1,true);
        Queen wQ  = new Queen('d',1,true);
        King wK   = new King('e',1,true);
        Bishop wB2 = new Bishop('f',1,true);
        Knight wN2 = new Knight('g',1,true);
        Rook wR2 = new Rook('h',1,true);

        Pawn wP1 = new Pawn('a',2,true);
        Pawn wP2 = new Pawn('b',2,true);
        Pawn wP3 = new Pawn('c',2,true);
        Pawn wP4 = new Pawn('d',2,true);
        Pawn wP5 = new Pawn('e',2,true);
        Pawn wP6 = new Pawn('f',2,true);
        Pawn wP7 = new Pawn('g',2,true);
        Pawn wP8 = new Pawn('h',2,true);

        //black pieces
        Rook bR1 = new Rook('a',8,false);
        Knight bN1 = new Knight('b',8,false);
        Bishop bB1 = new Bishop('c',8,false);
        Queen bQ  = new Queen('d',8,false);
        King bK   = new King('e',8,false);
        Bishop bB2 = new Bishop('f',8,false);
        Knight bN2 = new Knight('g',8,false);
        Rook bR2 = new Rook('h',8,false);

        Pawn bP1 = new Pawn('a',7,false);
        Pawn bP2 = new Pawn('b',7,false);
        Pawn bP3 = new Pawn('c',7,false);
        Pawn bP4 = new Pawn('d',7,false);
        Pawn bP5 = new Pawn('e',7,false);
        Pawn bP6 = new Pawn('f',7,false);
        Pawn bP7 = new Pawn('g',7,false);
        Pawn bP8 = new Pawn('h',7,false);


    }
    public static void main (String[]args){

        JFrame frame = new JFrame("Chess Board");
        frame.setLayout(new BorderLayout());

        ChessBoard board = new ChessBoard();

        //Knight k1 = new Knight('d', 5, true);
        //Knight k2 = new Knight('b',7,false);
        //Knight k3 = new Knight('d',2,false);
        //Knight k4 = new Knight('c',4,true);

        //Rook r1 = new Rook('b', 4, false);
        //Rook r2 = new Rook('c',7,true);
        //Rook r3 = new Rook('b',8,false);

        //Pawn p1 = new Pawn('c',2,true);
        //Pawn p2 = new Pawn('d',7,false);
        //Pawn p3 = new Pawn('f',7,true);

        //King king1 = new King('e',1,true);
        //King king2 = new King('d',8,false);
/*
        System.out.println();


        r1.moveCheck();
        r2.moveCheck();

        JPanel whiteBar = new JPanel(); // right bar
        whiteBar.setPreferredSize(new Dimension(pieceBarWidth, pieceBarLength));
        whiteBar.setBackground(Color.LIGHT_GRAY);

        JPanel blackBar = new JPanel(); // right bar
        blackBar.setPreferredSize(new Dimension(pieceBarWidth, pieceBarLength));
        blackBar.setBackground(Color.DARK_GRAY);

        frame.add(blackBar, BorderLayout.WEST);
        frame.add(whiteBar, BorderLayout.EAST);

         */

        frame.add(board, BorderLayout.CENTER);
        frame.setResizable(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }


}

