package jeco.core.algorithm.moga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import jeco.core.algorithm.Algorithm;
import jeco.core.operator.comparator.ArrayDominance;
import jeco.core.operator.comparator.PropertyComparator;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * 
 * 
 * Input parameters:
 * - MAX_GENERATIONS
 * - MAX_POPULATION_SIZE
 * 
 * Operators:
 * - CROSSOVER: Crossover operator
 * - MUTATION: Mutation operator
 * - SELECTION: Selection operator
 * 
 * @author José L. Risco-Martín
 *
 */
public class SPEA2<T extends Variable<?>> extends Algorithm<T> {
    /////////////////////////////////////////////////////////////////////////

    protected int maxGenerations;
    protected int maxPopulationSize;
    /////////////////////////////////////////////////////////////////////////
    protected Comparator<Solution<T>> dominance;
    protected int currentGeneration;
    protected Solutions<T> population;
    protected Solutions<T> archive;
    protected MutationOperator<T> mutationOperator;
    protected CrossoverOperator<T> crossoverOperator;
    protected SelectionOperator<T> selectionOperator;
    protected int K;

    public SPEA2(Problem<T> problem, int maxPopulationSize, int maxGenerations, MutationOperator<T> mutationOperator, CrossoverOperator<T> crossoverOperator, SelectionOperator<T> selectionOperator) {
        super(problem);
        this.maxPopulationSize = maxPopulationSize;
        this.maxGenerations = maxGenerations;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
        dominance = new SolutionDominance<T>();
    }

    public void initialize() {
        K = (int) Math.sqrt(maxPopulationSize + maxPopulationSize);
        //Initialize the variables
        archive = new Solutions<T>();
        population = problem.newRandomSetOfSolutions(maxPopulationSize);
        problem.evaluate(population);
        currentGeneration = 0;
    }

    public Solutions<T> execute() {
        while (currentGeneration < maxGenerations) {
            step();
        }
        archive.reduceToNonDominated(dominance);
        return archive;
    } // execute

    public void step() {
        currentGeneration++;

        Solutions<T> union = new Solutions<T>();
        union.addAll(population);
        union.addAll(archive);
        assignFitness(union);
        Solutions<T> unionReduced = reduceByFitness(union);
        if (unionReduced.size() < maxPopulationSize) // External archive size
        {
            expand(unionReduced, union, maxPopulationSize - unionReduced.size());
        } else if (unionReduced.size() > maxPopulationSize) {
            unionReduced = reduce(unionReduced, maxPopulationSize);
        }

        archive = unionReduced;
        if (currentGeneration == maxGenerations) {
            return;
        }

        // Create a new offspringPopulation
        Solutions<T> offSpringSolutionSet = new Solutions<T>();
        Solution<T> parent1, parent2;
        while (offSpringSolutionSet.size() < maxPopulationSize) {
            parent1 = selectionOperator.execute(archive).get(0);
            parent2 = selectionOperator.execute(archive).get(0);
            //make the crossover
            Solutions<T> offSpring = crossoverOperator.execute(parent1, parent2);
            // We only add one of the two children:
            for (Solution<T> solution : offSpring) {
                mutationOperator.execute(solution);
                offSpringSolutionSet.add(solution);
            }
        } // while
        problem.evaluate(offSpringSolutionSet);
        // End Create a offSpring solutionSet
        population = offSpringSolutionSet;
    }

    public void assignFitness(Solutions<T> solutions) {
        int i, j, popSize = solutions.size();
        int strength[] = new int[popSize];
        int raw[] = new int[popSize];
        double density[] = new double[popSize];
        double sigma;
        Solution<T> solI;
        int compare;
        double fitness;

        for (i = 0; i < popSize; ++i) {
            strength[i] = 0;
            raw[i] = 0;
            density[i] = 0;
        }

        // Assigns strength
        for (i = 0; i < popSize; ++i) {
            solI = solutions.get(i);
            for (j = 0; j < popSize; ++j) {
                if (i == j) {
                    continue;
                }
                compare = dominance.compare(solI, solutions.get(j));
                if (compare < 0) {
                    strength[i]++;
                }
            }
        }

        // Assigns raw fitness
        for (i = 0; i < popSize; ++i) {
            solI = solutions.get(i);
            for (j = 0; j < popSize; ++j) {
                if (i == j) {
                    continue;
                }
                compare = dominance.compare(solI, solutions.get(j));
                if (compare == 1 || compare == 2) {
                    raw[i] += strength[j];
                }
            }
        }

        // Assigns density
        for (i = 0; i < popSize; ++i) {
            sigma = computeSigma(i, solutions);
            density[i] = 1 / (sigma + 2);
            fitness = raw[i] + density[i];
            solutions.get(i).getProperties().put("fitness", fitness);
        }
    }

    private double computeSigma(int i, Solutions<T> solutions) {
        return computeSigmas(i, solutions).get(K);
    }

    public double euclideanDistance(Solution<T> sol1, Solution<T> sol2) {
        int nObjs = Math.min(sol1.getObjectives().size(), sol2.getObjectives().size());

        double sum = 0;
        for (int i = 0; i < nObjs; i++) {
            sum += ((sol1.getObjectives().get(i) - sol2.getObjectives().get(i)) * (sol1.getObjectives().get(i) - sol2.getObjectives().get(i)));
        }
        return Math.sqrt(sum);
    }

    private ArrayList<Double> computeSigmas(int i, Solutions<T> solutions) {
        int popSize = solutions.size();
        int j;
        double distance;
        ArrayList<Double> distancesToI = new ArrayList<Double>();
        Solution<T> solI = solutions.get(i);
        for (j = 0; j < popSize; j++) {
            distance = euclideanDistance(solI, solutions.get(j));
            distancesToI.add(distance);
        }
        Collections.sort(distancesToI);
        return distancesToI;
    }

    public Solutions<T> reduceByFitness(Solutions<T> pop) {
        Solutions<T> result = new Solutions<T>();
        Solution<T> indI;
        for (int i = 0; i < pop.size(); ++i) {
            indI = pop.get(i);
            if (indI.getProperties().get("fitness").doubleValue() < 1) {
                result.add(indI);
            }
        }
        return result;
    }

    public void expand(Solutions<T> pop, Solutions<T> all, int nElems) {
        int i = 0, count = 0, allSize = all.size();
        Solution<T> indI;
        Collections.sort(all, new PropertyComparator<T>("fitness"));
        for (i = 0; i < allSize; ++i) {
            indI = all.get(i);
            if (indI.getProperties().get("fitness").doubleValue() >= 1) {
                pop.add(indI);
                count++;
                if (count == nElems) {
                    break;
                }
            }
        }
    }

    public Solutions<T> reduce(Solutions<T> pop, int maxSize) {
        int i, min;
        ArrayList<ArrayList<Double>> allSigmas = new ArrayList<ArrayList<Double>>();
        HashSet<Integer> erased = new HashSet<Integer>();
        int toErase = pop.size() - maxSize;
        ArrayDominance comparator = new ArrayDominance();

        for (i = 0; i < pop.size(); i++) {
            allSigmas.add(computeSigmas(i, pop));
        }

        while (erased.size() < toErase) {
            min = 0;
            while (erased.contains(min)) {
                min++;
            }
            for (i = 0; i < pop.size(); i++) {
                if (i == min || erased.contains(i)) {
                    continue;
                }
                if (comparator.compare(allSigmas.get(i), allSigmas.get(min)) == -1) {
                    min = i;
                }
            }
            erased.add(min);
        }

        Solutions<T> result = new Solutions<T>();
        for (i = 0; i < pop.size(); i++) {
            if (!erased.contains(i)) {
                result.add(pop.get(i));
            }
        }

        return result;
    }

    public void setMutationOperator(MutationOperator<T> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    public void setCrossoverOperator(CrossoverOperator<T> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    public void setSelectionOperator(SelectionOperator<T> selectionOperator) {
        this.selectionOperator = selectionOperator;
    }

    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }
} // Spea2

