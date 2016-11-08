package jeco.core.algorithm.moge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Example
 * Pease note that using the Script Engine is too slow.
 * We recommend using an external evaluation library like JEval (sourceforge).
 * @author José Luis Risco Martín
 *
 */
public class GrammaticalEvolution_example extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(GrammaticalEvolution_example.class.getName());

	protected ScriptEngine evaluator = null;
	protected double[] func = {0, 4, 30, 120, 340, 780, 1554}; //x^4+x^3+x^2+x

	public GrammaticalEvolution_example(String pathToBnf) {
		super(pathToBnf);
		ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		String originalFunction = phenotype.toString();
		double error, totError = 0, maxError = Double.NEGATIVE_INFINITY;
		for(int i=0; i<func.length; ++i) {
			String currentFunction = originalFunction.replaceAll("X", String.valueOf(i));	
			double funcI;
			try {
				String aux = evaluator.eval(currentFunction).toString();
				if(aux.equals("NaN"))
					funcI = Double.POSITIVE_INFINITY;
				else
					funcI = Double.valueOf(aux);
			} catch (NumberFormatException e) {
				logger.severe(e.getLocalizedMessage());
				funcI = Double.POSITIVE_INFINITY;
			} catch (ScriptException e) {
				logger.severe(e.getLocalizedMessage());
				funcI = Double.POSITIVE_INFINITY;
			}
			error = Math.abs(funcI-func[i]);
			totError += error;
			if(error>maxError)
				maxError = error;
		}
		solution.getObjectives().set(0, maxError);
		solution.getObjectives().set(1, totError);
	}	

  @Override
  public GrammaticalEvolution_example clone() {
  	GrammaticalEvolution_example clone = new GrammaticalEvolution_example(super.pathToBnf);
  	return clone;
  }

  public static void main(String[] args) {
		// First create the problem
		GrammaticalEvolution_example problem = new GrammaticalEvolution_example("test/grammar_example.bnf");
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, 100, 1000);
		algorithm.initialize();
		Solutions<Variable<Integer>> solutions = algorithm.execute();
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info("Fitness = (" + solution.getObjectives().get(0) + ", " + solution.getObjectives().get(1) + ")");
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
		}
	}		

}
