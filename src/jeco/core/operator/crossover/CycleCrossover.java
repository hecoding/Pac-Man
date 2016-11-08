package jeco.core.operator.crossover;

import java.util.LinkedList;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class CycleCrossover<V extends Variable<?>> extends CrossoverOperator<V> {

	public static final double DEFAULT_PROBABILITY = 0.9;
	protected double probability;

	public CycleCrossover() {
		probability = DEFAULT_PROBABILITY;
	}

	public CycleCrossover(double probability) {
		this.probability = probability;
	}

	private Integer lookForPosition(Solution<V> parent, V variable) {
		if (variable == null) {
			return 0;
		}
		V varJ = null;
		for (int j = 0; j < parent.getVariables().size(); ++j) {
			varJ = parent.getVariables().get(j);
			if (variable.equals(varJ)) {
				return j;
			}
		}
		return -1;
	}

	public Solutions<V> doCrossover(double probability, Solution<V> parent1, Solution<V> parent2) {

		Solutions<V> offSpring = new Solutions<V>();

		offSpring.add(parent1.clone());
		offSpring.add(parent2.clone());

		if (RandomGenerator.nextDouble() <= probability) {
			// We obtain the cycle, first allele:
				Integer currentPos = 0;
			LinkedList<Integer> cycle = new LinkedList<Integer>();
			cycle.add(currentPos);

			V variable = parent2.getVariables().get(currentPos);
			currentPos = lookForPosition(parent1, variable);
			while (currentPos != 0) {
				cycle.add(currentPos);
				variable = parent2.getVariables().get(currentPos);
				currentPos = lookForPosition(parent1, variable);
			}

			Solution<V> clon1 = parent1.clone();
			Solution<V> clon2 = parent2.clone();
			for (int i = 0; i < parent1.getVariables().size(); ++i) {
				if (cycle.contains(i)) {
					offSpring.get(0).getVariables().set(i, clon1.getVariables().get(i));
					offSpring.get(1).getVariables().set(i, clon2.getVariables().get(i));
				} else {
					offSpring.get(0).getVariables().set(i, clon2.getVariables().get(i));
					offSpring.get(1).getVariables().set(i, clon1.getVariables().get(i));
				}
			}
		}
		return offSpring;
	} // doCrossover

	/**
	 * Executes the operation
	 * @param object An object containing an array of two parents
	 * @return An object containing the offSprings
	 */
	public Solutions<V> execute(Solution<V> parent1, Solution<V> parent2) {
		return doCrossover(probability, parent1, parent2);
	} // execute
} // CX

