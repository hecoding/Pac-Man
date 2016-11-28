package pacman.controllers;

import pacman.game.Game;
import pacman.game.Constants.MOVE;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class GrammaticalAdapterController extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	private String fenotipo;
	private int poslectura;
	
	public GrammaticalAdapterController(String fenotipo) {
		this.fenotipo = fenotipo;
		poslectura = 0;
	}

	public MOVE getMove(Game game, long timeDue) 
	{
		char mov = fenotipo.charAt(poslectura);
		
		poslectura++;
		if(poslectura >= fenotipo.length())
			poslectura = 0;
		
		if(mov == 'U')
			myMove = MOVE.UP;
		else if(mov == 'D')
			myMove = MOVE.DOWN;
		else if(mov == 'R')
			myMove = MOVE.RIGHT;
		else if (mov == 'L')
			myMove = MOVE.LEFT;
		else
			System.err.println("FENOTIPO INCORRECTO");
		
		return myMove;
	}
}