package view.gui.swing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.crossover.CombinatorialCrossover;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.CycleCrossover;
import jeco.core.operator.crossover.SBXCrossover;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.evaluator.fitness.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.fitness.NaiveFitness;
import jeco.core.operator.mutation.BooleanMutation;
import jeco.core.operator.mutation.CombinatorialMutation;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.mutation.NonUniformMutation;
import jeco.core.operator.mutation.SwapMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.EliteSelectorOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.operator.selection.TournamentSelect;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;
import jeco.core.util.observer.AlgObserver;
import parser.TreeParser;
import parser.nodes.NicerTree;

public class GeneralController {
	static ProgramWorker programWorker;
	static MasterWorkerThreads<Variable<Integer>> algorithmWorker;
	static GrammaticalEvolution algorithm;
	ArrayList<AlgObserver> algorithmObservers = new ArrayList<>();
	static PacmanGrammaticalEvolution problem;
	static Logger logger;
	
	// Configure parameters
	int populationSize = 100;// = 400;
	int generations = 100;// = 500;
	double mutationProb = 0.02;
  	double crossProb = 0.6;
  	FitnessEvaluatorInterface fitnessFunc = new NaiveFitness();

	int iterPerIndividual = 3;// = 10; // games ran per evaluation
  	
  	int chromosomeLength = PacmanGrammaticalEvolution.CHROMOSOME_LENGTH_DEFAULT;
  	int codonUpperBound = PacmanGrammaticalEvolution.CODON_UPPER_BOUND_DEFAULT;
  	int maxCntWrappings = PacmanGrammaticalEvolution.MAX_CNT_WRAPPINGS_DEFAULT;
  	int numOfObjectives = PacmanGrammaticalEvolution.NUM_OF_OBJECTIVES_DEFAULT;
	
	public GeneralController() {
		
	}
	
	public void execute() {		
		/***
		 * 
		 * TEST BENCH CODE
		 * 
		***/
		// First create the problem
		problem = new PacmanGrammaticalEvolution("test/gramaticadelaqueseforjanlossuenos.bnf",
				populationSize, generations, mutationProb, crossProb, fitnessFunc, iterPerIndividual,
				this.numOfObjectives, this.chromosomeLength, this.maxCntWrappings, this.codonUpperBound
				);
		
		
		//Create lists with algorithms. Can't use generic arrays due to Java compiler restrictions.
	  	LinkedList<SelectionOperator<Variable<Integer>>> selectionOperators = new LinkedList<SelectionOperator<Variable<Integer>>>();
	  	selectionOperators.add(new BinaryTournament<Variable<Integer>>());
	  	selectionOperators.add(new BinaryTournamentNSGAII<Variable<Integer>>());
	  	selectionOperators.add(new EliteSelectorOperator<Variable<Integer>>());
	  	selectionOperators.add(new TournamentSelect<Variable<Integer>>());
	  	
	  	LinkedList<CrossoverOperator<Variable<Integer>>> crossoverOperators = new LinkedList<CrossoverOperator<Variable<Integer>>>();
	  	crossoverOperators.add(new CombinatorialCrossover(crossProb));
	  	crossoverOperators.add(new CycleCrossover<Variable<Integer>>(crossProb));
	  	//crossoverOperators.add(new SBXCrossover<Variable<Integer>>(problem, 20, crossProb)); Cant use it with Integer variables
	  	crossoverOperators.add(new SinglePointCrossover<Variable<Integer>>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, crossProb, SinglePointCrossover.ALLOW_REPETITION));
	  	
	  	LinkedList<MutationOperator<Variable<Integer>>> mutationOperators = new LinkedList<MutationOperator<Variable<Integer>>>();
	  	mutationOperators.add(new CombinatorialMutation(mutationProb, 0, codonUpperBound));
	  	mutationOperators.add(new IntegerFlipMutation<>(problem, mutationProb));
	  	mutationOperators.add(new SwapMutation<Variable<Integer>>(mutationProb));
		
		
	  	for(SelectionOperator<Variable<Integer>> selectionOperator : selectionOperators)
	  	{
		  	for(CrossoverOperator<Variable<Integer>> crossOverOperator : crossoverOperators)
		  	{
				for(MutationOperator<Variable<Integer>> mutationOperator : mutationOperators)
				{
					//Hay que volver a definir todo cada vez que se ejecuta un bucle
					// Second create the algorithm (here we do a dirty trick to preserve observers)
					if(algorithm != null)
						algorithmObservers = algorithm.getObservers();
					algorithm = new GrammaticalEvolution(problem, populationSize, generations, mutationProb, crossProb);
					algorithm.setObservers(algorithmObservers);
					
					// Set multithreading
					int avalaibleThreads = Runtime.getRuntime().availableProcessors();
					algorithmWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, avalaibleThreads);
					programWorker = new ProgramWorker(algorithm, problem, algorithmWorker, this, selectionOperator.toString(), crossOverOperator.toString(), mutationOperator.toString());
					
					// We can set different operators using
				  	algorithm.setSelectionOperator(selectionOperator);
					algorithm.setCrossoverOperator(crossOverOperator);
					algorithm.setMutationOperator(mutationOperator);
					
					System.out.println(selectionOperator.toString() + " - " + crossOverOperator.toString() + " - " + mutationOperator.toString());
					
					programWorker.execute();
					
					//Added for Test Bench
					try {
						programWorker.get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		  	}
	  	}
		
		//Added for Test Bench
		try {
			programWorker.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/***
		 * 
		 * TEST BENCH CODE - END
		 * 
		***/
	}
	
	public void addObserver(AlgObserver o) {
		if(algorithm == null)
			this.algorithmObservers.add(o);
		else
			algorithm.addObserver(o);
	}
	
	public void programWorkerStop() {
		programWorker.stop();
	}
	
	public int getPopulationSize() {
		return this.populationSize;
	}
	
	public void setPopulationSize(int size) {
		this.populationSize = size;
	}
	
	public int getGenerations() {
		return this.generations;
	}
	
	public void setGenerations(int generations) {
		this.generations = generations;
	}
	
	public int getItersPerIndividual() {
		return this.iterPerIndividual;
	}
	
	public void setItersPerIndividual(int itersPerIndiv) {
		this.iterPerIndividual = itersPerIndiv;
	}
	
	public int getChromosomeLength() {
		return this.chromosomeLength;
	}
	
	public void setChromosomeLength(int length) {
		this.chromosomeLength = length;
	}
	
	public int getCodonUpperBound() {
		return this.codonUpperBound;
	}
	
  	public FitnessEvaluatorInterface getFitnessFunc() {
		return fitnessFunc;
	}

	public void setFitnessFunc(FitnessEvaluatorInterface fitnessFunc) {
		this.fitnessFunc = fitnessFunc;
	}
	
	public void setCodonUpperBound(int upperBound) {
		this.codonUpperBound = upperBound;
	}
	
	public int getMaxCntWrappings() {
		return this.maxCntWrappings;
	}
	
	public void setMaxCntWrappings(int maxCntWrappings) {
		this.maxCntWrappings = maxCntWrappings;
	}
	
	public int getNumOfObjectives() {
		return this.numOfObjectives;
	}
	
	public void setNumOfObjectives(int num) {
		this.numOfObjectives = num;
	}
	
	public double getCrossProb() {
		return this.crossProb;
	}
	
	public void setCrossProb(double prob) {
		this.crossProb = prob;
	}
	
	public double getMutationProb() {
		return this.mutationProb;
	}
	
	public void setMutationProb(double prob) {
		this.mutationProb = prob;
	}
	
	public ArrayList<Double> getWorstObjectives() {
		return algorithm.worstObjetives;
	}
	
	public ArrayList<Double> getBestObjectives() {
		return algorithm.bestObjetives;
	}
	
	public ArrayList<Double> getAverageObjetives() {
		return algorithm.averageObjetives;
	}
	
	public ArrayList<Double> getAbsoluteBestObjetives() {
		return algorithm.absoluteBestObjetives;
	}
	
	public JProgressBar getProgressBar() {
		return ProgramWorker.getProgressBar();
	}
	
	public String getBestProgram() {
		return ProgramWorker.problem.generatePhenotype(ProgramWorker.solutions.get(0)).toString();
	}
	
	public String getBestProgramPretty() {
		NicerTree tree = TreeParser.parseTree(this.getBestProgram());
		
		return tree.pretty();
	}
	
	public String getCleanProgram(String s) {
		
		return TreeParser.clean(s);
	}
	
	public Logger getLogger() {
		return PacmanGrammaticalEvolution.logger;
	}
	
}
