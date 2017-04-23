package jeco.core.algorithm.neat;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.evaluator.fitness.MOFitnessWrapper;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import pacman.CustomExecutor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.game.Constants;
import pacman.game.util.GameInfo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Logger;

public class GrammaticalCPPNProblem extends Problem<Variable<CPPN>> {
    public static final Logger logger = Logger.getLogger(GrammaticalCPPNProblem.class.getName());
    private BnfReader reader;
    private int currentIdx;
    private int currentWrp;
    private boolean correctSol;

    private MOFitnessWrapper fitnessWrapper;
    private int iterPerIndividual; // games ran per evaluation

    private Controller<EnumMap<Constants.GHOST,Constants.MOVE>> ghostController;

    public GrammaticalCPPNProblem(Controller<EnumMap<Constants.GHOST,Constants.MOVE>> ghostController, String pathToBnf, MOFitnessWrapper fitnessWrapper, int iterPerIndividual, int chromosomeLength) { // TODO parametros que sobran
        super(0, fitnessWrapper.getNumberOfObjs()); // TODO what is number of variables here?

        reader = new BnfReader();
        reader.load(pathToBnf);

        this.ghostController = ghostController;
        this.fitnessWrapper = fitnessWrapper;
        this.iterPerIndividual = iterPerIndividual;
    }

    @Override
    public void evaluate(Solution<Variable<CPPN>> solution) {
        Phenotype phenotype = generatePhenotype(solution);
        if(correctSol)
            evaluate(solution, phenotype);
        else {
            for(int i=0; i<super.numberOfObjectives; ++i) {
                solution.getObjectives().set(i, Double.POSITIVE_INFINITY);
            }
        }
    }

    public void evaluate(Solution<Variable<CPPN>> solution, Phenotype phenotype) {
        CustomExecutor exec = new CustomExecutor();
        GrammaticalAdapterController pacman = new GrammaticalAdapterController(phenotype.toString());

        GameInfo avgGameInfo = exec.runExecution(pacman, this.ghostController, iterPerIndividual);

        ArrayList<Double> MOFitness = fitnessWrapper.evaluate(avgGameInfo);

        // Security check
        for (int i = 0; i < MOFitness.size(); i++) {
            Double objFitness = MOFitness.get(i);

            if (objFitness < 0) {
                logger.severe("ERROR: NEGATIVE FITNESS");
                System.err.println("FITNESS < 0!!!!!");
                MOFitness.set(i, this.fitnessWrapper.getWorstFitness(i));
            }
        }

        // Return objectives
        for (int i = 0; i < MOFitness.size(); i++) {
            solution.getObjectives().set(i, MOFitness.get(i));
        }
    }

    @Override
    public Solutions<Variable<CPPN>> newRandomSetOfSolutions(int size) {
        Solutions<Variable<CPPN>> solutions = new Solutions<>();
        for (int i=0; i<size; ++i) {
            Solution<Variable<CPPN>> solI = new Solution<>(numberOfObjectives);
            for (int j = 0; j < numberOfVariables; ++j) {
                //Variable<CPPN> varJ = new Variable<>(RandomGenerator.nextInteger((int) upperBound[j]));
                //solI.getVariables().add(varJ);
            }
            solutions.add(solI);
        }
        return solutions;
    }

    @Override
    public Problem<Variable<CPPN>> clone() {
        return null;
    } // TODO

    public Phenotype generatePhenotype(Solution<Variable<CPPN>> solution) {
        currentIdx = 0;
        currentWrp = 0;
        correctSol = true;
        Phenotype phenotype = new Phenotype();
        Rule firstRule = reader.getRules().get(0);
        // TODO get idx from CPPN instead of codon. solution.getVariable(0).getValue().query(parameters)
        //Production firstProduction = firstRule.get(solution.getVariables().get(currentIdx++).getValue() % firstRule.size());
        //processProduction(firstProduction, solution, phenotype);
        return phenotype;
    }

    private void processProduction(Production currentProduction, Solution<Variable<CPPN>> solution, LinkedList<String> phenotype) {
        if(!correctSol)
            return;
        for (Symbol symbol : currentProduction) {
            if (symbol.isTerminal()) {
                phenotype.add(symbol.toString());
            } else {
                //if(currentIdx >= solution.getVariables().size() && currentWrp < maxCntWrappings) {
                if(false) {
                    currentIdx = 0;
                    currentWrp++;
                }
                if (currentIdx < solution.getVariables().size()) {
                    Rule rule = reader.findRule(symbol);
                    // TODO get idx from CPPN instead of codon. solution.getVariable(0).getValue().query(parameters)
                    //Production production = rule.get(solution.getVariables().get(currentIdx++).getValue() % rule.size());
                    //processProduction(production, solution, phenotype);
                }
                else {
                    correctSol = false;
                    return;
                }
            }
        }
    }
}
