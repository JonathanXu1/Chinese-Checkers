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
  static int[][] friendlyPieces = new int[10][2];
  static int[][] currentBestMove = new int[2][2];
  static int moves = 0;
  static int[][] visited = new int[26][18];
  // TODO: Implement move count tracking

  public static void main(String[] args) {
    initGrid();
    String start = "BOARD 1 0 (9, 5) (10, 5) (10, 6) (11, 5) (11, 6) (11, 7) (12, 5) (12, 6) (12, 7) (12, 8)";
    String rand1 = "BOARD 6 0 (14, 8) (15, 5) (17, 7) (19, 14) (20, 6) (22, 12)";
    readGrid(start);
    printGrid(board);

    System.out.println("Score: " + getScore(friendlyPieces));
    for (int i = 0; i < 150; i++) {
      int score = findBestMove(0, board, friendlyPieces);
      if (score != -42069) {
        move(board, friendlyPieces, currentBestMove);
      }
      printGrid(board);
      System.out.println("Score: " + getScore(friendlyPieces));
      moves++;
    }
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
    // Manually creates bounds for board
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
    // Loads up piece positions
    int pieceNum = 0;
    for(int i = 9; i<=12; i++){
      for(int j = 5; j <= i-4; j++){
        int[] coordinates = {i, j};
        friendlyPieces[pieceNum] = coordinates;
        pieceNum ++;
      }
    }
  }

  private static int getScore(int[][] friendlyPieces){
    // TODO: calculate score for area around piece instead
    int score = 0;
    // Iterate through all friendly pieces
    for(int[] piece:friendlyPieces){
        int i = piece[0]; // Row
        int j = piece[1]; // Column
        // Checks for nearby friendly pieces
        int nearbyPieces = 0;
        for(int v = -1; v <=1; v++){
          for(int h = -1; h <=1; h++){
            if(i+v >= 9 && i+v <= 25 && j+h >= 1 && j+h <= 17) { // If in board
              if((v==-1 && h!=1) || (v==0 && h!=0) || (v==1 && h!=-1)) { // Excludes j=1:r-1, j=0:r+0, j=-1:i=1
                if(board[i+v][j+h] == 1){ // If friendly
                  nearbyPieces ++;
                }
              }
            }
          }
        }
        // Finds distance from end (in steps)
        int distanceFromEnd = (25 - i) + (int)Math.abs(j-(i+1.0)/2); // V distance from bottom + H distance to center line
        // Being close to friendlies should be scored higher when the piece is closer to the end
        score += ((16 - distanceFromEnd) + nearbyPieces);
    }

    // Add # of pieces already at end

    // Subtract turns taken
    score -= moves;
    // TODO: Get suitable multiplier for moves score reduction
    return score;
  }

  //
  // TESTING FUNCTION
  //
  private static void printGrid(int[][] board) {
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

  public static ArrayList<ArrayList<int[]>> nextAvailableMoves (int r1, int c1, int[][] board, ArrayList<int[]> prevTurn){
    ArrayList<ArrayList<int[]>> moves = new ArrayList<>();
    int[] move;
    //System.out.print(r1 + " " + c1 + ": ");
    // Checks adjacent moves and jump moves
    for(int i = -1; i <= 1; i++){
      for(int j = -1; j <= 1; j++){
        if(r1+i >= 9 && r1+i <= 25 && c1+j >= 1 && c1+j <= 17){ // If in board
          if((i==-1 && j!=1) || (i==0 && j!=0) || (i==1 && j!=-1)){ // Excludes j=1:r-1, j=0:r+0, j=-1:i=1
            if(board[r1+i][c1+j] == 0 && prevTurn.size() == 0){ // If adjacent is empty and has not just jumped over another piece
              ArrayList<int[]> turn = new ArrayList<>();
              move = new int[]{r1+i, c1+j};
              turn.add(move);
              moves.add(turn);
              //System.out.print(move[0] + " " + move[1] + " | ");
            } else if(board[r1+i][c1+j] > 0){
              if(r1+2*i >= 9 && r1+2*i <= 25 && c1+2*j >= 1 && c1+2*j <= 17){ // If in board
                if(board[r1+2*i][c1+2*j] == 0 && visited[r1+2*i][c1+2*j] == 0) { // If jump is empty and not previously been there
                  ArrayList<int[]> turn = prevTurn;
                  move = new int[]{r1+2*i, c1+2*j};
                  turn.add(move);
                  moves.add(turn);
                  int[][] tempBoard = board;
                  tempBoard[r1][c1] = 0;
                  tempBoard[r1+2*i][c1+2*j] = 1;
                  visited[r1][c1] = 1;
                  //Iterates through each jump to find combinations of jumps
                  ArrayList<ArrayList<int[]>> possibleNextMoves = nextAvailableMoves(r1+2*i, c1+2*j, tempBoard, turn);
                  visited[r1][c1] = 0;
                  for(ArrayList<int[]> possibleNextMoveSet : possibleNextMoves){
                    ArrayList<int[]> combinedMoveSet = turn;
                    combinedMoveSet.addAll(possibleNextMoveSet);
                    moves.add(combinedMoveSet);
                  }
                  //System.out.print(move[0] + " " + move[1] + " | ");
                }
              }
            }
          }
        }
      }
    }
    //System.out.println("Possible moves for piece at " + r1 + " " + c1);
    /*
    for(int i = 0; i < moves.size(); i++){
      System.out.println(moves.get(i)[0] + " " + moves.get(i)[1]);
    }*/
    return moves;
  }

  public static int findBestMove (int depth, int[][] board, int[][] friendlyPieces) {
    /*
    if (depth >= 3) {
      return getScore(friendlyPieces);
    }
    int[][] tempFriendlyPieces = copyArray(friendlyPieces);
    int maxVal = Integer.MIN_VALUE;
    for (int i = 0; i < tempFriendlyPieces.length; i++) {
      int[] piece = tempFriendlyPieces[i];
      ArrayList<int[]> emptyMoves = new ArrayList<>();
      ArrayList<ArrayList<int[]>> possibleMoves = nextAvailableMoves(piece[0], piece[1], board, emptyMoves);
      for (int j = 0; j < possibleMoves.size(); j++) {
        int[] move = possibleMoves.get(j);
        int[][] tempBoard = copyArray(board);
        tempBoard[move[0]][move[1]] = 1;
        tempBoard[piece[0]][piece[1]] = 0;
        tempFriendlyPieces[i] = move;
        // TODO: prevent piece from jumping back and forth
        if (move[0] > piece[0]) {
          int val = findBestMove(depth + 1, tempBoard, tempFriendlyPieces);
          if (val == -42069) {
            val = getScore(friendlyPieces);
          }
          maxVal = Math.max(maxVal, val);
          if (maxVal == val && depth == 0) {
            currentBestMove[0][0] = piece[0];
            currentBestMove[0][1] = piece[1];
            currentBestMove[1][0] = move[0];
            currentBestMove[1][1] = move[1];
            System.out.println(depth + "  " + maxVal + " " + currentBestMove[0][0] + " " + currentBestMove[0][1] + " " + currentBestMove[1][0] + " " + currentBestMove[1][1]);

          }
        }
      }
    }
    if (maxVal == Integer.MIN_VALUE) {
      return -42069;
    }
    return maxVal;
    */
    return 0;
  }

  private static void move (int[][] board, int[][] friendlyPieces, int[][] move) {
    System.out.println(move[0][0] + " " + move[0][1] + " " + move[1][0] + " " + move[1][1]);
    board[move[1][0]][move[1][1]] = board[move[0][0]][move[0][1]];
    board[move[0][0]][move[0][1]] = 0;
    for (int[] piece: friendlyPieces) {
      if (piece[0] == move[0][0] && piece[1] == move[0][1]) {
        piece[0] = move[1][0];
        piece[1] = move[1][1];
      }
    }
  }

  private static int[][] copyArray (int[][] board) {
    int[][] copy = new int[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        copy[i][j] = board[i][j];
      }
    }
    return copy;
  }

  private static boolean checkWin(int[][] board){
    boolean full = true;
    boolean containsFriendly = false;
    for(int i = 22; i <= 25; i++){ // Checks bottom point of star
      for(int j = i-12; j<=13; j++){
        if(board[i][j] == 0){
          full = false;
        } else if (board[i][j] == 1){
          containsFriendly = true;
        }
      }
    }

    return full && containsFriendly;
  }
}

