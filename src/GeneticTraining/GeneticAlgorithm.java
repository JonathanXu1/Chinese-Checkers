/* Currently working on updating scoring so that personalities
to the end as fast as possible by themselves.

Hopefully can then train between personalities against others
*/
package GeneticTraining;

/**
 * This class is responsible for the creation, mutation and crossover of populations and personalities.
 *
 * @author Jonathan Xu
 * @since 2019-05-01
 */

public class GeneticAlgorithm{

  public static final int POPULATION_SIZE = 8;
  public static final int NUM_GENES = 3;
  public static final double MUTATION_RATE = 0.30;
  public static final int NUM_ELITE_PERSONALITIES = 1; // Will not be subjected to crossover/mutation
  public static final int TARGET_STEPS = 27; //In 1971, Octave Levenspiel found a solution in 27 moves [Ibid.]; we demonstrate that no shorter solution exists.
  public static final int TOURNAMENT_SELECTION_SIZE = 4;

  public static final String BOARD_INIT = "BOARD 1 0 (9,5) (10,5) (10,6) (11,5) (11,6) (11,7) (12,5) (12,6) (12,7) (12,8)";

  /**
   * Evolves a population by mutating and crossing its personalities
   * @param population The population to be evolved.
   * @return A mutated and crossed population.
   */
  public Population evolve(Population population){
    return mutatePopulation(crossoverPopulation(population));
  }

  /**
   * Randomly reorganizes the personalities in a population, except for the elite personality
   * @param population The population to cross-over
   * @return crossoverPop The swapped population
   */
  private Population crossoverPopulation(Population population){
    Population crossoverPop = new Population(POPULATION_SIZE);
    Personality[] newPersonalities = new Personality[POPULATION_SIZE];
    for(int i = 0; i < NUM_ELITE_PERSONALITIES; i++){
      newPersonalities[i] = population.getPersonalities()[i];
    }
    for(int i = NUM_ELITE_PERSONALITIES ; i < POPULATION_SIZE; i++){
      Personality p1 = selectTourneyPop(population).getPersonalities()[0];
      Personality p2 = selectTourneyPop(population).getPersonalities()[0];
      newPersonalities[i] = crossoverPersonality(p1, p2);
    }
    crossoverPop.setPersonalities(newPersonalities);
    return crossoverPop;
  }

  /**
   * Mutates a population by mutating its personalities
   * @param population The population to be mutated
   * @return mutatePop The mutated population
   */
  private Population mutatePopulation(Population population){
    Population mutatePop = new Population(POPULATION_SIZE);
    Personality[] newPersonalities = new Personality[POPULATION_SIZE];
    for(int i = 0; i < NUM_ELITE_PERSONALITIES; i++){
      newPersonalities[i] = population.getPersonalities()[i];
    }
    for(int i = NUM_ELITE_PERSONALITIES; i < POPULATION_SIZE; i++){
      newPersonalities[i] = mutatePersonality(population.getPersonalities()[i]);
    }
    mutatePop.setPersonalities(newPersonalities);
    return mutatePop;
  }

  /**
   * Crosses over a personality by randomly choosing genes between two personalities
   * @param p1 Personality 1
   * @param p2 Personality 2
   * @return crossover The new personality
   */
  private Personality crossoverPersonality(Personality p1, Personality p2){
    Personality crossover = new Personality(NUM_GENES);
    double[] newGenes = new double[NUM_GENES];
    for(int i = 0; i < NUM_GENES; i++){
      if(Math.random() < 0.5){
        newGenes[i] = p1.getGenes()[i];
      } else {
        newGenes[i] = p2.getGenes()[i];
      }
    }
    crossover.setGenes(newGenes);
    return crossover;
  }

  /**
   * Randomly swaps traits in the personality to discover better genes
   * @param personality The personality to mutate
   * @return mutated The muated personality
   */
  private Personality mutatePersonality(Personality personality){
    Personality mutated = new Personality(NUM_GENES);
    double[] newGenes = new double[NUM_GENES];
    for(int i = 0; i < NUM_GENES; i++){
      if(Math.random() < MUTATION_RATE){
        newGenes[i] = personality.getGenes()[i] + Math.round(Math.random() * 100.0) / 100.0 -1;;
      } else {
        newGenes[i] = personality.getGenes()[i];
      }
    }
    mutated.setGenes(newGenes);
    return mutated;
  }

  /**
   * Creates a small population with personalities randomly selected from a population
   * @param pop The reference population
   * @return tournamentPop The new population
   */
  private Population selectTourneyPop(Population pop){
    Population tournamentPop = new Population(TOURNAMENT_SELECTION_SIZE);
    Personality[] newPersonalities = new Personality[NUM_GENES];
    for(int i = 0; i < TOURNAMENT_SELECTION_SIZE; i++){
      newPersonalities[i] = pop.getPersonalities()[(int)(Math.random()*POPULATION_SIZE)];
    }
    tournamentPop.setPersonalities(newPersonalities);
    tournamentPop.sortPersonalitiesByFitness();
    return  tournamentPop;
  }
}