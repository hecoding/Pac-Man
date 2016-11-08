/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeco.core.optimization.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 *
 * @author jlrisco
 */
public class Worker<V extends Variable<?>> extends Thread {
	private static final Logger logger = Logger.getLogger(Worker.class.getName());
	
	protected Problem<V> problem;
	protected LinkedBlockingQueue<Solution<V>> sharedQueue = null;
	

	public Worker(Problem<V> problem, LinkedBlockingQueue<Solution<V>> sharedQueue) {
		this.problem = problem;
		this.sharedQueue = sharedQueue;
	}
	
	public void run() {
		Solutions<V> solutions = new Solutions<V>();
		while(!sharedQueue.isEmpty()) {
				try {
					Solution<V> solution = sharedQueue.poll(3, TimeUnit.SECONDS);
					if(solution!=null) {
						solutions.add(solution);
						problem.evaluate(solutions);
						solutions.clear();
					}
				} catch (InterruptedException e) {
					logger.severe(e.getLocalizedMessage());
					logger.severe("Thread " + super.getId() + " has been interrupted. Shuting down ...");
					break;
				}
		}
	}
}
