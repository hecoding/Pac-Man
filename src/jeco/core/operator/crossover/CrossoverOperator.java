package jeco.core.operator.crossover;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
 /**
  * El cruce sí necesita plantillas porque se accede continuamente a las variables
  * @author jlrisco
  * @param <T>
  */

public abstract class CrossoverOperator<T extends Variable<?>> {
    abstract public Solutions<T> execute(Solution<T> parent1, Solution<T> parent2);
}
