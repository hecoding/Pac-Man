package model.genProgAlgorithm.mutation;

public class MutationFactory {
	private static MutationFactory instance;
	private static String[] strategies = {"Smp. terminal", "Smp. functional", "Initialisation"};
	
	private MutationFactory() {}
	
	public static MutationFactory getInstance() {
		if (instance == null){
			instance = new MutationFactory();
		}
		return instance;
	}
	
	public MutationInterface create(String id) {
		if (id == "Smp. terminal")
			return new SimpleTerminalMutation();
		else if (id == "Smp. functional")
			return new SimpleFunctionalMutation();
		else if (id == "Initialisation")
			return new InitializationMutation();
		else
			throw new IllegalArgumentException("Unknown mutation method");
	}
	
	public static String[] selectionList() {
		return strategies;
	}
}
