package jeco.core.operator.crossover;

import java.util.ArrayList;
import java.util.LinkedList;

import jeco.core.algorithm.moge.AbstractProblemGE;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class LHSCrossover extends CrossoverOperator<Variable<Integer>>
{
	private double _probability;
	private AbstractProblemGE _problem;
	
	public LHSCrossover(Problem<Variable<Integer>> problem, double probability)
	{
		_problem = (AbstractProblemGE)problem;
		_probability = probability;
	}

	@Override
	public Solutions<Variable<Integer>> execute(Solution<Variable<Integer>> parent1, Solution<Variable<Integer>> parent2)
	{
		Solutions<Variable<Integer>> children = new Solutions<Variable<Integer>>();
		
		//Check if we cross the parents
		if(RandomGenerator.nextDouble() <= _probability)
		{
			//Obtain the expansions array
			//Expansion array: array of integers representing the index of the rule that each codon expands in the BNF
			ArrayList<Integer> expansionParent1 = _problem.generateRuleExpansionArray(parent1);
			ArrayList<Integer> expansionParent2 = _problem.generateRuleExpansionArray(parent2);
			
			if(expansionParent1.isEmpty() || expansionParent2.isEmpty())
			{
				//No crossover, return the parents as children
				children.add(parent1.clone());
				children.add(parent2.clone());
			}
			else
			{			
				//Select a random point in the parent1
				int point_parent1 = RandomGenerator.nextInt(0, expansionParent1.size());
				//Select a random point in the parent2 and search for a coincidence with parent1 expansion element
				int point_parent2 = getParent2Point(expansionParent1.get(point_parent1), expansionParent2);			
				if(point_parent2 < 0)
				{
					//No crossover, return the parents as children
					children.add(parent1.clone());
					children.add(parent2.clone());
				}
				else
				{				
					//Create children
					Solution<Variable<Integer>> child1 = parent1.clone();
					Solution<Variable<Integer>> child2 = parent2.clone();
					
					int child1_expansionNumber = _problem.getNumberOfNodesBeforeTerminal(parent1, point_parent1, expansionParent1.get(point_parent1));
					int child2_expansionNumber = _problem.getNumberOfNodesBeforeTerminal(parent2, point_parent2, expansionParent2.get(point_parent2));
					
					//Use queues to create easily the new children
					LinkedList<Variable<Integer>> queue1 = new LinkedList<>();
					LinkedList<Variable<Integer>> queue2 = new LinkedList<>();			
					for(int i = 0; i < point_parent1; i++)
					{
						//Change this as its O(n)
						queue1.add(child1.getVariables().get(0));
						child1.getVariables().remove(0);
					}
					for(int i = 0; i < point_parent2; i++)
					{
						//Change this as its O(n)
						queue2.add(child2.getVariables().get(0));
						child2.getVariables().remove(0);
					}
					//Do crossover
					for(int i = 0; i < child2_expansionNumber; i++)
					{
						//Change this as its O(n)
						queue1.add(child2.getVariables().get(0));
						child2.getVariables().remove(0);
					}
					for(int i = 0; i < child1_expansionNumber; i++)
					{
						//Change this as its O(n)
						queue2.add(child1.getVariables().get(0));
						child1.getVariables().remove(0);
					}
					//Copy the rest
					queue1.addAll(child1.getVariables());
					queue2.addAll(child2.getVariables());
					
					child1.getVariables().clear();
					child2.getVariables().clear();
					child1.getVariables().addAll(queue1);
					child2.getVariables().addAll(queue2);
					
					//Check if the new children length gets out of the maximum number of variables
					ArrayList<Integer> expansionParent11 = _problem.generateRuleExpansionArray(child1);
					ArrayList<Integer> expansionParent22 = _problem.generateRuleExpansionArray(child2);					
					if(expansionParent11.size() <= _problem.getNumberOfVariables())
					{
						while(child1.getVariables().size() > _problem.getNumberOfVariables())
						{
							child1.getVariables().remove(child1.getVariables().size() - 1);
						}
					}
					else
					{
						child1 = parent1.clone();
					}
					
					if(expansionParent22.size() <= _problem.getNumberOfVariables())
					{
						while(child2.getVariables().size() > _problem.getNumberOfVariables())
						{
							child2.getVariables().remove(child2.getVariables().size() - 1);
						}
					}
					else
					{
						child2 = parent2.clone();
					}
					
					
					children.add(child1);
					children.add(child2);
				}
			}
			
		}
		else
		{
			//No crossover, return the parents as childs
			children.add(parent1.clone());
			children.add(parent2.clone());
		}
		
		return children;
	}

	//Returns the index of the first coincidence of the element in the arraylist, starting from a random point
	private int getParent2Point(Integer element, ArrayList<Integer> expansionParent2)
	{
		int point = RandomGenerator.nextInt(0, expansionParent2.size());
		
		//Go to the right of the point in search of a coincidence
		for(int i = point; i < expansionParent2.size(); i++)
		{
			if(element == expansionParent2.get(i))
			{
				return i;
			}
		}
		
		//Couldn't find in the right, find to the left
		for(int i = point; i >= 0; i--)
		{
			if(element == expansionParent2.get(i))
			{
				return i;
			}
		}
		
		
		//Element not inside the array
		return -1;
	}

}
