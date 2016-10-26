package model.genProgAlgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import model.chromosome.AbstractChromosome;
import model.chromosome.comparator.AptitudeComparator;
import model.genProgAlgorithm.crossover.CrossoverInterface;
import model.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import model.genProgAlgorithm.initialization.InitializationInterface;
import model.genProgAlgorithm.mutation.MutationInterface;
import model.genProgAlgorithm.selection.SelectionInterface;
import model.observer.GeneticAlgorithmObserver;
import model.observer.Observable;
import util.RandGenerator;

public abstract class AbstractGeneticAlgorithm<T extends AbstractChromosome> implements Observable<GeneticAlgorithmObserver> {
	protected ArrayList<T> population;
	protected FitnessFunctionInterface fitnessFunc;
	protected int populationNum;
	protected int currentGeneration;
	protected int maxGenerationNum;
	protected int maxProgramHeight;
	protected double crossProb;
	protected double mutationProb;
	protected boolean useElitism;
	protected double elitePercentage;
	protected ArrayList<T> elite;
	protected ArrayList<Double> inspectedAptitude;
	protected static Random random;
	protected boolean contentBasedTermination;
	protected ArrayList<GeneticAlgorithmObserver> observers;
	
	protected T bestChromosome;
	protected double bestAptitude;
	protected double averageAptitude;
	protected double averageScore;
	protected static Comparator<AbstractChromosome> aptitudeComparator;
	protected ArrayList<Double> bestChromosomeList;
	protected ArrayList<Double> averageAptitudeList;
	protected ArrayList<Double> bestAptitudeList;
	
	protected InitializationInterface initializationStrategy;
	protected SelectionInterface selectionStrategy;
	protected CrossoverInterface crossoverStrategy;
	protected MutationInterface mutationStrategy;
	
	public AbstractGeneticAlgorithm() {
		this.observers = new ArrayList<GeneticAlgorithmObserver>();
	}
	
	public AbstractGeneticAlgorithm(FitnessFunctionInterface func, InitializationInterface initializationStrategy,
			SelectionInterface selectionStrategy, CrossoverInterface crossoverStrategy, MutationInterface mutationStrategy,
			int populationNum, boolean useElitism, double elitePercentage, int maxGenerationNum, int maxProgramHeight,
			double crossProb, double mutationProb) {
		this.fitnessFunc = func;
		this.initializationStrategy = initializationStrategy;
		this.selectionStrategy = selectionStrategy;
		this.crossoverStrategy = crossoverStrategy;
		this.mutationStrategy = mutationStrategy;
		this.populationNum = populationNum;
		this.useElitism = useElitism;
		this.elitePercentage = elitePercentage;
		this.population = null;
		this.elite = null;
		this.currentGeneration = 0;
		this.maxGenerationNum = maxGenerationNum;
		this.maxProgramHeight = maxProgramHeight;
		this.bestChromosome = null;
		this.bestAptitude = 0;
		this.averageAptitude = 0;
		this.averageScore = 0;
		this.crossProb = crossProb;
		this.mutationProb = mutationProb;
		this.bestChromosomeList = new ArrayList<Double>(this.maxGenerationNum);
		this.averageAptitudeList = new ArrayList<Double>(this.maxGenerationNum);
		this.bestAptitudeList = new ArrayList<Double>(this.maxGenerationNum);
		this.inspectedAptitude = new ArrayList<Double>(populationNum);
		this.contentBasedTermination = false;
		this.observers = new ArrayList<GeneticAlgorithmObserver>();
		
		random = RandGenerator.getInstance();
		if(aptitudeComparator == null)
			aptitudeComparator = new AptitudeComparator();
	}
	
	public void initialize() {
		this.population = this.initializationStrategy.initialize(this.populationNum, this.fitnessFunc, this.maxProgramHeight);
	}
	
	public void selection() {
		this.selectionStrategy.select(this.population);
	}
	public void reproduction() {
		this.crossoverStrategy.crossover(this.population, this.crossProb);
	}
	public void mutation() {
		this.mutationStrategy.mutate(this.population, this.mutationProb);
	}
	
	public void reset() {
		this.population = null;
		this.elite = null;
		this.currentGeneration = 0;
		this.bestChromosome = null;
		this.bestAptitude = 0;
		this.averageAptitude = 0;
		this.averageScore = 0;
		this.bestChromosomeList = new ArrayList<Double>(this.maxGenerationNum);
		this.averageAptitudeList = new ArrayList<Double>(this.maxGenerationNum);
		this.bestAptitudeList = new ArrayList<Double>(this.maxGenerationNum);
		this.inspectedAptitude = new ArrayList<Double>(populationNum);
	}

	/** If you have modified parameters between launches of the algorithm, you must call reset() method (the one with no parameters) */
	public void run() {
		this.notifyStartRun();
		
		this.initialize();
		if(this.fitnessFunc.isMinimization())
			this.aptitudeShifting();
		this.evaluatePopulation();
		
		while(!this.finished()) {
			if (this.useElitism)
				this.elite = this.selectElite();
			if(!this.contentBasedTermination || // increase generations as usual or as the condition below
				(this.currentGeneration < 2 ||
						this.averageAptitudeList.get(this.averageAptitudeList.size() - 1)
						> this.averageAptitudeList.get(this.averageAptitudeList.size() - 2)))
				this.increaseGeneration();
			this.selection();
			this.reproduction();
			this.mutation();
			if (this.useElitism)
				this.includeElite(this.elite);
			if(this.fitnessFunc.isMinimization())
				this.aptitudeShifting();
			this.evaluatePopulation();
			
			this.makeIncrement();
			this.gatherStatistics();
		}
		
		this.notifyEndRun();
	}

	protected void gatherStatistics() {
		this.bestChromosomeList.add(this.getBestChromosome().getAptitude());
		this.bestAptitudeList.add(this.getBestAptitude());
		this.averageAptitudeList.add(this.getAverageAptitude());
	}
	
	@SuppressWarnings("unchecked")
	public void evaluatePopulation() {
		double aggregateScore = 0;
		double bestAptitude = Double.NEGATIVE_INFINITY;
		if(this.fitnessFunc.isMinimization())
			bestAptitude = Double.POSITIVE_INFINITY;
		double aggregateAptitude = 0;
		double aggregateInspectedAptitude = 0;
		T currentBest = null;
		
		// compute best and aggregate aptitude
		int i = 0;
		for (T chromosome : this.population) {
			double chromAptitude = chromosome.getAptitude();
			aggregateAptitude += chromAptitude;
			if(this.fitnessFunc.isMinimization())
				aggregateInspectedAptitude += this.inspectedAptitude.get(i);
			
			if(this.fitnessFunc.isMinimization()) {
				if (chromAptitude < bestAptitude) {
					currentBest = chromosome;
					bestAptitude = chromAptitude;
				}
			}
			else {
				if (chromAptitude > bestAptitude) {
					currentBest = chromosome;
					bestAptitude = chromAptitude;
				}
			}
			i++;
		}
		
		// compute and set score of population individuals
		i = 0;
		for (T chromosome : this.population) {
			if(this.fitnessFunc.isMinimization())
				chromosome.setScore(this.inspectedAptitude.get(i) / aggregateInspectedAptitude);
			else
				chromosome.setScore(chromosome.getAptitude() / aggregateAptitude);
			chromosome.setAggregateScore(chromosome.getScore() + aggregateScore);
			aggregateScore += chromosome.getScore();
			i++;
		}
		
		// refresh best individual and aptitude statistics
		if(this.fitnessFunc.isMinimization()) {
			if (this.bestChromosome == null || bestAptitude < this.bestChromosome.getAptitude()) {
				this.bestChromosome = (T) currentBest.clone();
			}
		}
		else {			
			if (this.bestChromosome == null || bestAptitude > this.bestChromosome.getAptitude()) {
				this.bestChromosome = (T) currentBest.clone();
			}
		}
		
		this.bestAptitude = bestAptitude;
		this.averageAptitude = aggregateAptitude / this.population.size();
		this.averageScore = aggregateScore / this.population.size();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> selectElite() {
		int eliteNum = (int) Math.ceil(this.populationNum * this.elitePercentage);
		ArrayList<T> elite = new ArrayList<T>(eliteNum);
		
		this.population.sort(aptitudeComparator);
		if(this.fitnessFunc.isMinimization()) {
			for (int i = 0; i < eliteNum; i++) {
				elite.add((T) this.population.get(i).clone());
			}
		}
		else {			
			for (int i = this.populationNum - eliteNum; i < this.populationNum; i++) {
				elite.add((T) this.population.get(i).clone());
			}
		}
		
		return elite;
	}
	
	public void includeElite(ArrayList<T> elite) {
		// substitution of the worst adapted individuals
		this.population.sort(aptitudeComparator);
		
		if(this.fitnessFunc.isMinimization()) {
			int j = 0;
			for (int i = this.populationNum - elite.size(); i < this.populationNum; i++) {
				this.population.set(i, elite.get(j));
				j++;
			}
		}
		else {
			for (int i = 0; i < elite.size(); i++) {
				this.population.set(i, elite.get(i));
			}			
		}
	}
	
	/** Transform minimization problem into maximization */
	private void aptitudeShifting() {
		this.inspectedAptitude.clear();
		Double cmax = Double.NEGATIVE_INFINITY;
		
		for (T chrom : this.population) {
			if(chrom.getAptitude() > cmax)
				cmax = chrom.getAptitude(); // get worst aptitude
		}
		cmax = cmax * 1.05; // avoid aggregateAptitude = 0 when population converges
		
		for (T chrom : this.population) {
			this.inspectedAptitude.add(cmax - chrom.getAptitude());
		}
	}
	
	public void increaseGeneration() {
		this.currentGeneration++;
	}
	
	public boolean finished() {
		return this.currentGeneration == this.maxGenerationNum;
	}

	public FitnessFunctionInterface getFitnessFunction() {
		return this.fitnessFunc;
	}
	
	public void setFitnessFunction(FitnessFunctionInterface func) {
		this.fitnessFunc = func;
	}
	
	public InitializationInterface getInitializationStrategy() {
		return this.initializationStrategy;
	}
	
	public void setInitializationStrategy(InitializationInterface strategy) {
		this.initializationStrategy = strategy;
	}
	
	public SelectionInterface getSelectionStrategy() {
		return this.selectionStrategy;
	}
	
	public void setSelectionStrategy(SelectionInterface strategy) {
		this.selectionStrategy = strategy;
	}
	
	public CrossoverInterface getCrossoverStrategy() {
		return this.crossoverStrategy;
	}
	
	public void setCrossoverStrategy(CrossoverInterface strategy) {
		this.crossoverStrategy = strategy;
	}
	
	public MutationInterface getMutationStrategy() {
		return this.mutationStrategy;
	}
	
	public void setMutationStrategy(MutationInterface strategy) {
		this.mutationStrategy = strategy;
	}

	public int getPopulationNum() {
		return populationNum;
	}
	
	public void setPopulation(int population) {
		this.populationNum = population;
	}

	public int getMaxGenerationNum() {
		return maxGenerationNum;
	}
	
	public void setMaxGenerations(int maxGenerations) {
		this.maxGenerationNum = maxGenerations;
	}
	
	public int getProgramHeight() {
		return this.maxProgramHeight;
	}
	
	public void setProgramHeight(int height) {
		this.maxProgramHeight = height;
	}

	public double getCrossProb() {
		return crossProb;
	}
	
	public void setCrossProb(double prob) {
		this.crossProb = prob;
	}

	public double getMutationProb() {
		return mutationProb;
	}
	
	public void setMutationProb(double prob) {
		this.mutationProb = prob;
	}

	public boolean isUseElitism() {
		return useElitism;
	}
	
	public void useElitism(boolean elitism) {
		this.useElitism = elitism;
	}

	public double getElitePercentage() {
		return elitePercentage;
	}
	
	public void setElitePercentage(int percentage) {
		this.elitePercentage = (double) percentage / 100;
	}

	public boolean isContentBasedTermination() {
		return contentBasedTermination;
	}

	public void setContentBasedTermination(boolean contentBasedTermination) {
		this.contentBasedTermination = contentBasedTermination;
	}

	public T getBestChromosome() {
		return bestChromosome;
	}
	
	public double getBestAptitude() {
		return this.bestAptitude;
	}
	
	public double getAverageAptitude() {
		return this.averageAptitude;
	}
	
	public double getAverageScore() {
		return this.averageScore;
	}

	public ArrayList<Double> getBestChromosomeList() {
		return bestChromosomeList;
	}

	public ArrayList<Double> getAverageAptitudeList() {
		return averageAptitudeList;
	}

	public ArrayList<Double> getBestAptitudeList() {
		return bestAptitudeList;
	}
	
	public void addObserver(GeneticAlgorithmObserver o) {
		this.observers.add(o);
	}
	
	public void removeObserver(GeneticAlgorithmObserver o) {
		this.observers.remove(o);
	}
	
	public void makeIncrement() {
		for (GeneticAlgorithmObserver obs : this.observers) {
			obs.onIncrement((int) Math.floor( 100 * this.currentGeneration / this.maxGenerationNum ));
		}
	}
	
	public ArrayList<GeneticAlgorithmObserver> getObservers() {
		return this.observers;
	}
	
	public void setObservers(ArrayList<GeneticAlgorithmObserver> observers) {
		this.observers = observers;
	}
	
	protected void notifyStartRun() {
		for (GeneticAlgorithmObserver obs : this.observers) {
			obs.onStartRun();
		}
	}
	
	protected void notifyEndRun() {
		for (GeneticAlgorithmObserver obs : this.observers) {
			obs.onEndRun();
		}
	}
}
