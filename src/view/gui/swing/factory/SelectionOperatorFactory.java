package view.gui.swing.factory;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.SelectionOperator;
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
        String[] a = {"BinaryTournamentNSGAII"}; //TODO add all
        return a;
    }

    public SelectionOperator create(String id, PacmanGrammaticalEvolution problem) {

        if (id.equals("BinaryTournamentNSGAII"))
            return new BinaryTournamentNSGAII<Variable<Integer>>();

        throw new InstantiationError("Unspecified instantiation of class: " + id);
    }
}
