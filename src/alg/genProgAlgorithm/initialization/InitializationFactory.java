package model.genProgAlgorithm.initialization;

public class InitializationFactory {
	private static InitializationFactory instance;
	private static String[] strategies = {"Full", "Grow", "Ra. & Half"};
	
	private InitializationFactory() {}
	
	public static InitializationFactory getInstance() {
		if (instance == null){
			instance = new InitializationFactory();
		}
		return instance;
	}
	
	public InitializationInterface create(String id) {
		if (id == "Full")
			return new FullInitialization();
		else if (id == "Grow")
			return new GrowInitialization();
		else if (id == "Ra. & Half")
			return new RampedAndHalfInitialization();
		else
			throw new IllegalArgumentException("Unknown initialization method");
	}
	
	public static String[] selectionList() {
		return strategies;
	}
}
