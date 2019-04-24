import java.util.ArrayList;

public class ChineseCheckers {
  public static void main(String[] args) {
    //Creates board
    int[][] board = new int[26][18];
    //Harcoding the boundaries
    for(int i = 0; i < 26; i++){
      for(int j = 0; j < 16; j++ ){
        // Check 1st triangle (points up)
        if(i >= 9 && i <= 21 && j >=5 && j <= i-4){
          board[i][j] = 0;
        } else if(i >= 13 && i <= 25 && j>= i-12 && j <= 13 ){ // Check 2nd triangle
          board[i][j] = 0;
        } else {
          board[i][j] = -1;
        }
      }
    }

  }

  public ArrayList<int[]> nextAvailableMoves (int r1, int c1, int[][] board){
    ArrayList<int[]> moves = new ArrayList<>();
    int[] move = new int[2];
    // Checks adjacent moves and jump moves
    for(int i = -1; i <= 1; i++){
      for(int j = -1; j <= 1; j++){
        if(r1+i >= 9 && r1+i <= 25 && c1+j >= 1 && c1+j <= 17){ // If in board
          if(board[r1+i][c1+j] == 0){ // If adjacent is empty
            move = {r1+i, c1+j};
            moves.add(move);
          } else if(i != 0 && j != 0){
            if(r1+2i >= 9 && r1+2i <= 25 && c1+2j >= 1 && c1+2j <= 17){ // If in board
              if(board[r1+2i][c1+2j] == 0) { // If jump is empty
                move = {r1+2i, c1+2j};
                moves.add(move);
              }
            }
          }
        }
      }
    }

    return moves;

  }

  public int checkAdjacent(int r, int c){
    return 0;
  }
}
