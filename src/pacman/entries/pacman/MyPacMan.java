package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	private String fenotipo;
	private int poslectura;
	
	public MyPacMan(String fenotipo) {
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