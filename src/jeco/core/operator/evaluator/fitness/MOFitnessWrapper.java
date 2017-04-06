package jeco.core.operator.evaluator.fitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import pacman.game.util.GameInfo;

public class MOFitnessWrapper {
	public ArrayList<FitnessEvaluatorInterface> funcs;
	
	public MOFitnessWrapper(FitnessEvaluatorInterface... funcs) {
		this.funcs = new ArrayList<>(funcs.length);

		Collections.addAll(this.funcs, funcs);
	}

	public void addObjectiveFunction(FitnessEvaluatorInterface f) {
		if (f != null)
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
	
	public String getFuncName(int i) {
		return this.funcs.get(i).getName();
	}
	
	public String[] getFuncNames() {
		String[] ret = new String[this.getNumberOfObjs()];
		
		for (FitnessEvaluatorInterface func : this.funcs) {
			Arrays.fill(ret, func.getName());
		}
		
		return ret;
	}
	
	public void clear() {
		this.funcs.clear();
	}

}
