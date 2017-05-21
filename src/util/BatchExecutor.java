package util;

import pacman.CustomExecutor;
import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.examples.Legacy;
import pacman.game.Constants;
import pacman.game.util.GameInfo;

import java.util.EnumMap;

public class BatchExecutor {

    public static void main(String[] args) {
        // custom params
        int numExecutions = 100;
        String stringPhenotype = null;

        // default instantiations
        CustomExecutor executor = new CustomExecutor();
        GameInfo info;
        GrammaticalAdapterController pacmanController = new GrammaticalAdapterController(stringPhenotype);
        Controller<EnumMap<Constants.GHOST,Constants.MOVE>> ghostController = new Legacy();

        // execution
        info = executor.runExecution(pacmanController, ghostController, numExecutions);

        System.out.println(info.getAvgScore());
    }
}
