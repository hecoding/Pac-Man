package jeco.core.algorithm.moge;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.sound.midi.SysexMessage;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.Esclavo;


public class Enlace extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(GrammaticalEvolution_example.class.getName());
	private static Double mejorFitness;
	private static BufferedWriter writer;
  	private static Path path = FileSystems.getDefault().getPath("logs", "Registro.log");
	
	protected ScriptEngine evaluator = null;

	public Enlace(String pathToBnf) {
		super(pathToBnf);
		ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		String stringtipo = phenotype.toString();
		Esclavo esc = new Esclavo();
		double fitness = esc.Ejecutar(stringtipo);
		//Registro del fitness y fenotipo
		if(fitness < mejorFitness){
			try {
				writer.write("Mejor fitness encontrado: " + fitness + ", con fenotipo: " + phenotype.toString() + System.getProperty("line.separator"));
			} catch (IOException e) {
				System.err.println("Error al escribir en el archivo Registro.log");
				e.printStackTrace();
			}
		}
		//Comprobacion del fitness por seguridad (Hasta encontrar mejor funcion que no se salga)
		if(fitness < 0){
			System.err.println("ERROR: FITNESS FUERA DE MARGEN <0");
			fitness = 0;
		}
		
		solution.getObjectives().set(0, fitness);
	}	

  @Override
  public Enlace clone() {
  	Enlace clone = new Enlace(super.pathToBnf);
  	return clone;
  }

  public static void main(String[] args) {
	  	//Registro del fitness y fenotipo cuando hay una mejora
	  	mejorFitness = Double.POSITIVE_INFINITY;
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
		Enlace problem = new Enlace("test/pacman.bnf");
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, 50, 200);
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
	}		

}
