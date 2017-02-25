package pacman.game.util;

import java.util.ArrayList;

public class GameInfo {
	
	private int pillsEaten;
	private int powerPillsEaten;
	private int ghostsEaten;
	private int timeLasted;
	private int lastLevelReached;
	private int score;

	private double avgPillsEaten;
	private double avgPowerPillsEaten;
	private double avgGhostsEaten;
	private double avgTimeLasted;
	private double avgLastLevelReached;
	private double avgScore;

	public GameInfo(){
		pillsEaten = 0;
		powerPillsEaten = 0;
		ghostsEaten = 0;
		timeLasted = 0;
		lastLevelReached = 0;
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void increasePillsEaten(){
		this.pillsEaten++;
	}
	
	public void increasePowerPillsEaten(){
		this.powerPillsEaten++;
	}
	
	public void increaseGhostsEaten(){
		this.ghostsEaten++;
	}
	
	public int getLastLevelReached() {
		return lastLevelReached;
	}
	public void setLastLevelReached(int lastLevelReached) {
		this.lastLevelReached = lastLevelReached;
	}
	public int getPillsEaten() {
		return pillsEaten;
	}
	public void setPillsEaten(int pillsEaten) {
		this.pillsEaten = pillsEaten;
	}
	public int getPowerPillsEaten() {
		return powerPillsEaten;
	}
	public void setPowerPillsEaten(int powerPillsEaten) {
		this.powerPillsEaten = powerPillsEaten;
	}
	public int getGhostsEaten() {
		return ghostsEaten;
	}
	public void setGhostsEaten(int ghostsEaten) {
		this.ghostsEaten = ghostsEaten;
	}
	public int getTimeLasted() {
		return timeLasted;
	}
	public void setTimeLasted(int timeLasted) {
		this.timeLasted = timeLasted;
	}
	public double getAvgPillsEaten() {
		return avgPillsEaten;
	}
	public double getAvgPowerPillsEaten() {
		return avgPowerPillsEaten;
	}
	public double getAvgGhostsEaten() {
		return avgGhostsEaten;
	}
	public double getAvgTimeLasted() {
		return avgTimeLasted;
	}
	public double getAvgLastLevelReached() {
		return avgLastLevelReached;
	}
	public double getAvgScore() {
		return avgScore;
	}
	public void setAvgPillsEaten(double avgPillsEaten) {
		this.avgPillsEaten = avgPillsEaten;
	}

	public void setAvgPowerPillsEaten(double avgPowerPillsEaten) {
		this.avgPowerPillsEaten = avgPowerPillsEaten;
	}

	public void setAvgGhostsEaten(double avgGhostsEaten) {
		this.avgGhostsEaten = avgGhostsEaten;
	}

	public void setAvgTimeLasted(double avgTimeLasted) {
		this.avgTimeLasted = avgTimeLasted;
	}

	public void setAvgLastLevelReached(double avgLastLevelReached) {
		this.avgLastLevelReached = avgLastLevelReached;
	}

	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
	}
	
	public static GameInfo averageGamesInfo(ArrayList<GameInfo> gamesInfo){
		GameInfo giFinal = new GameInfo();
		
		double pillsTotales = 0;
		double powerPillsTotales = 0;
		double ghostsTotales = 0;
		double timeTotal = 0;
		double levelsTotal = 0;
		double scoreTotal = 0;
		
		int numGames = gamesInfo.size();
		
		for(int i = 0; i < numGames; i++){
			pillsTotales += gamesInfo.get(i).getPillsEaten();
			powerPillsTotales += gamesInfo.get(i).getPowerPillsEaten();
			ghostsTotales += gamesInfo.get(i).getGhostsEaten();
			timeTotal += gamesInfo.get(i).getTimeLasted();
			levelsTotal += gamesInfo.get(i).getLastLevelReached();
			scoreTotal += gamesInfo.get(i).getScore();
		}

		pillsTotales /= numGames;
		powerPillsTotales /= numGames;
		ghostsTotales /= numGames;
		timeTotal /= numGames;
		levelsTotal /= numGames;
		scoreTotal /= numGames;
		
		giFinal.setAvgPillsEaten(pillsTotales);
		giFinal.setAvgPowerPillsEaten(powerPillsTotales);
		giFinal.setAvgGhostsEaten(ghostsTotales);
		giFinal.setAvgTimeLasted(timeTotal);
		giFinal.setAvgLastLevelReached(levelsTotal);
		giFinal.setAvgScore(scoreTotal);
		
		return giFinal;
	}
	
}
