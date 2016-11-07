package alg.chromosome;

import java.util.ArrayList;

import util.RandGenerator;
import util.Tree;
import util.nodeGenerator.NodeGeneratorImp;
import alg.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import alg.program.Node;
import alg.program.Terminal;
import pacman.Executor;
import pacman.controllers.GeneticController;
import pacman.controllers.examples.StarterGhosts;

public class PacmanChromosome extends AbstractChromosome {
	private Tree<Node> program;
	private static int maxSteps;
	public static int maxHeight;
	private static int trialsPerEvaluation;
	
	public PacmanChromosome() {
		this.program = new Tree<>();
		//TODO make this a parameter
		trialsPerEvaluation = 10;
	}
	
	public PacmanChromosome(FitnessFunctionInterface function, int maxProgramHeight) {
		fitnessFunc = function;
		maxSteps = 400;
		maxHeight = maxProgramHeight;
		this.program = new Tree<>();
		//TODO make this a parameter
		trialsPerEvaluation = 10;
	}
	
	@Override
	public double evaluate() {
		//TODO GeneticController, Executor static?
		Executor executor = new Executor();
		ArrayList<Double> fitnessArgs = new ArrayList<>(3);
		double experimentScore;
		
		experimentScore = executor.runExperiment(new GeneticController(this.program), new StarterGhosts(), trialsPerEvaluation);
		
		//fitnessArgs.add((double) ant.getNumberOfBitsEaten());
		//fitnessArgs.add((double) ant.getNumberOfSteps());
		//fitnessArgs.add((double) maxSteps);
		//fitnessArgs.add((double) this.program.getHeight());
		//fitnessArgs.add((double) maxHeight);
		fitnessArgs.add(experimentScore);
		
		return fitnessFunc.f(fitnessArgs);
	}

	@Override
	public String getPhenotype() {
		return this.program.preOrder();
	}
	
	public Tree<Node> getProgram() {
		return this.program;
	}
	
	public void setProgram(Tree<Node> program) {
		this.program = program;
	}

	@Override
	public PacmanChromosome clone() {
		// deep copy indeed
		PacmanChromosome chr = new PacmanChromosome();
		chr.setProgram(this.program.clone());
		chr.aggregateScore = this.aggregateScore;
		chr.aptitude = this.aptitude;
		chr.score = this.score;
		
		return chr;
	}
	
	public PacmanChromosome copyAllButProgram() {
		// deep copy indeed
		PacmanChromosome chr = new PacmanChromosome();
		chr.setProgram(null);
		chr.aggregateScore = this.aggregateScore;
		chr.aptitude = this.aptitude;
		chr.score = this.score;
		// maxSteps static
		// maxHeight static
		
		return chr;
	}
	
	public void trimProgram() {
		Tree<Node> terminal = new Tree<Node>();
		terminal.setValue(Terminal.values()[RandGenerator.getInstance().nextInt(Terminal.values().length)]);
		this.program.replaceBelowDepth(maxHeight, NodeGeneratorImp.getInstance());
	}

	@Override
	public String toString() {
		return this.program.toString();
	}

}
