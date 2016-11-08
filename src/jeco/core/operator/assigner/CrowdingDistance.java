package jeco.core.operator.assigner;

import java.util.Collections;

import jeco.core.operator.comparator.ObjectiveComparator;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class CrowdingDistance<V extends Variable<?>> {

    protected int numberOfObjectives;
    public static final String propertyCrowdingDistance = "crowdingDistance";

    public CrowdingDistance(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    public Solutions<V> execute(Solutions<V> arg) {
        Solutions<V> solutions = new Solutions<V>();
        solutions.addAll(arg);

        int size = solutions.size();
        if (size == 0) {
            return solutions;
        }

        if (size == 1) {
            solutions.get(0).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            return solutions;
        } // if

        if (size == 2) {
            solutions.get(0).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            solutions.get(1).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            return solutions;
        } // if

        for (int i = 0; i < size; ++i) {
            solutions.get(i).getProperties().put(propertyCrowdingDistance, 0.0);
        }

        double objetiveMaxn;
        double objetiveMinn;
        double distance = 0.0;

        for (int i = 0; i < numberOfObjectives; ++i) {
            // Sort the population by objective i
            Collections.sort(solutions, new ObjectiveComparator<V>(i));
            objetiveMinn = solutions.get(0).getObjectives().get(i);
            objetiveMaxn = solutions.get(size - 1).getObjectives().get(i);

            //Set the crowding distance
            solutions.get(0).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            solutions.get(size - 1).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);

            for (int j = 1; j < size - 1; j++) {
                distance = solutions.get(j + 1).getObjectives().get(i) - solutions.get(j - 1).getObjectives().get(i);
                distance = distance / (objetiveMaxn - objetiveMinn);
                distance += solutions.get(j).getProperties().get(propertyCrowdingDistance).doubleValue();
                solutions.get(j).getProperties().put(propertyCrowdingDistance, distance);
            } // for
        } // for

        return solutions;
    }
}
