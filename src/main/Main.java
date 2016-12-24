package main;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;
import java.util.logging.Logger;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Main {
	static int avalaibleThreads = Runtime.getRuntime().availableProcessors();
	static Logger logger = PacmanGrammaticalEvolution.logger;
	final static int ticks = 19;

	public static void main(String[] args) {
	  	// Configure parameters
		int populationSize = 100;// = 400;
		int generations = 500;// = 500;
		double mutationProb = 0.02;
	  	double crossProb = 0.6;
	  	FitnessEvaluatorInterface fitnessFunc = new NaiveFitness();
	  	int iterPerIndividual = 10; // games ran per evaluation
	  	
		// First create the problem
		PacmanGrammaticalEvolution problem = new PacmanGrammaticalEvolution("test/pacman.bnf", populationSize, generations, mutationProb, crossProb, fitnessFunc, iterPerIndividual);
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, populationSize, generations, mutationProb, crossProb);
		
		// We can set operators using
	  	//algorithm.setSelectionOperator(selectionOperator);
		//algorithm.setCrossoverOperator(crossoverOperator);
		//algorithm.setMutationOperator(mutationOperator);
		
		// Set multithreading
		MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, avalaibleThreads);
		// Execute algorithm
	    Solutions<Variable<Integer>> solutions = masterWorker.execute();
	    
	    // Log solution
	    for (Solution<Variable<Integer>> solution : solutions) {
	      logger.info(System.lineSeparator());
	      logger.info("Fitness =  " + solution.getObjectives().get(0));
	      logger.info("Average points = " + ((NaiveFitness) fitnessFunc).fitnessToPoints(solution.getObjectives().get(0)));
	      logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
	    }
	    
	    // Run visuals for the best program
	    System.out.println();
	    System.out.println("Press any key to begin visualization of best individual...");
        Scanner sc = new java.util.Scanner(System.in);
        sc.nextLine();
        sc.close();
        
	    Executor exec = new Executor();
	    exec.runGame(new GrammaticalAdapterController(problem.generatePhenotype(solutions.get(0)).toString()), new StarterGhosts(), true, ticks);
	    
	}

	public static void maine(String[] args) {
		runPhenotype("?BEE?PH");
		//multipleExecAvg("?BEE?PH", 5000);
	}

	public static void runPhenotype(String ph) {
		Executor exec = new Executor();
		exec.runGame(new GrammaticalAdapterController(ph), new StarterGhosts(), true, ticks);
	}

	public static void multipleExecAvg(String ph, int trials) {
		Controller<MOVE> pacManController = new GrammaticalAdapterController(ph);
		Controller<EnumMap<GHOST, MOVE>> ghostController = new StarterGhosts();

		Executor exec = new Executor();
		ArrayList<Double> results = exec.runExperiment(pacManController, ghostController, trials);

		System.out.println("avgScore:" + results.get(0));
		System.out.println("avgTime:" + results.get(1));
	}

}
