package main;

import java.util.logging.Logger;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;
import view.gui.swing.GUIView;

public class Main {
	static int avalaibleThreads = Runtime.getRuntime().availableProcessors();
	static Logger logger = PacmanGrammaticalEvolution.logger;
	final static int ticks = 19;

	public static void main(String[] args) {
	  	@SuppressWarnings("unused")
		//CLIView view = new CLIView();
		GUIView view = new GUIView();
	}

}
