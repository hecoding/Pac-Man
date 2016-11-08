package jeco.core.operator.selection;

import java.util.Collections;
import java.util.Comparator;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class TournamentSelect<T extends Variable<?>> extends SelectionOperator<T> {

    public static final int DEFAULT_TOURNAMENT_SIZE = 2;
    protected Comparator<Solution<T>> comparator;
    protected int tournamentSize;

    public TournamentSelect(int tournamentSize, Comparator<Solution<T>> comparator) {
        this.tournamentSize = tournamentSize;
        this.comparator = comparator;
    } // TournamentSelect

    public TournamentSelect(Comparator<Solution<T>> comparator) {
        this(TournamentSelect.DEFAULT_TOURNAMENT_SIZE, comparator);
    } // TournamentSelect

    public TournamentSelect() {
        this(TournamentSelect.DEFAULT_TOURNAMENT_SIZE, new SolutionDominance<T>());
    } // TournamentSelect

    public Solutions<T> execute(Solutions<T> solutions) {
        Solutions<T> result = new Solutions<T>();
        Solutions<T> tournamentSet = new Solutions<T>();
        for (int i = 0; i < tournamentSize; ++i) {
            tournamentSet.add(solutions.get(RandomGenerator.nextInteger(solutions.size())));
        }
        Collections.sort(tournamentSet, comparator);
        result.add(tournamentSet.get(0));
        return result;
    } // execute
} // TournamentSelect

