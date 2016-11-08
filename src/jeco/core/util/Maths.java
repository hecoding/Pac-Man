package jeco.core.util;

import java.util.Collections;
import java.util.List;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jmetal.qualityIndicator.Hypervolume;

/**
 * Utility functions. Must be refactored.
 * 
 * @author jlrisco, J. M. Colmenar
 */
public class Maths {

  public static double sum(List<Double> numbers) {
    double res = 0;
    for (Double number : numbers) {
      res += number;
    }
    return res;
  }

  public static double mean(List<Double> numbers) {
    if (numbers.isEmpty()) {
      return 0;
    }
    double res = sum(numbers) / numbers.size();
    return res;
  }

  public static double median(List<Double> numbers) {
    Collections.sort(numbers);
    int middle = numbers.size() / 2;
    if (numbers.size() % 2 == 1) {
      return numbers.get(middle);
    } else {
      return (numbers.get(middle - 1) + numbers.get(middle)) / 2.0;
    }
  }

  public static double std(List<Double> numbers) {
    double res = 0;
    double avg = mean(numbers);
    for(Double number : numbers) {
      res += Math.pow(number-avg, 2);
    }
    res = Math.sqrt(res/(numbers.size()-1));
    return res;
  }
  
  public static <V extends Variable<?>> double calculateHypervolume(Solutions<V> solFront, int numObjectives) {
        
        double [][] front = new double[solFront.size()][numObjectives];
        for (int i = 0; i < solFront.size(); i++) {
            for (int obj=0; obj<numObjectives; obj++) {
                front[i][obj] = solFront.get(i).getObjective(obj);
            }
        }
              
        Hypervolume hv = new Hypervolume();
        
        return hv.calculateHypervolume(front, front.length, numObjectives);
    }
  
}
