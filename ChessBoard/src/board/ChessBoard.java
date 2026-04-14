package board;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;



public class ChessBoard extends JPanel {

    private boolean rotating = false; //if board is rotating
    private final int TILE_SIZE = 80;
    private final static int pieceBarWidth = 240;
    private final static int pieceBarLength = 640;

    private boolean flipped = false;

    private double angle = 0;
    private double targetAngle = 0;

    private Timer timer;

    private Image wPawn,wKnight,wBishop,wRook,wQueen,wKing,
                  bPawn,bKnight,bBishop,bRook,bQueen,bKing;

    private static Piece[][] board = new Piece[8][8]; //[row][col]

    public static final List<Character> COLUMN_LETTERS = List.of('a','b','c','d','e','f','g','h');

    public static Piece[][] getBoard() {
        return board;
    }

    public static void setBoard(Piece[][] board) {
        ChessBoard.board = board;
    }

    public static void insertPiece(int chessCol, int chessRow, Piece piece){
        int row = 8-chessRow;
        Piece[][] board = getBoard();
        board[row][chessCol] = piece;
        setBoard(board);
    }



    private Image getPieceImage(Piece piece){
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

    public void loadPieces(){
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
        if (piece != null){
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

                int col = 1 + (e.getX() / TILE_SIZE);
                int row = 8 - (e.getY() / TILE_SIZE) ;

                // determine logical orientation
                boolean isFlipped = ((int)(targetAngle / Math.PI)) % 2 != 0;

                if (isFlipped) {
                    col = 8 - col + 1;
                    row = 1 + (e.getY() / TILE_SIZE);
                }

                Character column = COLUMN_LETTERS.get(col - 1);

                System.out.println("Clicked: row=" + row + ", col=" + column);

                // trigger animated flip
                flipBoard();
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

        if (!flipped){
        //draw the pieces
            for (int row = 0; row <8; row++){
                for (int col = 0; col<8; col++){
                    Piece piece = board[row][col];
                    Image pieceIcon = getPieceImage(piece);

                    if (pieceIcon != null){

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
            for (int row = 0; row <8; row++){
                for (int col = 0; col<8; col++){
                    Piece piece = board[row][col];
                    Image flippedPiece = flipPiece(getPieceImage(piece));

                    if (flippedPiece != null){

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
        highlightValidSquare(g2d, new Rook('b',5,false));
    }

    private void highlightValidSquare(Graphics2D g2d,Piece piece){
        /*moveSet = new int[][]{
                {row + 2, col + 1}, {row + 2, col - 1},
                {row - 2, col + 1}, {row - 2, col - 1},
                {row + 1, col + 2}, {row + 1, col - 2},
                {row - 1, col + 2}, {row - 1, col - 2}
        };*/
        piece.moveCheck();

        for (int i = 0; i < piece.getMoveSet().length; i++){

            if (piece.getValidMoveSet()[i]){
                int row = piece.getMoveSet()[i][0];
                int col = piece.getMoveSet()[i][1];

                // bounds check
                if (row < 0 || row >= 8 || col < 0 || col >= 8)
                    continue;
                if (board[row][col] != null){
                    g2d.setColor(new Color(255, 0, 0, 60));
                }
                else {
                    g2d.setColor(new Color(0, 255, 0, 60));
                }

                /*if ((row + col) % 2 == 0)
                    g2d.setColor(Color.cyan);
                else
                    g2d.setColor(Color.blue);*/

                g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            }

        }

    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Chess Board");
        frame.setLayout(new BorderLayout());

        ChessBoard board = new ChessBoard();

        Knight k1 = new Knight('d',5,true);
        //Knight k2 = new Knight('d',4,false);




        Rook r1 = new Rook('b',5,false);
        Rook r2 = new Rook('c',6,true);


        System.out.println();
        r1.moveCheck();
        r2.moveCheck();

        JPanel whiteBar = new JPanel(); // right bar
        whiteBar.setPreferredSize(new Dimension(pieceBarWidth, pieceBarLength));
        whiteBar.setBackground(Color.LIGHT_GRAY);

        JPanel blackBar = new JPanel(); // right bar
        blackBar.setPreferredSize(new Dimension(pieceBarWidth, pieceBarLength));
        blackBar.setBackground(Color.DARK_GRAY);

        frame.add(board, BorderLayout.CENTER);
        frame.add(blackBar, BorderLayout.WEST);
        frame.add(whiteBar, BorderLayout.EAST);

        frame.setResizable(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }
}