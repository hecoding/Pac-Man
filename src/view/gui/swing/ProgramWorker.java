package view.gui.swing;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.externallogger.ExtLog;
import jeco.core.util.externallogger.ExtLogger;
import jeco.core.util.observer.AlgObserver;

public class ProgramWorker extends SwingWorker<Void, Integer> implements AlgObserver {
	private static JProgressBar progressBar;
	static Logger logger;
	static PacmanGrammaticalEvolution problem;
	static MasterWorkerThreads<Variable<Integer>> algorithmWorker;
	public static Solutions<Variable<Integer>> solutions;
	public static String phenotypeString;
	private static GeneralController ctrl;
	
	
	public ProgramWorker(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem, MasterWorkerThreads<Variable<Integer>> algorithmWorker, GeneralController ctrl) {
		ProgramWorker.ctrl = ctrl;
		algorithm.addObserver(this);
		if(progressBar == null)
			progressBar = new JProgressBar();
		
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
		progressBar.setValue(chunks.get(0));
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onIncrement(int n) {
		int percentage = Math.round((n * 100) / problem.generations);
		
		publish(percentage); // this calls process()
	}

	public static void exec() {
	  	boolean externalLogger = true;
		
	  	long tstart = System.nanoTime();
	  	
		// Execute algorithm
		solutions = algorithmWorker.execute();
		
		long tend = System.nanoTime();
	    double tTotal = tend - tstart;
	    //a segundos
	    tTotal /= 1000000000.0;
		
		Double extFitness = null;
	    double extAvgPoints = -1;
	    String extPhenotype = null;
		// Log solution
		for (Solution<Variable<Integer>> solution : solutions) {
			extFitness = solution.getObjectives().get(0);
		 	extAvgPoints = NaiveFitness.fitnessToPoints(solution.getObjectives().get(0));
		 	extPhenotype = problem.generatePhenotype(solution).toString();
			
			logger.info(System.lineSeparator());
			logger.info("Fitness =  " + solution.getObjectives().get(0));
			logger.info("Average points = " + NaiveFitness.fitnessToPoints( solution.getObjectives().get(0) ));
			phenotypeString = problem.generatePhenotype(solution).toString();
			logger.info("Phenotype = (" + phenotypeString + ")");
		}
		
		if(externalLogger){
		  	//String nombreTXT = "Registro.txt";
		  	String nombreCSV = "Registro.csv";
		  	
		  	ExtLog milog = new ExtLog(ctrl.getMutationProb(), ctrl.getCrossProb(), ctrl.getPopulationSize(), ctrl.getGenerations(), ctrl.getItersPerIndividual(), ctrl.getFitnessFunc().getNombre(),
		  			extFitness.doubleValue(), extAvgPoints, extPhenotype, tTotal);
	
		  	ExtLogger milogger = new ExtLogger();
		  	milogger.generarCSV(milog, nombreCSV);
		  	//milogger.generarTXT(milog, nombreTXT); (Pendiente poner bonito para TXT)
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
}

