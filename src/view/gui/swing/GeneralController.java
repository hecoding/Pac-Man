package view.gui.swing;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.fitness.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.fitness.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;
import jeco.core.util.observer.AlgObserver;
import parser.TreeParser;
import parser.nodes.NicerTree;
import util.FileList;

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
  	String grammarFolder ="./grammar/";
  	String grammar = grammarFolder + "base.bnf";

	int iterPerIndividual = 3;// = 10; // games ran per evaluation
  	
  	int chromosomeLength = PacmanGrammaticalEvolution.CHROMOSOME_LENGTH_DEFAULT;
  	int codonUpperBound = PacmanGrammaticalEvolution.CODON_UPPER_BOUND_DEFAULT;
  	int maxCntWrappings = PacmanGrammaticalEvolution.MAX_CNT_WRAPPINGS_DEFAULT;
  	int numOfObjectives = PacmanGrammaticalEvolution.NUM_OF_OBJECTIVES_DEFAULT;
	
	public GeneralController() {
		
	}
	
	public void execute() {
		// First create the problem
		problem = new PacmanGrammaticalEvolution(grammar,
				populationSize, generations, mutationProb, crossProb, fitnessFunc, iterPerIndividual,
				this.numOfObjectives, this.chromosomeLength, this.maxCntWrappings, this.codonUpperBound
				);
		// Second create the algorithm (here we do a dirty trick to preserve observers)
		if(algorithm != null)
			algorithmObservers = algorithm.getObservers();
		algorithm = new GrammaticalEvolution(problem, populationSize, generations, mutationProb, crossProb);
		algorithm.setObservers(algorithmObservers);
		
		// We can set different operators using
	  	//algorithm.setSelectionOperator(selectionOperator);
		//algorithm.setCrossoverOperator(crossoverOperator);
		//algorithm.setMutationOperator(mutationOperator);
		
		// Set multithreading
		int avalaibleThreads = Runtime.getRuntime().availableProcessors();
		algorithmWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, avalaibleThreads);
		programWorker = new ProgramWorker(algorithm, problem, algorithmWorker, this);
		
		programWorker.execute();
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
	
	public String getGrammar() {
		return this.grammar;
	}
	
	public String getCleanGrammar() {
		return FileList.cleanFileName(this.grammar);
	}
	
	public ArrayList<String> getGrammarNames() {
		return FileList.listFilesInto(this.grammarFolder, ".bnf");
	}
	
	public ArrayList<String> getCleanGrammarNames() {
		return FileList.cleanListFiles(this.grammarFolder, ".bnf");
	}
	
	public void setGrammar(String grammar) {
		this.grammar = this.getGrammarRealName(grammar);
	}
	
	private String getGrammarRealName(String s) {
		for(String g : this.getGrammarNames()) {
			if(s.equals(FileList.cleanFileName(g)))
				return g;
		}
		
		return null;
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
