package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import pacman.CustomExecutor;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.GameView;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	TVThread thread;
	Game game;
	GameView gv;
	JTextArea drafText;
	JButton runButton;
	JButton stopButton;
	static final String NO_PROGRAM = "No trained solution yet";
	String savedInitialState;
	Game initialGame;
	JPanel TV;
	JPanel buttonPanel;
	
	public GamePanel() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	private void initGUI() {
		game = CustomExecutor.getNewGame(); // TODO this line slows down the whole GUI initialization
		gv = CustomExecutor.getGameViewFromGame(this.game);
		
		this.setLayout(new BorderLayout());
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.NORTH;

		gc.gridy=0;
		gc.weighty = 0.2;
		TV = new JPanel(new BorderLayout());
		TV.add(this.gv, BorderLayout.CENTER);
		buttonPanel.add(this.TV, gc);
		
		runButton = new JButton("Run code");
		runButton.setToolTipText("Run this code on pacman window");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runGame();
			}
		});
		gc.gridy++;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(runButton, gc);
		
		stopButton = new JButton("Stop");
		stopButton.setVisible(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.switchOffTV();
				endadf();
			}
		});
		gc.gridy++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(stopButton, gc);
		
		JButton copyButton = new JButton("Copy best here");
		copyButton.setToolTipText("Copy best solution code in the text area");
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyBest();
			}
		});
		gc.gridy++;
		gc.weighty = 3;
		gc.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(copyButton, gc);
		
		this.add(buttonPanel, BorderLayout.LINE_START);
		
		this.drafText = new JTextArea();
		this.add(new JScrollPane(drafText), BorderLayout.CENTER);
	}
	
	public void runGame() {
		// https://coderanch.com/t/345177/java/repaint-actionPerformed-Event
		this.thread = new TVThread();
	    thread.setPriority(Thread.NORM_PRIORITY);
	    thread.start();
	}
	
	public class TVThread extends Thread {
		private AtomicBoolean stopIt = new AtomicBoolean(false);
		
		public void run() {
        	if(drafText != null && !drafText.getText().isEmpty() && !drafText.getText().equals(NO_PROGRAM)) {
        		if(initialGame == null) // to recover initial state and allow replay
        			initialGame = game.copy();
        		else
        			resetGame();
        		
        		beginasdf();
        		CustomExecutor.runGameView(new GrammaticalAdapterController(drafText.getText()), new StarterGhosts(), game, gv, 19, stopIt);
        		endadf();
        	}
        }
        
        public void switchOffTV() {
        	this.stopIt.set(true);
        }
	}
	
	public void resetGame() {
		this.game = initialGame.copy();
		this.gv = CustomExecutor.getGameViewFromGame(game);
		this.TV.removeAll();
		this.TV.add(this.gv);
		this.TV.revalidate();
		this.TV.repaint();
	}
	
	public void copyAndRun(String program) {
		this.copyBest();		
		this.runGame();
	}
	
	public void copyBest() {
		if(ProgramWorker.phenotypeString == null || ProgramWorker.phenotypeString.isEmpty())
			this.drafText.setText(NO_PROGRAM);
		else
			this.drafText.setText(ProgramWorker.phenotypeString);
	}
	
	private void beginasdf() {
		runButton.setEnabled(false);
		stopButton.setVisible(true);
	}
	
	private void endadf() {
		runButton.setEnabled(true);
		stopButton.setVisible(false);
	}
}
