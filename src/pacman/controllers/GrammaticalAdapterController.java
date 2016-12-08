package pacman.controllers;

import pacman.game.Game;
import pacman.game.Constants.DM;
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

	
	
	public MOVE getMove(Game game, long timeDue) {
        char mov = fenotipo.charAt(poslectura);
        //TODO: hacer estatica postlectura que si no se jode
        poslectura++;
        if(poslectura >= fenotipo.length())
            poslectura = 0;
        switch (mov) {
        case 'U':
            myMove = MOVE.UP;
             break;
        case 'D':
            myMove = MOVE.DOWN;
             break;
        case 'R':
            myMove = MOVE.RIGHT;
             break;
        case 'L':
            myMove = MOVE.LEFT;
             break;
       case '?': {    
    	   		mov = fenotipo.charAt(poslectura);
    	   		poslectura++;
    	   		//Ifs para diferenciar función condicional
    	   		if (mov == 'P') {
    	   			if(game.getClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex()) != null){//Chekeo de que los fantasmas hayan salido ya
	    	   			if(!game.closerThan(game.getPacmanCurrentNodeIndex(), game.getClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex()).currentNodeIndex, 10))
	    	   				return getMove(game, timeDue);
    	   			}
    	   		}
    	   		else {
    	   			System.out.println("ERROR EN FORMATO DE FENOTIPO: ?");
    	   		}
    	   	break;
       	}
       	case 'H':{
       		//game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex()).currentNodeIndex, game.getPacmanLastMoveMade(), DM.PATH);
       		break;
       	}
       	case 'C':{
       		game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getClosestPillOrPowerPill(game.getPacmanCurrentNodeIndex()),DM.PATH);
       		break;
       	}
        default:
            System.err.println("FENOTIPO INCORRECTO");
            break;
        }
        return myMove;
    }
	
	/*public MOVE getMove(Game game, long timeDue) 
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
	}*/
}