package view.gui.swing;

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
	
	private long etaStart;
	
	
	public ProgramWorker(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem, MasterWorkerThreads<Variable<Integer>> algorithmWorker, GeneralController ctrl) {
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
		Integer currentStep = chunks.get(0);
		if(currentStep == 1)
			etaStart = System.nanoTime();
		
		progressBar.setValue(currentStep);
		
		long elapsedTime = System.nanoTime() - etaStart;
		long remainingTime = (elapsedTime * (100 / currentStep)) - elapsedTime;
		progressBar.setString("ETA: " + formatNanoSeconds(remainingTime));
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
		
	    
		Double extFitness = null;
	    double extAvgPoints = -1;
	    String extPhenotype = null;
		// Log solution
		for (Solution<Variable<Integer>> solution : solutions) {
			extFitness = solution.getObjectives().get(0);
		 	extAvgPoints = NaiveFitness.fitnessToPoints(solution.getObjectives().get(0)); // TODO wrapper.naive.fitnesstopoints
		 	extPhenotype = problem.generatePhenotype(solution).toString();
			
			logger.info(System.lineSeparator());
			logger.info("Fitness =  " + solution.getObjectives());
			logger.info("Average points = " + NaiveFitness.fitnessToPoints( solution.getObjectives().get(0) ));
			phenotypeString = problem.generatePhenotype(solution).toString();
			logger.info("Phenotype = (" + phenotypeString + ")");
		}
		
		if(externalLogger) {
		  	//String txtName = "Registro.txt";
		  	String csvName = "Registro.csv";
		  	String fitns = "[";
		  	for(FitnessEvaluatorInterface func : ctrl.getFitnessFuncs())
		  		fitns += func.getName();
		  	fitns = "]";
		  	
		  	ExtLog extLog = new ExtLog(ctrl.getMutationProb(), ctrl.getCrossProb(), ctrl.getPopulationSize(), ctrl.getGenerations(), ctrl.getItersPerIndividual(), fitns,
		  			extFitness.doubleValue(), extAvgPoints, extPhenotype, totalTime);
	
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

