/* Currently working on updating scoring so that personalities
to the end as fast as possible by themselves.

Hopefully can then train between personalities against others
*/
package GeneticTraining;

public class GeneticAlgorithm{
  public static final int POPULATION_SIZE = 8;
  public static final int NUM_GENES = 10;
  public static final double MUTATION_RATE = 0.20;
  public static final int NUM_ELITE_PERSONALITIES = 1; // Will not be subjected to crossover/mutation
  public static final int TARGET_STEPS = 27; //In 1971, Octave Levenspiel found a solution in 27 moves [Ibid.]; we demonstrate that no shorter solution exists.
  public static final int TOURNAMENT_SELECTION_SIZE = 4;

  public Population evolve(Population population){
    return mutatePopulation(crossoverPopulation(population));
  }

  private Population crossoverPopulation(Population population){
    Population crossoverPop = new Population(POPULATION_SIZE);
    for(int i = 0; i < NUM_ELITE_PERSONALITIES; i++){
      // TODO: Getter
      crossoverPop.getPersonalities()[i] = population.getPersonalities()[i];
    }
    for(int i = NUM_ELITE_PERSONALITIES ; i < POPULATION_SIZE; i++){
      Personality p1 = selectTourneyPop(population).getPersonalities()[0];
      Personality p2 = selectTourneyPop(population).getPersonalities()[0];
      // TODO: Getter
      crossoverPop.getPersonalities()[i] = crossoverPersonality(p1, p2);
    }
    return crossoverPop;
  }

  private Population mutatePopulation(Population population){
    Population mutatePop = new Population(POPULATION_SIZE);
    for(int i = 0; i < NUM_ELITE_PERSONALITIES; i++){
      // TODO: Getter
      mutatePop.getPersonalities()[i] = population.getPersonalities()[i];
    }
    for(int i = NUM_ELITE_PERSONALITIES; i < POPULATION_SIZE; i++){
      mutatePop.getPersonalities()[i] = mutatePersonality(population.getPersonalities()[i]);
    }
    return population;
  }

  // Randomly mixes the traits from two personalities
  private Personality crossoverPersonality(Personality p1, Personality p2){
    Personality crossover = new Personality(NUM_GENES);
    for(int i = 0; i < NUM_GENES; i++){
      // TODO: getter
      if(Math.random() < 0.5){
        crossover.getGenes()[i] = p1.getGenes()[i];
      } else {
        crossover.getGenes()[i] = p2.getGenes()[i];
      }
    }
    return crossover;
  }

  // TODO: Verify that mutation algo works
  // Randomly swaps traits in the personality for randomness
  private Personality mutatePersonality(Personality personality){
    Personality mutated = new Personality(NUM_GENES);
    for(int i = 0; i < personality.getGenes().length; i++){
      if(Math.random() < MUTATION_RATE){
        if(Math.random() < 0.5){
          mutated.getGenes()[i] += 0.5;
        } else {
          mutated.getGenes()[i] -= 0.5;
        }
      } else {
        mutated.getGenes()[i] = personality.getGenes()[i];
      }
    }
    return mutated;
  }

  // Selects outputs random personalities in sorted order
  private Population selectTourneyPop(Population pop){
    Population tournamentPop = new Population(TOURNAMENT_SELECTION_SIZE);
    for(int i = 0; i < TOURNAMENT_SELECTION_SIZE; i++){
      // TODO: getter
      tournamentPop.getPersonalities()[i] = pop.getPersonalities()[(int)(Math.random()*pop.getPersonalities().length)];
    }
    tournamentPop.sortPersonalitiesByFitness();
    return  tournamentPop;
  }
}