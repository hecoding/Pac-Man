package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import jeco.core.optimization.threads.MasterWorkerThreads;
import jeco.core.problem.Variable;


public class GUIView extends JFrame {
	private static final long serialVersionUID = 1L;
	final static int ticks = 19;
	//private static Controller ctrl;
	private CenterPanel centerPanel;
	private SettingsPanel settingsPanel;
	private StatusBarPanel status;
	
	static ProgramWorker programWorker;
	static MasterWorkerThreads<Variable<Integer>> algorithmWorker;
	static GrammaticalEvolution algorithm;
	static PacmanGrammaticalEvolution problem;
	static Logger logger;
	
	public GUIView(GrammaticalEvolution algorithm, PacmanGrammaticalEvolution problem) {
		//ctrl = controller;
		//ctrl.addModelObserver(worker);
		GUIView.algorithm = algorithm;
		GUIView.problem = problem;
		logger = GrammaticalEvolution.logger;
		
		this.setTitle("Pac-Man");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	public GUIView(MasterWorkerThreads<Variable<Integer>> algorithmWorker, GrammaticalEvolution algorithm,
			PacmanGrammaticalEvolution problem) {
		//ctrl = controller;
		//ctrl.addModelObserver(worker);
		GUIView.algorithmWorker = algorithmWorker;
		GUIView.algorithm = algorithm;
		GUIView.problem = problem;
		logger = GrammaticalEvolution.logger;
		
		this.setTitle("Pac-Man");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {
		programWorker = new ProgramWorker(algorithm, problem, algorithmWorker);
		
		GamePanel gp = new GamePanel();
		this.status = new StatusBarPanel(algorithm);
		this.centerPanel = new CenterPanel(algorithm, this.status, programWorker, gp);
		this.settingsPanel = new SettingsPanel(algorithm, problem, this.status, programWorker, gp);
		
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

}
