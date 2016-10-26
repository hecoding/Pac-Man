package util;

import java.util.Random;

public class RandGenerator extends Random {
	private static final long serialVersionUID = 1L;
	private static RandGenerator instance;
	
	private RandGenerator() {}
	
	public static RandGenerator getInstance() {
		if (instance == null){
			instance = new RandGenerator();
		}
		return instance;
	}
}
