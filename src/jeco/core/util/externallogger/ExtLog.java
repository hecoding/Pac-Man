package jeco.core.util.externallogger;

public class ExtLog { 

	private double mutationProb	= -1;
	private double crossProb = -1;
	private int tamPob = -1;
	private int numIteraciones = -1;
	private int iteracionesPorIndividuo = -1;
	private String fitnessFunc = null;
	private double fitness = -1;
	private double averagePoints = -1;
	private String phenotype = null;
	private double execTime = -1;
	
	public ExtLog (double mP, double cP, int tP, int nI, int iPI, String fF, double f, double aP, String p, double eT){
		this.mutationProb = mP;
		this.crossProb = cP;
		this.tamPob = tP;
		this.numIteraciones = nI;
		this.iteracionesPorIndividuo = iPI;
		this.fitnessFunc = fF;
		this.fitness = f;
		this.averagePoints = aP;
		this.phenotype = p;
		this.execTime = eT;
	}	
	
	public double getMutationProb() {
		return mutationProb;
	}

	public double getCrossProb() {
		return crossProb;
	}

	public int getTamPob() {
		return tamPob;
	}

	public int getNumIteraciones() {
		return numIteraciones;
	}

	public int getIteracionesPorIndividuo() {
		return iteracionesPorIndividuo;
	}

	public String getFitnessFunc() {
		return fitnessFunc;
	}

	public double getFitness() {
		return fitness;
	}

	public double getAveragePoints() {
		return averagePoints;
	}

	public String getPhenotype() {
		return phenotype;
	}

	public double getExecTime() {
		return execTime;
	}
	
}
