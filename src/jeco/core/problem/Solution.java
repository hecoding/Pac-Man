package jeco.core.problem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Solution<V extends Variable<?>> {

  protected ArrayList<V> variables = new ArrayList<V>();
  protected ArrayList<Double> objectives = new ArrayList<Double>();
  protected HashMap<String, Number> properties = new HashMap<String, Number>();

  public Solution(int numberOfObjectives) {
    for (int i = 0; i < numberOfObjectives; ++i) {
      objectives.add(0.0);
    }
  }

  public ArrayList<V> getVariables() {
    return variables;
  }

  public V getVariable(int idx) {
    return variables.get(idx);
  }

  public ArrayList<Double> getObjectives() {
    return objectives;
  }

  public Double getObjective(int idx) {
    return objectives.get(idx);
  }

  public HashMap<String, Number> getProperties() {
    return properties;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Solution<V> clone() {
    Solution<V> clone = new Solution<V>(objectives.size());
    for (int i = 0; i < objectives.size(); ++i) {
      clone.objectives.set(i, objectives.get(i));
    }
    for (int i = 0; i < variables.size(); ++i) {
      clone.variables.add((V) variables.get(i).clone());
    }

    for (Map.Entry<String, Number> entry : properties.entrySet()) {
      clone.properties.put(entry.getKey(), entry.getValue());
    }

    return clone;
  }

  public int compareTo(Solution<V> solution, Comparator<Solution<V>> comparator) {
    return comparator.compare(this, solution);
  }

  @SuppressWarnings("unchecked")
	@Override
  public boolean equals(Object right) {
    Solution<V> sol = (Solution<V>) right;
    int nVar = Math.min(variables.size(), sol.variables.size());
    for (int i = 0; i < nVar; ++i) {
      if (!this.getVariable(i).equals(sol.getVariable(i))) {
        return false;
      }
    }
    int nObj = Math.min(objectives.size(), sol.objectives.size());

    for (int i = 0; i < nObj; ++i) {
      if (!this.getObjective(i).equals(sol.getObjective(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < this.objectives.size(); ++i) {
      buffer.append(objectives.get(i)).append(" ");
    }
    return buffer.toString();
  }
}
