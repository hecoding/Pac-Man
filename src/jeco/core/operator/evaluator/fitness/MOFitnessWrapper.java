package jeco.core.operator.evaluator.fitness;

import java.util.ArrayList;
import pacman.game.util.GameInfo;

public class MOFitnessWrapper {
	public ArrayList<FitnessEvaluatorInterface> funcs;
	
	public MOFitnessWrapper() {
		this.funcs = new ArrayList<>();
	}
	
	public MOFitnessWrapper(FitnessEvaluatorInterface... funcs) {
		this.funcs = new ArrayList<>(funcs.length);
		
		for (int i = 0; i < funcs.length; i++) {
			this.funcs.add(funcs[i]);
		}
	}
	
	public MOFitnessWrapper(ArrayList<FitnessEvaluatorInterface> funcs) {
		this((FitnessEvaluatorInterface[]) funcs.toArray());
	}

	public void addObjectiveFunction(FitnessEvaluatorInterface f) {
		this.funcs.add(f);
	}
	
	public ArrayList<Double> evaluate(GameInfo gi) {
		ArrayList<Double> fit = new ArrayList<>(this.funcs.size());
		
		for(FitnessEvaluatorInterface f : this.funcs) {
			fit.add(f.evaluate(gi));
		}
		
		return fit;
	}
	
	public Double getWorstFitness(int i) {
		return this.funcs.get(i).worstFitness();
	}
	
	public int getNumberOfObjs() {
		return this.funcs.size();
	}

}
