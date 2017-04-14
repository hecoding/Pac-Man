package jeco.core.operator.mutation;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class NeutralMutation<T extends Variable<Integer>> extends MutationOperator<T> {
	protected Problem<T> problem;

	public NeutralMutation(Problem<T> problem, double probability) {
		super(probability);
		this.problem = problem;
	}

	public Solution<T> execute(Solution<T> solution) {
		for (int i = 0; i < solution.getVariables().size(); i++) {
			int lowerBound = (int)Math.round(problem.getLowerBound(i));
			int upperBound = (int)Math.round(problem.getUpperBound(i));
			
			if(i < solution.getNoOptionsPhenotype().size())
				solution.getVariables().get(i).setValue(neutralMutation(solution.getVariables().get(i).getValue(), solution.getNoOptionsPhenotype().get(i), lowerBound, upperBound));
		}
		return solution;
	} 
	
  	private int neutralMutation(int codon, int multOf, int lB, int uB){
  		if(multOf == 1)
  			return codon;
  		
  		boolean done = false;
  		int desp;

  		while(!done){  			
  			desp = RandomGenerator.nextInteger(lB, uB);
  			while((desp % multOf) != 0)
  				desp--;
  			if(codon+desp < uB){
  				codon = codon+desp;
  				done = true;
  			}
  			else if(codon-desp > lB){
  				codon = codon-desp;
  				done = true;
  			}
  		}
  		return codon;
	}
}
