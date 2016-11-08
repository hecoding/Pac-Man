package jeco.core.operator.comparator;

import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

public class ObjectiveComparator<V extends Variable<?>> implements Comparator<Solution<V>> {

	protected int obj = 0;

	public ObjectiveComparator(int obj) {
		this.obj = obj;
	}

	public int compare(Solution<V> left, Solution<V> right) {
		if (left.getObjectives().get(obj) < right.getObjectives().get(obj)) {
			return -1;
		} else if (left.getObjectives().get(obj) > right.getObjectives().get(obj)) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setObj(int obj) {
		this.obj = obj;
	}
}
