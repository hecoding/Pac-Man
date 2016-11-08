package jeco.core.operator.comparator;

import java.util.ArrayList;
import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on epsilon dominance.
 */
public class EpsilonDominanceComparator<V extends Variable<?>> implements Comparator<Solution<V>> {

	private double eta;

	public EpsilonDominanceComparator(double eta) {
		this.eta = eta;
	}

	public int compare(Solution<V> s1, Solution<V> s2) {
		int dominate1; // dominate1 indicates if some objective of solution1
		// dominates the same objective in solution2. dominate2
		int dominate2; // is the complementary of dominate1.

		dominate1 = 0;
		dominate2 = 0;

		int flag;
		double value1, value2;
		// Idem number of violated constraint. Apply a dominance Test
		int n = Math.min(s1.getObjectives().size(), s2.getObjectives().size());
		ArrayList<Double> z1 = s1.getObjectives();
		ArrayList<Double> z2 = s2.getObjectives();
		for (int i = 0; i < n; i++) {
			value1 = z1.get(i);
			value2 = z2.get(i);

			if (value1 / (1 + eta) < value2) {
				flag = -1;
			} else if (value1 / (1 + eta) > value2) {
				flag = 1;
			} else {
				flag = 0;
			}

			if (flag == -1) {
				dominate1 = 1;
			}
			if (flag == 1) {
				dominate2 = 1;
			}
		}

		if (dominate1 == dominate2) {
			return 0; // No one dominates the other
		}
		if (dominate1 == 1) {
			return -1; // solution1 dominates
		}
		return 1;    // solution2 dominates
	} // compare
} // EpsilonDominanceComparator

