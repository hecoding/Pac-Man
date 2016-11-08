package jeco.core.algorithm.ge;

import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Example Pease note that using the Script Engine is too slow. We recommend
 * using an external evaluation library like JEval (sourceforge).
 *
 * @author José Luis Risco Martín
 *
 */
public class SimpleGrammaticalEvolution_example extends AbstractProblemGE {

  private static final Logger logger = Logger.getLogger(SimpleGrammaticalEvolution_example.class.getName());
  protected ScriptEngine evaluator = null;
  protected double[] func = {0, 4, 30, 120, 340, 780, 1554}; //x^4+x^3+x^2+x

  public SimpleGrammaticalEvolution_example(String pathToBnf) {
    super(pathToBnf, 1);
    ScriptEngineManager mgr = new ScriptEngineManager();
    evaluator = mgr.getEngineByName("JavaScript");
  }

  @Override
  public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
    String originalFunction = phenotype.toString();
    double error, totError = 0;
    for (int i = 0; i < func.length; ++i) {
      String currentFunction = originalFunction.replaceAll("X", String.valueOf(i));
      double funcI;
      try {
        String aux = evaluator.eval(currentFunction).toString();
        if (aux.equals("NaN")) {
          funcI = Double.POSITIVE_INFINITY;
        } else {
          funcI = Double.valueOf(aux);
        }
      } catch (NumberFormatException e) {
        logger.severe(e.getLocalizedMessage());
        funcI = Double.POSITIVE_INFINITY;
      } catch (ScriptException e) {
        logger.severe(e.getLocalizedMessage());
        funcI = Double.POSITIVE_INFINITY;
      }
      error = Math.pow(funcI - func[i], 2);
      totError += error;
    }
    solution.getObjectives().set(0, totError);
  }

  @Override
  public SimpleGrammaticalEvolution_example clone() {
    SimpleGrammaticalEvolution_example clone = new SimpleGrammaticalEvolution_example(super.pathToBnf);
    return clone;
  }

    public static void main(String[] args) {
        // First create the problem
        SimpleGrammaticalEvolution_example problem = new SimpleGrammaticalEvolution_example("test/grammar_example.bnf");
        // Second create the algorithm
        SimpleGrammaticalEvolution algorithm = new SimpleGrammaticalEvolution(problem,100,1000,1.0 / problem.getNumberOfVariables(),SinglePointCrossover.DEFAULT_PROBABILITY);
        // Run
        algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        for (Solution<Variable<Integer>> solution : solutions) {
            logger.info("Fitness = (" + solution.getObjectives().get(0) + ")");
            logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
  }
}
