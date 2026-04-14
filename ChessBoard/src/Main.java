import pieces.Piece;
import pieces.PieceIdentification;

import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.\

    Image whitePawn = new ImageIcon("pieces/white/white-pawn.png").getImage();
    Image scaled = whitePawn.getScaledInstance(50, 50, Image.SCALE_SMOOTH);

    JLabel label = new JLabel(new ImageIcon(scaled));
    label.setBounds(50,50,50,50);

    JFrame frame = new JFrame();
    frame.setBounds(200,200,200,200);
    frame.setLayout(null);
    frame.setVisible(true);
    frame.add(label);

    System.out.println(PieceIdentification.W_KNIGHT);



}
