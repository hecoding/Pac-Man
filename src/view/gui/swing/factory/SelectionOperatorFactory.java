package view.gui.swing.factory;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.selection.*;
import jeco.core.problem.Variable;

public class SelectionOperatorFactory {
    private static SelectionOperatorFactory instance;

    private SelectionOperatorFactory() {}

    public static SelectionOperatorFactory getInstance() {
        if (instance == null){
            instance = new SelectionOperatorFactory();
        }
        return instance;
    }

    public String[] getRegisteredKeys() {
        String[] a = {"BinaryTournament", "BinaryTournamentNSGAII", "TournamentSelect", "EliteSelectorOperator"};
        return a;
    }

    public SelectionOperator create(String id, PacmanGrammaticalEvolution problem) {

        switch (id) {
            case "BinaryTournament":
                return new BinaryTournament<Variable<Integer>>(new SimpleDominance<>());
            case "BinaryTournamentNSGAII":
                return new BinaryTournamentNSGAII<Variable<Integer>>();
            case "TournamentSelect":
                return new TournamentSelect<Variable<Integer>>(new SimpleDominance<>());
            case "EliteSelectorOperator":
                return new EliteSelectorOperator<Variable<Integer>>(10);
            default:
                throw new InstantiationError("Unspecified instantiation of class: " + id);
        }
    }
}
