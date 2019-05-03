package GeneticTraining;

import java.util.Arrays;

// TODO: https://www.youtube.com/watch?v=UcVJsV-tqlo

public class Trainer {

  public static void main(String[] args) {
    Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).intializePopulation();
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

  public static void printPopulation(Population pop){
    System.out.println("---------------------------------------------------------");
    for(int i = 0; i < pop.getPersonalities().length; i++){
      System.out.println("Personality #" + i + ": " + Arrays.toString(pop.getPersonalities()[i].getGenes()) +
              " | Fitness: " + pop.getPersonalities()[i].getFitness());
    }
  }
}
