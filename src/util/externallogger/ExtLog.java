package util.externallogger;

import java.util.ArrayList;

public class ExtLog { 

	private double mutationProb	= -1;
	private double crossProb = -1;
	private int popSize = -1;
	private int numOfIter = -1;
	private int iterPerIndiv = -1;
	private String fitnessFuncs = null;
	private ArrayList<Double> fitness = new ArrayList<Double>();
	private String phenotype = null;
	private double execTime = -1;
	
	public ExtLog (double mP, double cP, int tP, int nI, int iPI, String fF, ArrayList<Double> f, String p, double eT) {
		this.mutationProb = mP;
		this.crossProb = cP;
		this.popSize = tP;
		this.numOfIter = nI;
		this.iterPerIndiv = iPI;
		this.fitnessFuncs = fF;
		this.fitness = f;
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

	public String getFitnessFuncs() {
		return fitnessFuncs;
	}

	public ArrayList<Double> getFitness() {
		return fitness;
	}

	public String getPhenotype() {
		return phenotype;
	}

	public double getExecTime() {
		return execTime;
	}
	
}
