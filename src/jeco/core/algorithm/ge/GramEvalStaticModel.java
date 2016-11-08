package jeco.core.algorithm.ge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.evaluator.AbstractPopPredictor;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.NormalizedDataTable;
import jeco.core.util.compiler.MyCompiler;
import jeco.core.util.compiler.MyLoader;

/**
 * Class to develop "static" (non-temporal) models
 * This class must be carefully revised and tested. I just copied/pasted a functional
 * version that I developed for Patricia.
 */
public class GramEvalStaticModel extends AbstractProblemGE {

    private static final Logger logger = Logger.getLogger(GramEvalStaticModel.class.getName());

    protected int threadId;
    protected MyCompiler compiler;
    protected NormalizedDataTable dataTable;
    protected Properties properties;
    protected AbstractPopPredictor predictor;
    protected String[] varNames;
    protected double bestFitness = Double.POSITIVE_INFINITY;
    protected String bestExpression = "";

    public GramEvalStaticModel(Properties properties, int threadId) throws IOException {
        super(properties.getProperty("BnfPathFile"), 1);
        this.properties = properties; // Just for its use in "clone" member function
        this.threadId = threadId;
        compiler = new MyCompiler(properties);
        dataTable = new NormalizedDataTable(this, properties.getProperty("TrainingPath"), properties.getProperty("ValidationPath"), Double.valueOf(properties.getProperty("ErrorThreshold")));
        varNames = properties.getProperty("VarNames").split(";");
    }

    public GramEvalStaticModel(Properties properties) throws IOException {
        this(properties, 1);
    }

    public void generateCodeAndCompile(Solutions<Variable<Integer>> solutions) throws Exception {
        // Phenotype generation
        ArrayList<String> phenotypes = new ArrayList<>();
        for (Solution<Variable<Integer>> solution : solutions) {
            Phenotype phenotype = super.generatePhenotype(solution);
            if (super.correctSol) {
                phenotypes.add(phenotype.toString());
            } else {
                phenotypes.add("0");
            }
        }
        // Compilation process:
        File file = new File(compiler.getWorkDir() + File.separator + "PopPredictor" + threadId + ".java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(AbstractPopPredictor.generateClassCode(threadId, phenotypes));
        writer.flush();
        writer.close();
        LinkedList<String> filePaths = new LinkedList<>();
        filePaths.add(file.getAbsolutePath());
        boolean sucess = compiler.compile(filePaths);
        if (!sucess) {
            logger.severe("Unable to compile, with errors:");
            logger.severe(compiler.getOutput());
        }
    }

    public double computeRMSE(double[] y, double[] yP) {
        double rmse = 0;
        for (int j = 0; j < y.length; ++j) {
            rmse += Math.pow(y[j] - yP[j], 2);
        }
        rmse = Math.sqrt(rmse / y.length);
        return rmse;
    }

    @Override
    public void evaluate(Solutions<Variable<Integer>> solutions) {
        try {
            this.generateCodeAndCompile(solutions);
            // And now we evaluate all the solutions with the compiled file:
            predictor = (AbstractPopPredictor) (new MyLoader(compiler.getWorkDir())).loadClass("PopPredictor" + threadId).newInstance();
            // double[][] x = dataTable.getXTReal();
            // double[] y = dataTable.getYTReal();
            // TODO: Fix the previous code:
            double[][] x = null;
            double[] y = null;
            for (int i = 0; i < solutions.size(); ++i) {
                double[] yP = predictor.computeYP(i, x);
                double rmse = computeRMSE(y, yP);
                if (rmse < bestFitness) {
                    bestFitness = rmse;
                    bestExpression = super.generatePhenotype(solutions.get(i)).toString();
                    for (int j = 0; j < varNames.length - 1; ++j) {
                        bestExpression = bestExpression.replaceAll("x\\[" + j + "\\]", varNames[j]);
                    }
                    logger.info("Best FIT=" + bestFitness + "; Expresion=" + bestExpression);
                }
                solutions.get(i).getObjectives().set(0, rmse);
            }
        } catch (Exception ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void validate(Solutions<Variable<Integer>> solutions) {
        try {
            this.generateCodeAndCompile(solutions);
            // And now we evaluate all the solutions with the compiled file:
            predictor = (AbstractPopPredictor) (new MyLoader(compiler.getWorkDir())).loadClass("PopPredictor" + threadId).newInstance();
            // double[][] x = dataTable.getXVReal();
            // double[] y = dataTable.getYVReal();
            // TODO: Fix the previous code:
            double[][] x = null;
            double[] y = null;
            for (int i = 0; i < solutions.size(); ++i) {
                double[] yP = predictor.computeYP(i, x);
                double rmse = computeRMSE(y, yP);
                solutions.get(i).getObjectives().set(0, rmse);
            }
        } catch (Exception ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution) {
        logger.severe("The solutions should be already evaluated. You should not see this message.");
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        logger.severe("The solutions should be already evaluated. You should not see this message.");
    }

    @Override
    public GramEvalStaticModel clone() {
        GramEvalStaticModel clone = null;
        try {
            clone = new GramEvalStaticModel(properties, threadId + 1);
        } catch (IOException ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clone;
    }

    public static void main(String[] args) {
        String propertiesFilePath = "test" + File.separator + GramEvalStaticModel.class.getSimpleName() + ".properties";
        int threadId = 1;
        if (args.length == 1) {
            propertiesFilePath = args[0];
        } else if (args.length >= 2) {
            propertiesFilePath = args[0];
            threadId = Integer.valueOf(args[1]);
        }
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader(new File(propertiesFilePath))));
            File clsDir = new File(properties.getProperty("WorkDir"));
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> sysclass = URLClassLoader.class;
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{clsDir.toURI().toURL()});
        } catch (Exception ex) {
            logger.severe(ex.getLocalizedMessage());
        }

        GramEvalStaticModel problem = null;
        try {
            problem = new GramEvalStaticModel(properties, threadId);
        } catch (IOException ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Second create the algorithm
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<>(problem, 1.0 / problem.reader.getRules().size());
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, SinglePointCrossover.DEFAULT_PROBABILITY, SinglePointCrossover.AVOID_REPETITION_IN_FRONT);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<>(comparator);
        SimpleGeneticAlgorithm<Variable<Integer>> algorithm = new SimpleGeneticAlgorithm<>(problem, Integer.valueOf(properties.getProperty("NumIndividuals")), Integer.valueOf(properties.getProperty("NumGenerations")), true, mutationOperator, crossoverOperator, selectionOp);
        algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        // Now we evaluate the solution in the validation data
        logger.info("Validation of solutions[0] with fitness " + solutions.get(0).getObjective(0));
        problem.validate(solutions);
        Solution<Variable<Integer>> solution = solutions.get(0);
        double validationFitness = solution.getObjectives().get(0);
        logger.info("Validation fitness for solutions[0] = " + validationFitness);
    }
}
