package GeneticTraining;

import java.util.Arrays;

// TODO: https://www.youtube.com/watch?v=UcVJsV-tqlo

public class Trainer {

  public static void main(String[] args) {
    Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).intializePopulation();
    System.out.println("---------------------------------------------------------");
    System.out.println("Generation # 0 " + " | Fittest personality steps: " + population.getPersonalities()[0].getFitness());
    printPopulation(population, "Target Steps: " + GeneticAlgorithm.TARGET_STEPS);
  }

  public static void printPopulation(Population pop, String heading){
    System.out.println(heading);
    System.out.println("---------------------------------------------------------");
    for(int i = 0; i < pop.getPersonalities().length; i++){
      System.out.println("Personality #" + i + ": " + Arrays.toString(pop.getPersonalities()[i].getGenes()) +
              " | Fitness: " + pop.getPersonalities()[i].getFitness());
    }
  }
}
