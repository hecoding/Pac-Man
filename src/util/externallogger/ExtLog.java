package util.externallogger;

public class ExtLog { 

	private double mutationProb	= -1;
	private double crossProb = -1;
	private int popSize = -1;
	private int numOfIter = -1;
	private int iterPerIndiv = -1;
	private String fitnessFunc = null;
	private double fitness = -1;
	private double averagePoints = -1;
	private String phenotype = null;
	private double execTime = -1;
	
	public ExtLog (double mP, double cP, int tP, int nI, int iPI, String fF, double f, double aP, String p, double eT) {
		this.mutationProb = mP;
		this.crossProb = cP;
		this.popSize = tP;
		this.numOfIter = nI;
		this.iterPerIndiv = iPI;
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
		return popSize;
	}

	public int getNumIteraciones() {
		return numOfIter;
	}

	public int getIteracionesPorIndividuo() {
		return iterPerIndiv;
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
