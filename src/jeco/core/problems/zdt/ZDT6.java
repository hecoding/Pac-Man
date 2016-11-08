package jeco.core.problems.zdt;

import java.util.ArrayList;
import java.util.logging.Logger;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class ZDT6 extends ZDT {

    private static final Logger logger = Logger.getLogger(ZDT6.class.getName());

    public ZDT6(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // ZDT1

    public ZDT6() {
        this(10);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        ArrayList<Variable<Double>> variables = solution.getVariables();
        double x0 = variables.get(0).getValue();
        double f1 = 1.0 - Math.exp(-4 * x0) * Math.pow(Math.sin(6 * Math.PI * x0), 6);
        double g = 0;
        for (int j = 1; j < numberOfVariables; ++j) {
            g += variables.get(j).getValue();
        }
        g /= (numberOfVariables - 1);
        g = Math.pow(g, 0.25);
        g *= 9;
        g += 1;
        double h = 1 - (f1 / g) * (f1 / g);
        solution.getObjectives().set(0, f1);
        solution.getObjectives().set(1, g * h);
    }

    public Solutions<Variable<Double>> computeParetoOptimalFront(int n) {
        // TODO: Finish this function
        logger.severe("This function is not finished");
        return null;
    }
    public ZDT6 clone() {
    	ZDT6 clone = new ZDT6(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }
}
