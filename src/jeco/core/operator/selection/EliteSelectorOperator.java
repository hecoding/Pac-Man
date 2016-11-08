package jeco.core.operator.selection;

import java.util.Collections;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Class for selection of elites.
 **/
public class EliteSelectorOperator<T extends Variable<?>> extends SelectionOperator<T> {

    public static final int DEFAULT_ELITE_SIZE = 10;
    protected int eliteSize;

    public EliteSelectorOperator(int eliteSize) {
        this.eliteSize = eliteSize;
    }

    public EliteSelectorOperator() {
        this(DEFAULT_ELITE_SIZE);
    }

    public Solutions<T> execute(Solutions<T> arg) {
        Solutions<T> solutions = new Solutions<T>();
        solutions.addAll(arg);

        SolutionDominance<T> comparator = new SolutionDominance<T>();
        Collections.sort(solutions, comparator);
        int popSize = solutions.size();

        Solutions<T> eliteSolutions = new Solutions<T>();
        int index = 0;
        while (index < eliteSize && index < popSize) {
            Solution<T> solution = solutions.get(index).clone();
            eliteSolutions.add(solution);
            index++;
        }
        eliteSolutions.reduceToNonDominated(comparator);
        return eliteSolutions;
    }
}
