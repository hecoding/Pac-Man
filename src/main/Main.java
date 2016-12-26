package main;

import java.util.logging.Logger;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;
import view.cli.CLIView;

public class Main {
	static int avalaibleThreads = Runtime.getRuntime().availableProcessors();
	static Logger logger = PacmanGrammaticalEvolution.logger;
	final static int ticks = 19;

	public static void main(String[] args) {
	  	// Configure parameters
		int populationSize = 100;// = 400;
		int generations = 100;// = 500;
		double mutationProb = 0.02;
	  	double crossProb = 0.6;
	  	FitnessEvaluatorInterface fitnessFunc = new NaiveFitness();
	  	int iterPerIndividual = 3;// = 10; // games ran per evaluation
	  	
		// First create the problem
		PacmanGrammaticalEvolution problem = new PacmanGrammaticalEvolution("test/pacman.bnf", populationSize, generations, mutationProb, crossProb, fitnessFunc, iterPerIndividual);
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, populationSize, generations, mutationProb, crossProb);
		
		// We can set different operators using
	  	//algorithm.setSelectionOperator(selectionOperator);
		//algorithm.setCrossoverOperator(crossoverOperator);
		//algorithm.setMutationOperator(mutationOperator);
		
		// Set multithreading
		MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, avalaibleThreads);

		@SuppressWarnings("unused")
		CLIView view = new CLIView(masterWorker, problem);
	}

}
