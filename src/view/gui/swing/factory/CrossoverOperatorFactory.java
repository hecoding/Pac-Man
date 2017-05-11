package view.gui.swing.factory;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.crossover.*;
import jeco.core.problem.Variable;

public class CrossoverOperatorFactory {
    private static CrossoverOperatorFactory instance;

    private CrossoverOperatorFactory() {}

    public static CrossoverOperatorFactory getInstance() {
        if (instance == null){
            instance = new CrossoverOperatorFactory();
        }
        return instance;
    }

    public String[] getRegisteredKeys() {
        String[] a = {"SinglePointCrossover", "CombinatorialCrossover", "CycleCrossover", "LHSCrossover"};
        return a;
    }

    public CrossoverOperator create(String id, PacmanGrammaticalEvolution problem, double crossProb) {

        switch (id) {
            case "SinglePointCrossover":
                return new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, crossProb, SinglePointCrossover.ALLOW_REPETITION);
            case "CombinatorialCrossover":
                return new CombinatorialCrossover(crossProb);
            case "CycleCrossover":
                return new CycleCrossover<Variable<Integer>>(crossProb);
            case "LHSCrossover":
                return new LHSCrossover(problem, crossProb);
            default:
                throw new InstantiationError("Unspecified instantiation of class: " + id);
        }
    }
}
