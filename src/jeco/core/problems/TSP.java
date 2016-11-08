package jeco.core.problems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * TSP.java
 *
 * @author José L. Risco-Martín
 * @version 1.0
 */
/**
 * Class representing a TSP (Traveling Salesman Problem) problem.
 */
public class TSP extends Problem<Variable<Integer>> {

    private static Logger logger = Logger.getLogger(TSP.class.getName());
    protected String xmlFilePath = null;
    public int numberOfCities;
    public double[][] distanceMatrix;
    public ArrayList<String> cityNames;

    /**
     * Creates a new TSP problem instance. It accepts data files from TSPLIB
     * @param filename The file containing the definition of the problem
     */
    public TSP(String xmlFilePath) {
        super(1, 1);
        this.xmlFilePath = xmlFilePath;
        try {
            readProblem(xmlFilePath);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    } // TSP
    
    public void evaluate(Solutions<Variable<Integer>> solutions) {
    	for(Solution<Variable<Integer>> solution : solutions)
    		evaluate(solution);
    }

    /**
     * Evaluates a solution
     * @param solution The solution to evaluate
     */
    public void evaluate(Solution<Variable<Integer>> solution) {
        double fitness;

        fitness = 0.0;

        for (int i = 0; i < (numberOfCities - 1); i++) {
            int x;
            int y;

            x = solution.getVariables().get(i).getValue();
            y = solution.getVariables().get(i + 1).getValue();
            fitness += distanceMatrix[x][y];
        } // for

        int firstCity;
        int lastCity;

        firstCity = solution.getVariables().get(0).getValue();
        lastCity = solution.getVariables().get(numberOfCities - 1).getValue();

        fitness += distanceMatrix[firstCity][lastCity];

        solution.getObjectives().set(0, fitness);
    } // evaluate

    public void readProblem(String xmlFilePath) throws Exception {
        /**
         * <tsp>
         *	<city name="Madrid"/>>
         *  <city name="Siruela"/>
         * </tsp>
         */
        Document xmlTspModel = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlFilePath));
        Element xmlTsp = (Element) xmlTspModel.getElementsByTagName("tsp").item(0);

        HashSet<String> cityNamesAsSet = new HashSet<String>();
        NodeList xmlDistances = xmlTsp.getElementsByTagName("distance");
        for (int i = 0; i < xmlDistances.getLength(); ++i) {
            Element xmlDistance = (Element) xmlDistances.item(i);
            cityNamesAsSet.add(xmlDistance.getAttribute("from"));
            cityNamesAsSet.add(xmlDistance.getAttribute("to"));
        }

        numberOfCities = cityNamesAsSet.size();
        distanceMatrix = new double[numberOfCities][numberOfCities];
        cityNames = new ArrayList<String>();
        int counter = 0;
        HashMap<String, Integer> cityIndexes = new HashMap<String, Integer>();
        for (String cityName : cityNamesAsSet) {
            cityIndexes.put(cityName, counter++);
            cityNames.add(cityName);
        }

        for (int i = 0; i < xmlDistances.getLength(); ++i) {
            Element xmlDistance = (Element) xmlDistances.item(i);
            String cityFrom = xmlDistance.getAttribute("from");
            Integer indexFrom = cityIndexes.get(cityFrom);
            String cityTo = xmlDistance.getAttribute("to");
            Integer indexTo = cityIndexes.get(cityTo);
            Double distance = Double.valueOf(xmlDistance.getAttribute("distance"));
            distanceMatrix[indexFrom][indexTo] = distance;
        }

    } // readProblem

    public Solutions<Variable<Integer>> newRandomSetOfSolutions(int size) {
        logger.severe("Method not implemented");
        return null;
        // TODO: Finish this method.
    }

    public TSP clone() {
    	TSP clone = new TSP(this.xmlFilePath);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	clone.cityNames.addAll(cityNames);
    	for(int i=0; i<distanceMatrix.length; ++i) {
    		for(int j=0; j<distanceMatrix[i].length; ++j) {
    			clone.distanceMatrix[i][j] = this.distanceMatrix[i][j];
    		}
    	}
    	clone.numberOfCities = this.numberOfCities;    	
    	return clone;
    }
} // TSP

