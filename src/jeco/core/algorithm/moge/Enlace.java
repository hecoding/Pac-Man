package jeco.core.algorithm.moge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import pacman.Esclavo;


public class Enlace extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(GrammaticalEvolution_example.class.getName());

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
		solution.getObjectives().set(0, fitness);
	}	

  @Override
  public Enlace clone() {
  	Enlace clone = new Enlace(super.pathToBnf);
  	return clone;
  }

  public static void main(String[] args) {
		// First create the problem
		Enlace problem = new Enlace("test/pacman.bnf");
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, 100, 1000);
		algorithm.initialize();
		Solutions<Variable<Integer>> solutions = algorithm.execute();
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info("Fitness = (" + solution.getObjectives().get(0) + ")");
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
		}
	}		

}
