package view.gui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
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

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
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
	private static String selecAlgo, mutAlgo, crossAlgo;
	
	
	public ProgramWorker(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem, MasterWorkerThreads<Variable<Integer>> algorithmWorker, GeneralController ctrl, String selection, String crossOver, String mutation) {
		ProgramWorker.ctrl = ctrl;
		algorithm.addObserver(this);
		if(progressBar == null)
			progressBar = new JProgressBar();
		
		logger = GrammaticalEvolution.logger;
		ProgramWorker.problem = problem;
		ProgramWorker.algorithmWorker = algorithmWorker;
		
		selecAlgo = selection;
		mutAlgo = mutation;
		crossAlgo = crossOver;
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
	    double totalTime = tend - tstart;
	    // To seconds
	    totalTime /= 1000000000.0;
		
	    
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
		
		if(externalLogger) {
		  	//String txtName = "Registro.txt";
		  	String csvName = "Registro.csv";
		  	
		  	ExtLog extLog = new ExtLog(ctrl.getMutationProb(), ctrl.getCrossProb(), ctrl.getPopulationSize(), ctrl.getGenerations(), ctrl.getItersPerIndividual(), selecAlgo + " - " + crossAlgo + " - " + mutAlgo,
		  			extFitness.doubleValue(), extAvgPoints, extPhenotype, totalTime);
	
		  	ExtLogger extlogger = new ExtLogger();
		  	extlogger.generateCSV(extLog, csvName);
		  	//extlogger.generateTXT(milog, nombreTXT); (Pendiente poner bonito para TXT)
		  	
		  	//Generate and save plot.
		  	generateAndSavePlot();
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
			worstSeries.add(i, ctrl.getWorstObjectives().get(i));
		}
		
		for(int i = 0; i < ctrl.getBestObjectives().size(); i++) {
			bestSeries.add(i, ctrl.getBestObjectives().get(i));
		}
		
		for(int i = 0; i < ctrl.getAverageObjetives().size(); i++) {
			avgSeries.add(i, ctrl.getAverageObjetives().get(i));
		}
		
		for(int i = 0; i < ctrl.getAbsoluteBestObjetives().size(); i++) {
			absoluteSeries.add(i, ctrl.getAbsoluteBestObjetives().get(i));
		}
		
		dataset.addSeries(worstSeries);
		dataset.addSeries(bestSeries);
		
		dataset2.addSeries(avgSeries);
		dataset2.addSeries(absoluteSeries);
		
		try {
			ChartUtilities.saveChartAsJPEG(new File(selecAlgo + " - " + crossAlgo + " - " + mutAlgo + ".jpeg"), chart, 1920, 1080);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

