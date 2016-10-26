package model.genProgAlgorithm.fitnessFunction;

public class FunctionFactory {
 	private static FunctionFactory instance;
 	
 	private FunctionFactory() {}
 	
	public static FunctionFactory getInstance() {
		if (instance == null){
			instance = new FunctionFactory();
		}
		return instance;
	}
	
	public FitnessFunctionInterface create(String id) {
		if (id == "función de aptitud")
			return null;
		else
			throw new IllegalArgumentException("Unknown function");
	}
}
