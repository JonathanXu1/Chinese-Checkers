package Game;
import java.util.ArrayList;
import java.util.Arrays;

//TODO: Follow through with decisions near end

/**
 * This class is the algorithm to find what move to make.
 *
 * @author Jonathan Xu, Bryan Zhang, and Carol Chen
 * @since 2019-04-24
 */
public class ChineseCheckers {
  int depthLayer;
  int NUM_PLAYERS = 6;
   int[][] board = new int[26][18];
  int[][] friendlyPieces = new int[10][2];
  ArrayList<int[]> currentBestMove = new ArrayList<int[]>();
  int moves = 0;
  int playersRemaining;
  int[][] visited = new int[26][18];
  int[] scoreConstants = new int[0];
  double[] scoreMultiplier = {0.5, 0.7, 3};
  // Default {1, 1, 3} GMO {0.5, 0.7, 3}

  /**
   * constructor
   */
  public ChineseCheckers() {
    initGrid();
  }

  /**
   * Getter for the score multipliers for the genetic algorithm.
   * @return A double array that stores the score multipliers.
   */
  public double[] getScoreMultiplier(){
    return  scoreMultiplier;
  }

  /**
   * Setter for the score multipliers for the genetic algorithm.
   * @param newMultiplier A double array that stores the score multipliers.
   */
  public void setScoreMultiplier(double[] newMultiplier){
    scoreMultiplier = newMultiplier;
  }

  /**
   * Makes the move and sends the move output to server to the client.
   * @return A String that represents the move to make.
   */
  public String makeMove(){
    // Displays board
    printGrid();

    String output = "MOVE";

    // Adjusts depth perception over time (3,2,1)
    // Plans ahead for fewer steps when there are more players due to increased chaos
    if(playersRemaining >= 5 && moves > 6){
      depthLayer = 1;
    } else if(playersRemaining >= 3 && moves > 8){
      depthLayer = 2 - moves % 2;
    } else {
      depthLayer = 3 - moves % 3;
    }

    if (moves == 0) { // Default open move right
      output += "(12,8) (13,8)";
    } else if (moves == 1) { // Default open move left
      output += "(12,5) (13,6)";
    } else { // Algorithm
      double score = findBestMove(0);
      for (int[] step: currentBestMove) {
        output += " (" + Integer.toString(step[0]) + "," + Integer.toString(step[1]) + ")";
      }
    }
    moves++;

    // Debug info
    System.out.println("Board score: " + getScore(0));
    System.out.println(output);
    System.out.println("Players remaining: " + playersRemaining);
    return(output);
  }

  /**
   * Reads the board input from the server and plots pieces onto the board.
   * @param boardMessage The board input from the server.
   */
  public void readGrid(String boardMessage) {
    // Resets board
    for (int i = 0; i < 26; i++) {
      for (int j = 0; j < 18; j++) {
        if (board[i][j] > 0) {
          board[i][j] = 0;
        }
      }
    }

    String[] boardInfo = boardMessage.split("\\s*[)] [(]|[)]|[(]\\s*");
    playersRemaining = Integer.parseInt(boardInfo[0].split(" ")[1]);
    int piecesProcessed = 0;

    String[] pieces = Arrays.copyOfRange(boardInfo, 1, boardInfo.length);
    for (String piece: pieces) {
      int row = Integer.parseInt(piece.split(",")[0]);
      int col = Integer.parseInt(piece.split(",")[1]);
      board[row][col] = (piecesProcessed / 10) + 1;
      if (piecesProcessed / 10 == 0) {
        int[] coordinate = {row, col};
        friendlyPieces[piecesProcessed % 10] = coordinate;
      }
      piecesProcessed++;
    }
  }

  /**
   * Initializes the grid by setting the bounds of the checkers board
   */
  private void initGrid() {
    // Manually creates bounds for board
    for (int i = 0; i < 26; i++) {
      for (int j = 0; j < 18; j++) {
            // Check 1st triangle (points up)
        if (i >= 9 && i <= 21 && j >= 5 && j <= i - 4) {
          board[i][j] = 0;
        } else if (i >= 13 && i <= 25 && j >= i - 12 && j <= 13) { // Check 2nd triangle
          board[i][j] = 0;
        } else {
          board[i][j] = -1;
        }
      }
    }
  }

  /**
   * Gets the score of the current board.
   * @param turnNum The turn number.
   * @return The score of the current board.
   */
  private double getScore(int turnNum){
    // TODO: calculate score for area around piece instead
    double score = 300;
    // Iterate through all friendly pieces
    for (int i = 0; i < friendlyPieces.length; i++) {
      int r = friendlyPieces[i][0]; // Row
      int c = friendlyPieces[i][1]; // Column
      // Checks for nearby friendly pieces
      int nearbyPieces = 0;
      for (int v = -1; v <= 1; v++) {
        for (int h = -1; h <= 1; h++) {
          if (r + v >= 9 && r + v <= 25 && c + h >= 1 && c + h <= 17) { // If in board
            if ((v == -1 && h != 1) || (v == 0 && h != 0) || (v == 1 && h != -1)) { // Excludes j=1:r-1, j=0:r+0, j=-1:i=1
              if (board[r + v][c + h] == 1) { // If friendly
                nearbyPieces++;
              }
            }
          }
        }
      }
      // Calculates the starting row of the piece (very hardcoded lol)
      int startRow;
      if (i >= 6) {
        startRow = 12;
      } else if (i >= 3) {
        startRow = 11;
      } else if (i >= 1) {
        startRow = 10;
      } else {
        startRow = 9;
      }
      // Finds distance from end (in steps)
      int vertDistanceFromEnd = 25 - r; // V distance from bottom + H distance to center line
      int horDistanceFromEnd = (int) Math.abs(c - (r + 1.0) / 2);
      // Being closer to the end is good
      // Prioritizes pieces that started at the back, hopefully this will bring a 'flip' move pattern
      score -= Math.pow(vertDistanceFromEnd, 2 )* scoreMultiplier[0];
      score -= horDistanceFromEnd * 0.5 * scoreMultiplier[1];
      //score += ((16 - vertDistanceFromEnd)*(startRow-8)*3 + (7 - horDistanceFromEnd)) * 1;
      // Being close to friendlies should be scored higher when the piece is closer to the end
      //score += nearbyPieces *1* (16-vertDistanceFromEnd);
    }

    // Pieces at end are good
    int piecesAtEnd = 0;
    for (int[] pieceCoordinate: friendlyPieces) {
      if (pieceCoordinate[0] >= 22) {
        piecesAtEnd++;
      }
    }
    //score += piecesAtEnd * 50;

    // Subtract turns taken
    score -= (moves + turnNum) * scoreMultiplier[2];
    // TODO: Get suitable multiplier for moves score reduction
    return score;
  }

  /**
   * Prints out the current grid, to be viewed on the client end.
   */
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
      for (int i = 0; i < 26 - rowNum; i++) {
        lineToPrint += " ";
        characters++;
      }
      for (int item: row) {
        if (item == -1) {
          lineToPrint += "  ";
          characters += 2;
        } else if (item == 0) {
          lineToPrint += "○" + " ";
          characters += 2;
        } else {
          lineToPrint += "\033[0;3" + item + "m";
          lineToPrint += "●" + " ";
          characters += 2;
          lineToPrint += "\033[0m";
        }
      }
      lineToPrint = lineToPrint.substring(12);
      System.out.println(lineToPrint);
    }
    System.out.println("____________________________");
  }

  /**
   * Finds the next available moves for a specific piece.
   * @param r1 The row of the piece.
   * @param c1 The column of the piece.
   * @param prevTurn An ArrayList of the path to get to end point.
   * @return An ArrayList of the available moves.
   */
  public ArrayList<ArrayList<int[]>> nextAvailableMoves (int r1, int c1, ArrayList<int[]> prevTurn){
    ArrayList<ArrayList<int[]>> moves = new ArrayList<>();
    int[] move;
    // Checks adjacent moves and jump moves
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        if (!(r1 + i >= 9 && r1 + i <= 25 && c1 + j >= 1 && c1 + j <= 17)) { // If not in board
          continue;
        }
        if ((i == -1 && j != 1) || (i == 0 && j != 0) || (i == 1 && j != -1)) { // Excludes j=1:r-1, j=0:r+0, j=-1:i=1
          if (board[r1 + i][c1 + j] == 0 && prevTurn.size() == 0) { // If adjacent is empty and has not just jumped over another piece
            if (checkLegalGoalArea(r1 + i, c1 + j)) {
              ArrayList<int[]> turn = new ArrayList<>();
              move = new int[] {r1 + i, c1 + j};
              turn.add(move);
              moves.add(turn);
            }
          } else if (board[r1 + i][c1 + j] > 0) {
            if (r1 + 2 * i >= 9 && r1 + 2 * i <= 25 && c1 + 2 * j >= 1 && c1 + 2 * j <= 17) { // If in board
              if (board[r1 + 2 * i][c1 + 2 * j] == 0 && visited[r1 + 2 * i][c1 + 2 * j] == 0) { // If jump is empty and not previously been there
                if (checkLegalGoalArea(r1 + i * 2, c1 + j * 2)) {
                  ArrayList<int[]> turn = new ArrayList<>(prevTurn);
                  move = new int[] {r1 + 2 * i, c1 + 2 * j};
                  turn.add(move);
                  moves.add(turn);
                  board[r1][c1] = 0;
                  board[r1 + 2 * i][c1 + 2 * j] = 1;
                  visited[r1][c1] = 1;
                  //Iterates through each jump to find combinations of jumps
                  ArrayList<ArrayList<int[]>> possibleNextMoves = nextAvailableMoves(r1 + 2 * i, c1 + 2 * j, turn);
                  visited[r1][c1] = 0;
                  for (ArrayList<int[]> possibleNextMoveSet: possibleNextMoves) {
                    moves.add(possibleNextMoveSet);
                  }

                      //revert changes made
                  board[r1][c1] = 1;
                  board[r1 + 2 * i][c1 + 2 * j] = 0;
                }
              }
            }
          }
        }
      }
    }
    return moves;
  }

  /**
  * Recursively finds the best move and saves it to class variable currentBestMove.
  * @param depth How many turns into the future its looking at.
  * @return Score of the board.
  */
  private double findBestMove(int depth) {
    if (checkWin()) {
      //System.out.println("win win win");
      return Integer.MAX_VALUE;
    }

    // Stop recursive search after 3 turns depth
    if (depth >= depthLayer) {
      double score = getScore(depth - 1 + moves);
      return score;
    }
    double maxVal = Integer.MIN_VALUE;

    // Loop through every move of every friendly piece
    for (int i = 0; i < friendlyPieces.length; i++) {
      int[] piece = friendlyPieces[i];
      ArrayList<int[]> emptyMoves = new ArrayList<>();
      visited = new int[26][18];
      ArrayList<ArrayList<int[]>> possibleMoves = nextAvailableMoves(piece[0], piece[1], emptyMoves);

      for (int j = 0; j < possibleMoves.size(); j++) {
        ArrayList<int[]> move = possibleMoves.get(j);
        int[] finalPos = move.get(move.size() - 1);

              // Make move on copy of board
        board[finalPos[0]][finalPos[1]] = 1;
        board[piece[0]][piece[1]] = 0;
        friendlyPieces[i] = finalPos;
        // TODO: prevent piece from jumping back and forth
        if (finalPos[0] >= piece[0]) { // Makes piece never move backwards

            // Recursive call, determines score of move depending on potential score of future moves
          double val = findBestMove(depth + 1);
            // Gets max score
          maxVal = Math.max(maxVal, val);

            // If found move with higher potential score, set the best move to the first move done
          if (maxVal == val && depth == 0) {
            move.add(0, piece);
            currentBestMove = move;
          }

        }

        //Revert changes made
        board[finalPos[0]][finalPos[1]] = 0;
        board[piece[0]][piece[1]] = 1;
        friendlyPieces[i] = piece;
      }
    }
    return maxVal;
  }

  /**
   * Checks if player has won on current board
   * @return A boolean that represents whether or not player has won
   */
  public boolean checkWin() {
    boolean full = true;
    boolean containsFriendly = false;
    for (int i = 22; i <= 25; i++) { // Checks bottom point of star
      for (int j = i - 12; j <= 13; j++) {
        if (board[i][j] == 0) {
          full = false;
        } else if (board[i][j] == 1) {
          containsFriendly = true;
        }
      }
    }
    return full && containsFriendly;
  }

  /**
   * Checks if the position is in an illegal goal area
   * @param r The row of the position
   * @param c The column of the position
   * @return A boolean that represents if move is legal
   */
  private boolean checkLegalGoalArea(int r, int c) {
    boolean legal = true;
    if ((r < 17) && ((c < 5) || (c > r - 4))) {
      legal = false;
    }
    if ((r > 17) && ((c < r - 12) || (c > 13))) {
      legal = false;
    }
    return legal;
  }
}