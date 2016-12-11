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
import java.util.logging.Logger;

import javax.script.ScriptEngine;

import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.CustomExecutor;
import pacman.Executor;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;


public class PacmanGrammaticalEvolution extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(PacmanGrammaticalEvolution.class.getName());
	private static Double mejorFitness;
	private static BufferedWriter writer;
  	private static Path path = FileSystems.getDefault().getPath("logs", "Registro.log");
	private static int iteracionesPorIndividuo;
  	
	protected ScriptEngine evaluator = null;

	public PacmanGrammaticalEvolution(String pathToBnf) {
		super(pathToBnf);
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		String stringtipo = phenotype.toString();
		
		double fitnesssuma = 0;
		double fitnessfinal;
		for( int i = 0 ; i < iteracionesPorIndividuo; ++i){
			CustomExecutor exec = new CustomExecutor();
			double fitness = exec.runExecution(stringtipo);
			
			// Comprobación del fitness por seguridad (Hasta encontrar mejor funcion que no se salga)
			if(fitness < 0){
				System.err.println("ERROR: FITNESS FUERA DE MARGEN < 0");
				fitness = 0;
			}
			
			fitnesssuma += fitness;
		}
		
		fitnessfinal = fitnesssuma/iteracionesPorIndividuo;
		
		
		
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
	  	//Valores de conf
	  	int tamPoblacion = 50;
	  	int numIteraciones = 250;
	  	iteracionesPorIndividuo = 5;
	  	int numHilos = Runtime.getRuntime().availableProcessors();
	  
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
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, tamPoblacion, numIteraciones);
		
		
		
		/*
		algorithm.initialize();
		Solutions<Variable<Integer>> solutions = algorithm.execute();
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info("Fitness = (" + solution.getObjectives().get(0) + ")");
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
		}
		*/
		
		
		
		//algorithm.initialize(iteracionesPorIndividuo);
		
		
		MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, numHilos);
	    Solutions<Variable<Integer>> solutions = masterWorker.execute();
	    for (Solution<Variable<Integer>> solution : solutions) {
	      logger.info("Fitness = (" + solution.getObjectives().get(0) + ", " + solution.getObjectives().get(1) + ")");
	      logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
	    }
	    
	    // Run visuals for the best program
	    Executor exec = new Executor();
	    exec.runGame(new GrammaticalAdapterController(problem.generatePhenotype(solutions.get(0)).toString()), new StarterGhosts(), true, 20);
	    
	}

}
