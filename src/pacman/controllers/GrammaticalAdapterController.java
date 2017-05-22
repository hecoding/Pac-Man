package pacman.controllers;

import pacman.game.Game;
import parser.TreeParser;
import parser.nodes.NicerTree;
import pacman.game.Constants.MOVE;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class GrammaticalAdapterController extends Controller<MOVE>
{
	private String fenotipoStr;
	private NicerTree tree;
	
	public GrammaticalAdapterController(String fenotipo) {
		this.fenotipoStr = fenotipo;
		tree = TreeParser.parseTree(fenotipoStr);
	}

	public MOVE getMove(Game game, long timeDue) {
		return tree.executeAndGetMove(game);
	}	
}