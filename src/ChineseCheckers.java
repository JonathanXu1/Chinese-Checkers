import java.util.ArrayList;
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

    Client client = new Client(); //start the client
    client.go(); //begin the connection


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
      piecesProcessed++;
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
      int characters = -12;
      String lineToPrint = "";
      rowNum++;
      if (rowNum < 10) {
        continue;
      }
      for (int i = 0; i < 26-rowNum; i++) {
        lineToPrint += "_";
        characters++;
      }
      for (int item: row) {
        if (item == -1) {
          lineToPrint += "__";
          characters += 2;
        } else if (item == 0) {
          lineToPrint += item + " ";
          characters += 2;
        } else {
          lineToPrint += "\033[0;3" + item + "m";
          lineToPrint += item + " ";
          characters += 2;
          lineToPrint += "\033[0m";
        }
      }
      lineToPrint = lineToPrint.substring(12);
      for (int i = 0; i < 40 - characters; i++) {
        lineToPrint += "_";
      }
      System.out.println(lineToPrint);
    }
    System.out.println("____________________________");
  }

  public ArrayList<int[]> nextAvailableMoves (int r1, int c1, int[][] board){
    ArrayList<int[]> moves = new ArrayList<>();
    int[] move;
    // Checks adjacent moves and jump moves
    for(int i = -1; i <= 1; i++){
      for(int j = -1; j <= 1; j++){
        if(r1+i >= 9 && r1+i <= 25 && c1+j >= 1 && c1+j <= 17){ // If in board
          if(board[r1+i][c1+j] == 0){ // If adjacent is empty
            move = new int[]{r1+i, c1+j};
            moves.add(move);
          } else if(i != 0 && j != 0){
            if(r1+2*i >= 9 && r1+2*i <= 25 && c1+2*j >= 1 && c1+2*j <= 17){ // If in board
              if(board[r1+2*i][c1+2*j] == 0) { // If jump is empty
                move = new int[]{r1+2*i, c1+2*j};
                moves.add(move);
              }
            }
          }
        }
      }
    }
    return moves;

  }

}

