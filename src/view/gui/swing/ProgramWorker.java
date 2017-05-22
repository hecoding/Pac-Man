package view.gui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import jeco.core.algorithm.Algorithm;
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
	Algorithm algorithm;
	static PacmanGrammaticalEvolution problem;
	static MasterWorkerThreads<Variable<Integer>> algorithmWorker;
	public static Solutions<Variable<Integer>> solutions;
	public static String phenotypeString;
	private static GeneralController ctrl;
	
	private long lastIterStart;
	private LinkedList<Long> etaQueue;
	private int etaLenght;
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
		this.algorithm = algorithm;
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

		long remainingTime = calculateETA(currentStep);
		
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

		if(algorithm.isStopped())
			progressBar.setValue(0);
		else
			publish(percentage); // this calls process()
	}

	public static void exec() {
	  	boolean externalLogger = false;
		
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
		
		System.out.println(ctrl.getAbsoluteBestObjectives());
		
		generateAndSavePlot();
		
		try {
			FileWriter fw = new FileWriter(new File("logs/longitud.txt"), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("\nLongitud: " + problem.getNumberOfVariables() + "\nFitness: " + extFitnesses + "\n" + ctrl.getBestProgramPretty() + "\n");
			
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public long calculateETA(int currentStep) {
		if(currentStep == 1 || currentStep == 0){
			lastIterStart = System.nanoTime();
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

		return (long) ((ctrl.getGenerations() - currentStep) * (queueTime / etaLenght));
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
	
	private static void generateAndSavePlot()
	{
	 	JFreeChart chart;
	 	XYPlot plot;
	 	XYSeries worstSeries;
		XYSeries bestSeries;
		XYSeries avgSeries;
		XYSeries absoluteSeries;
	 	XYSeriesCollection dataset;
	 	XYSeriesCollection dataset2;
	 	
		// Fitness tab
		worstSeries = new XYSeries("Worst of generation");
		bestSeries = new XYSeries("Best of generation");
		avgSeries = new XYSeries("Generation average");
		absoluteSeries = new XYSeries("Absolute best");
		
		dataset = new XYSeriesCollection();
		dataset2 = new XYSeriesCollection();
		
		Color transparent = new Color(0,0,0,0);
		Color lighterGray = new Color(200, 200, 200);
		Color blue = new Color(175, 224, 229);
		Color blue1 = new Color(119, 141, 178);
		Color blue2 = new Color(46, 77, 127);
		
		chart = ChartFactory.createXYAreaChart("", "Generations", "Fitness", dataset);
		chart.setBackgroundPaint(Color.white);
		
		plot = chart.getXYPlot();
		plot.setDataset(0, dataset2);
		XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);
		lineRenderer.setSeriesPaint(0, blue1);
		lineRenderer.setSeriesStroke(0, new BasicStroke(0.8f));
		lineRenderer.setSeriesPaint(1, blue2);
		lineRenderer.setSeriesStroke(1, new BasicStroke(2.5f));
		plot.setRenderer(0, lineRenderer);
		
		plot.setDataset(1, dataset);
		XYDifferenceRenderer diffRenderer = new XYDifferenceRenderer(
				blue, blue, false
			);
		diffRenderer.setSeriesPaint(0, transparent);
		diffRenderer.setSeriesPaint(1, transparent);
		diffRenderer.setSeriesVisibleInLegend(0, false);
		diffRenderer.setSeriesVisibleInLegend(1, false);
		plot.setRenderer(1, diffRenderer);
		plot.setOutlinePaint(null);
		plot.setBackgroundPaint(Color.white);
		plot.setForegroundAlpha(1);
		plot.setDomainGridlinePaint(lighterGray);
		plot.setRangeGridlinePaint(lighterGray);
		
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setTickMarkPaint(Color.black);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickMarkPaint(Color.black);
		rangeAxis.setLowerMargin(0.01);
		rangeAxis.setUpperMargin(0.01);
		rangeAxis.setAutoRangeIncludesZero(false);
		
		for(int i = 0; i < ctrl.getWorstObjectives().size(); i++) {
			worstSeries.add(i, ctrl.getWorstObjectives().get(i).get(0));
		}
		
		for(int i = 0; i < ctrl.getBestObjectives().size(); i++) {
			bestSeries.add(i, ctrl.getBestObjectives().get(i).get(0));
		}
		
		for(int i = 0; i < ctrl.getAverageObjectives().size(); i++) {
			avgSeries.add(i, ctrl.getAverageObjectives().get(i).get(0));
		}
		
		for(int i = 0; i < ctrl.getAbsoluteBestObjectives().size(); i++) {
			absoluteSeries.add(i, ctrl.getAbsoluteBestObjectives().get(i).get(0));
		}
		
		dataset.addSeries(worstSeries);
		dataset.addSeries(bestSeries);
		
		dataset2.addSeries(avgSeries);
		dataset2.addSeries(absoluteSeries);
		
		try {
			ChartUtilities.saveChartAsJPEG(new File("logs/longitud_" + problem.getNumberOfVariables() + ".jpeg"), chart, 1080, 720);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

