package jeco.core.optimization.threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.algorithm.Algorithm;
import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.GrammaticalEvolution_example;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class MasterWorkerThreads<V extends Variable<?>> extends Problem<V> {

  private static final Logger logger = Logger.getLogger(MasterWorkerThreads.class.getName());
  protected Algorithm<V> algorithm = null;
  protected Problem<V> problem = null;
  protected LinkedBlockingQueue<Solution<V>> sharedQueue = new LinkedBlockingQueue<Solution<V>>();
  protected ArrayList<Problem<V>> problemClones = new ArrayList<Problem<V>>();
  protected Integer numWorkers = null;

  public MasterWorkerThreads(Algorithm<V> algorithm, Problem<V> problem, Integer numWorkers) {
    super(problem.getNumberOfVariables(), problem.getNumberOfObjectives());
    for (int i = 0; i < numberOfVariables; ++i) {
      super.lowerBound[i] = problem.getLowerBound(i);
      super.upperBound[i] = problem.getUpperBound(i);
    }
    this.algorithm = algorithm;
    this.problem = problem;
    this.numWorkers = numWorkers;
    for (int i = 0; i < numWorkers; ++i) {
      problemClones.add(problem.clone());
    }
  }

  public MasterWorkerThreads(Algorithm<V> algorithm, Problem<V> problem) {
    this(algorithm, problem, Runtime.getRuntime().availableProcessors());
  }

  @Override
  public void evaluate(Solutions<V> solutions) {
    sharedQueue.addAll(solutions);
    LinkedList<Worker<V>> workers = new LinkedList<Worker<V>>();
    for (int i = 0; i < numWorkers; ++i) {
      Worker<V> worker = new Worker<V>(problemClones.get(i), sharedQueue);
      workers.add(worker);
      worker.start();
    }
    for (Worker<V> worker : workers) {
      try {
        worker.join();
      } catch (InterruptedException e) {
        logger.severe(e.getLocalizedMessage());
        logger.severe("Main thread cannot join to: " + worker.getId());
      }
    }
  }
  
  @Override
  public void evaluate(Solution<V> solution) {
    logger.log(Level.SEVERE, this.getClass().getSimpleName() + "::evaluate() - I do not know why I am here, doing nothing to evaluate solution");
  }

  @Override
  public Solutions<V> newRandomSetOfSolutions(int size) {
    return problem.newRandomSetOfSolutions(size);
  }

  public Solutions<V> execute() {
    algorithm.setProblem(this);
    algorithm.initialize();
    return algorithm.execute();
  }

  @Override
  public Problem<V> clone() {
    logger.severe("This master cannot be cloned.");
    return null;
  }

  public static void main(String[] args) {
    long begin = System.currentTimeMillis();
    // First create the problem
    GrammaticalEvolution_example problem = new GrammaticalEvolution_example("test/grammar_example.bnf");
    // Second create the algorithm
    GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, 100, 250);
    // Now the master/worker
    MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, 4);
    Solutions<Variable<Integer>> solutions = masterWorker.execute();
    for (Solution<Variable<Integer>> solution : solutions) {
      logger.info("Fitness = (" + solution.getObjectives().get(0) + ", " + solution.getObjectives().get(1) + ")");
      logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
    }
    long end = System.currentTimeMillis();
    logger.info("Time: " + ((end - begin) / 1000.0) + " seconds");
  }
}
