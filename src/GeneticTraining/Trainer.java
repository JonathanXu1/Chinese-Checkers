package GeneticTraining;

import java.util.Arrays;

/**
 * This class is responsible for initiating the training of the algorithm
 *
 * @Author Jonathan Xu
 * @Since 2019-05-01
 */
public class Trainer {

  public static void main(String[] args) {
    System.out.println("Begin training:");
    System.out.println("Reading from file has not yet been implemented.");
    Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
    GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
    System.out.println("Target steps: " + GeneticAlgorithm.TARGET_STEPS);
    System.out.println("---------------------------------------------------------");
    System.out.println("Generation # 0 " + " | Fittest personality steps: " + population.getPersonalities()[0].getFitness());
    printPopulation(population);

    int genNumber = 0;
    while(population.getPersonalities()[0].getFitness() > GeneticAlgorithm.TARGET_STEPS){
      genNumber ++;
      System.out.println("\n---------------------------------------------------------");
      population = geneticAlgorithm.evolve(population);
      population.sortPersonalitiesByFitness();

      System.out.println("Generation # " + genNumber + " | Fittest personality steps: " +
              population.getPersonalities()[0].getFitness());
      printPopulation(population);
    }
  }

  /**
   * Displays the training results
   * @param pop The population to print
   */
  public static void printPopulation(Population pop){
    System.out.println("---------------------------------------------------------");
    for(int i = 0; i < pop.getPersonalities().length; i++){
      System.out.println("Personality #" + i + ": " + Arrays.toString(pop.getPersonalities()[i].getGenes()) +
              " | Fitness: " + pop.getPersonalities()[i].getFitness());
    }
  }
}
