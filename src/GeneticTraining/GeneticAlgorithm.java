/* Currently working on updating scoring so that personalities
to the end as fast as possible by themselves.

Hopefully can then train between personalities against others
*/
package GeneticTraining;

public class GeneticAlgorithm{
  public static final int POPULATION_SIZE = 8;
  public static final int TRAIT_NUMS = 10;
  public static final int TARGET_STEPS = 27; //In 1971, Octave Levenspiel found a solution in 27 moves [Ibid.]; we demonstrate that no shorter solution exists.

  private Population evolve(Population population){
    return mutatePopulation(crossoverPopulation(population));
  }

  private Population crossoverPopulation(Population population){
    return population;
  }

  private Population mutatePopulation(Population population){
    return population;
  }
}