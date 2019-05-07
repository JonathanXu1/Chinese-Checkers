package Game;
import java.util.ArrayList;
import java.util.Arrays;

//TODO: Force a solution if running out of time

/**
 * This class is responsible for playing the game
 *
 * @author
 * @since   2019-04-24
 */
public class ChineseCheckers {
  int DEPTH_LAYER = 3;
  int NUM_PLAYERS = 6;
  int[][] board  = new int[26][18];
  int[][] friendlyPieces = new int[10][2];
  ArrayList<int[]>  currentBestMove = new ArrayList<int[]>();
  int moves = 0;
  int[][] visited = new int[26][18];
  int[] scoreConstants = new int[0];
  double startTime = System.nanoTime()/1000000000.0;
  double scoreTime = 0;
  double availTime = 0;
  double arrayCopyTime = 0;
  double[] scoreMultiplier = {3, 1, 1, 50, 10};

  // Board configs for testing
  String start = "BOARD 1 0 (9, 5) (10, 5) (10, 6) (11, 5) (11, 6) (11, 7) (12, 5) (12, 6) (12, 7) (12, 8)";
  String rand1 = "BOARD 6 0 (14, 8) (15, 5) (17, 7) (19, 14) (20, 6) (22, 12)";

  public ChineseCheckers() {
    initGrid();
  }

  public double[] getScoreMultiplier(){
    return  scoreMultiplier;
  }
  public void setScoreMultiplier(double[] newMultiplier){
    scoreMultiplier = newMultiplier;
  }

  public String makeMove(){
    startTime = System.nanoTime()/1000000000.0;
    System.out.println("Start Timer.");
    String output = "MOVE";
    startTime = System.nanoTime()/1000000000.0;
    scoreTime = 0;
    availTime = 0;
    arrayCopyTime = 0;

    if(moves == 0){ // Default open move right
      output += "(12,8) (13,8)";
    } else if (moves == 1){ // Default open move left
      output += "(12,5) (13,6)";
    } else { // Algorithm
      //printGrid(board);
      //System.out.println("Start alg " + (System.nanoTime()/1000000000.0 - startTime));
      double score = findBestMove(0, board, friendlyPieces);
      if (score == -42069) {
        // No valid moves
      }
      System.out.println("Score: " + getScore(friendlyPieces, 0));

      for(int[] step:currentBestMove){
        output += " (" + Integer.toString(step[0]) + "," + Integer.toString(step[1]) + ")";
      }
    }
    //System.out.println("total time: " + (System.nanoTime()/1000000000.0 - startTime));
    //System.out.println("Score time: " + scoreTime);
    //System.out.println("Available moves time: " + availTime);
    //System.out.println("Copy array time: " + arrayCopyTime);
    moves++;
    return(output);
  }

  public void readGrid(String boardMessage) {
    // Resets board
    for(int i = 0; i < 26; i++){
      for(int j = 0; j < 18; j++){
        if(board[i][j] > 0){
          board[i][j] = 0;
        }
      }
    }

    String[] boardInfo = boardMessage.split("\\s*[)] [(]|[)]|[(]\\s*");
    int playersRemaining = Integer.parseInt(boardInfo[0].split(" ")[1]);
    int piecesProcessed = 0;

    String[] pieces = Arrays.copyOfRange(boardInfo, 1, boardInfo.length);
    for (String piece: pieces) {
      int row = Integer.parseInt(piece.split(",")[0]);
      int col = Integer.parseInt(piece.split(",")[1]);
      board[row][col] = (piecesProcessed / 10) + 1;
      if(piecesProcessed/10 == 0){
        int[] coordinate = {row, col};
        friendlyPieces[piecesProcessed%10] = coordinate;
      }
      piecesProcessed++;
    }
    printGrid();
  }

  private void initGrid() {
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
  }

  private double getScore(int[][] friendlyPieces, int turnNum){
    // TODO: calculate score for area around piece instead
    double score = 0;
    // Iterate through all friendly pieces
    for(int i = 0; i < friendlyPieces.length; i++){
      int r = friendlyPieces[i][0]; // Row
      int c = friendlyPieces[i][1]; // Column
      // Checks for nearby friendly pieces
      int nearbyPieces = 0;
      for(int v = -1; v <=1; v++){
        for(int h = -1; h <=1; h++){
          if(r+v >= 9 && r+v <= 25 && c+h >= 1 && c+h <= 17) { // If in board
            if((v==-1 && h!=1) || (v==0 && h!=0) || (v==1 && h!=-1)) { // Excludes j=1:r-1, j=0:r+0, j=-1:i=1
              if(board[r+v][c+h] == 1){ // If friendly
                nearbyPieces ++;
              }
            }
          }
        }
      }
      // Calculates the starting row of the piece (very hardcoded lol)
      int startRow;
      if(i >= 6){
        startRow = 12;
      } else if(i >= 3){
        startRow = 11;
      } else if(i >= 1){
        startRow = 10;
      } else{
        startRow = 9;
      }
      // Finds distance from end (in steps)
      int vertDistanceFromEnd = 25 - i; // V distance from bottom + H distance to center line
      int horDistanceFromEnd = (int)Math.abs(c-(r+1.0)/2);
      // Being closer to the end is good
      // Prioritizes pieces that started at the back, hopefully this will bring a 'flip' move pattern
      score += ((16 - vertDistanceFromEnd)*(startRow-8)*3 + (7 - horDistanceFromEnd)) * 1;
      // Being close to friendlies should be scored higher when the piece is closer to the end
      score += nearbyPieces *1* (16-vertDistanceFromEnd);
    }

    // Pieces at end are good
    int piecesAtEnd = 0;
    for(int[] pieceCoordinate:friendlyPieces){
      if(pieceCoordinate[0] >= 22){
        piecesAtEnd ++;
      }
    }
    score += piecesAtEnd * 50;

    // Subtract turns taken
    score -= (moves + turnNum) * 10;
    // TODO: Get suitable multiplier for moves score reduction
    return score;
  }

  //
  // TESTING FUNCTION
  //
  private void printGrid() {
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
        lineToPrint += " ";
        characters++;
      }
      for (int item: row) {
        if (item == -1) {
          lineToPrint += "  ";
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
      System.out.println(lineToPrint);
    }
    System.out.println("____________________________");
  }

  public ArrayList<ArrayList<int[]>> nextAvailableMoves (int r1, int c1, int[][] board, ArrayList<int[]> prevTurn){
    ArrayList<ArrayList<int[]>> moves = new ArrayList<>();
    int[] move;
    //System.out.print(r1 + " " + c1 + ": ");
    // Checks adjacent moves and jump moves
    for(int i = -1; i <= 1; i++){
      for(int j = -1; j <= 1; j++){
        if(r1+i >= 9 && r1+i <= 25 && c1+j >= 1 && c1+j <= 17){ // If in board
          if((i==-1 && j!=1) || (i==0 && j!=0) || (i==1 && j!=-1)){ // Excludes j=1:r-1, j=0:r+0, j=-1:i=1
            if(board[r1+i][c1+j] == 0 && prevTurn.size() == 0){ // If adjacent is empty and has not just jumped over another piece
              if(checkLegalGoalArea(r1+i, c1+j)){
                ArrayList<int[]> turn = new ArrayList<>();
                move = new int[]{r1+i, c1+j};
                turn.add(move);
                moves.add(turn);
                //System.out.print(move[0] + " " + move[1] + " | ");
              }
              //System.out.print(move[0] + " " + move[1] + " | ");
            } else if(board[r1+i][c1+j] > 0){
              if(r1+2*i >= 9 && r1+2*i <= 25 && c1+2*j >= 1 && c1+2*j <= 17){ // If in board
                if(board[r1+2*i][c1+2*j] == 0 && visited[r1+2*i][c1+2*j] == 0) { // If jump is empty and not previously been there
                  ArrayList<int[]> turn = new ArrayList<>(prevTurn);
                  move = new int[]{r1+2*i, c1+2*j};
                  turn.add(move);
                  moves.add(turn);
                  board[r1][c1] = 0;
                  board[r1+2*i][c1+2*j] = 1;
                  visited[r1][c1] = 1;
                  //Iterates through each jump to find combinations of jumps
                  ArrayList<ArrayList<int[]>> possibleNextMoves = nextAvailableMoves(r1+2*i, c1+2*j, board, turn);
                  visited[r1][c1] = 0;
                  for(ArrayList<int[]> possibleNextMoveSet : possibleNextMoves){
                    moves.add(possibleNextMoveSet);
                  }
                  //System.out.print(move[0] + " " + move[1] + " | ");

                  //revert changes made
                  board[r1][c1] = 1;
                  board[r1+2*i][c1+2*j] = 0;
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

  private double findBestMove (int depth, int[][] board, int[][] friendlyPieces) {
    // Stop recursive search after 3 turns depth
    if (depth >= DEPTH_LAYER) {
      double time = System.nanoTime()/1000000000.0;
      //System.out.println((System.nanoTime()/1000000000.0 - startTime) + "depth: " + depth + "start Score");
      double score = getScore(friendlyPieces, depth - 1 + moves);
      //System.out.println((System.nanoTime()/1000000000.0 - startTime) + "depth: " + depth + "stop Score");
      scoreTime += System.nanoTime()/1000000000.0 - time;

      return score;
    }
    double time = System.nanoTime()/1000000000.0;
    arrayCopyTime += System.nanoTime()/1000000000.0 - time;
    double maxVal = Integer.MIN_VALUE;

    // Loop through every move of every friendly piece
    for (int i = 0; i < friendlyPieces.length; i++) {
      int[] piece = friendlyPieces[i];
      time = System.nanoTime()/1000000000.0;
      //System.out.println((System.nanoTime()/1000000000.0 - startTime) + "depth: " + depth + "start find possible moves");

      ArrayList<int[]> emptyMoves = new ArrayList<>();
      visited = new int[26][18];
      //System.out.print(piece[0] + " " + piece[1] + ": ");
      ArrayList<ArrayList<int[]>> possibleMoves = nextAvailableMoves(piece[0], piece[1], board, emptyMoves);
      //System.out.println((System.nanoTime()/1000000000.0 - startTime) + "depth: " + depth + "stop find possible moves");
      availTime += System.nanoTime()/1000000000.0 - time;

      for (int j = 0; j < possibleMoves.size(); j++) {
        ArrayList<int[]> move = possibleMoves.get(j);
        int[] finalPos = move.get(move.size() - 1);
        time = System.nanoTime()/1000000000.0;
        arrayCopyTime += System.nanoTime()/1000000000.0 - time;

        // Make move on copy of board
        board[finalPos[0]][finalPos[1]] = 1;
        board[piece[0]][piece[1]] = 0;
        friendlyPieces[i] = finalPos;
        // TODO: prevent piece from jumping back and forth
        if (finalPos[0] >= piece[0]) { // Makes piece never move backwards

          // Recursive call, determines score of move depending on potential score of future moves
          double val = findBestMove(depth + 1, board, friendlyPieces);
          if (val == -42069) {
            val = getScore(friendlyPieces, depth + moves);
          }
          // Gets max score
          maxVal = Math.max(maxVal, val);

          // If found move with higher potential score, set the best move to the first move done
          if (maxVal == val && depth == 0) {
            move.add(0, piece);
            currentBestMove = move;
          }
          //System.out.println(depth + "  " + maxVal + " " + currentBestMove[0][0] + " " + currentBestMove[0][1] + " " + currentBestMove[1][0] + " " + currentBestMove[1][1]);

        }

        //Revert changes made
        board[finalPos[0]][finalPos[1]] = 0;
        board[piece[0]][piece[1]] = 1;
        friendlyPieces[i] = piece;
      }
    }

    // If current board has no move available, return -42069
    if (maxVal == Integer.MIN_VALUE) {
      return -42069;
    }
    return maxVal;
  }

  public boolean checkWin(){
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

  private boolean checkLegalGoalArea(int r, int c){
    boolean legal = true;
    if(r <=17 &&(c<5 || c >r-4)){
      legal = false;
    }
    if(r >=17 && (c<r-12 || c>13)){
      legal = false;
    }
    return legal;
  }
}

