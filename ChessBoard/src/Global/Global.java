package Global;

public class Global {

    public static void print2D(Object[][] arr) {
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {

                if (arr[r][c] == null) {
                    System.out.print("null ");
                } else {
                    System.out.print(arr[r][c] + " ");
                }

            }
            System.out.println();
        }
    }

    public static void print1D(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                System.out.print("null ");
            } else {
                System.out.print(arr[i] + " ");
            }
        }
        System.out.println();
    }

    public static void print2D(boolean[][] arr) {
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                System.out.print(arr[r][c] + " ");
            }
            System.out.println();
        }
    }

    public static void print1D(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] ? "1 " : "0 ");
        }
        System.out.println();
    }

    public static int chessRowToIndex(int chessRow) {
        if (chessRow < 1 || chessRow > 8) {
            throw new IllegalArgumentException("chessRow must be between 1 and 8: " + chessRow);
        }
        return 8 - chessRow;
    }
}
