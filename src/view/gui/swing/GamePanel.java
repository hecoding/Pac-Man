package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pacman.CustomExecutor;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.GameView;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Game game;
	GameView gv;
	JButton runButton;
	
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
		this.add(this.gv, BorderLayout.CENTER);
		
		runButton = new JButton("Run");
		runButton.setToolTipText("Run game");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runGame();
			}
		});
		this.add(this.runButton, BorderLayout.PAGE_END);
	}
	
	public void runGame() {
		// https://coderanch.com/t/345177/java/repaint-actionPerformed-Event
		Thread thread = new Thread(new Runnable() {
	        public void run() {
	        	CustomExecutor.runGameView(new GrammaticalAdapterController("?B?PC?BHCHC"), new StarterGhosts(), game, gv, 19);
	        }
	    });
	    thread.setPriority(Thread.NORM_PRIORITY);
	    thread.start();
	}
}
