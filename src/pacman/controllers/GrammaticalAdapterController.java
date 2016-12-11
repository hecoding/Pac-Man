package pacman.controllers;

import pacman.game.Game;
import pacman.game.internal.Ghost;
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
	private static final int PANIC_DISTANCE = 10;
	
	public GrammaticalAdapterController(String fenotipo) {
		this.fenotipo = fenotipo;
		poslectura = 0;
	}

	public MOVE getMove(Game game, long timeDue) {
		int currentPos = game.getPacmanCurrentNodeIndex();
		Ghost ghost = game.getClosestNonEdibleGhost(currentPos);
		int closestPillOrPowerPill = game.getClosestPillOrPowerPill(currentPos);
		
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
       case '?': { // conditional
    	   		mov = fenotipo.charAt(poslectura);
    	   		poslectura++;
    	   		
    	   		if (mov == 'P') {
	    	   		if(ghost != null && !game.closerThan(currentPos, ghost.currentNodeIndex, PANIC_DISTANCE))
	    	   			myMove = getMove(game, timeDue);
    	   		}
    	   		else
    	   			System.out.println("ERROR EN FORMATO DE FENOTIPO: ?");
    	   	break;
       	}
       	case 'H':{ // go away
       		if(ghost != null)
       			myMove = game.getNextMoveAwayFromTarget(currentPos, ghost.currentNodeIndex, game.getPacmanLastMoveMade(), DM.PATH);;
       		break;
       	}
       	case 'C':{ // seek food
       		myMove = game.getNextMoveTowardsTarget(currentPos, closestPillOrPowerPill, DM.PATH);
       		break;
       	}
        default:
            System.err.println("FENOTIPO INCORRECTO");
            break;
        }
        return myMove;
    }
	
}