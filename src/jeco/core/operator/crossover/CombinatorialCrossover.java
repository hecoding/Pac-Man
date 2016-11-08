package jeco.core.operator.crossover;

import java.util.ArrayList;
import java.util.Collections;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * This operator is devoted to combine two chromosomes of integer values
 * that represent permutations, avoiding repeated integers.
 * 
 * Way it works: the children receive always the genes that both parents
 * have. With the rest (genes that only one parent has) are picked by
 * random.
 * 
 * @author J. M. Colmenar
 */
public class CombinatorialCrossover extends CrossoverOperator<Variable<Integer>>{

    protected double probability;

    public CombinatorialCrossover(double cxProb) {
        this.probability = cxProb;
    }
    
    @Override
    public Solutions<Variable<Integer>> execute(Solution<Variable<Integer>> parent1, Solution<Variable<Integer>> parent2) {
        Solutions<Variable<Integer>> offSpring = new Solutions<>();
         
        if (RandomGenerator.nextDouble() <= probability) {
            
            int i = 0;
            ArrayList<Integer> union = new ArrayList<>();
            ArrayList<Integer> fixedIdxs = new ArrayList<>();
            ArrayList<Integer> freeIdxs = new ArrayList<>();
            
            // First parent
            for (Variable<Integer> v : parent1.getVariables()) {
                if (!union.contains(v.getValue())) {
                    union.add(v.getValue());
                    if (parent1.getVariables().contains(v) && 
                        parent2.getVariables().contains(v)) {
                        fixedIdxs.add(i);
                    } else {
                        freeIdxs.add(i);
                    }
                    i++;
                }
            }
            
            // Second parent
            for (Variable<Integer> v : parent2.getVariables()) {
                if (!union.contains(v.getValue())) {
                    union.add(v.getValue());
                    if (parent1.getVariables().contains(v) && 
                        parent2.getVariables().contains(v)) {
                        fixedIdxs.add(i);
                    } else {
                        freeIdxs.add(i);
                    }
                    i++;
                }
            }
            
            // Generate two offspring with the fixed facilities and randomly choose the rest.
            Solution<Variable<Integer>> offSpring1 = parent1.clone();
            Solution<Variable<Integer>> offSpring2 = parent2.clone();
            
            // Fixed
            for (int j=0; j<fixedIdxs.size(); j++) {
                offSpring1.getVariable(j).setValue(union.get(fixedIdxs.get(j)));
                offSpring2.getVariable(j).setValue(union.get(fixedIdxs.get(j)));
            }
            
            // Free
            ArrayList<Integer> freeIdxs2 = new ArrayList<>();
            freeIdxs2.addAll(freeIdxs);
            Collections.shuffle(freeIdxs);
            Collections.shuffle(freeIdxs2);
            
            int j2 = fixedIdxs.size();
            int idx = 0;
            while (j2 < offSpring1.getVariables().size()) {
                offSpring1.getVariable(j2).setValue(union.get(freeIdxs.get(idx)));
                offSpring2.getVariable(j2).setValue(union.get(freeIdxs2.get(idx)));
                idx++;
                j2++;
            }
            
            offSpring.add(offSpring1);
            offSpring.add(offSpring2);
        } else {
            // No crossover, return parents.
            offSpring.add(parent1.clone());
            offSpring.add(parent2.clone());
        }
        
        return offSpring;
    }

}
