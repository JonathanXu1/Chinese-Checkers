package GeneticTraining;

import Game.ChineseCheckers;

public class Personality {
  private double[] genes;
  private int fitness = -1;
  private boolean changedGenes = true;

  private ChineseCheckers algorithm = new ChineseCheckers();

  public Personality(int length){
    genes = new double[length];
    for(int i = 0; i < genes.length; i++){
      genes[i] = Math.random()*50;
    }
    algorithm.setScoreMultiplier(genes);
  }

  public double[] getGenes(){
    return genes;
  }

  public int getFitness(){
    if(changedGenes){
      recalculateFitness();
    }
    return  fitness;
  }

  public void recalculateFitness(){
    fitness = 0;
    String board = GeneticAlgorithm.BOARD_INIT;
    while(!algorithm.checkWin() && fitness < 100){
      algorithm.readGrid(board);
      String move = algorithm.makeMove();
      String startPos = move.substring(5,move.indexOf(')') + 1);
      String stopPos = move.substring(move.lastIndexOf('('));
      board = board.substring(0, board.indexOf('(')) + startPos + board.substring(board.indexOf(')')+1, board.lastIndexOf('(')) + stopPos;
      fitness ++;
    }
    changedGenes = false;
  }

  public void setGenesChanged(){
    changedGenes = true;
  }
}
