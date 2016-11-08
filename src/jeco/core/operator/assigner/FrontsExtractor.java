package jeco.core.operator.assigner;

import java.util.ArrayList;
import java.util.Comparator;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class FrontsExtractor<V extends Variable<?>> {

  protected Comparator<Solution<V>> comparator;
  public static final String propertyN = "n";
  public static final String propertyRank = "rank";
  public static final String propertyIndexS = "indexS";

  public FrontsExtractor(Comparator<Solution<V>> comparator) {
    this.comparator = comparator;
  }

  public ArrayList<Solutions<V>> execute(Solutions<V> arg) {
    Solutions<V> solutions = new Solutions<V>();
    solutions.addAll(arg);
    ArrayList<Solutions<V>> fronts = new ArrayList<Solutions<V>>();
    Solutions<V> rest = solutions.reduceToNonDominated(comparator);
    fronts.add(solutions);
    while (rest.size() > 0) {
      solutions = rest;
      rest = solutions.reduceToNonDominated(comparator);
      fronts.add(solutions);
    }
    int rank = 1;
    for (int i = 0; i < fronts.size(); ++i) {
      Solutions<V> front = fronts.get(i);
      for (int j = 0; j < front.size(); ++j) {
        front.get(j).getProperties().put(propertyRank, rank);
      }
      rank++;
    }
    return fronts;
  }
}
