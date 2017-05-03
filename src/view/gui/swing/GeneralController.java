package view.gui.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.fitness.*;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;
import jeco.core.util.observer.AlgObserver;
import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import parser.TreeParser;
import parser.nodes.NicerTree;
import util.FileList;
import view.gui.swing.factory.GhostControllerFactory;
import view.gui.swing.factory.ObjectiveFactory;

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
	double crossProb = 0.2;
	double mutationProb = 0.4;
  	String grammarFolder ="./grammar/";
  	String grammar = grammarFolder + "base.bnf";
  	MOFitnessWrapper fitnessWrapper = new MOFitnessWrapper(new NaiveFitness());
  	Controller<EnumMap<GHOST,MOVE>> ghostController = new Legacy();

	int iterPerIndividual = 10; // games ran per evaluation
	double elitismPerc = 0.05;
  	
  	int chromosomeLength = PacmanGrammaticalEvolution.CHROMOSOME_LENGTH_DEFAULT;
  	int codonUpperBound = PacmanGrammaticalEvolution.CODON_UPPER_BOUND_DEFAULT;
  	int maxCntWrappings = PacmanGrammaticalEvolution.MAX_CNT_WRAPPINGS_DEFAULT;
  	
  	static GhostControllerFactory ghostControllerFactory = GhostControllerFactory.getInstance();
  	String selectedGhostController = ghostController.getClass().getSimpleName();
  	static ObjectiveFactory objectiveFactory = ObjectiveFactory.getInstance();
  	String[] selectedFitnessObjectives = fitnessWrapper.getFuncNames();
	
	public GeneralController() {
		// Register all ghost controllers
		ghostControllerFactory.register(AggressiveGhosts.class);
		ghostControllerFactory.register(Legacy.class);
		ghostControllerFactory.register(Legacy2TheReckoning.class);
		ghostControllerFactory.register(RandomGhosts.class);
		ghostControllerFactory.register(StarterGhosts.class);
		
		// Register all objectives into its factory
		objectiveFactory.register(new LevelsCompletedFitness());
		objectiveFactory.register(new NaiveFitness());
		objectiveFactory.register(new PointsNoGhostMultFitness());
	}

	public void execute() {
		// Create multiobjective wrapper
		this.fitnessWrapper.clear();
		for (String name : this.selectedFitnessObjectives) {
			this.fitnessWrapper.addObjectiveFunction(objectiveFactory.create(name));
		}
		
		// Create ghosts controller
		ghostController = ghostControllerFactory.create(this.selectedGhostController);
		
		
		// Now first create the problem
		problem = new PacmanGrammaticalEvolution(
				ghostController, grammar, fitnessWrapper, iterPerIndividual,
				this.chromosomeLength, this.maxCntWrappings, this.codonUpperBound
				);
		// Second create the algorithm (here we do a dirty trick to preserve observers)
		if(algorithm != null)
			algorithmObservers = algorithm.getObservers();
		algorithm = new GrammaticalEvolution(problem, populationSize, generations, mutationProb, crossProb, (int) Math.floor(elitismPerc * populationSize));
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
	
  	public ArrayList<FitnessEvaluatorInterface> getFitnessFuncs() {
		return this.fitnessWrapper.funcs;
	}
	
	public String getFitnessName(int i) {
		return this.fitnessWrapper.getFuncName(i);
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
	
	public String[] getObjectivesNames() {
		return objectiveFactory.getRegisteredKeys();
	}
	
	public int getNumOfSelectedObjectives() {
		return this.fitnessWrapper.getNumberOfObjs();
	}
	
	public String[] getSelectedObjectivesNames() {
		return this.fitnessWrapper.getFuncNames();
	}
	
	public void setSelectedObjectives(String[] names) {
		this.selectedFitnessObjectives = names;
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
	
	public double getElitismPercentage() {
		return this.elitismPerc;
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
		ArrayList<String> ret = FileList.cleanListFiles(this.grammarFolder, ".bnf");
		Collections.sort(ret);
		
		return ret;
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
	
	public String getGhostControllerName() {
		return this.ghostController.getClass().getSimpleName();
	}
	
	public void setSelectedGhostController(String ghostCtrl) {
		this.selectedGhostController = ghostCtrl;
	}
	
	public ArrayList<String> getGhostControllerNames() {
		ArrayList<String> ret = new ArrayList<String>(Arrays.asList(ghostControllerFactory.getRegisteredKeys()));
		Collections.sort(ret);
		
		return ret;
	}
	
	public ArrayList<ArrayList<Double>> getWorstObjectives() {
		return algorithm.worstObjetives;
	}
	
	public ArrayList<ArrayList<Double>> getBestObjectives() {
		return algorithm.bestObjetives;
	}
	
	public ArrayList<ArrayList<Double>> getAverageObjectives() {
		return algorithm.averageObjetives;
	}
	
	public ArrayList<ArrayList<Double>> getAbsoluteBestObjectives() {
		return algorithm.absoluteBestObjetives;
	}
	
	public JProgressBar getProgressBar() {
		return ProgramWorker.getProgressBar();
	}
	
	public String getBestProgram() {
		if(ProgramWorker.solutions == null)
			return "";
		else
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
