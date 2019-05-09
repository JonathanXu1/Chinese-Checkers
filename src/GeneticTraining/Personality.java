package GeneticTraining;

import Game.ChineseCheckers;

/**
 * This class contains the set of genes (parameters) that are used as score multipliers
 *
 * @Author Jonathan Xu
 * @Since 2019-05-01
 */
public class Personality {
  private double[] genes;
  private int fitness = -1;
  private boolean changedGenes = true;

  private ChineseCheckers algorithm = new ChineseCheckers();

  /**
   * Constructor
   * @param length The number of genes within the personality
   */
  public Personality(int length){
    //genes = new double[length];
    // Starts with a predefined, working value of genes
    genes = new double[]{0.5, 0.7, 3};

    for(int i = 0; i < length; i++){
      genes[i] += Math.round(Math.random() * 100.0) / 100.0 -1;
    }
    algorithm.setScoreMultiplier(genes);
  }

  /**
   * Getter for the genes within the personality
   * @return The personality's genes
   */
  public double[] getGenes(){
    return genes;
  }

  /**
   * Setter for the genes within the personality
   * @param newGenes The new genes to be set
   */
  public void setGenes(double[] newGenes){
    genes = newGenes;
    changedGenes = true;
  }

  /**
   * Calculates the fitness level for each personality
   * @return the number of steps required to move all pieces to the other side
   */
  public int getFitness(){
    if(changedGenes){
      recalculateFitness();
      //fitness = (int)(Math.random()*100);
      changedGenes = false;
    }
    return  fitness;
  }

  /**
   * Calculates the fitness based on the multipliers provided by the genes
   * Emulates a 1v0 board and lets the algorithm get to the other side
   */
  public void recalculateFitness(){
    System.out.println("recalculate");
    fitness = 0;
    String board = GeneticAlgorithm.BOARD_INIT;
    algorithm.readGrid(board);

    while(!algorithm.checkWin() && fitness < 100){
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
        fitness = 100;
      } else {
        board = board.substring(0, board.indexOf(startPos)) + stopPos + board.substring(board.indexOf(startPos) + startPos.length());
        algorithm.readGrid(board);
        fitness ++;
      }
    }
  }

}
