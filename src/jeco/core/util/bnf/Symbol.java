package jeco.core.util.bnf;

public class Symbol {
	public static enum SYMBOL_TYPE {
		NT_SYMBOL, T_SYMBOL
	};

	// Variables
	protected SYMBOL_TYPE type; // Symbol type
	protected String symbolString;

	/*
	 * public Symbol() { this.type = SYMBOL_TYPE.T_SYMBOL; this.symbolString = "";
	 * }
	 */

	public Symbol(String symbolString, SYMBOL_TYPE type) {
		this.type = type;
		this.symbolString = symbolString;
	}

	public Symbol(SYMBOL_TYPE type) {
		this("", type);
	}

	public Symbol() {
		this(Symbol.SYMBOL_TYPE.T_SYMBOL);
	}

	public Symbol clone() {
		Symbol clone = new Symbol(this.symbolString, this.type);
		return clone;
	}

	public boolean equals(Symbol right) {
		return symbolString.equals(right.symbolString) && (type == right.type);
	}

	public boolean isTerminal() {
		return type == SYMBOL_TYPE.T_SYMBOL;
	}

	/*
	 * public boolean equals(Symbol newSymbol) { //Check the symbolString and the
	 * type return (getSymbolString().equals(newSymbol.getSymbolString())) &&
	 * (getType() == newSymbol.getType()); }
	 * 
	 * public boolean equals(String newSymbol) { //Check the symbolString and the
	 * type return (getSymbolString().equals(newSymbol)); }
	 */

	/*
	 * public void clear() { this.symbolString = null; this.type = null; }
	 */

	public String toString() {
		return this.symbolString;
	}
}