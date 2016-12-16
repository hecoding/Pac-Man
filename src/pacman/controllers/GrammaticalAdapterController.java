package pacman.controllers;

import pacman.game.Game;
import pacman.game.internal.Ghost;

import org.apache.commons.lang.StringUtils;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class GrammaticalAdapterController extends Controller<MOVE>
{
	private String fenotipo;
	private int poslectura;
	private static final int PANIC_DISTANCE = 2;
	private static final int HUNGER_DISTANCE = 30;
	
	public GrammaticalAdapterController(String fenotipo) {
		this.fenotipo = fenotipo;
		poslectura = 0;
	}
	
	public void reset(){
		poslectura = 0;
	}

	public MOVE getMove(Game game, long timeDue) {
		MOVE myMove=MOVE.NEUTRAL;
		
		if ((double)fenotipo.length()/3 <= StringUtils.countMatches(fenotipo, "?")) {
	        return myMove;
	    }
		
		int currentPos = game.getPacmanCurrentNodeIndex();
		
        char mov = fenotipo.charAt(poslectura);
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
	    	   		else{ 
	    	   			skipifs();
	    	   			myMove = getMove(game, timeDue);
	    	   		}
    	   		}
    	   		else if (mov == 'B') {
    	   			Ghost closestEdibleGhost = game.getClosestEdibleGhost(currentPos); 
    	   			//Ghost closestEdibleGhost = game.getClosestReachableEdibleGhost(currentPos);
	    	   		if(closestEdibleGhost != null && !game.closerThan(currentPos, closestEdibleGhost.currentNodeIndex, HUNGER_DISTANCE))
	    	   			myMove = getMove(game, timeDue);
	    	   		else{
	    	   			skipifs();
	    	   			myMove = getMove(game, timeDue); 
	    	   		}
    	   		}
    	   		else
    	   			System.err.println("ERROR EN FORMATO DE FENOTIPO: ?");
    	   	break;
       	}
       	case 'H':{ // go away
    		Ghost closestNonEdibleGhost = game.getClosestNonEdibleGhost(currentPos);
       		if(closestNonEdibleGhost != null && game.closerThan(currentPos, closestNonEdibleGhost.currentNodeIndex, HUNGER_DISTANCE))
       			myMove = game.getNextMoveAwayFromTarget(currentPos, closestNonEdibleGhost.currentNodeIndex, DM.PATH);
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
	
	private void skipifs(){//skipea ifs+condiciones y en ultima instancia la accion, dejando el puntero listo para leer el siguiente MOV correcto
		boolean skip = true;
		while (skip){
			if(fenotipo.charAt(poslectura) == '?'){//if
				 poslectura++;
				 char cond = fenotipo.charAt(poslectura);
				 if(cond == 'P' || cond == 'B'){//cond
					 poslectura++;
				 }
				 else
					 System.err.println("FENOTIPO INCORRECTO(SKIPING IFS)");
			}
			else
				skip = false;
		}
		poslectura++;//Accion a skipear tras la cadena de if (o tras el if inicial que era false de no haber habido cadena)
		if(poslectura >= fenotipo.length()) //Puede ser el final del fenotipo
            poslectura = 0;
	}
	
	
}