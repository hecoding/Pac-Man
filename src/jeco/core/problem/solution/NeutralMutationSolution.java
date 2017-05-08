package jeco.core.problem.solution;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

import java.util.ArrayList;
import java.util.Map;

public class NeutralMutationSolution extends Solution<Variable<Integer>> {
    protected ArrayList<Integer> noOptionsPhenotype = new ArrayList<Integer>();

    public NeutralMutationSolution(int numberOfObjectives) {
        super(numberOfObjectives);
    }

    public ArrayList<Integer> getNoOptionsPhenotype() {
        return noOptionsPhenotype;
    }

    public void setNoOptionsPhenotype(ArrayList<Integer> noOptionsPhenotype) {
        this.noOptionsPhenotype = noOptionsPhenotype;
    }

    @Override
    public NeutralMutationSolution clone() {
        NeutralMutationSolution clone = new NeutralMutationSolution(objectives.size());
        for (int i = 0; i < objectives.size(); ++i) {
            clone.objectives.set(i, objectives.get(i));
        }
        for (int i = 0; i < variables.size(); ++i) {
            clone.variables.add(variables.get(i).clone());
        }

        for (Map.Entry<String, Number> entry : properties.entrySet()) {
            clone.properties.put(entry.getKey(), entry.getValue());
        }

        for (int i = 0; i < noOptionsPhenotype.size(); ++i) {
            clone.noOptionsPhenotype.add(noOptionsPhenotype.get(i).intValue()); // clone doesn't work
        }

        return clone;
    }

    // noOptionsPhenotype is dependent of variables array, thus no equals method modification is needed
}
