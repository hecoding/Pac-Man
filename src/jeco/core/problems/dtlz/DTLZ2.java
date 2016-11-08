package jeco.core.problems.dtlz;

import java.util.ArrayList;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

public class DTLZ2 extends DTLZ {

    public DTLZ2(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ2

    public DTLZ2() {
        this(12);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        ArrayList<Variable<Double>> variables = solution.getVariables();
        int k = numberOfVariables - numberOfObjectives + 1;
        double f = 0, g = 0;
        for (int i = numberOfVariables - k + 1; i <= numberOfVariables; ++i) {
            g += Math.pow(variables.get(i-1).getValue() - 0.5, 2);
        }

        for (int i = 1; i <= numberOfObjectives; i++) {
            f = (1 + g);
            for (int j = numberOfObjectives - i; j >= 1; j--) {
                f *= Math.cos(variables.get(j-1).getValue() * Math.PI / 2);
            }

            if (i > 1) {
                f *= Math.sin(variables.get(numberOfObjectives-i).getValue() * Math.PI / 2);
            }

            solution.getObjectives().set(i - 1, f);
        } // for
    }
    
    public DTLZ2 clone() {
    	DTLZ2 clone = new DTLZ2(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
