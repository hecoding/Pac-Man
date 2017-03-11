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

import jeco.core.operator.evaluator.fitness.MOFitnessWrapper;
import jeco.core.operator.evaluator.fitness.NaiveFitness;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.CustomExecutor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.util.GameInfo;


public class PacmanGrammaticalEvolution extends AbstractProblemGE {
	public static final Logger logger = Logger.getLogger(PacmanGrammaticalEvolution.class.getName());
	public static BufferedWriter writer;
	public static Path path = FileSystems.getDefault().getPath("logs", "Registro.log");
	
	//Execution parameters
	public int populationSize;
	public int generations;
	public double mutationProb;
  	public double crossProb;
  	public MOFitnessWrapper fitnessWrapper;
  	private ArrayList<Double> bestFitness;
  	public int iterPerIndividual; // games ran per evaluation
  	public int codonUpperBound;
  	
  	public PacmanGrammaticalEvolution(String pathToBnf, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover, MOFitnessWrapper fitnessWrapper, int iterPerIndividual, int chromosomeLength, int maxCntWrappings, int codonUpperBound) {
  		super(pathToBnf, fitnessWrapper.getNumberOfObjs(), chromosomeLength, maxCntWrappings, codonUpperBound);
  		
  		this.populationSize = maxPopulationSize;
		this.generations = maxGenerations;
		this.mutationProb = probMutation;
		this.crossProb = probCrossover;
		this.fitnessWrapper = fitnessWrapper;
		this.iterPerIndividual = iterPerIndividual;
		//chromosomeLenght == numOfVariables, no need to save
		this.codonUpperBound = codonUpperBound;
		
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

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		CustomExecutor exec = new CustomExecutor();
		GrammaticalAdapterController pacman = new GrammaticalAdapterController(phenotype.toString());
		Controller<EnumMap<GHOST,MOVE>> ghosts = new StarterGhosts();
		
		GameInfo avgGameInfo = exec.runExecution(pacman, ghosts, iterPerIndividual);
		
		ArrayList<Double> MOFitness = fitnessWrapper.evaluate(avgGameInfo);
		
		// Security check
		for (int i = 0; i < MOFitness.size(); i++) {
			Double objFitness = MOFitness.get(i);
			
			if (objFitness < 0) {
				logger.severe("ERROR: NEGATIVE FITNESS");
				System.err.println("FITNESS < 0!!!!!!");
				MOFitness.set(i, this.fitnessWrapper.getWorstFitness(i));
			}
		}
		
		// Log best fitness and respective phenotype
		if (bestFitness == null || MOFitness.get(0) < bestFitness.get(0)){ // jorge dime cómo querías la comparación
			try {
				writer.write("Best fitness found: " + MOFitness +
						", with score: " + avgGameInfo.getScore() +
						" and phenotype: " + phenotype + System.lineSeparator());
				this.bestFitness = MOFitness;
			} catch (IOException e) {
				System.err.println("Error writing in log.");
				e.printStackTrace();
			}
		}
		
		// Return objectives
		for (int i = 0; i < MOFitness.size(); i++) {
			solution.getObjectives().set(i, MOFitness.get(i));
		}
	}	

	@Override
	public PacmanGrammaticalEvolution clone() {
		PacmanGrammaticalEvolution clone = new PacmanGrammaticalEvolution(this.pathToBnf, this.populationSize, this.generations, this.mutationProb, this.crossProb, this.fitnessWrapper, this.iterPerIndividual, this.numberOfVariables, this.maxCntWrappings, this.codonUpperBound);
		return clone;
	}

	public static void main(String[] args) {
		int populationSize = 100;// = 400;
		int generations = 100;// = 500;
		double mutationProb = 0.02;
	  	double crossProb = 0.6;
	  	MOFitnessWrapper fitnessWrapper = new MOFitnessWrapper(new NaiveFitness());
	  	int iterPerIndividual = 2; // games ran per evaluation
	  	int numberOfVariables = 100;
	  	int codonUpperBound = 256;
	  	int maxCntWrappings = 3;
	  	
		// First create the problem
		PacmanGrammaticalEvolution problem = new PacmanGrammaticalEvolution("grammar/base.bnf", populationSize, generations, mutationProb, crossProb, fitnessWrapper, iterPerIndividual, numberOfVariables, maxCntWrappings, codonUpperBound);
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
