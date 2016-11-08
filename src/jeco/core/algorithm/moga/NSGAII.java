package jeco.core.algorithm.moga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;
import jeco.core.algorithm.Algorithm;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.assigner.FrontsExtractor;
import jeco.core.operator.comparator.ComparatorNSGAII;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.Maths;

/**
 *
 *
 * Input parameters: - MAX_GENERATIONS - MAX_POPULATION_SIZE
 *
 * Operators: - CROSSOVER: Crossover operator - MUTATION: Mutation operator -
 * SELECTION: Selection operator
 *
 * @author José L. Risco-Martín
 *
 */
public class NSGAII<T extends Variable<?>> extends Algorithm<T> {

  private static final Logger logger = Logger.getLogger(NSGAII.class.getName());
  /////////////////////////////////////////////////////////////////////////
  protected int maxGenerations;
  protected int maxPopulationSize;
  /////////////////////////////////////////////////////////////////////////
  protected Comparator<Solution<T>> dominance;
  protected int currentGeneration;
  protected Solutions<T> population;

  public Solutions<T> getPopulation() {
    return population;
  }
  protected MutationOperator<T> mutationOperator;
  protected CrossoverOperator<T> crossoverOperator;
  protected SelectionOperator<T> selectionOperator;

  public NSGAII(Problem<T> problem, int maxPopulationSize, int maxGenerations, MutationOperator<T> mutationOperator, CrossoverOperator<T> crossoverOperator, SelectionOperator<T> selectionOperator) {
    super(problem);
    this.maxPopulationSize = maxPopulationSize;
    this.maxGenerations = maxGenerations;
    this.mutationOperator = mutationOperator;
    this.crossoverOperator = crossoverOperator;
    this.selectionOperator = selectionOperator;
  }

  @Override
  public void initialize() {
    dominance = new SolutionDominance<T>();
    // Create the initial solutionSet
    population = problem.newRandomSetOfSolutions(maxPopulationSize);
    problem.evaluate(population);
    // Compute crowding distance
    CrowdingDistance<T> assigner = new CrowdingDistance<T>(problem.getNumberOfObjectives());
    assigner.execute(population);
    currentGeneration = 0;

  }

  @Override
  public Solutions<T> execute() {
    int nextPercentageReport = 10;
    HashMap<String,String> obsData = new HashMap<>();
    // For observers:
    obsData.put("MaxGenerations", String.valueOf(maxGenerations));
    double hv = Double.MAX_VALUE;
    stop = false;
    while ((currentGeneration < maxGenerations) && !stop){
      step();
      
      // Report Hv each 10 generations to observer:
      if ((this.countObservers() > 0) && ((currentGeneration % 10) == 0)) {
          hv = Maths.calculateHypervolume(population, problem.getNumberOfObjectives());
          obsData.put("CurrentGeneration", String.valueOf(currentGeneration));
          obsData.put("Hypervolume", String.valueOf(hv));
          this.setChanged();
          this.notifyObservers(obsData);
      }
      
      int percentage = Math.round((currentGeneration * 100) / maxGenerations);
      if (percentage == nextPercentageReport) {
          logger.info(percentage + "% performed ... -> Hypervol.: "+hv);
          nextPercentageReport += 10;
      }

    }
    return this.getCurrentSolution();
  }

  public Solutions<T> getCurrentSolution() {
    population.reduceToNonDominated(dominance);
    return population;
  }

  public void step() {
    currentGeneration++;
    // Create the offSpring solutionSet
    if (population.size() < 2) {
      logger.severe("Generation: " + currentGeneration + ". Population size is less than 2.");
      return;
    }

    Solutions<T> childPop = new Solutions<T>();
    Solution<T> parent1, parent2;
    for (int i = 0; i < (maxPopulationSize / 2); i++) {
      //obtain parents
      parent1 = selectionOperator.execute(population).get(0);
      parent2 = selectionOperator.execute(population).get(0);
      Solutions<T> offSpring = crossoverOperator.execute(parent1, parent2);
      for (Solution<T> solution : offSpring) {
        mutationOperator.execute(solution);
        childPop.add(solution);
      }
    } // for
    problem.evaluate(childPop);

    // Create the solutionSet union of solutionSet and offSpring
    Solutions<T> mixedPop = new Solutions<T>();
    mixedPop.addAll(population);
    mixedPop.addAll(childPop);

    // Reducing the union
    population = reduce(mixedPop, maxPopulationSize);
    logger.fine("Generation " + currentGeneration + "/" + maxGenerations + "\n" + population.toString());
  } // step

  public Solutions<T> reduce(Solutions<T> pop, int maxSize) {
    FrontsExtractor<T> extractor = new FrontsExtractor<T>(dominance);
    ArrayList<Solutions<T>> fronts = extractor.execute(pop);

    Solutions<T> reducedPop = new Solutions<T>();
    CrowdingDistance<T> assigner = new CrowdingDistance<T>(problem.getNumberOfObjectives());
    Solutions<T> front;
    int i = 0;
    while (reducedPop.size() < maxSize && i < fronts.size()) {
      front = fronts.get(i);
      assigner.execute(front);
      reducedPop.addAll(front);
      i++;
    }

    ComparatorNSGAII<T> comparator = new ComparatorNSGAII<T>();
    if (reducedPop.size() > maxSize) {
      Collections.sort(reducedPop, comparator);
      while (reducedPop.size() > maxSize) {
        reducedPop.remove(reducedPop.size() - 1);
      }
    }
    return reducedPop;
  }

  public void setMutationOperator(MutationOperator<T> mutationOperator) {
    this.mutationOperator = mutationOperator;
  }

  public void setCrossoverOperator(CrossoverOperator<T> crossoverOperator) {
    this.crossoverOperator = crossoverOperator;
  }

  public void setSelectionOperator(SelectionOperator<T> selectionOperator) {
    this.selectionOperator = selectionOperator;
  }

  public void setMaxGenerations(int maxGenerations) {
    this.maxGenerations = maxGenerations;
  }

  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize;
  }
}
