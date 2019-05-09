package GeneticTraining;

import java.util.Arrays;

/**
 * This class represents a group of personalities
 *
 * @author Jonathan Xu
 * @since 2019-05-01
 */
public class Population {
  private Personality[] personalities;

  /**
   * Constructor
   * @param length The population size
   */
  public Population(int length){
    personalities = new Personality[length];
  }

  /**
   * Creates a new population
   * @return The new population
   */
  public Population initializePopulation(){
    for(int i = 0; i < personalities.length; i++){
      personalities[i] = new Personality(GeneticAlgorithm.NUM_GENES);
    }
    sortPersonalitiesByFitness();
    return this;
  }

  /**
   * Getter for the personalities within the population
   * @return The personalities within the population
   */
  public Personality[] getPersonalities(){
    return personalities;
  }

  /**
   * Personality setter
   * @param newPersonalities The new personalities to be set
   */
  public void setPersonalities(Personality[] newPersonalities){
    this.personalities = newPersonalities;
  }

  /**
   * Sorts the personalities within the population by their fitness
   */
  public void sortPersonalitiesByFitness(){
    // Sorts smallest to largest since fitness = steps taken
    Arrays.sort(personalities, (p1, p2) ->{
      if(p1.getFitness() > p2.getFitness()) return 1;
      else if (p1.getFitness() < p2.getFitness()) return -1;
      else return 0;
    });
  }
}
