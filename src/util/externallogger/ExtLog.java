package util.externallogger;

import java.util.ArrayList;

public class ExtLog { 

	private String selecFunc = null;
	private String mutaFunc = null;
	private double mutationProb	= -1;
	private String crossFunc = null;
	private double crossProb = -1;
	private double elitePercent = -1;
	private int popSize = -1;
	private int numOfIter = -1;
	private int iterPerIndiv = -1;
	private String fitnessFuncs = null;
	private ArrayList<Double> fitness = new ArrayList<Double>();
	private String phenotype = null;
	private double execTime = -1;
	private int aborted = -1;
	
	public ExtLog (String sF, String mF, double mP, String cF, double cP, double ePc, int tP, int nI, int iPI, String fF, ArrayList<Double> f, String p, double eT, int aborted) {
		this.selecFunc = sF;
		this.mutaFunc = mF;
		this.mutationProb = mP;
		this.crossFunc = cF;
		this.crossProb = cP;
		this.elitePercent = ePc;
		this.popSize = tP;
		this.numOfIter = nI;
		this.iterPerIndiv = iPI;
		this.fitnessFuncs = fF;
		this.fitness = f;
		this.phenotype = p;
		this.execTime = eT;
		this.aborted = aborted;
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

	public String getSelecFunc() {
		return selecFunc;
	}

	public String getMutaFunc() {
		return mutaFunc;
	}

	public String getCrossFunc() {
		return crossFunc;
	}

	public double getElitePercent() {
		return elitePercent;
	}

	public int getPopSize() {
		return popSize;
	}

	public int getNumOfIter() {
		return numOfIter;
	}

	public int getIterPerIndiv() {
		return iterPerIndiv;
	}

	public int getAborted() {
		return aborted;
	}
	
}
