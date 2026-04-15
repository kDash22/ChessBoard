package board;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;



public class ChessBoard extends JPanel {

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
        //white
        wPawn = new ImageIcon("pieces/white/white-pawn.png").getImage();
        wKnight = new ImageIcon("pieces/white/white-knight.png").getImage();
        wBishop = new ImageIcon("pieces/white/white-bishop.png").getImage();
        wRook = new ImageIcon("pieces/white/white-rook.png").getImage();
        wQueen = new ImageIcon("pieces/white/white-Queen.png").getImage();
        wKing = new ImageIcon("pieces/white/white-king.png").getImage();

        //black
        bPawn = new ImageIcon("pieces/black/black-pawn.png").getImage();
        bKnight = new ImageIcon("pieces/black/black-knight.png").getImage();
        bBishop = new ImageIcon("pieces/black/black-bishop.png").getImage();
        bRook = new ImageIcon("pieces/black/black-rook.png").getImage();
        bQueen = new ImageIcon("pieces/black/black-queen.png").getImage();
        bKing = new ImageIcon("pieces/black/black-king.png").getImage();

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

                    Piece target = refBoard[selectedToRow][selectedToCol];

                    if (target != null) {
                        if (target.getIdentification().isWhite()){
                            capturedByBlack.add(target);
                        } else {
                            capturedByWhite.add(target);
                        }
                    }

                    movingPiece.setChessCol(Piece.colToChessCol(selectedToCol));
                    movingPiece.setChessRow(Piece.rowToChessRow(selectedToRow));

                    refBoard[selectedToRow][selectedToCol] = movingPiece;
                    refBoard[selectedRow][selectedCol] = null;

                    selectedRow = selectedCol = selectedToRow = selectedToCol = -1;
                    selection = false;

                    setBoard(refBoard);
                    repaint();
                    flipBoard();//flips board after a successfull move
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

    public static void main (String[]args){

        JFrame frame = new JFrame("Chess Board");
        frame.setLayout(new BorderLayout());

        ChessBoard board = new ChessBoard();

        Knight k1 = new Knight('d', 5, true);
        Knight k2 = new Knight('b',7,false);
        Knight k3 = new Knight('d',2,false);

        Rook r1 = new Rook('b', 4, false);
        Rook r2 = new Rook('c',7,true);
        Rook r3 = new Rook('b',8,false);


        System.out.println();
        //r1.moveCheck();
        //r2.moveCheck();

        /*JPanel whiteBar = new JPanel(); // right bar
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

