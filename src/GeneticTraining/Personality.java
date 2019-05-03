package GeneticTraining;

public class Personality {
  private int[] genes;
  private int fitness = 69; //Integer.MAX_VALUE

  public Personality(int length){
    genes = new int[length];
  }

  public Personality initalize(){
    for(int i = 0; i < genes.length; i++){
      if(Math.random() >= 0.5){
        genes[i] = 1;
      } else {
        genes[i] = 0;
      }
    }
    return this;
  }

  public int[] getGenes(){
    return genes;
  }

  public int getFitness(){
    return  fitness;
  }

  public void recalculateFitness(){
    // TODO
    fitness = 0;
    fitness --;
  }
}
