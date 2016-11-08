package jeco.core.util.random;

import java.util.Random;

public class RandomGenerator {
	protected static Random randomGenerator = new Random();
	
	public static void setSeed(long seed) {
		randomGenerator.setSeed(seed);
	}
	
	public static double nextDouble() {
		return randomGenerator.nextDouble();
	}

	public static double nextDouble(double lowerBound, double upperBound) {
		return lowerBound + (upperBound-lowerBound)*randomGenerator.nextDouble();
	}

	public static int nextInt(int lowerBound, int upperBound) {
            if ((upperBound-lowerBound) <= 0) {
                return 0;
            } else {
		return lowerBound + randomGenerator.nextInt(upperBound-lowerBound);
            }
	}
	
		public static int nextInteger(int lowerBound, int upperBound) {
			return nextInt(lowerBound, upperBound);
		}
	
	public static int nextInt(int upperBound) {
		return randomGenerator.nextInt(upperBound);
	}
	
		public static int nextInteger(int upperBound) {
			return nextInt(upperBound);
		}

		public static boolean nextBoolean() {
		return randomGenerator.nextBoolean();
	}

	public static int[] intPermutation(int length){
		int[] aux    = new int[length];
		int[] result = new int[length];

		// First, create an array from 0 to length - 1. 
		// Also is needed to create an random array of size length
		for (int i = 0; i < length; i++) {
			result[i] = i;
			aux[i] = RandomGenerator.nextInt(0,length);
		} // for

		// Sort the random array with effect in result, and then we obtain a
		// permutation array between 0 and length - 1
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				if (aux[i] > aux[j]) {
					int tmp;
					tmp = aux[i];
					aux[i] = aux[j];
					aux[j] = tmp;
					tmp = result[i];
					result[i] = result[j];
					result[j] = tmp;
				} // if
			} // for
		} // for

		return result;
	}// intPermutation

}
