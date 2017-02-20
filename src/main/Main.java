package main;

import view.gui.swing.GUIView;

public class Main {

	public static void main(String[] args) {
		
		// la GUI
	  	@SuppressWarnings("unused")
		//CLIView view = new CLIView();
		GUIView view = new GUIView();
		
		
		
		// para probar árboles
		/*
	  	String phenotypeEx = "if( getDistanceToClosestNonEdibleGhost GE 67 ){ if( ! isJunction and ! isJunction ){ moveDown } else{ moveRight } } else{ moveLeft }";
	  	NicerTree tree = TreeParser.parseTree(TreeParser.toUgly(phenotypeEx), null);
	  	System.out.println(phenotypeEx);
	  	System.out.println(TreeParser.toUgly(tree.pretty()));
	  	System.out.println();
	  	System.out.println(tree.pretty());
	  	tree.executeAndGetMove();
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
