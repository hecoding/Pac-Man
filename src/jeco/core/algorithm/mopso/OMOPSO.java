package jeco.core.algorithm.mopso;

import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import javax.management.JMException;
import jeco.core.algorithm.Algorithm;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.comparator.PropertyComparator;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.operator.mutation.NonUniformMutation;
import jeco.core.operator.mutation.UniformMutation;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * 
 * 
 * Input parameters:
 * - MAX_GENERATIONS
 * - MAX_POPULATION_SIZE
 * 
 * @author José L. Risco-Martín
 *
 */
public class OMOPSO<V extends Variable<Double>> extends Algorithm<V> {

    private static final Logger logger = Logger.getLogger(OMOPSO.class.getName());
    
    private int swarmSize;
    protected int maxT;
    protected int t;
    protected Solutions<V> swarm;
    private Solutions<V> personalBests;
    protected Solutions<V> leaders;
    //private Solutions externalArchive;
    //private Comparator<Solution> epsilonComparator;
    private double[][] speeds;
    private SolutionDominance<V> objectivesComparator;
    private Comparator<Solution<V>> crowdingDistanceComparator;
    private CrowdingDistance<V> crowdingDistanceAssigner;
    private UniformMutation<V> uniformMutation;
    private NonUniformMutation<V> nonUniformMutation;
    //private double eta = 0.0075;

    // For testing purposes
    protected boolean dynamicVelocity = false;
    private double[] delta;

    public OMOPSO(Problem<V> problem, int populationSize, int maxT) {
        super(problem);
        this.swarmSize = populationSize;
        this.maxT = maxT;
    } // OMOPSO

    public OMOPSO(Problem<V> problem, int populationSize, int maxT, boolean dynamicVelocity) {
        this(problem, populationSize, maxT);
        this.dynamicVelocity = dynamicVelocity;
    }

    /**
     * Initialize all parameter of the algorithm
     */
    @Override
    public void initialize() {

        swarm =  problem.newRandomSetOfSolutions(swarmSize);
        personalBests = new Solutions<V>();
        for(int i=0; i<swarmSize; ++i) {
            personalBests.add(swarm.get(i).clone());
        }
        leaders = new Solutions<V>();

        // Create the dominator for equadless and dominance
        objectivesComparator = new SolutionDominance<V>();
        crowdingDistanceComparator = new PropertyComparator<V>(CrowdingDistance.propertyCrowdingDistance);
        crowdingDistanceAssigner = new CrowdingDistance<V>(problem.getNumberOfObjectives());

        // Create the speed_ vector
        speeds = new double[swarmSize][problem.getNumberOfVariables()];
        if(dynamicVelocity) {
            delta = new double[problem.getNumberOfVariables()];
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                delta[j] = (problem.getUpperBound(j)-problem.getLowerBound(j))/2;
            }
        }

        uniformMutation = new UniformMutation<V>(problem);
        nonUniformMutation = new NonUniformMutation<V>(problem, maxT);


        t = 0;
        //->Step 1 (and 3) Evaluate initial population
        problem.evaluate(swarm);

        //-> Step2. Initialize the speed_ of each particle to 0
        for (int i = 0; i < swarmSize; i++) {
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                speeds[i][j] = 0.0;
            }
        }


        // Step4 and 5
        for (Solution<V> particle : swarm) {
            leaders.add(particle.clone());
            //externalArchive.add(new Solution(particle));
        }

        reduceLeaders();
        //externalArchive.keepParetoNonDominated(epsilonComparator);

        //-> Step 6. Initialize the memory of each particle
        for (int i = 0; i < swarm.size(); ++i) {
            personalBests.set(i, swarm.get(i).clone());
        }

        //Crowding the leaders
        //crowdingDistanceAssigner.execute(leaders);

    } // initialize

    public void reduceLeaders() {
        leaders.reduceToNonDominated(objectivesComparator);
        if (leaders.size() <= swarmSize) {
            return;
        }
        crowdingDistanceAssigner.execute(leaders);
        Collections.sort(leaders, crowdingDistanceComparator);
        while (leaders.size() > swarmSize) {
            leaders.remove(0);
        }
    }

    /**
     * Update the spped of each particle
     */
    private void computeSpeed() {
        double r1, r2, W, C1, C2;
        Solution<V> particle, personalBest, globalBest, one, two;
        V vPart, pBest, gBest;

        crowdingDistanceAssigner.execute(leaders);

        for (int i = 0; i < swarmSize; i++) {
            particle = swarm.get(i);
            personalBest = personalBests.get(i);

            //Select a global best_ for calculate the speed of particle i, bestGlobal
            int pos1 = RandomGenerator.nextInt(0, leaders.size());
            int pos2 = RandomGenerator.nextInt(0, leaders.size());
            one = leaders.get(pos1);
            two = leaders.get(pos2);

            if (crowdingDistanceComparator.compare(two, one) < 1) {
                globalBest = one;
            } else {
                globalBest = two;
            }
            //

            //Params for velocity equation
            r1 = RandomGenerator.nextDouble();
            r2 = RandomGenerator.nextDouble();
            C1 = RandomGenerator.nextDouble(1.5, 2.0);
            C2 = RandomGenerator.nextDouble(1.5, 2.0);
            W = RandomGenerator.nextDouble(0.1, 0.5);
            //

            for (int j = 0; j < problem.getNumberOfVariables(); ++j) {
                vPart = particle.getVariables().get(j);
                pBest = personalBest.getVariables().get(j);
                gBest = globalBest.getVariables().get(j);
                //Computing the velocity of this particle
                speeds[i][j] = W * speeds[i][j]
                        + C1 * r1 * (pBest.getValue() - vPart.getValue())
                        + C2 * r2 * (gBest.getValue() - vPart.getValue());
                if(dynamicVelocity) {
                    if(speeds[i][j]>delta[j]) {
                        speeds[i][j] = delta[j];
                    }
                    if(speeds[i][j]<=-delta[j]) {
                        speeds[i][j] = -delta[j];
                    }
                }
            }

        }
    } // computeSpeed

    /**
     * Update the position of each particle
     */
    private void computeNewPositions() {
        for (int i = 0; i < swarmSize; i++) {
            Solution<V> particle = swarm.get(i);
            //particle.move(speed_[i]);
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                V variable = particle.getVariables().get(j);
                variable.setValue(variable.getValue() + speeds[i][j]);
                if (variable.getValue() < problem.getLowerBound(j)) {
                    variable.setValue(problem.getLowerBound(j));
                    speeds[i][j] = speeds[i][j] * -1.0;
                }
                if (variable.getValue() > problem.getUpperBound(j)) {
                    variable.setValue(problem.getUpperBound(j));
                    speeds[i][j] = speeds[i][j] * -1.0;
                }
            }
        }
    } // computeNewPositions

    /**
     * Apply a mutation operator to all particles in the swarm
     */
    private void mopsoMutation(int currentIteration) {
        //There are three groups of particles_, the ones that are mutated with
        //a non-uniform mutation operator, the ones that are mutated with a
        //uniform mutation and the one that are not mutated
        nonUniformMutation.setCurrentIteration(currentIteration);
        //*/

        for (int i = 0; i < swarm.size(); i++) {
            if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation
                nonUniformMutation.execute(swarm.get(i));
            } else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
                uniformMutation.execute(swarm.get(i));
            } else //particles_ without mutation
            ;
        }
    } // mopsoMutation

    /**
     * Runs of the OMOPSO algorithm.
     * @return a <code>SolutionSet</code> that is a set of non dominated solutions
     * as a result of the algorithm execution
     * @throws JMException
     */
    @Override
    public Solutions<V> execute() {
        int nextPercentageReport = 10;
       
        //-> Step 7. Iterations ..
        while (t < maxT) {
            step();
            int percentage = Math.round((t * 100) / maxT);
            if (percentage == nextPercentageReport) {
                logger.info(percentage + "% performed ...");
                nextPercentageReport += 10;
            }
        }

        return leaders;
        //return eArchive_;
    } // execute

    @Override
    public void step() {
        t++;

        //Compute the speed_
        computeSpeed();

        //Compute the new positions for the particles_
        computeNewPositions();

        //Mutate the particles_
        mopsoMutation(t);

        //Evaluate the new particles_ in new positions
        problem.evaluate(swarm);

        //Actualize the memory of this particle
        for (int i = 0; i < swarm.size(); i++) {
            int flag = objectivesComparator.compare(swarm.get(i), personalBests.get(i));
            if (flag != 1) { // the new particle is best_ than the older remeber
                Solution<V> particle = swarm.get(i).clone();
                //this.best_.reemplace(i,particle);
                personalBests.set(i, particle);
            }
        }

        //Actualize the archive
        for (int i = 0; i < swarm.size(); i++) {
            Solution<V> particle = swarm.get(i).clone();
            leaders.add(particle);
            //externalArchive.add(new Solution(particle));
        }
        reduceLeaders();
        //externalArchive.keepParetoNonDominated(epsilonComparator);

        //Crowding the leaders_
        //crowdingDistanceAssigner.execute(leaders);

    }

    public void setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
    }

    public void setMaxT(int maxT) {
        this.maxT = maxT;
    }
} // OMOPSO
