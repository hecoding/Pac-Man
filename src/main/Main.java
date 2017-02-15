package main;

import treeProgram.ProgramTree;
import util.TreeParser;
import view.gui.swing.GUIView;

public class Main {

	public static void main(String[] args) {
		
		// la GUI
	  	@SuppressWarnings("unused")
		//CLIView view = new CLIView();
		GUIView view = new GUIView();
		
		
		/*
		// para probar árboles
		ProgramTree tree = TreeParser.toTree("if( distancia LE 1 ) { if( distancia EQ 2 ) { comer } } else{ if( distancia GT 3 ) { comer } }");
		System.out.println(tree);
		*/
		
		/*
		// para probar generación de gramáticas
		int NUM_DE_DERIVACIONES_A_GENERAR = 20;
		PacmanGrammaticalEvolution problem = new PacmanGrammaticalEvolution("test/gramaticadelaqueseforjanlossuenos.bnf",
				100, 100, 0.02, 0.6, null, 3, 2, 100, 3, 256
				);
		Solutions<Variable<Integer>> solutions = problem.newRandomSetOfSolutions(NUM_DE_DERIVACIONES_A_GENERAR);
		for(Solution<Variable<Integer>> sol : solutions) {
			System.out.println(problem.generatePhenotype(sol));
		}
		*/
	}

}
