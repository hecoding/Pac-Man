package view.gui.swing.factory;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.mutation.MutationOperator;

public class MutationOperatorFactory {
    private static MutationOperatorFactory instance;

    private MutationOperatorFactory() {}

    public static MutationOperatorFactory getInstance() {
        if (instance == null){
            instance = new MutationOperatorFactory();
        }
        return instance;
    }

    public String[] getRegisteredKeys() {
        String[] a = {"IntegerFlipMutation"}; //TODO add all
        return a;
    }

    public MutationOperator create(String id, PacmanGrammaticalEvolution problem, double mutationProb) {

        if (id.equals("IntegerFlipMutation"))
            return new IntegerFlipMutation<>(problem, mutationProb);

        throw new InstantiationError("Unspecified instantiation of class: " + id);
    }
}
