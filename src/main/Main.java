package main;

import alg.genProgAlgorithm.PacmanGeneticAlgorithm;
import alg.genProgAlgorithm.crossover.CrossoverInterface;
import alg.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import alg.genProgAlgorithm.fitnessFunction.PacmanFitness;
import alg.genProgAlgorithm.initialization.InitializationInterface;
import alg.genProgAlgorithm.initialization.RampedAndHalfInitialization;
import alg.genProgAlgorithm.mutation.MutationInterface;
import alg.genProgAlgorithm.selection.SelectionInterface;
import alg.program.Node;
import pacman.Executor;
import pacman.controllers.GeneticController;
import pacman.controllers.examples.StarterGhosts;
import util.Tree;
import alg.genProgAlgorithm.crossover.OnePointCrossover;
import alg.genProgAlgorithm.mutation.SimpleTerminalMutation;
import alg.genProgAlgorithm.selection.RouletteSelection;

public class Main {

	public static void main(String[] args) {
		// parameters
		InitializationInterface initializationStrategy = new RampedAndHalfInitialization();
		FitnessFunctionInterface func = new PacmanFitness();
		SelectionInterface selectionStrategy = new RouletteSelection();
		CrossoverInterface crossoverStrategy = new OnePointCrossover();
		MutationInterface mutationStrategy = new SimpleTerminalMutation();
		int populationNum = 400;
		boolean useElitism = false;
		double elitePercentage = 0.1;
		int maxGenerationNum = 500;
		int maxProgramHeight = 4;
		double crossProb = 0.6;
		double mutationProb = 0.05;

		PacmanGeneticAlgorithm pacmanGA = new PacmanGeneticAlgorithm(initializationStrategy, func, selectionStrategy, crossoverStrategy, mutationStrategy, populationNum, useElitism, elitePercentage, maxGenerationNum, maxProgramHeight, crossProb, mutationProb);
		
		// run algorithm
		pacmanGA.reset();
		pacmanGA.run();
		
		// get results and stats
		Tree<Node> bestProgram = pacmanGA.getBestChromosome().getProgram();
		System.out.println("aptitude: " + pacmanGA.getBestChromosome().getAptitude());
		System.out.println(pacmanGA.getBestChromosome().getPhenotype());
		
		// run best program
		Executor exec = new Executor();
		exec.runGame(new GeneticController(bestProgram), new StarterGhosts(), true, 20);
	}

}
