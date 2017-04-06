package view.gui.swing.factory;

import java.util.HashMap;

import jeco.core.operator.evaluator.fitness.FitnessEvaluatorInterface;

public class ObjectiveFactory {
	private static ObjectiveFactory instance;
	private HashMap<String, Class<? extends FitnessEvaluatorInterface>> registered = new HashMap<>();
	
	private ObjectiveFactory() {}
 	
	public static ObjectiveFactory getInstance() {
		if (instance == null){
			instance = new ObjectiveFactory();
		}
		return instance;
	}
	
	public void register(FitnessEvaluatorInterface obj) {
		this.registered.put(obj.getName(), obj.getClass());
	}
	
	public FitnessEvaluatorInterface create(String id) {
		Class<? extends FitnessEvaluatorInterface> prod = this.registered.get(id);
		
		if (prod == null)
			return null;
		
		try {
			return prod.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	public String[] getRegisteredKeys() {
		return this.registered.keySet().toArray(new String[this.registered.size()]).clone();
	}
}
