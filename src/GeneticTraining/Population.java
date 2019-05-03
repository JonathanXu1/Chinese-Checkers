package GeneticTraining;

import java.util.ArrayList;
import java.util.Arrays;

public class Population {
  private Personality[] personalities;

  public Population(int length){
    personalities = new Personality[length];
  }

  public Population intializePopulation(){
    for(int i = 0; i < personalities.length; i++){
      personalities[i] = new Personality(GeneticAlgorithm.TRAIT_NUMS);
    }
    sortPersonalitiesByFitness();
    return this;
  }

  public Personality[] getPersonalities(){
    return personalities;
  }

  public void sortPersonalitiesByFitness(){
    // Sorts in reverse order since fitness = steps taken
    Arrays.sort(personalities, (p1, p2) ->{
      if(p1.getFitness() > p2.getFitness()) return 1;
      else if (p1.getFitness() < p2.getFitness()) return -1;
      else return 0;
    });
  }
}
