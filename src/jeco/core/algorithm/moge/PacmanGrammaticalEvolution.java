package jeco.core.algorithm.moge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.logging.Logger;

import jeco.core.operator.evaluator.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.NaiveFitness;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.CustomExecutor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;


public class PacmanGrammaticalEvolution extends AbstractProblemGE {
	public static final Logger logger = Logger.getLogger(PacmanGrammaticalEvolution.class.getName());
	public static BufferedWriter writer;
	public static Path path = FileSystems.getDefault().getPath("logs", "Registro.log");
	
	//Execution parameters
	public int populationSize;
	public int generations;
	public double mutationProb;
  	public double crossProb;
  	public FitnessEvaluatorInterface fitnessFunc;
  	public ArrayList<Double> fitnessParams = new ArrayList<>(2); // for efficiency. NOT static
  	private Double bestFitness = Double.POSITIVE_INFINITY; // because minimization
  	public int iterPerIndividual; // games ran per evaluation
  	
  	public PacmanGrammaticalEvolution(String pathToBnf, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover, FitnessEvaluatorInterface fitnessFunc, int iterPerIndividual, int numberOfObjectives, int chromosomeLength, int maxCntWrappings, int codonUpperBound) {
  		super(pathToBnf, numberOfObjectives, chromosomeLength, maxCntWrappings, codonUpperBound);
  		
  		this.populationSize = maxPopulationSize;
		this.generations = maxGenerations;
		this.mutationProb = probMutation;
		this.crossProb = probCrossover;
		this.fitnessFunc = fitnessFunc;
		this.iterPerIndividual = iterPerIndividual;
		
		// Create log
		if(writer == null) {
		  	File dir = new File("logs");
		  	dir.mkdir();
		  	
		  	try {
		  	    Files.delete(path);
		  	} catch (NoSuchFileException x) {
		  	    //System.err.format("%s: no such" + " file or directory%n", path);
		  	} catch (DirectoryNotEmptyException x) {
		  	    System.err.format("%s not empty%n", path);
		  	} catch (IOException x) {
		  	    // File permission problems are caught here.
		  	    System.err.println(x);
		  	}
		  	
		  	try {
				writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.err.println("Error opening the log.");
				e.printStackTrace();
			}
		}
  	}

	public PacmanGrammaticalEvolution(String pathToBnf, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover, FitnessEvaluatorInterface fitnessFunc, int iterPerIndividual) {
		this(pathToBnf, maxPopulationSize, maxGenerations, probMutation, probCrossover, fitnessFunc, iterPerIndividual, NUM_OF_OBJECTIVES_DEFAULT, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		CustomExecutor exec = new CustomExecutor();
		GrammaticalAdapterController pacman = new GrammaticalAdapterController(phenotype.toString());
		Controller<EnumMap<GHOST,MOVE>> ghosts = new StarterGhosts();
		
		double score = exec.runExecution(pacman, ghosts, iterPerIndividual);
		fitnessParams.clear();
		fitnessParams.add(score);
		
		double fitness = fitnessFunc.evaluate(fitnessParams);
		
		// Security check
		if (fitness < 0) {
			logger.severe("ERROR: NEGATIVE FITNESS");
			fitness = 0;
		}
		
		// Log best fitness and respective phenotype
		if (fitness < bestFitness){
			try {
				writer.write("Best fitness found: " + fitness +
						", with score: " + score +
						" and phenotype: " + phenotype + System.lineSeparator());
				this.bestFitness = fitness;
			} catch (IOException e) {
				System.err.println("Error writing in log.");
				e.printStackTrace();
			}
		}
		
		solution.getObjectives().set(0, fitness);
	}	

	@Override
	public PacmanGrammaticalEvolution clone() {
		PacmanGrammaticalEvolution clone = new PacmanGrammaticalEvolution(super.pathToBnf, this.populationSize, this.generations, this.mutationProb, this.crossProb, this.fitnessFunc, this.iterPerIndividual);
		return clone;
	}

	public static void main(String[] args) {
		int populationSize = 100;// = 400;
		int generations = 100;// = 500;
		double mutationProb = 0.02;
	  	double crossProb = 0.6;
	  	FitnessEvaluatorInterface fitnessFunc = new NaiveFitness();
	  	int iterPerIndividual = 2; // games ran per evaluation
	  	
		// First create the problem
		PacmanGrammaticalEvolution problem = new PacmanGrammaticalEvolution("test/pacman.bnf", populationSize, generations, mutationProb, crossProb, fitnessFunc, iterPerIndividual);
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, populationSize, generations, mutationProb, crossProb);
		
		// We can set operators using
		//algorithm.setSelectionOperator(selectionOperator);
		//algorithm.setCrossoverOperator(crossoverOperator);
		//algorithm.setMutationOperator(mutationOperator);

		/** We can set multithread execution:
		// Set multithreading
		MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, avalaibleThreads);
		// Execute algorithm
		Solutions<Variable<Integer>> solutions = masterWorker.execute();

		or single thread: */
		algorithm.initialize();
		Solutions<Variable<Integer>> solutions = algorithm.execute();

		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info("Fitness = " + solution.getObjectives().get(0));
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
		}
	}

}
