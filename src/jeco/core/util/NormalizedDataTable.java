/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeco.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.operator.evaluator.AbstractPopEvaluator;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * Class to manage a normalized data table. 
 * Originally, the data table is passed to this class as a regular data table. 
 * After the constructor, the data table is normalized in the interval [1,2].
 *
 * @author José Luis Risco Martín
 */
public class NormalizedDataTable {

    private static final Logger logger = Logger.getLogger(NormalizedDataTable.class.getName());

    protected AbstractProblemGE problem;
    protected String trainingPath = null;
    protected String validationPath = null;
    protected double errorThreshold = 0;
    protected ArrayList<double[]> trainingTable = new ArrayList<>();
    protected ArrayList<double[]> validationTable = new ArrayList<>();
    protected int numInputColumns = 0;
    protected int numTotalColumns = 0;
    protected double[] xLs = null;
    protected double[] xHs = null;

    protected double bestFitness = Double.POSITIVE_INFINITY;
    
    public NormalizedDataTable(AbstractProblemGE problem, String trainingPath, String validationPath, double errorThreshold) throws IOException {
        this.problem = problem;
        this.trainingPath = trainingPath;
        this.validationPath = validationPath;
        this.errorThreshold = errorThreshold;
        logger.info("Reading data file ...");
        fillDataTable(trainingPath, trainingTable);
        fillDataTable(validationPath, validationTable);
        logger.info("... done.");
        normalize(1.0, 2.0);
    }

    public final void fillDataTable(String dataPath, ArrayList<double[]> dataTable) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(dataPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(";");
            if (parts.length > numInputColumns) {
                numInputColumns = parts.length;
                numTotalColumns = numInputColumns + 1;
            }
            double[] dataLine = new double[numTotalColumns];
            for (int j = 0; j < numInputColumns; ++j) {
                dataLine[j] = Double.valueOf(parts[j]);
            }
            dataTable.add(dataLine);
        }
        reader.close();
    }

    public double evaluate(AbstractPopEvaluator evaluator, Solution<Variable<Integer>> solution, int idx) {
        String functionAsString = problem.generatePhenotype(solution).toString();
        double fitness = computeFitness(evaluator, idx);
        if (fitness < bestFitness) {
            bestFitness = fitness;
            logger.info("Best FIT=" + bestFitness + "; Expresion=" + functionAsString);
        }
        return fitness;
    }

    public double computeFitness(AbstractPopEvaluator evaluator, int idx) {
        evaluator.evaluateExpression(idx);
        ArrayList<double[]> timeTable = evaluator.getDataTable();
        double fitness = 0;
        for (int i = 0; i < timeTable.size(); ++i) {
            double min = Math.min(timeTable.get(i)[numInputColumns], timeTable.get(i)[0]);
            double max = Math.max(timeTable.get(i)[numInputColumns], timeTable.get(i)[0]);
            if (1 - (min / max) > errorThreshold) {
                fitness += max - min;
            }
        }
        return fitness;
    }

    public final void normalize(double yL, double yH) {
        logger.info("Normalizing data in [" + yL + ", " + yH + "] ...");
        xLs = new double[numInputColumns];
        xHs = new double[numInputColumns];
        for (int i = 0; i < numInputColumns; ++i) {
            xLs[i] = Double.POSITIVE_INFINITY;
            xHs[i] = Double.NEGATIVE_INFINITY;
        }
        // We compute first minimum and maximum values:
        ArrayList<double[]> fullTable = new ArrayList<>();
        fullTable.addAll(trainingTable);
        fullTable.addAll(validationTable);
        for (int i = 0; i < fullTable.size(); ++i) {
            double[] row = fullTable.get(i);
            for (int j = 0; j < numInputColumns; ++j) {
                if (xLs[j] > row[j]) {
                    xLs[j] = row[j];
                }
                if (xHs[j] < row[j]) {
                    xHs[j] = row[j];
                }
            }
        }

        // Now we compute "m" and "n", being y = m*x + n
        // y is the new data
        // x is the old data
        double[] m = new double[numInputColumns];
        double[] n = new double[numInputColumns];
        for (int j = 0; j < numInputColumns; ++j) {
            m[j] = (yH - yL) / (xHs[j] - xLs[j]);
            n[j] = yL - m[j] * xLs[j];
        }
        // Finally, we normalize ...
        for (int i = 0; i < fullTable.size(); ++i) {
            double[] row = fullTable.get(i);
            for (int j = 0; j < numInputColumns; ++j) {
                row[j] = m[j] * row[j] + n[j];
            }
        }

        // ... and report the values of both xLs and xHs ...
        StringBuilder xLsAsString = new StringBuilder();
        StringBuilder xHsAsString = new StringBuilder();
        for (int j = 0; j < numInputColumns; ++j) {
            if (j > 0) {
                xLsAsString.append(", ");
                xHsAsString.append(", ");
            } else {
                xLsAsString.append("xLs=[");
                xHsAsString.append("xHs=[");
            }
            xLsAsString.append(xLs[j]);
            xHsAsString.append(xHs[j]);
        }
        xLsAsString.append("]");
        xHsAsString.append("]");
        logger.info(xLsAsString.toString());
        logger.info(xHsAsString.toString());
        logger.info("... done.");
    }

    public double computeAvgError(ArrayList<double[]> timeTable) {
        double error = 0.0;
        for (int k = 0; k < timeTable.size(); ++k) {
            double[] row = timeTable.get(k);
            error += Math.abs(row[0] - row[row.length - 1]);
        }
        error /= timeTable.size();
        return error;
    }

    public ArrayList<double[]> getTrainingTable() {
        return trainingTable;
    }

    public ArrayList<double[]> getValidationTable() {
        return validationTable;
    }
}
