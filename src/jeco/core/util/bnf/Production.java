package jeco.core.util.bnf;

import java.util.ArrayList;

public class Production extends ArrayList<Symbol> {

	private static final long serialVersionUID = 1L;
	// Variables
    protected boolean recursive; // Recursive nature of production
    protected int minimumDepth; // Minimum depth of parse tree for production to map to terminal symbol(s)
    
    /*public Production(int newLength){
        super(newLength);
        setRecursive(false);
        setMinimumDepth(Integer.MAX_VALUE>>1);
    }*/
    
    public Production(){
        super();
    }
    
    public Production clone() {
    	Production clone = new Production();
    	for(Symbol symbol : this) {
    		clone.add(symbol.clone());
    	}
    	clone.recursive = recursive;
    	clone.minimumDepth = minimumDepth;
    	return clone;
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for(int i = 0;i<this.size();i++) {
        	Symbol symbol = this.get(i);
            buffer.append(symbol.symbolString);
        }
        return buffer.toString();
    }
    
/*
     public Production(Production copy){
 
        super(copy);
        this.recursive = copy.recursive;
        this.minimumDepth = copy.minimumDepth;
    }*/
    
/*    public boolean getRecursive() {
        return recursive;
    }
    
    public void setRecursive(boolean newRecursive){
        recursive = newRecursive;
    }*/
    
    /*
     
    public int getMinimumDepth() {    
        return minimumDepth;
    }*/
    
    /*public void setMinimumDepth(int newMinimumDepth){
        minimumDepth = newMinimumDepth;
    }*/
    
    /*public int getNTSymbols() {
        int cnt = 0;
        for (Symbol o : this) {
            if (o.type == Symbol.SYMBOL_TYPE.NT_SYMBOL) {
                cnt++;
            }
        }
        return cnt;
    }*/
    
    /*@Override
    @SuppressWarnings({"ForLoopReplaceableByForEach"})
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i = 0;i<this.size();i++) {
            s.append(this.get(i).getSymbolString());
        }
        return s.toString();
    }*/
}