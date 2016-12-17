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

import javax.script.ScriptEngine;

import jeco.core.operator.evaluator.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.CustomExecutor;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;


public class PacmanGrammaticalEvolution extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(PacmanGrammaticalEvolution.class.getName());
	private static Double mejorFitness;
	private static BufferedWriter writer;
  	private static Path path = FileSystems.getDefault().getPath("logs", "Registro.log");
	private static final int ticks = 19;
	
	//Execution parameters
	public static double mutationProb;
  	public static double crossProb;
  	public static int tamPob;
  	public static int numIteraciones;
  	public static int iteracionesPorIndividuo;
  	public static FitnessEvaluatorInterface fitnessFunc;
  	public ArrayList<Double> fitnessParams = new ArrayList<>(2); // for efficiency. NOT static
	
	protected ScriptEngine evaluator = null;

	public PacmanGrammaticalEvolution(String pathToBnf) {
		super(pathToBnf);
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		String stringtipo = phenotype.toString();
		CustomExecutor exec = new CustomExecutor();
		GrammaticalAdapterController pacman = new GrammaticalAdapterController(stringtipo);
		Controller<EnumMap<GHOST,MOVE>> ghosts = new StarterGhosts();
		
		double score = exec.runExecution(pacman, ghosts, iteracionesPorIndividuo);
		fitnessParams.clear();
		fitnessParams.add(score);
		
		double fitnessfinal = fitnessFunc.evaluate(fitnessParams);
		
		// Comprobación del fitness por seguridad (Hasta encontrar mejor funcion que no se salga)
		if(fitnessfinal < 0){
			System.err.println("ERROR: SCORE FUERA DE MARGEN < 0");
			fitnessfinal = 0;
		}
		
		// Registro del fitness y fenotipo
		if(fitnessfinal < mejorFitness){
			try {
				writer.write("Mejor fitness encontrado: " + fitnessfinal + ", con fenotipo: " + phenotype + System.lineSeparator());
			} catch (IOException e) {
				System.err.println("Error al escribir en el archivo Registro.log");
				e.printStackTrace();
			}
		}
		
		solution.getObjectives().set(0, fitnessfinal);
	}	

  @Override
  public PacmanGrammaticalEvolution clone() {
  	PacmanGrammaticalEvolution clone = new PacmanGrammaticalEvolution(super.pathToBnf);
  	return clone;
  }

  public static void main(String[] args) {
	  	int numHilos = Runtime.getRuntime().availableProcessors(); 
	  	//Valores de conf
	  	mutationProb = 0.02;
	  	crossProb = 0.6;
	  	//tamPob = 400; 
	  	//numIteraciones = 500;
	  	tamPob = 50; 
	  	numIteraciones = 250;
	  	iteracionesPorIndividuo = 5; 
	  	fitnessFunc = new NaiveFitness();
	  
	  	//Registro del fitness y fenotipo cuando hay una mejora
	  	mejorFitness = Double.POSITIVE_INFINITY;
	  	
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
			System.err.println("Error al abrir el archivo Registro.log");
			e.printStackTrace();
		}
		// First create the problem
		PacmanGrammaticalEvolution problem = new PacmanGrammaticalEvolution("test/pacman.bnf");
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, tamPob, numIteraciones);
		
		MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, numHilos);
	    Solutions<Variable<Integer>> solutions = masterWorker.execute();
	    for (Solution<Variable<Integer>> solution : solutions) {
	      logger.info(System.lineSeparator());
	      logger.info("Fitness =  " + solution.getObjectives().get(0)); 
	      logger.info("Average points = " + ((NaiveFitness) fitnessFunc).fitnessToPoints(solution.getObjectives().get(0)));
	      logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
	    }
	    
	    // Run visuals for the best program
	    Executor exec = new Executor();
	    exec.runGame(new GrammaticalAdapterController(problem.generatePhenotype(solutions.get(0)).toString()), new StarterGhosts(), true, ticks);
	    
	}
  public static void maine(String[] args) {
	  runPhenotype("H");
  }
  
  public static void runPhenotype(String ph) {
	  Executor exec = new Executor();
	  exec.runGame(new GrammaticalAdapterController(ph), new StarterGhosts(), true, ticks);
  }

}
