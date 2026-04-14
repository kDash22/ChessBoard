package board;

import pieces.*;
import pieces.PieceIdentification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;



public class ChessBoard extends JPanel {


    private final int TILE_SIZE = 80;
    private final static int pieceBarWidth = 240;
    private final static int pieceBarLength = 640;
    private double angle = 0;
    private double targetAngle = 0;
    private Timer timer;
    private int direction = 1;

    private Image wPawn,wKnight,wBishop,wRook,wQueen,wKing,
                  bPawn,bKnight,bBishop,bRook,bQueen,bKing;

    private static Piece[][] board = new Piece[8][8];

    public static final List<Character> COLUMN_LETTERS = List.of('a','b','c','d','e','f','g','h');

    public static Piece[][] getBoard() {
        return board;
    }

    public static void setBoard(Piece[][] board) {
        ChessBoard.board = board;
    }

    public static void insertPiece(int col, int row, Piece piece){
        Piece[][] board = getBoard();
        board[col][row] = piece;
        setBoard(board);
    }

    private Image getPieceImage(Piece piece){
        if (piece == null) return null;

        if (piece instanceof Knight){
            return piece.getIdentification().isWhite() ? wKnight : bKnight;
        }
        if (piece instanceof Bishop){
            return piece.getIdentification().isWhite() ? wBishop : bBishop;
        }
        if (piece instanceof Rook){
            return piece.getIdentification().isWhite() ? wRook : bRook;
        }
        if (piece instanceof Queen){
            return piece.getIdentification().isWhite() ? wPawn : bPawn;
        }

        return null;
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

        direction *= -1;
        targetAngle = angle + direction * Math.PI;

        if (timer != null && timer.isRunning())
            return;

        timer = new Timer(25, e -> {

            double speed = 0.08;

            if (Math.abs(targetAngle - angle) < speed) {
                angle = targetAngle;
                timer.stop();
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
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Chess Board");
        frame.setLayout(new BorderLayout());

        ChessBoard board = new ChessBoard();
        Knight k1 = new Knight('d',5,true);
        //Knight k2 = new Knight('c',7,false);
        //insertPiece(2,7,PieceIdentification.B_KING);

        k1.moveCheck();

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