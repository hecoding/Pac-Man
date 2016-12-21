package jeco.core.operator.mutation;

import java.util.ArrayList;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Mutation that removes an element from the chromosome and includes a new element
 * not previously selected; given that is based on integer, it needs a maximum
 * value to be restricted to.
 * 
 * @author J. M. Colmenar
 */
public class CombinatorialMutation extends MutationOperator<Variable<Integer>> {

    int minValue;
    int maxValue;
    
    /**
     * Changes each gene with an integer value not selected and avoiding
     * repetitions.
     * 
     * @param probability 
     * @param minValue minnimum value to be selected (included)
     * @param maxValue maximum value to be selected (included)
     */
    public CombinatorialMutation(double probability, int minValue, int maxValue) {
        super(probability);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Solution<Variable<Integer>> execute(Solution<Variable<Integer>> solution) {        
        
        // Collect integers not in solution.
        ArrayList<Integer> notInSol = new ArrayList<>();
        
        for (int j=minValue; j<=maxValue; j++) {
            if (!solution.getVariables().contains(new Variable<Integer>(j))) {
                notInSol.add(j);
            }
        }
        
        for (int i = 0; i < solution.getVariables().size(); i++) {
            if (RandomGenerator.nextDouble() < probability) {
                int idx = RandomGenerator.nextInteger(0,notInSol.size());
                
                // Move value from solution to notInSol
                int v = notInSol.get(idx);
                notInSol.set(idx, solution.getVariables().get(i).getValue());
                
                // Update solution with the previous notInSol value
                solution.getVariables().get(i).setValue(v);
            }
        }
        return solution;
    }

}
