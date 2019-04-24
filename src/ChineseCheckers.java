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
}
