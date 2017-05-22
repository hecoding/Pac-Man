package util;

import pacman.CustomExecutor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.NearestPillPacMan;
import pacman.controllers.examples.NearestPillPacManVS;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.RandomNonRevPacMan;
import pacman.controllers.examples.RandomPacMan;
import pacman.game.Constants;
import pacman.game.util.GameInfo;

import java.util.EnumMap;

public class BatchExecutor {

    public static void main(String[] args) {
        // custom params
        int numExecutions = 1000;
        
    		//H vs Legacy
        String stringPhenotype = "if(_getDistanceToClosestNonEdibleGhost_LE_5_){_huir_}_else{_farmear_}";
        	//M vs Random
        //String stringPhenotype = "if(_getDistanceToClosestNonEdibleGhost_GT_10_){_if(_getDistanceToClosestNonEdibleGhost_LT_20_){_getDirectionTowardsClosestPowerPill_}_else{_getDirectionTowardsClosestPill_}_}_else{_getDirectionAwayFromClosestNonEdibleGhost_}";
        	//M vs Legacy
        //String stringPhenotype = "if(_getDistanceToClosestNonEdibleGhost_GE_20_){_getDirectionTowardsClosestPill_}_else{_if(_getDistanceToClosestNonEdibleGhostUp_NE_15_){_getDirectionTowardsClosestPowerPill_}_else{_getDirectionTowardsClosestPill_}_}";

        // default instantiations
        CustomExecutor executor = new CustomExecutor();
        GameInfo info;
        
        GrammaticalAdapterController pacmanController = new GrammaticalAdapterController(stringPhenotype);
        
        //RandomPacMan pacmanController = new RandomPacMan();
        //RandomNonRevPacMan pacmanController = new RandomNonRevPacMan();
        //NearestPillPacMan pacmanController = new NearestPillPacMan();
        //NearestPillPacManVS pacmanController = new NearestPillPacManVS();

        Controller<EnumMap<Constants.GHOST,Constants.MOVE>> ghostController = new RandomGhosts();
        //Controller<EnumMap<Constants.GHOST,Constants.MOVE>> ghostController = new Legacy();

        // execution
        info = executor.runExecution(pacmanController, ghostController, numExecutions);

        /*System.out.println(info.getAvgScore());
        System.out.println(info.getAvgLastLevelReached());
        System.out.println(info.getAvgTimeLasted());*/
    }
}
