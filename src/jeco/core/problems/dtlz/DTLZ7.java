package jeco.core.problems.dtlz;

import java.util.ArrayList;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

public class DTLZ7 extends DTLZ {

    public DTLZ7(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ7

    public DTLZ7() {
        this(22);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        int k = numberOfVariables - numberOfObjectives + 1;

        double g = 0.0;
        ArrayList<Variable<Double>> variables = solution.getVariables();
        for (int i = numberOfVariables - k + 1; i <= numberOfVariables; i++) {
            g += variables.get(i - 1).getValue();
        }
        g = 1.0 + 9.0 * g / k;

        for (int i = 1; i <= numberOfObjectives - 1; i++) {
            solution.getObjectives().set(i - 1, variables.get(i - 1).getValue());
        }

        double h = 0.0;
        double xJ_1 = 0.0;
        for (int j = 1; j <= numberOfObjectives - 1; j++) {
            xJ_1 = variables.get(j - 1).getValue();
            h += xJ_1 / (1.0 + g) * (1.0 + Math.sin(3.0 * Math.PI * xJ_1));
        }

        h = numberOfObjectives - h;
        solution.getObjectives().set(numberOfObjectives - 1, (1 + g) * h);
    }
    
    public DTLZ7 clone() {
    	DTLZ7 clone = new DTLZ7(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
