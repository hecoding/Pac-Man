package model.chromosome.exception;

public class IllegalChromosomeException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	
	public IllegalChromosomeException(String s) {
		super(s);
	}
	
	public IllegalChromosomeException(Class<?> currentChromosome, Class<?> expectedChromosome) {
		super(extractName(currentChromosome) + " can't perform the action as a " + extractName(expectedChromosome));
	}

	private static String extractName(Class<?> s) {
		String[] name = s.toString().split("\\.");
		
		return name[name.length - 1];
	}
}
