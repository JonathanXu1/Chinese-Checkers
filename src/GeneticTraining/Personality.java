package GeneticTraining;

import Game.ChineseCheckers;

public class Personality {
  private double[] genes;
  private int fitness = -1;
  private boolean changedGenes = true;

  private ChineseCheckers algorithm = new ChineseCheckers();

  public Personality(int length){
    //genes = new double[length];
    genes = new double[]{3, 1, 1, 50, 10};

    for(int i = 0; i < length; i++){

      genes[i] += Math.round(Math.random() * 100.0) / 100.0 -1;

      //genes[i] = Math.random()*5;
    }
    algorithm.setScoreMultiplier(genes);
  }

  public double[] getGenes(){
    return genes;
  }

  public void setGenes(double[] newGenes){
    genes = newGenes;
    changedGenes = true;
  }

  public int getFitness(){
    if(changedGenes){
      recalculateFitness();
      //fitness = (int)(Math.random()*100);
      changedGenes = false;
    }
    return  fitness;
  }

  public void recalculateFitness(){
    System.out.println("recalculate");
    fitness = 0;
    String board = GeneticAlgorithm.BOARD_INIT;
    algorithm.readGrid(board);

    while(!algorithm.checkWin() && fitness < 300){
      String move = algorithm.makeMove();
      String startPos = move.substring(move.indexOf('('), move.indexOf(')') + 1);
      String stopPos = move.substring(move.lastIndexOf('('));

      /*
      System.out.println(board);
      System.out.println(move);
      System.out.println(startPos);
      System.out.println(stopPos);
      */
      if(!board.contains(startPos)){ // If algorithm cannot find best next move, it breaks
        fitness = 300;
      } else {
        board = board.substring(0, board.indexOf(startPos)) + stopPos + board.substring(board.indexOf(startPos) + startPos.length());
        algorithm.readGrid(board);
        fitness ++;
      }
    }
  }

}
