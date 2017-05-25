package jeco.core.operator.selection;

import java.util.Comparator;

import java.util.logging.Logger;

import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.comparator.ComparatorNSGAII;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class BinaryTournamentNSGAII<T extends Variable<?>> extends SelectionOperator<T> {

    private static final Logger logger = Logger.getLogger(BinaryTournamentNSGAII.class.getName());
    protected Comparator<Solution<T>> comparator;

    /**
     * Constructor
     * TODO: Creates a new instance of the Binary tournament operator (Deb's
     * NSGA-II implementation version)
     */
    public BinaryTournamentNSGAII() {
        //comparator = new SolutionDominance<T>();
    	comparator = new ComparatorNSGAII<T>();
    } // BinaryTournament2

    /**
     * Performs the operation
     * @param object Object representing a SolutionSet
     * @return the selected solution
     */
    public Solutions<T> execute(Solutions<T> population) {
        Solutions<T> result = new Solutions<T>();
        int popSize = population.size();
        if (popSize < 2) {
            logger.severe("Population size must be greater or equal than 2.");
            return result;
        }

        int index1 = RandomGenerator.nextInt(popSize);
        int index2 = index1;
        while (index2 == index1) {
            index2 = RandomGenerator.nextInt(popSize);
        }

        Solution<T> solution1, solution2;
        solution1 = population.get(index1);
        solution2 = population.get(index2);
        int flag = comparator.compare(solution1, solution2);
        if (flag < 0) {
            result.add(solution1);
        } else if (flag > 0) {
            result.add(solution2);
        } else if (solution1.getProperties().get(CrowdingDistance.propertyCrowdingDistance).doubleValue() > solution2.getProperties().get(CrowdingDistance.propertyCrowdingDistance).doubleValue()) {
            result.add(solution1);
        } else if (solution2.getProperties().get(CrowdingDistance.propertyCrowdingDistance).doubleValue() > solution1.getProperties().get(CrowdingDistance.propertyCrowdingDistance).doubleValue()) {
            result.add(solution2);
        } else if (RandomGenerator.nextDouble() < 0.5) {
            result.add(solution1);
        } else {
            result.add(solution2);
        }
        return result;
    } // execute
} // BinaryTournament2

