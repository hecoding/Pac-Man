package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;


public class GUIView extends JFrame {
	private static final long serialVersionUID = 1L;
	//private static Controller ctrl;
	private CenterPanel centerPanel;
	private SettingsPanel settingsPanel;
	private StatusBarPanel status;
	Worker1 progressWorker;
	
	MasterWorkerThreads<Variable<Integer>> masterWorker;
	PacmanGrammaticalEvolution problem;
	
	public GUIView(MasterWorkerThreads<Variable<Integer>> masterWorker, PacmanGrammaticalEvolution problem) {
		//ctrl = controller;
		this.masterWorker = masterWorker;
		this.problem = problem;
		this.progressWorker = new Worker1();
		//ctrl.addModelObserver(worker);
		
		this.setTitle("Pac-Man");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	private void initGUI() {
		this.status = new StatusBarPanel();
		this.centerPanel = new CenterPanel(this.status);
		this.settingsPanel = new SettingsPanel(this.status, this.progressWorker);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(settingsPanel, BorderLayout.LINE_START);
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(1024,668));
		this.setMinimumSize(new Dimension(750, 550));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public static class Worker1 extends SwingWorker<Void, Integer> {//implements GeneticAlgorithmObserver {
		public static JProgressBar progressBar;
		
		public Worker1() {
			if(progressBar == null)
				progressBar = new JProgressBar();
		}

		@Override
		protected Void doInBackground() throws Exception {
			//ctrl.run();
			
			return null;
		}
		
		@Override
		protected void process(List<Integer> chunks) {
			progressBar.setValue(chunks.get(0));
		}
/*
		@Override
		public void onStartRun() {
			
		}

		@Override
		public void onEndRun() {
			
		}

		@Override
		public void onIncrement(int n) {
			publish(n); // this calls process()
		}*/
	}

}
