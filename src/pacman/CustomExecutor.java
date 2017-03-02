package pacman;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.mutable.MutableDouble;

import pacman.controllers.Controller;
import pacman.controllers.GrammaticalAdapterController;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.NearestPillPacMan;
import pacman.controllers.examples.NearestPillPacManVS;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.RandomNonRevPacMan;
import pacman.controllers.examples.RandomPacMan;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.util.GameInfo;

import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and 
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class CustomExecutor {	
	/**
	 * The main method. Several options are listed - simply remove comments to use the option you want.
	 *
	 * @param args the command line arguments
	 */
	public GameInfo runExecution(GrammaticalAdapterController pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController, int trials) {
		ArrayList<GameInfo> gsi = new ArrayList<GameInfo>();
		
		for( int i = 0 ; i < trials; ++i){
			pacManController.reset();
			gsi.add(this.runGame(pacManController, ghostController));
		}
		
		return GameInfo.averageGamesInfo(gsi);
	}
	
	
	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
	 * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController The Ghosts controller
	 * @return 
	 */
	public GameInfo runGame(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController)
	{
		GameInfo gi = new GameInfo();
		int lastLevel = 0;
		Game game=new Game(0);
		game.setGi(gi);
		
		while(!game.gameOver()){
			
			if (game.wasPacManEaten() || game.getCurrentLevel() != lastLevel)
				((GrammaticalAdapterController) pacManController).reset();
			
			game.advanceGame(pacManController.getMove(game.copy(),-1),ghostController.getMove(game.copy(),-1));
			lastLevel = game.getCurrentLevel();
		}
		
		gi.setScore(game.getScore());
		gi.setLastLevelReached(game.getLevelCount());
		gi.setLastLevelReached(lastLevel);
		gi.setTimeLasted(game.getTotalTime());
		return gi;
	}
	
	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
	 * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController The Ghosts controller
	 * @param game The game to run
	 * @param gv The GameView to paint on
	 * @param visual Indicates whether or not to use visuals
	 * @param delay The delay between time-steps
	 */
	public static void runGameView(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,Game game,GameView gv,int delay,AtomicBoolean stop)
	{
		int lastLevel = 0;
		
		while(!stop.get() && !game.gameOver())
		{
			if (game.wasPacManEaten() || game.getCurrentLevel() != lastLevel)
				((GrammaticalAdapterController) pacManController).reset();
				
	        game.advanceGame(pacManController.getMove(game.copy(),-1),ghostController.getMove(game.copy(),-1));
	        lastLevel = game.getCurrentLevel();
	        
	        try{Thread.sleep(delay);}catch(Exception e){}
	        
	        gv.repaint();
		}
	}
	
	public static Game getNewGame() {
		GameInfo gi = new GameInfo();
		return new Game(0);
	}
	
	public static GameView getGameViewFromGame(Game game) {
		
		return new GameView(game);
	}
	
	/**
	 * Replay a previously saved game.
	 *
	 * @param fileName The file name of the game to be played
	 * @param visual Indicates whether or not to use visuals
	 */
	public void replayGame(String fileName,boolean visual)
	{
		ArrayList<String> timeSteps=loadReplay(fileName);
		
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		for(int j=0;j<timeSteps.size();j++)
		{			
			game.setGameState(timeSteps.get(j));

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
	        if(visual)
	        	gv.repaint();
		}
	}
	
	//save file for replays
    public static void saveToFile(String data,String name,boolean append)
    {
        try 
        {
            FileOutputStream outS=new FileOutputStream(name,append);
            PrintWriter pw=new PrintWriter(outS);

            pw.println(data);
            pw.flush();
            outS.close();

        } 
        catch (IOException e)
        {
            System.out.println("Could not save data!");	
        }
    }  

    //load a replay
    private static ArrayList<String> loadReplay(String fileName)
	{
    	ArrayList<String> replay=new ArrayList<String>();
		
        try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName))))
        {
            String input=br.readLine();		
            
            while(input!=null)
            {
            	if(!input.equals(""))
            		replay.add(input);

            	input=br.readLine();	
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        
        return replay;
	}
}