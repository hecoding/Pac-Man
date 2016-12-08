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
	public double runExecution(String phenotype) {
		MutableDouble fitness = new MutableDouble(100000); //Nunca debe ser < 0 por muchas galletas o fantasmas comidos o duracion aguantada
		//fitness.setValue(100000);
		
		
		CustomExecutor slave = new CustomExecutor();
		
		Controller<MOVE> pacman = new GrammaticalAdapterController(phenotype);
		Controller<EnumMap<GHOST,MOVE>> ghosts = new AggressiveGhosts();

		slave.runGame(pacman, ghosts, fitness);
		
		if(fitness.doubleValue() < 0)
			return 0;
		else
			return fitness.doubleValue();
	}
	
	
	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
	 * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController The Ghosts controller
	 * @param visual Indicates whether or not to use visuals
	 * @param delay The delay between time-steps
	 */
	public void runGame(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController, MutableDouble fitness)
	{
		Double maxscore = (double) 0;
		
		
		
		Game game=new Game(0);
		
		while(!game.gameOver()){
			fitness.decrement(); //Version muy basica, el fitness solo disminuye al aguantar vivo el pacman
			game.advanceGame(pacManController.getMove(game.copy(),-1),ghostController.getMove(game.copy(),-1));
			if(maxscore < game.getScore()){
				maxscore = (double) game.getScore();
			}
		}
		
		fitness = new MutableDouble(maxscore);
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