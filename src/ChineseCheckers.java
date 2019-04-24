import java.util.Arrays; 

/**
* This class is responsible for playing the game
*
* @author  
* @since   2019-04-24
*/
public class ChineseCheckers {
  static int NUM_PLAYERS = 6; 
  static int[][] board  = new int[26][18]; 

  public static void main(String[] args) {
    initGrid();
    readGrid("BOARD 6 0 (14, 8) (15, 5) (17, 7) (19, 14) (20, 6) (22, 12)");
    printGrid();

  }



  private static void readGrid(String boardMessage) {
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

  private static void initGrid() {
    for (int i = 0; i < 26; i++) {
      for (int j = 0; j < 18; j++ ) {
        // Check 1st triangle (points up)
        if(i >= 9 && i <= 21 && j >= 5 && j <= i - 4){
          board[i][j] = 0;
        } else if(i >= 13 && i <= 25 && j>= i-12 && j <= 13 ){ // Check 2nd triangle
          board[i][j] = 0;
        } else {
          board[i][j] = -1;
        }
      }
    }
  }

  // 
  // TESTING FUNCTION
  // 
  private static void printGrid() {
    System.out.println("____________________________"); 

    int rowNum = 0; 
    for (int[] row: board) {
      String lineToPrint = ""; 
      rowNum++;
      if (rowNum < 10) {
        continue;
      }
      for (int i = 0; i < 26-rowNum; i++) {
        lineToPrint += "_";
      }
      for (int item: row) {
        if (item == -1) {
          lineToPrint += "__"; 
        } else {
          lineToPrint += item + " ";
        }
      }
      lineToPrint += "__________________";
      System.out.println(lineToPrint.substring(13, 41)); 
    }
    System.out.println("____________________________"); 
  }
}


