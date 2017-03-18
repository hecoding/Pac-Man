package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.PlainDocument;

import pacman.CustomExecutor;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.GameView;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	GeneralController ctrl;
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
	int defaultTick = 19;
	AtomicInteger tick;
	JPanel buttonPanel;
	
	public GamePanel(GeneralController ctrl) {
		this.ctrl = ctrl;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	private void initGUI() {
		game = CustomExecutor.getNewGame(); // TODO this line slows down the whole GUI initialization
		gv = CustomExecutor.getGameViewFromGame(this.game);
		tick = new AtomicInteger(defaultTick);
		
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
		
		JPanel speedPanel = new JPanel(new BorderLayout());
		JSlider speedSlider = new JSlider(5, 50);
		speedSlider.setInverted(true);
		speedSlider.setValue(defaultTick);
		speedSlider.addChangeListener(new TickSliderListener());
		speedSlider.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e))
					speedSlider.setValue(defaultTick);
			}
		});
		speedSlider.setToolTipText("Right click to reset speed value.");
		JLabel speedLabel = new JLabel("Speed");
		speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
		speedPanel.add(speedLabel, BorderLayout.PAGE_START);
		speedPanel.add(speedSlider, BorderLayout.CENTER);
		JPanel tvAndControls = new JPanel(new BorderLayout());
		tvAndControls.add(this.TV, BorderLayout.CENTER);
		tvAndControls.add(speedPanel, BorderLayout.PAGE_END);
		buttonPanel.add(tvAndControls, gc);
		
		runButton = new JButton("Run code");
		runButton.setMnemonic(KeyEvent.VK_R);
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
				endDraftExecution();
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
		this.drafText.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
		this.drafText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
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
        		
        		String prog = ctrl.getCleanProgram(drafText.getText());
        		beginDraftExecution();
        		CustomExecutor.runGameView(new GrammaticalAdapterController(prog), new StarterGhosts(), game, gv, tick, stopIt);
        		endDraftExecution();
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
			this.drafText.setText(ctrl.getBestProgramPretty());
	}
	
	private void beginDraftExecution() {
		runButton.setEnabled(false);
		stopButton.setVisible(true);
	}
	
	private void endDraftExecution() {
		runButton.setEnabled(true);
		stopButton.setVisible(false);
	}
	
	class TickSliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			if (!source.getValueIsAdjusting()) {
				tick.set((int)source.getValue());
			}
		}
	}
}
