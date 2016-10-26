package model.genProgAlgorithm.crossover;

public class CrossoverFactory {
	private static CrossoverFactory instance;
	private static String[] strategies = {"One-point", "Two-point"};
	
	private CrossoverFactory() {}
 	
	public static CrossoverFactory getInstance() {
		if (instance == null){
			instance = new CrossoverFactory();
		}
		return instance;
	}
	
	public CrossoverInterface create(String id) {
		if (id == "One-point")
			return new OnePointCrossover();
		else if(id == "Two-point")
			return new TwoPointCrossover();
		else
			throw new IllegalArgumentException("Unknown crossover method");
	}
	
	public static String[] selectionList() {
		return strategies;
	}
}
