package jeco.core.algorithm.moge;

import java.util.LinkedList;

public class Phenotype extends LinkedList<String> {

	private static final long serialVersionUID = 1L;

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(String symbol : this) {
			buffer.append(symbol);
		}
		return buffer.toString();
	}
}
