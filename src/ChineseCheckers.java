import java.util.Arrays; 

public class ChineseCheckers {
  static int NUM_PLAYERS = 6; 
  static int[][] board  = new int[26][18]; 

  public static void main(String[] args) {
    readGrid("BOARD 6 0 (14, 8) (15, 5) (17, 7) (19, 14) (20, 6) (22, 12)");
    printGrid();
  }



  public static void readGrid(String boardMessage) {
    String[] boardInfo = boardMessage.split("\\s*[)] [(]|[)]|[(]\\s*"); 
    int playersRemaining = Integer.parseInt(boardInfo[0].split(" ")[1]); 

    int piecesProcessed = 0; 

    String[] pieces = Arrays.copyOfRange(boardInfo, 1, boardInfo.length);
    for (String piece: pieces) {
      int row = Integer.parseInt(piece.split(", ")[0]);
      int col = Integer.parseInt(piece.split(", ")[1]);
      board[row][col] = (piecesProcessed / 10) + 1; 
    }
  }


  // 
  // TESTING FUNCTION
  // 
  private static void printGrid() {
    for (int[] row: board) {
      for (int item: row) {
        if (item == -1) {
          System.out.print("* "); 
        } else {
          System.out.print(item + " "); 
        }
      }
      System.out.println(); 
    }
  }
}


