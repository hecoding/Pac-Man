package jeco.core.util.indicator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.Maths;

/**
 * This class implements the unary hypervolume indicator as proposed in Zitzler,
 * E., and Thiele, L. (1998): Multiobjective Optimization Using Evolutionary
 * Algorithms - A Comparative Case Study. Parallel Problem Solving from Nature
 * (PPSN-V), 292-301. A more detailed discussion can be found in Zitzler, E.,
 * Thiele, L., Laumanns, M., Fonseca, C., and Grunert da Fonseca, V (2003):
 * Performance Assessment of Multiobjective Optimizers: An Analysis and Review.
 * IEEE Transactions on Evolutionary Computation, 7(2), 117-132.
 *
 * IMPORTANT: A lower indicator value corresponds to a better approximation set.
 *
 * @author José L. Risco-Martín
 *
 */
public class UnaryHyperVolume {

  /**
   * Number of objectives
   */
  protected int dim;
  /**
   * Reference points
   */
  protected double[] nadir;
  /**
   * Approximation sets
   */
  ArrayList<ArrayList<Solutions<Variable<?>>>> fronts;
  /**
   * Reference front
   */
  Solutions<Variable<?>> referenceFront;
  /**
   * Hypervolumes
   */
  ArrayList<ArrayList<Double>> hyperVolumes;

  public UnaryHyperVolume(ArrayList<String> pathsToDataFiles) throws FileNotFoundException, IOException {
    fronts = new ArrayList<ArrayList<Solutions<Variable<?>>>>();
    for (String filePath : pathsToDataFiles) {
      ArrayList<Solutions<Variable<?>>> temp = Solutions.readFrontsFromFile(filePath);
      fronts.add(temp);
    }

    // Check if there is one file or more
    if (fronts.size() <= 0) {
      throw new IOException("The number of files is zero or impossible to read.");
    }
    // Check if there is one or more fronts for each file
    dim = Integer.MAX_VALUE;
    for (int i = 0; i < fronts.size(); ++i) {
      ArrayList<Solutions<Variable<?>>> solutionsSet = fronts.get(i);
      if (solutionsSet.size() <= 0) {
        throw new IOException("The number of fronts in the file " + pathsToDataFiles.get(i) + " is zero.");
      }
      int dimAux = solutionsSet.get(0).get(0).getObjectives().size();
      if (dimAux < dim) {
        dim = dimAux;
      }
    }
    referenceFront = null;
    nadir = null;
  }

  public UnaryHyperVolume(ArrayList<String> pathsToDataFiles, String pathToReferenceFront) throws FileNotFoundException, IOException {
    this(pathsToDataFiles);
    referenceFront = Solutions.readFrontFromFile(pathToReferenceFront);
  }

  public void normalize() {
    ArrayList<Solutions<Variable<?>>> totalSet = new ArrayList<Solutions<Variable<?>>>();
    for (int i = 0; i < fronts.size(); ++i) {
      ArrayList<Solutions<Variable<?>>> temp = fronts.get(i);
      for (Solutions<Variable<?>> solutions : temp) {
        totalSet.add(solutions);
      }
    }
    if (referenceFront != null) {
      totalSet.add(referenceFront);
    }
    Solutions.normalize(totalSet, dim);
    nadir = new double[dim];
    for (int i = 0; i < dim; ++i) {
      nadir[i] = 2.1;
    }
  }

  private void computeReferencePoints() {
    nadir = new double[dim];
    for (int i = 0; i < dim; ++i) {
      nadir[i] = Double.NEGATIVE_INFINITY;
    }
    for (int i = 0; i < fronts.size(); ++i) {
      ArrayList<Solutions<Variable<?>>> temp = fronts.get(i);
      for (int j = 0; j < temp.size(); ++j) {
        Solutions<Variable<?>> solutions = temp.get(j);
        for (int k = 0; k < solutions.size(); ++k) {
          Solution<Variable<?>> solution = solutions.get(k);
          for (int l = 0; l < dim; ++l) {
            if (solution.getObjectives().get(l) > nadir[l]) {
              nadir[l] = solution.getObjectives().get(l);
            }
          }
        }
      }
    }
    if (referenceFront != null) {
      for (int i = 0; i < referenceFront.size(); ++i) {
        Solution<Variable<?>> solution = referenceFront.get(i);
        for (int j = 0; j < dim; ++j) {
          if (solution.getObjectives().get(j) > nadir[j]) {
            nadir[j] = solution.getObjectives().get(j);
          }
        }
      }
    }
    for (int i = 0; i < dim; ++i) {
      nadir[i] = nadir[i] + 0.1;
    }
  }

  public ArrayList<ArrayList<Double>> calculateHyperVolumes() {
    if (nadir == null) {
      computeReferencePoints();
    }

    hyperVolumes = new ArrayList<ArrayList<Double>>();
    double referenceFrontHyperVolume = 0.0;
    if (referenceFront != null) {
      referenceFrontHyperVolume = calcIndValue(referenceFront);
    }

    double value;
    for (int i = 0; i < fronts.size(); ++i) {
      ArrayList<Double> aux = new ArrayList<Double>();
      ArrayList<Solutions<Variable<?>>> solutionsSet = fronts.get(i);
      for (int j = 0; j < solutionsSet.size(); ++j) {
        value = referenceFrontHyperVolume - calcIndValue(solutionsSet.get(j));
        aux.add(value);
      }
      hyperVolumes.add(aux);
    }

    return hyperVolumes;
  }

  /**
   * Returns true if point1 dominates point2 with respect to the first
   * 'no_objectives' objectives.
   *
   * @param point1 Point 1
   * @param point2 Point 2
   * @param no_objectives Number of objectives
   * @return true if point1 dominates point2 with respect to the first
   * 'no_objectives' objectives.
   */
  private boolean dominates(ArrayList<Double> point1, ArrayList<Double> point2, int no_objectives) {
    int i;
    boolean better_in_any_objective, worse_in_any_objective;
    better_in_any_objective = false;
    worse_in_any_objective = false;
    for (i = 0; i < no_objectives && !worse_in_any_objective; i++) {
      if (point1.get(i) > point2.get(i)) {
        better_in_any_objective = true;
      } else if (point1.get(i) < point2.get(i)) {
        worse_in_any_objective = true;
      }
    }
    return (!worse_in_any_objective && better_in_any_objective);
  }

  /**
   * Returns true if point1 weakly dominates 'points2' with respect to the to
   * the first 'no_objectives' objectives
   *
   * @param point1 Point 1
   * @param point2 Point 2
   * @param no_objectives Number of objectives
   * @return true if point1 weakly dominates 'points2' with respect to the to
   * the first 'no_objectives' objectives
   */
  /*private boolean weaklyDominates(ArrayList<Double> point1, ArrayList<Double> point2, int no_objectives) {
   int i;
   boolean worse_in_any_objective;

   worse_in_any_objective = false;
   for (i = 0; i < no_objectives && !worse_in_any_objective; i++) {
   if (point1.get(i) < point2.get(i)) {
   worse_in_any_objective = true;
   }
   }
   return (!worse_in_any_objective);
   }*/
  private void swap(Solutions<Variable<?>> front, int i, int j) {
    Solution<Variable<?>> tempI, tempJ;

    tempI = front.get(i);
    tempJ = front.get(j);
    front.set(i, tempJ);
    front.set(j, tempI);
  }

  /**
   * All non-dominated points regarding the first 'no_objectives' dimensions are
   * collected; the points 0..no_points-1 in 'front' are considered; the points
   * in 'front' are resorted, such that points [0..n-1] represent the
   * non-dominated points; n is returned
   *
   * @param front The Front under consideration
   * @param no_points Number of points considered (from 0 to no_points-1)
   * @param no_objectives Numbger of objectives considered
   * @return The first n points that are non-dominated
   */
  private int filterNondominatedSet(Solutions<Variable<?>> front, int no_points, int no_objectives) {
    int i, j;
    int n;

    n = no_points;
    i = 0;
    while (i < n) {
      j = i + 1;
      while (j < n) {
        if (dominates(front.get(i).getObjectives(), front.get(j).getObjectives(), no_objectives)) {
          // remove point 'j'
          n--;
          swap(front, j, n);
        } else if (dominates(front.get(j).getObjectives(), front.get(i).getObjectives(), no_objectives)) {
          // remove point 'i'; ensure that the point 
          // copied to index 'i' is considered in the next 
          // outer loop (thus, decrement i)
          n--;
          swap(front, i, n);
          i--;
          break;
        } else {
          j++;
        }
      }
      i++;
    }
    return n;
  }

  /**
   * calculate next value regarding dimension 'objective'; consider points
   * 0..no_points-1 in 'front'
   *
   * @param front Front under consideration
   * @param no_points Number of points
   * @param objective Objective
   * @return The minimum value
   * @throws Exception if no_points is less than 1
   */
  private double surfaceUnchangedTo(Solutions<Variable<?>> front, int no_points, int objective) {
    int i;
    double min;
    double value;

    if (no_points < 1) {
      System.err.println("Run-time error. no_points must be greater or equal than 1");
    }
    min = front.get(0).getObjectives().get(objective);
    for (i = 1; i < no_points; i++) {
      value = front.get(i).getObjectives().get(objective);
      if (value < min) {
        min = value;
      }
    }

    return min;
  }

  /**
   * Remove all points which have a value <= 'threshold' regarding the dimension
   * 'objective'; the points [0..no_points-1] in 'front' are considered; 'front'
   * is resorted, such that points [0..n-1] represent the remaining points; 'n'
   * is returned
   */
  int reduceNondominatedSet(Solutions<Variable<?>> front, int no_points, int objective, double threshold) {
    int n;
    int i;

    n = no_points;
    for (i = 0; i < n; i++) {
      if (front.get(i).getObjectives().get(objective) <= threshold) {
        n--;
        swap(front, i, n);
      }
    }
    return n;
  }

  private double calcHypervolume(Solutions<Variable<?>> front, int no_points, int no_objectives) {
    int n;
    double volume, distance;

    volume = 0;
    distance = 0;
    n = no_points;
    while (n > 0) {
      int no_nondominated_points;
      double temp_vol, temp_dist;

      no_nondominated_points = filterNondominatedSet(front, n,
              no_objectives - 1);
      if (no_objectives < 3) {
        if (no_nondominated_points < 1) {
          System.err.println("Run-time error. The number of non-dominated points must be greater than zero.");
        }
        temp_vol = front.get(0).getObjectives().get(0);
      } else {
        temp_vol = calcHypervolume(front, no_nondominated_points,
                no_objectives - 1);
      }
      temp_dist = surfaceUnchangedTo(front, n, no_objectives - 1);
      volume += temp_vol * (temp_dist - distance);
      distance = temp_dist;
      n = reduceNondominatedSet(front, n, no_objectives - 1, distance);
    }

    return volume;
  }

  private double calcIndValue(Solutions<Variable<?>> front) {
    int i, k;
    double temp;

    /* re-calculate objective values relative to reference point */
    for (i = 0; i < front.size(); i++) {
      Solution<Variable<?>> solution = front.get(i);
      for (k = 0; k < dim; k++) {
        temp = nadir[k] - solution.getObjectives().get(k);
        if (temp < 0) {
          System.err.println("Error in data or reference set file. Reference points must be the anti-optimal values.");
        }
        solution.getObjectives().set(k, temp);
      }
    }
    /* calculate indicator values */
    return calcHypervolume(front, front.size(), dim);
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage:");
      System.err.println("java -jar " + UnaryHyperVolume.class.getSimpleName() + ".jar [-ref <PathToReferenceFront>] -dat <PathToFrontsFile1> -dat <PathToFrontsFile2> ...");
      System.err.println("java -jar " + UnaryHyperVolume.class.getSimpleName() + ".jar -dat NSGAIIfronts.txt");
      return;
    }
    ArrayList<String> dataPaths = new ArrayList<String>();
    String refPath = null;
    int i = 0;
    while (i < args.length) {
      if (args[i].equals("-ref")) {
        refPath = args[++i];
      } else if (args[i].equals("-dat")) {
        dataPaths.add(args[++i]);
      }
      i++;
    }

    UnaryHyperVolume hyperVolume = null;
    try {
      if (refPath == null) {
        hyperVolume = new UnaryHyperVolume(dataPaths);
      } else {
        hyperVolume = new UnaryHyperVolume(dataPaths, refPath);
      }
      hyperVolume.normalize();
      ArrayList<ArrayList<Double>> volumesSet = hyperVolume.calculateHyperVolumes();
      for (i = 0; i < volumesSet.size(); ++i) {
        ArrayList<Double> volumes = volumesSet.get(i);
        System.out.println("HyperVolumes for:  " + dataPaths.get(i));
        for (Double volume : volumes) {
          System.out.println(volume);
        }
        System.out.println("--------------------------------------");
        System.out.println("AVG=" + Maths.mean(volumes) + ", STD=" + Maths.std(volumes) + "\n");
      }
    } catch (FileNotFoundException e) {
      System.err.println(e.getLocalizedMessage());
    } catch (IOException e) {
      System.err.println(e.getLocalizedMessage());
    }
  }
}
