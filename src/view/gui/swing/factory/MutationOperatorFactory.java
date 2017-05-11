package view.gui.swing.factory;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.mutation.*;
import jeco.core.problem.Variable;

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
        String[] a = {"IntegerFlipMutation", "CombinatorialMutation", "NeutralMutation", "SwapMutation", "SwapMutationDouble"};
        return a;
    }

    public MutationOperator create(String id, PacmanGrammaticalEvolution problem, double mutationProb) {

        switch (id) {
            case "IntegerFlipMutation":
                return new IntegerFlipMutation<>(problem, mutationProb);
            case "CombinatorialMutation":
                return new CombinatorialMutation(mutationProb, 0, problem.codonUpperBound);
            case "NeutralMutation":
                return new NeutralMutation<>(problem, mutationProb);
            case "SwapMutation":
                return new SwapMutation<Variable<Integer>>(mutationProb);
            case "SwapMutationDouble":
                return new SwapMutationDouble<Variable<Integer>>(mutationProb);
            default:
                throw new InstantiationError("Unspecified instantiation of class: " + id);
        }
    }
}
