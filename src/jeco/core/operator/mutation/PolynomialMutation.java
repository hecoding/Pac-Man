package jeco.core.operator.mutation;

import java.util.ArrayList;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class PolynomialMutation<T extends Variable<Double>> extends MutationOperator<T> {

	/**
	 * DEFAULT_INDEX_MUTATION defines a default index for mutation
	 */
	public static final double DEFAULT_ETA_M = 20.0;
	protected Problem<T> problem;
	/**
	 * eta_c stores the index for mutation to use
	 */
	protected double eta_m;
	/**
	 * Constructor.
	 * Create a new PolynomialMutation operator with an specific index
	 */
	public PolynomialMutation(Problem<T> problem, double eta_m, double probability) {
		super(probability);
		this.problem = problem;
		this.eta_m = eta_m;
	}

	/**
	 * Constructor
	 * Creates a new instance of the polynomial mutation operator
	 */
	public PolynomialMutation(Problem<T> problem) {
		this(problem, DEFAULT_ETA_M, 1.0 / problem.getNumberOfVariables());
	} // PolynomialMutation

	public Solution<T> execute(Solution<T> solution) {
		double rnd, delta1, delta2, mut_pow, deltaq;
		double y, yl, yu, val, xy;

		ArrayList<T> variables = solution.getVariables();
		for (int i = 0; i < variables.size(); ++i) {
			T variable = variables.get(i);
			if (RandomGenerator.nextDouble() <= probability) {
				y = variable.getValue();
				yl = problem.getLowerBound(i);
				yu = problem.getUpperBound(i);
				delta1 = (y - yl) / (yu - yl);
				delta2 = (yu - y) / (yu - yl);
				rnd = RandomGenerator.nextDouble();
				mut_pow = 1.0 / (eta_m + 1.0);
				if (rnd <= 0.5) {
					xy = 1.0 - delta1;
					val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (eta_m + 1.0)));
					deltaq = java.lang.Math.pow(val, mut_pow) - 1.0;
				} else {
					xy = 1.0 - delta2;
					val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (java.lang.Math.pow(xy, (eta_m + 1.0)));
					deltaq = 1.0 - (java.lang.Math.pow(val, mut_pow));
				}
				y = y + deltaq * (yu - yl);
				if (y < yl) {
					y = yl;
				}
				if (y > yu) {
					y = yu;
				}
				variable.setValue(y);
			}
		}
		return solution;
	} // execute
} // PolynomialMutation

