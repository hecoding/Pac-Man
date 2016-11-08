package jeco.core.operator.mutation;

import java.util.ArrayList;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

//Solutions must be real
public class UniformMutation<T extends Variable<Double>> extends MutationOperator<T> {
	public static final double DEFAULT_PERTURBATION_INDEX = 0.5;

	/**
	 * Stores the value used in a uniform mutation operator
	 */
	protected double perturbationIndex;
	protected double probability;
	protected Problem<T> problem;

	/**
	 * Constructor
	 * Creates a new uniform mutation operator instance
	 */
	public UniformMutation(Problem<T> problem, double probability, double perturbationIndex) {
		super(probability);
		this.problem = problem;
		this.perturbationIndex = perturbationIndex;
	} // UniformMutation

	public UniformMutation(Problem<T> problem) {
		this(problem, 1.0/problem.getNumberOfVariables(), DEFAULT_PERTURBATION_INDEX);
	}

	public Solution<T> execute(Solution<T> solution) {
		ArrayList<T> variables = solution.getVariables();
		for (int i = 0; i < variables.size(); ++i) {
			T variable = variables.get(i);
			if (RandomGenerator.nextDouble() < probability) {
				double rand = RandomGenerator.nextDouble();
				double tmp = (rand - 0.5) * perturbationIndex;

				tmp += variable.getValue();

				if (tmp < problem.getLowerBound(i)) {
					tmp = problem.getLowerBound(i);
				} else if (tmp > problem.getUpperBound(i)) {
					tmp = problem.getUpperBound(i);
				}

				variable.setValue(tmp);
			}
		}

		return solution;
	} // execute
} // UniformMutation

