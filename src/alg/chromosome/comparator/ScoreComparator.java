package model.chromosome.comparator;

import java.util.Comparator;

import model.chromosome.AbstractChromosome;

public class ScoreComparator implements Comparator<AbstractChromosome> {

	@Override
	public int compare(AbstractChromosome o1, AbstractChromosome o2) {
		double score = (o1.getScore() - o2.getScore());
		
		if (score > 0)
			return 1;
		else if (score < 0)
			return -1;
		else return 0;
	}

}
