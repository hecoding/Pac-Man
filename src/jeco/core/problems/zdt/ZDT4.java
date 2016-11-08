package jeco.core.problems.zdt;

import java.util.ArrayList;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class ZDT4 extends ZDT {

    public ZDT4(Integer numberOfVariables) {
        super(numberOfVariables);
        lowerBound[0] = 0.0;
        upperBound[0] = 1.0;
        for (int i = 1; i < numberOfVariables; i++) {
            lowerBound[i] = -5.0;
            upperBound[i] = 5.0;
        }
    } // ZDT1

    public ZDT4() {
        this(10);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        ArrayList<Variable<Double>> variables = solution.getVariables();
        double xJ = variables.get(0).getValue();
        double f1 = xJ;
        double g = 0;
        for (int j = 1; j < numberOfVariables; ++j) {
            xJ = variables.get(j).getValue();
            g += (xJ * xJ - 10.0 * Math.cos(4 * Math.PI * xJ));
        }
        g += (1 + 10 * (numberOfVariables - 1));
        double h = 1 - Math.sqrt(f1 / g);
        solution.getObjectives().set(0, f1);
        solution.getObjectives().set(1, g * h);
    }

    public Solutions<Variable<Double>> computeParetoOptimalFront(int n) {
        Solutions<Variable<Double>> result = new Solutions<Variable<Double>>();

        double temp;
        for (int i = 0; i < n; ++i) {
            Solution<Variable<Double>> sol = new Solution<Variable<Double>>(numberOfObjectives);
            temp = 0.0 + (1.0 * i) / (n - 1);
            sol.getVariables().add(new Variable<Double>(temp));
            for (int j = 1; j < numberOfVariables; ++j) {
                sol.getVariables().add(new Variable<Double>(0.0));
            }

            evaluate(sol);
            result.add(sol);
        }

        result.reduceToNonDominated(new SolutionDominance<Variable<Double>>());
        return result;
    }
    public ZDT4 clone() {
    	ZDT4 clone = new ZDT4(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }
}
