package jeco.core.operator.mutation;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;
/**
 *
 * @author cia
 */ 

//Solutions must be numeric
public class SwapMutationDouble<T extends Variable<?>> extends MutationOperator<T> {

	protected double probability;

	/**
	 * Constructor
	 * Creates a new IntegerFlipMutation mutation operator instance
	 */
	public SwapMutationDouble(double probability) {
		super(probability);
	} // IntegerFlipMutation

	public Solution<T> execute(Solution<T> solution) {

		int halfsize = solution.getVariables().size() /2;
		int offset = 0;
		if (RandomGenerator.nextDouble() < probability) {
			offset=halfsize;}

		if (RandomGenerator.nextDouble() < probability) {
			int indexI = RandomGenerator.nextInt(halfsize) + offset;
			int indexJ = RandomGenerator.nextInt(halfsize) + offset;
			if (indexI != indexJ) {
				T varI = solution.getVariables().get(indexI);
				solution.getVariables().set(indexI, solution.getVariables().get(indexJ));
				solution.getVariables().set(indexJ, varI);
			}
		}
		return solution;
	} // execute

} // IntegerFlipMutation

