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
	private static final int HUNGER_DISTANCE = 30;
	
	public GrammaticalAdapterController(String fenotipo) {
		this.fenotipo = fenotipo;
		poslectura = 0;
	}

	public MOVE getMove(Game game, long timeDue) {
		int currentPos = game.getPacmanCurrentNodeIndex();
		
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
    	   			Ghost closestNonEdibleGhost = game.getClosestNonEdibleGhost(currentPos);
	    	   		if(closestNonEdibleGhost != null && !game.closerThan(currentPos, closestNonEdibleGhost.currentNodeIndex, PANIC_DISTANCE))
	    	   			myMove = getMove(game, timeDue);
    	   		}
    	   		else if (mov == 'B') {
    	   			Ghost closestEdibleGhost = game.getClosestEdibleGhost(currentPos);
    	   			//Ghost closestEdibleGhost = game.getClosestReachableEdibleGhost(currentPos);
	    	   		if(closestEdibleGhost != null && !game.closerThan(currentPos, closestEdibleGhost.currentNodeIndex, HUNGER_DISTANCE))
	    	   			myMove = getMove(game, timeDue);
    	   		}
    	   		else
    	   			System.out.println("ERROR EN FORMATO DE FENOTIPO: ?");
    	   	break;
       	}
       	case 'H':{ // go away
    		Ghost closestNonEdibleGhost = game.getClosestNonEdibleGhost(currentPos);
       		if(closestNonEdibleGhost != null)
       			myMove = game.getNextMoveAwayFromTarget(currentPos, closestNonEdibleGhost.currentNodeIndex, game.getPacmanLastMoveMade(), DM.PATH);
       		break;
       	}
       	case 'C':{ // seek food
    		int closestPillOrPowerPill = game.getClosestPillOrPowerPill(currentPos);
       		myMove = game.getNextMoveTowardsTarget(currentPos, closestPillOrPowerPill, DM.PATH);
       		break;
       	}
       	case 'F':{ // seek ghost
    		Ghost closestEdibleGhost = game.getClosestEdibleGhost(currentPos);
    		//Ghost closestEdibleGhost = game.getClosestReachableEdibleGhost(currentPos);
	   		if(closestEdibleGhost != null)
       			myMove = game.getNextMoveTowardsTarget(currentPos, closestEdibleGhost.currentNodeIndex, game.getPacmanLastMoveMade(), DM.PATH);
       		break;
       	}
        default:
            System.err.println("FENOTIPO INCORRECTO");
            break;
        }
        return myMove;
    }
	
}