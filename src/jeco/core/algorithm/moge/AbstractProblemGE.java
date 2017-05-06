package jeco.core.algorithm.moge;

import java.util.ArrayList;
import java.util.LinkedList;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problem.solution.NeutralMutationSolution;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public abstract class AbstractProblemGE extends Problem<Variable<Integer>> {

	public static final int CHROMOSOME_LENGTH_DEFAULT = 100;	
	public static final int CODON_UPPER_BOUND_DEFAULT = 256;
	public static final int MAX_CNT_WRAPPINGS_DEFAULT = 3;
	public static final int NUM_OF_OBJECTIVES_DEFAULT = 2;

	protected String pathToBnf;
	protected BnfReader reader;
	protected int maxCntWrappings = MAX_CNT_WRAPPINGS_DEFAULT;
	protected int currentIdx;
	protected int currentWrp;
	protected boolean correctSol;

	public AbstractProblemGE(String pathToBnf, int numberOfObjectives, int chromosomeLength, int maxCntWrappings, int codonUpperBound) {
		super(chromosomeLength, numberOfObjectives);
		this.pathToBnf = pathToBnf;
		reader = new BnfReader();
		reader.load(pathToBnf);
		this.maxCntWrappings = maxCntWrappings;
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = codonUpperBound;
		}
	}

	public AbstractProblemGE(String pathToBnf, int numberOfObjectives) {
		this(pathToBnf, numberOfObjectives, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}

	public AbstractProblemGE(String pathToBnf) {
		this(pathToBnf, NUM_OF_OBJECTIVES_DEFAULT, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}

	abstract public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype);
	
	public void evaluate(Solutions<Variable<Integer>> solutions) {
		for(Solution<Variable<Integer>> solution : solutions)
			evaluate(solution);
	}

	public void evaluate(Solution<Variable<Integer>> solution) {
		Phenotype phenotype = generatePhenotype(solution);
		if(correctSol)
			evaluate(solution, phenotype);
		else {
			for(int i=0; i<super.numberOfObjectives; ++i) {
				solution.getObjectives().set(i, Double.POSITIVE_INFINITY);
			}
		}
	}

	public Phenotype generatePhenotype(Solution<Variable<Integer>> solution) {
		NeutralMutationSolution nmSolution = (NeutralMutationSolution) solution;
		currentIdx = 0;
		currentWrp = 0;
		correctSol = true;
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		nmSolution.getNoOptionsPhenotype().add(firstRule.size());
		Production firstProduction = firstRule.get(nmSolution.getVariables().get(currentIdx++).getValue() % firstRule.size());
		processProduction(firstProduction, nmSolution, phenotype);
		return phenotype;
	}

	public void processProduction(Production currentProduction, Solution<Variable<Integer>> solution, LinkedList<String> phenotype) {
		NeutralMutationSolution nmSolution = (NeutralMutationSolution) solution;

		if(!correctSol)
			return;
		for (Symbol symbol : currentProduction) {
			if (symbol.isTerminal()) {
				phenotype.add(symbol.toString());
			} else {
				if(currentIdx >= nmSolution.getVariables().size() && currentWrp<maxCntWrappings) {
					currentIdx = 0;
					currentWrp++;
				}
				if (currentIdx < nmSolution.getVariables().size()) {
					Rule rule = reader.findRule(symbol);
					nmSolution.getNoOptionsPhenotype().add(rule.size());
					Production production = rule.get(nmSolution.getVariables().get(currentIdx++).getValue() % rule.size());
					processProduction(production, nmSolution, phenotype);
				}
				else {
					correctSol = false;
					return;
				}
			}
		}
	}
	
	//Returns an array of integers representing the index of the rule that each codon expands
	public ArrayList<Integer> generateRuleExpansionArray(Solution<Variable<Integer>> solution)
	{
		ArrayList<Integer> expansionArray = new ArrayList<Integer>();
		
		currentIdx = 0;
		currentWrp = 0;
		correctSol = true;
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		//Add the root element 
		expansionArray.add(0);
		Production firstProduction = firstRule.get(solution.getVariables().get(currentIdx++).getValue() % firstRule.size());
		processProduction_expansion(firstProduction, solution, phenotype, expansionArray);
		
		
		return expansionArray;
	}

	private void processProduction_expansion(Production currentProduction, Solution<Variable<Integer>> solution, LinkedList<String> phenotype, ArrayList<Integer> expansionArray)
	{
		if(!correctSol)
			return;
		for (Symbol symbol : currentProduction) {
			if (symbol.isTerminal()) {
				phenotype.add(symbol.toString());
			} else {
				if(currentIdx >= solution.getVariables().size() && currentWrp<maxCntWrappings) {
					//Warp found, return an empty array as its invalid
					expansionArray.clear();
					return;
				}
				if (currentIdx < solution.getVariables().size()) {
					Rule rule = reader.findRule(symbol);
					//Add the index of the rule to the expansion array
					expansionArray.add(reader.indexOf(symbol));
					Production production = rule.get(solution.getVariables().get(currentIdx++).getValue() % rule.size());
					processProduction_expansion(production, solution, phenotype, expansionArray);
				}
				else {
					correctSol = false;
					return;
				}
			}
		}		
	}

	public Solutions<Variable<Integer>> newRandomSetOfSolutions(int size) {
		Solutions<Variable<Integer>> solutions = new Solutions<Variable<Integer>>();
		for (int i=0; i<size; ++i) {
			Solution<Variable<Integer>> solI = new NeutralMutationSolution(numberOfObjectives);
			for (int j = 0; j < numberOfVariables; ++j) {
				Variable<Integer> varJ = new Variable<Integer>(RandomGenerator.nextInteger((int) upperBound[j])); 
				solI.getVariables().add(varJ);
			}
			solutions.add(solI);
		}
		return solutions;
	}

	//Return the number of times the rule is expanded until its completely expanded
	public int getNumberOfNodesBeforeTerminal(Solution<Variable<Integer>> parent2, int point, Integer firstRuleIndex)
	{
		int aux = point;
		currentIdx = point;
		currentWrp = 0;
		correctSol = true;
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(firstRuleIndex);
		Production firstProduction = firstRule.get(parent2.getVariables().get(currentIdx++).getValue() % firstRule.size());
		processProduction(firstProduction, parent2, phenotype);
		
		return currentIdx - aux;
	}

}
