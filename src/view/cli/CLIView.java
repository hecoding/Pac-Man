package view.cli;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;
import java.util.logging.Logger;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
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

public class CLIView {
	final static int ticks = 19;
	Logger logger;
	GrammaticalEvolution algorithm;
	PacmanGrammaticalEvolution problem;
	MasterWorkerThreads<Variable<Integer>> masterWorker; // if algorithm is multithreading
	
	public CLIView(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem) {
		logger = GrammaticalEvolution.logger;
		this.algorithm = algorithm;
		this.problem = problem;
		this.exec();
	}
	
	public CLIView(MasterWorkerThreads<Variable<Integer>> masterWorker, PacmanGrammaticalEvolution problem) {
		logger = GrammaticalEvolution.logger;
		this.masterWorker = masterWorker;
		this.problem = problem;
		this.exec();
	}

	private void exec() {
		// Execute algorithm
		Solutions<Variable<Integer>> solutions;
		if(algorithm == null)
			solutions = masterWorker.execute();
		else
			solutions = algorithm.execute();
		
		// Log solution
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info(System.lineSeparator());
			logger.info("Fitness =  " + solution.getObjectives().get(0));
			logger.info("Average points = " + NaiveFitness.fitnessToPoints( solution.getObjectives().get(0) ));
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
