package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;

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
		int populationNum = 800;
		boolean useElitism = true;
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
		System.out.println(pacmanGA.getBestAptitudeList());
		System.out.println("aptitude: " + pacmanGA.getBestChromosome().getAptitude());
		System.out.println(pacmanGA.getBestChromosome().getPhenotype());
		
		// show statistics
		StatisticsWindow stats = new StatisticsWindow(pacmanGA);
		
		// run best program
		Executor exec = new Executor();
		exec.runGame(new GeneticController(bestProgram), new StarterGhosts(), true, 20);
	}
	
	public static class StatisticsWindow extends JFrame {
		Plot2DPanel plot;
		
		public StatisticsWindow(PacmanGeneticAlgorithm ctrl) {
			this.plot = new Plot2DPanel();
			updateGraphPanel(plot, ctrl);
			initGUI();
		}
	
		private void initGUI() {
			this.setLayout(new BorderLayout());
			this.add(this.plot, BorderLayout.CENTER);
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setSize(new Dimension(1024,668));
			this.setMinimumSize(new Dimension(750, 550));
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
		
		private static void updateGraphPanel(Plot2DPanel plot, PacmanGeneticAlgorithm ctrl) {
			double[] avgApt = toPrimitiveArray( ctrl.getAverageAptitudeList() );
			plot.addLinePlot("Absolute best", toPrimitiveArray( ctrl.getBestChromosomeList()) );
			plot.addLinePlot("Best of generation", toPrimitiveArray( ctrl.getBestAptitudeList()) );
			plot.addLinePlot("Generation average", toPrimitiveArray( ctrl.getAverageAptitudeList()) );
			plot.setFixedBounds(0, 0, avgApt.length);
			
			plot.setVisible(true);
		}
		
		private static double[] toPrimitiveArray(ArrayList<Double> a) {
			return a.stream().mapToDouble(d -> d).toArray();
		}
	
	}

}
