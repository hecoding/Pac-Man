package model.genProgAlgorithm.selection;

public class SelectionFactory {
	private static SelectionFactory instance;
	private static String[] strategies = {"Roulette", "Tournament", "Truncation", "Linear Rank"};
	private double parameter = 2;
	
	private SelectionFactory() {}
 	
	public static SelectionFactory getInstance() {
		if (instance == null){
			instance = new SelectionFactory();
		}
		return instance;
	}
	
	public SelectionInterface create(String id) {
		if (id == "Roulette")
			return new RouletteSelection();
		else if (id == "Tournament")
			return new TournamentSelection((int) parameter);
		else if (id == "Truncation")
			return new TruncationSelection();
		else if (id == "Linear Rank")
			return new LinearRankSelection();
		else
			throw new IllegalArgumentException("Unknown selection method");
	}
	
	public double getParameter() {
		return this.parameter;
	}
	
	public void setParameter(double param) {
		this.parameter = param;
	}
	
	public static String[] selectionList() {
		return strategies;
	}
	
}
