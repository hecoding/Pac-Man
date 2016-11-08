package jeco.core.problems.dtlz;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public abstract class DTLZ extends Problem<Variable<Double>> {

    public DTLZ(Integer numberOfVariables) {
        super(numberOfVariables, 3);
    }

  	public Solutions<Variable<Double>> newRandomSetOfSolutions(int size) {
  		Solutions<Variable<Double>> solutions = new Solutions<Variable<Double>>();
  		for (int i=0; i<size; ++i) {
  			Solution<Variable<Double>> solI = new Solution<Variable<Double>>(numberOfObjectives);
  			for (int j = 0; j < numberOfVariables; ++j) {
  				Variable<Double> varJ = new Variable<Double>(RandomGenerator.nextDouble(lowerBound[j], upperBound[j]));
  				solI.getVariables().add(varJ);
  			}
  			solutions.add(solI);
  		}
  		return solutions;
  	}
  	
    public void evaluate(Solutions<Variable<Double>> solutions) {
    	for(Solution<Variable<Double>> solution : solutions)
    		this.evaluate(solution);
    }

    public abstract void evaluate(Solution<Variable<Double>> solution);        
}
