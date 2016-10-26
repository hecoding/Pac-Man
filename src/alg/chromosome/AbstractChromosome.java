package model.chromosome;

import model.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;

public abstract class AbstractChromosome implements Cloneable {
	protected static FitnessFunctionInterface fitnessFunc;
	protected double aptitude;
	protected double score;
	protected double aggregateScore;
	
	public AbstractChromosome() {
		this.aptitude = 0;
		this.score = 0;
		this.aggregateScore = 0;
	}
	
	public abstract double evaluate();
	
	public FitnessFunctionInterface getFunction() {
		return fitnessFunc;
	}

	public void setFunction(FitnessFunctionInterface func) {
		fitnessFunc = func;
	}

	public abstract String getPhenotype();

	public double getAptitude() {
		return aptitude;
	}

	public void setAptitude(double aptitude) {
		this.aptitude = aptitude;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getAggregateScore() {
		return aggregateScore;
	}

	public void setAggregateScore(double aggregateScore) {
		this.aggregateScore = aggregateScore;
	}
	
	public abstract AbstractChromosome clone();
	
	public abstract String toString();
}
