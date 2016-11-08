package jeco.core.operator.comparator;

import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

public class PropertyComparator<V extends Variable<?>> implements Comparator<Solution<V>> {

	protected String propertyName;

	public PropertyComparator(String propertyName) {
		this.propertyName = propertyName;
	}

	public int compare(Solution<V> left, Solution<V> right) {
		if (left.getProperties().get(propertyName).doubleValue() < right.getProperties().get(propertyName).doubleValue()) {
			return -1;
		} else if (left.getProperties().get(propertyName).doubleValue() > right.getProperties().get(propertyName).doubleValue()) {
			return 1;
		} else {
			return 0;
		}
	}
}
