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
import java.util.LinkedList;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.evaluator.AbstractPopEvaluator;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.NormalizedDataTable;
import jeco.core.util.compiler.MyCompiler;
import jeco.core.util.compiler.MyLoader;

public class GramEvalTemporalModel extends AbstractProblemGE {

    private static final Logger logger = Logger.getLogger(GramEvalTemporalModel.class.getName());

    protected int threadId;
    protected MyCompiler compiler;
    protected NormalizedDataTable dataTable;
    protected Properties properties;
    protected AbstractPopEvaluator evaluator;

    public GramEvalTemporalModel(Properties properties, int threadId) throws IOException {
        super(properties.getProperty("BnfPathFile"), 1);
        this.properties = properties;
        this.threadId = threadId;
        compiler = new MyCompiler(properties);
        dataTable = new NormalizedDataTable(this, properties.getProperty("TrainingPath"), properties.getProperty("ValidationPath"), Double.valueOf(properties.getProperty("ErrorThreshold")));
    }

    @Override
    public void evaluate(Solutions<Variable<Integer>> solutions) {
        StringBuilder currentJavaFile = new StringBuilder();

        currentJavaFile.append("public class PopEvaluator").append(threadId).append(" extends algorithm.AbstractPopEvaluator {\n\n");

        currentJavaFile.append("\tpublic void evaluateExpression(int idxExpr) {\n");
        currentJavaFile.append("\t\tdouble[] rowPred = dataTable.get(0);\n");
        currentJavaFile.append("\t\trowPred[rowPred.length - 1] = rowPred[0];\n");
        currentJavaFile.append("\t\tfor (int k = 0; k < dataTable.size() - 1; ++k) {\n");
        currentJavaFile.append("\t\t\trowPred = dataTable.get(k + 1);\n");
        currentJavaFile.append("\t\t\trowPred[rowPred.length - 1] = evaluate(idxExpr, k);\n");
        currentJavaFile.append("\t\t}\n");
        currentJavaFile.append("\t}\n\n");

        currentJavaFile.append("\tpublic double getVariable(int idxVar, int k) {\n");
        currentJavaFile.append("\t\tif (k < 0) {\n");
        currentJavaFile.append("\t\t\treturn dataTable.get(0)[idxVar];\n");
        currentJavaFile.append("\t\t} else {\n");
        currentJavaFile.append("\t\t\treturn dataTable.get(k)[idxVar];\n");
        currentJavaFile.append("\t\t}\n");
        currentJavaFile.append("\t}\n\n");

        currentJavaFile.append("\tpublic double evaluate(int idxExpr, int k) {\n");
        currentJavaFile.append("\t\tdouble result = 0.0;\n");
        currentJavaFile.append("\t\ttry {\n");

        currentJavaFile.append("\t\t\tswitch(idxExpr) {\n");
        for (int i = 0; i < solutions.size(); ++i) {
            currentJavaFile.append("\t\t\t\tcase ").append(i).append(":\n");
            Solution<Variable<Integer>> solution = solutions.get(i);
            Phenotype phenotype = generatePhenotype(solution);
            if (correctSol) {
                currentJavaFile.append("\t\t\t\t\tresult = ").append(phenotype.toString()).append(";\n");
            } else {
                currentJavaFile.append("\t\t\t\t\tresult = Double.POSITIVE_INFINITY;\n");
            }
            currentJavaFile.append("\t\t\t\t\tbreak;\n");
        }
        currentJavaFile.append("\t\t\t\tdefault:\n");
        currentJavaFile.append("\t\t\t\t\tresult = Double.POSITIVE_INFINITY;\n");
        currentJavaFile.append("\t\t\t}\n"); // End switch

        currentJavaFile.append("\t\t}\n"); // End try
        currentJavaFile.append("\t\tcatch (Exception ee) {\n");
        currentJavaFile.append("\t\t\t// System.err.println(ee.getLocalizedMessage());\n");
        currentJavaFile.append("\t\t\tresult = Double.POSITIVE_INFINITY;\n");
        currentJavaFile.append("\t\t}\n"); // End catch
        currentJavaFile.append("\t\tif(Double.isNaN(result)) {\n");
        currentJavaFile.append("\t\t\tresult = Double.POSITIVE_INFINITY;\n");
        currentJavaFile.append("\t\t}\n");
        currentJavaFile.append("\t\treturn result;\n");
        currentJavaFile.append("\t}\n");
        currentJavaFile.append("}\n");
        // Compilation process:
        try {
            File file = new File(compiler.getWorkDir() + File.separator + "PopEvaluator" + threadId + ".java");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentJavaFile.toString());
            writer.flush();
            writer.close();
            LinkedList<String> filePaths = new LinkedList<>();
            filePaths.add(file.getAbsolutePath());
            boolean sucess = compiler.compile(filePaths);
            if (!sucess) {
                logger.severe("Unable to compile, with errors:");
                logger.severe(compiler.getOutput());
            }
        } catch (Exception ex) {
            logger.severe(ex.getLocalizedMessage());
        }
        // And now we evaluate all the solutions with the compiled file:
        evaluator = null;
        try {
            evaluator = (AbstractPopEvaluator) (new MyLoader(compiler.getWorkDir())).loadClass("PopEvaluator" + threadId).newInstance();
            evaluator.setDataTable(dataTable.getTrainingTable());
        } catch (Exception ex) {
            logger.severe(ex.getLocalizedMessage());
        }
        for (int i = 0; i < solutions.size(); ++i) {
            Solution<Variable<Integer>> solution = solutions.get(i);
            double fitness = dataTable.evaluate(evaluator, solution, i);
            if (Double.isNaN(fitness)) {
                logger.info("I have a NaN number here");
            }
            solution.getObjectives().set(0, fitness);
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
    public GramEvalTemporalModel clone() {
        GramEvalTemporalModel clone = null;
        try {
            clone = new GramEvalTemporalModel(properties, threadId + 1);
        } catch (IOException ex) {
            logger.severe(ex.getLocalizedMessage());
        }
        return clone;
    }

    public static Properties loadProperties(String propertiesFilePath) {
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
        return properties;
    }
    
    public static void runGE(Properties properties, int threadId, Observer obs) {
        logger.setLevel(Level.FINE);
        GramEvalTemporalModel problem = null;
        try {
            problem = new GramEvalTemporalModel(properties, threadId);
        } catch (IOException ex) {
            logger.severe(ex.getLocalizedMessage());
        }
        // Second create the algorithm
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<>(problem, 1.0 / problem.reader.getRules().size());
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, SinglePointCrossover.DEFAULT_PROBABILITY, SinglePointCrossover.AVOID_REPETITION_IN_FRONT);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<>(comparator);
        SimpleGeneticAlgorithm<Variable<Integer>> algorithm = new SimpleGeneticAlgorithm<>(problem, Integer.valueOf(properties.getProperty("NumIndividuals")), Integer.valueOf(properties.getProperty("NumGenerations")), true, mutationOperator, crossoverOperator, selectionOp);
        if (obs != null) {
            algorithm.addObserver(obs);
        }
        algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        
        String[] solsForTable = new String[]{String.valueOf(solutions.get(0).getObjective(0)),
        problem.generatePhenotype(solutions.get(0)).toString()};
        solutionsTableModel.getDataVector().clear();
        solutionsTableModel.addRow(solsForTable);
        
        // Now we evaluate the solution in the validation data
        logger.info("Validation of solutions[0] with fitness " + solutions.get(0).getObjective(0));
        problem.evaluate(solutions);
        Solution<Variable<Integer>> solution = solutions.get(0);
        problem.evaluator.setDataTable(problem.dataTable.getValidationTable());
        double validationFitness = problem.dataTable.evaluate(problem.evaluator, solution, 0);
        logger.info("Validation fitness for solutions[0] = " + validationFitness);
        logger.info("Average error for solutions[0]");
        logger.info("Training = " + problem.dataTable.computeAvgError(problem.dataTable.getTrainingTable()));
        logger.info("Validation = " + problem.dataTable.computeAvgError(problem.dataTable.getValidationTable()));        
    }

    
    private static DefaultTableModel solutionsTableModel = new DefaultTableModel();
    
    public static void setViewTable(JTable tableSolutions) {
        solutionsTableModel.setColumnIdentifiers(new String[]{"Obj.","Solution"});
        tableSolutions.setModel(solutionsTableModel);
    }
    
    
    public static void main(String[] args) {
        String propertiesFilePath = "TemporalModel.properties";
        int threadId = 1;
        if (args.length == 1) {
            propertiesFilePath = args[0];
        } else if (args.length >= 2) {
            propertiesFilePath = args[0];
            threadId = Integer.valueOf(args[1]);
        }
        Properties properties = loadProperties(propertiesFilePath);
        runGE(properties,threadId,null);
    }
}
