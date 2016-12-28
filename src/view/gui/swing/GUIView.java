package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.operator.evaluator.NaiveFitness;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.observer.AlgObserver;


public class GUIView extends JFrame {
	private static final long serialVersionUID = 1L;
	final static int ticks = 19;
	//private static Controller ctrl;
	private CenterPanel centerPanel;
	private SettingsPanel settingsPanel;
	private StatusBarPanel status;
	Worker1 progressWorker;
	
	static MasterWorkerThreads<Variable<Integer>> masterWorker;
	static GrammaticalEvolution algorithm;
	static PacmanGrammaticalEvolution problem;
	static Logger logger;
	
	public GUIView(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem) {
		//ctrl = controller;
		//ctrl.addModelObserver(worker);
		GUIView.algorithm = algorithm;
		GUIView.problem = problem;
		logger = GrammaticalEvolution.logger;
		this.progressWorker = new Worker1(algorithm);
		
		this.setTitle("Pac-Man");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	public GUIView(MasterWorkerThreads<Variable<Integer>> masterWorker, GrammaticalEvolution algorithm,
			PacmanGrammaticalEvolution problem) {
		//ctrl = controller;
		//ctrl.addModelObserver(worker);
		GUIView.masterWorker = masterWorker;
		GUIView.algorithm = algorithm;
		GUIView.problem = problem;
		logger = GrammaticalEvolution.logger;
		this.progressWorker = new Worker1(algorithm);
		
		this.setTitle("Pac-Man");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {
		this.status = new StatusBarPanel(algorithm);
		this.centerPanel = new CenterPanel(algorithm, this.status);
		this.settingsPanel = new SettingsPanel(algorithm, problem, this.status, this.progressWorker);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(settingsPanel, BorderLayout.LINE_START);
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(1024,668));
		this.setMinimumSize(new Dimension(750, 550));
		this.setLocationRelativeTo(null); // center on the screen (doesn't show nice with multiple monitors)
		this.setVisible(true);
	}
	
	public static class Worker1 extends SwingWorker<Void, Integer> implements AlgObserver {
		public static JProgressBar progressBar;
		
		public Worker1(GrammaticalEvolution algorithm) {
			algorithm.addObserver(this);
			if(progressBar == null)
				progressBar = new JProgressBar();
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
	}
	
	public static void exec() {
		// Execute algorithm
		Solutions<Variable<Integer>> solutions = masterWorker.execute();
		
		// Log solution
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info(System.lineSeparator());
			logger.info("Fitness =  " + solution.getObjectives().get(0));
			logger.info("Average points = " + NaiveFitness.fitnessToPoints( solution.getObjectives().get(0) ));
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
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

}
