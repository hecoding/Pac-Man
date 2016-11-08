package jeco.core.operator.mutation;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

//Solutions must be numeric
public class BooleanMutation<T extends Variable<Boolean>> extends MutationOperator<T> {
	/**
	 * Constructor
	 * Creates a new IntegerFlipMutation mutation operator instance
	 */
	public BooleanMutation(double probability) {
		super(probability);
	} // IntegerFlipMutation

	public Solution<T> execute(Solution<T> solution) {
		for (int i = 0; i < solution.getVariables().size(); i++) {
			if (RandomGenerator.nextDouble() < probability) {
				solution.getVariables().get(i).setValue(!solution.getVariables().get(i).getValue());
			}
		}
		return solution;
	} // execute
}

