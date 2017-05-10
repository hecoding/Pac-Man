package view.gui.swing;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.fitness.FitnessEvaluatorInterface;
import jeco.core.operator.evaluator.fitness.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.observer.AlgObserver;
import util.externallogger.ExtLog;
import util.externallogger.ExtLogger;

public class ProgramWorker extends SwingWorker<Void, Integer> implements AlgObserver {
	private static JProgressBar progressBar;
	static Logger logger;
	static PacmanGrammaticalEvolution problem;
	static MasterWorkerThreads<Variable<Integer>> algorithmWorker;
	public static Solutions<Variable<Integer>> solutions;
	public static String phenotypeString;
	private static GeneralController ctrl;
	
	private long lastIterStart;
	private LinkedList<Long> etaQueue;
	private int etaLenght;
	private int generations;
	long queueTime;
	
	public ProgramWorker(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem, MasterWorkerThreads<Variable<Integer>> algorithmWorker, GeneralController ctrl) {
		etaLenght = 5;
		etaQueue = new LinkedList<Long>();
		ProgramWorker.ctrl = ctrl;
		algorithm.addObserver(this);
		if(progressBar == null)
			progressBar = new JProgressBar();
		
		progressBar.setStringPainted(true);
		progressBar.setString("Initializing...");
		logger = GrammaticalEvolution.logger;
		ProgramWorker.problem = problem;
		ProgramWorker.algorithmWorker = algorithmWorker;
	}

	@Override
	protected Void doInBackground() throws Exception {
		//ctrl.run();
		exec();
		
		return null;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		int currentStep = chunks.get(0);
		
		if(currentStep == 1 || currentStep == 0){
			lastIterStart = System.nanoTime();
			generations = ctrl.getGenerations();
		}
		
		progressBar.setValue(currentStep);
		
		long lastIterTime = System.nanoTime() - lastIterStart;
		
		if(etaQueue.size() < etaLenght){ //+2 margin
			etaQueue.addFirst(lastIterTime);
			queueTime += lastIterTime;
		}
		else{
			queueTime -= etaQueue.pollLast();
			etaQueue.addFirst(lastIterTime);
			queueTime += lastIterTime;
		}
		
		long remainingTime = (long) ((generations - currentStep) * (queueTime / etaLenght));
		
		if(currentStep < etaLenght + 2) //+2 margin
			progressBar.setString("Calculating ETA...");
		else
			progressBar.setString("ETA: " + formatNanoSeconds(remainingTime));
		
		lastIterStart = System.nanoTime();
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onIncrement(int n) {
		int percentage = Math.round((n * 100) / ctrl.getGenerations());
		
		publish(percentage); // this calls process()
	}

	public static void exec() {
	  	boolean externalLogger = true;
		
	  	long tstart = System.nanoTime();
	  	
		// Execute algorithm
		solutions = algorithmWorker.execute();
		
		long tend = System.nanoTime();
	    double totalTime = tend - tstart;
	    // To seconds
	    totalTime /= 1000000000.0;
		
	    
		ArrayList<Double> extFitnesses = new ArrayList<Double>();
	    String extPhenotype = null;
	    
		// Log solution
		for (Solution<Variable<Integer>> solution : solutions) {
			extFitnesses = solution.getObjectives();
		 	extPhenotype = problem.generatePhenotype(solution).toString();
			
			logger.info(System.lineSeparator());
			logger.info("Fitness for selected objetive(s) =  " + solution.getObjectives());
			phenotypeString = problem.generatePhenotype(solution).toString();
			logger.info("Phenotype = (" + phenotypeString + ")");
		}
		
		if(externalLogger) {
			ArrayList<FitnessEvaluatorInterface> fitnessFuncs = ctrl.getFitnessFuncs();
		  	String csvName = "Registro.csv";
		  	
		  	String fitns = "[";
		  	for(int i = 0; i < fitnessFuncs.size(); i++){
		  		fitns += fitnessFuncs.get(i).getName();
		  		if(i != fitnessFuncs.size()-1)
		  			fitns += ", ";
		  	}
		  	fitns += "]";
		  	
		  	int aborted = (ctrl.getCurrentGeneration() == ctrl.getGenerations()) ? 0 : 1;
		  	
		  	ExtLog extLog = new ExtLog(ctrl.getSelectedSelectionOperator(), ctrl.getSelectedMutationOperator(), ctrl.getMutationProb(),
		  			ctrl.getSelectedCrossoverOperator(), ctrl.getCrossProb(), ctrl.getElitismPercentage(), ctrl.getPopulationSize(),
		  			ctrl.getGenerations(), ctrl.getItersPerIndividual(), fitns, extFitnesses, extPhenotype, totalTime, aborted);
	
		  	ExtLogger extlogger = new ExtLogger();
		  	extlogger.generateCSV(extLog, csvName);
		  	//extlogger.generateTXT(milog, nombreTXT); (TODO Pendiente poner bonito para TXT)
	    }
		
		/*
		// Run visuals for the best program
		System.out.println();
		System.out.println("Press any key to begin visualization of best individual...");
		Scanner sc = new java.util.Scanner(System.in);
		sc.nextLine();
		sc.close();
		
		Executor exec = new Executor();
		exec.runGame(new GrammaticalAdapterController(problem.generatePhenotype(solutions.get(0)).toString()), new StarterGhosts(), true, ticks);
		*/
	}
	
	public void stop() {
		algorithmWorker.stop();
	}
	
	public static JProgressBar getProgressBar() {
		if(progressBar == null)
			progressBar = new JProgressBar();
		
		return progressBar;
	}
	
	public static String formatNanoSeconds(long timeInNanoSeconds) {
		int timeInSeconds = (int) Math.floor(timeInNanoSeconds / 1000000000.0);
		int hours = timeInSeconds / 3600;
		int secondsLeft = timeInSeconds - hours * 3600;
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft - minutes * 60;

		String formattedTime = "";
		if (hours > 0) {
			if (hours < 10)
				formattedTime += "0";
			formattedTime += hours + ":";
		}

		if (minutes > 0) {
			if (minutes < 10)
				formattedTime += "0";
			formattedTime += minutes + ":";
		}

		if (seconds < 10)
			formattedTime += "0";
		formattedTime += seconds ;

		return formattedTime;
	}
}

