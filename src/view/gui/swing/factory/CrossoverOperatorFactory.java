package view.gui.swing.factory;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.SinglePointCrossover;

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
        String[] a = {"SinglePointCrossover"}; //TODO add all
        return a;
    }

    public CrossoverOperator create(String id, PacmanGrammaticalEvolution problem, double crossProb) {

        if (id.equals("SinglePointCrossover"))
            return new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, crossProb, SinglePointCrossover.ALLOW_REPETITION);

        throw new InstantiationError("Unspecified instantiation of class: " + id);
    }
}
