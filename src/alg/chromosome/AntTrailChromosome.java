package model.chromosome;

import java.util.ArrayList;

import model.Ant;
import model.Map;
import model.Map.CellType;
import model.genProgAlgorithm.AntTrailGeneticAlgorithm;
import model.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import model.program.Function;
import model.program.Node;
import model.program.Terminal;
import util.RandGenerator;
import util.Tree;
import util.nodeGenerator.NodeGeneratorImp;

public class AntTrailChromosome extends AbstractChromosome {
	private Tree<Node> program;
	private static int maxSteps;
	public static int maxHeight;
	
	public AntTrailChromosome() {
		this.program = new Tree<>();
	}
	
	public AntTrailChromosome(FitnessFunctionInterface function, int maxProgramHeight) {
		fitnessFunc = function;
		maxSteps = 400;
		maxHeight = maxProgramHeight;
		this.program = new Tree<>();
	}
	
	@Override
	public double evaluate() {
		Map map = AntTrailGeneticAlgorithm.getMap();
		Ant ant = new Ant(map.getColumns(), map.getRows());
		ArrayList<Double> steps = new ArrayList<>(3);

		runProgram(this.program, map, ant);
		steps.add((double) ant.getNumberOfBitsEaten());
		steps.add((double) ant.getNumberOfSteps());
		steps.add((double) maxSteps);
		steps.add((double) this.program.getHeight());
		steps.add((double) maxHeight);
		
		return fitnessFunc.f(steps);
	}
	
	public static void runProgram(Tree<Node> program, Map map, Ant ant) {
		while (ant.steps < maxSteps && ant.bitsEaten < map.getFoodHere()) {
			runProgramRecursive(program, map, ant);
		}
	}
	
	private static void runProgramRecursive(Tree<Node> program, Map map, Ant ant) {

		if(ant.steps < maxSteps && ant.bitsEaten < map.getFoodHere()) {
			// nomnom
			if(map.get(ant.getPosition()) == CellType.food) {
				ant.eat();
				map.set(CellType.eatenfood, ant.getPosition());
			}
		
			// functions
			if(program.getValue() == Function.ifFoodAhead) {
				if(map.get(ant.getForwardPosition()) == CellType.food)
					runProgramRecursive(program.getLeftChild(), map, ant);
				else
					runProgramRecursive(program.getRightChild(), map, ant);
			}
			else if(program.getValue() == Function.progn3) {
				for (int i = 0; i < Function.numberOfChildren(Function.progn3); i++) {
					runProgramRecursive(program.getNChild(i), map, ant);
				}
			}
			else if(program.getValue() == Function.progn2) {
				runProgramRecursive(program.getLeftChild(), map, ant);
				runProgramRecursive(program.getRightChild(), map, ant);
			}
			// terminals
			else if(program.getValue() == Terminal.turnLeft)
				ant.turnLeft();
			else if(program.getValue() == Terminal.turnRight)
				ant.turnRight();
			else if(program.getValue() == Terminal.goForward) {
				if(map.get(ant.getPosition()) == CellType.nothing)
					map.set(CellType.trail, ant.getPosition());
				ant.moveForward();
			}
		}
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
	public AntTrailChromosome clone() {
		// deep copy indeed
		AntTrailChromosome chr = new AntTrailChromosome();
		chr.setProgram(this.program.clone());
		chr.aggregateScore = this.aggregateScore;
		chr.aptitude = this.aptitude;
		chr.score = this.score;
		
		return chr;
	}
	
	public AntTrailChromosome copyAllButProgram() {
		// deep copy indeed
		AntTrailChromosome chr = new AntTrailChromosome();
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
