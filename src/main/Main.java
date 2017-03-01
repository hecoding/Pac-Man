package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.crossover.CombinatorialCrossover;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.CycleCrossover;
import jeco.core.operator.crossover.SBXCrossover;
import jeco.core.operator.evaluator.fitness.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.fitness.NaiveFitness;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.EliteSelectorOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.operator.selection.TournamentSelect;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;
import jeco.core.util.observer.AlgObserver;
import view.gui.swing.GUIView;
import view.gui.swing.GeneralController;
import view.gui.swing.ProgramWorker;

public class Main {

	public static void main(String[] args)
	{
		int populationSize = 100;
		int generations = 100;
		double mutationProb = 0.1;
		double crossProb = 0.60;
		FitnessEvaluatorInterface fitnessFunc = new NaiveFitness();
		int iterPerIndividual = 3;
	  	int chromosomeLength = PacmanGrammaticalEvolution.CHROMOSOME_LENGTH_DEFAULT;
	  	int codonUpperBound = PacmanGrammaticalEvolution.CODON_UPPER_BOUND_DEFAULT;
	  	int maxCntWrappings = PacmanGrammaticalEvolution.MAX_CNT_WRAPPINGS_DEFAULT;
	  	int numOfObjectives = PacmanGrammaticalEvolution.NUM_OF_OBJECTIVES_DEFAULT;
	  	
	  	//Controller para el logger del Worker
	  	GeneralController ctrl = new GeneralController();	  	
	  	ctrl.setPopulationSize(populationSize);
	  	ctrl.setGenerations(generations);
	  	ctrl.setMutationProb(mutationProb);
	  	ctrl.setCrossProb(crossProb);
	  	ctrl.setFitnessFunc(fitnessFunc);
	  	ctrl.setItersPerIndividual(iterPerIndividual);
	  	ctrl.setChromosomeLength(chromosomeLength);
	  	ctrl.setCodonUpperBound(codonUpperBound);
	  	ctrl.setMaxCntWrappings(maxCntWrappings);
	  	ctrl.setNumOfObjectives(numOfObjectives);
		
		
		ctrl.execute();
	}

}
