package jeco.core.operator.comparator;

import java.util.Comparator;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

public class SimpleDominance<T extends Variable<?>> implements Comparator<Solution<T>> {

    @Override
    public int compare(Solution<T> s1, Solution<T> s2) {
        Double fLeft = s1.getObjectives().get(0);
        Double fRight = s2.getObjectives().get(0);

        if (fLeft < fRight) {
            return -1;
        }
        if (fLeft > fRight) {
            return 1;
        }
        return 0;
    }
}
