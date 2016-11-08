package jeco.core.util.bnf;

import java.util.ArrayList;

public class Rule extends ArrayList<Production> {

	private static final long serialVersionUID = 1L;
	//Variables
    protected boolean recursive = false;// Recursive nature of rule
    protected int minimumDepth = Integer.MAX_VALUE>>1;	// Minimum depth of parse tree for production to map to terminal symbol(s)
    protected Symbol lhs = null; //Left hand side symbol of the rule
    
    public Rule(){
        super();
    }
    
    public Rule clone() {
    	Rule clone = new Rule();
    	for(Production production : this) {
    		clone.add(production.clone());
    	}
    	clone.lhs = this.lhs.clone();
    	clone.recursive = this.recursive;
    	clone.minimumDepth = this.minimumDepth;
    	return clone;
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(lhs.symbolString);
        buffer.append("::=");
        for(int i=0;i<this.size();i++) {
        	Production production = this.get(i);
            buffer.append(production.toString());
            if(i<(this.size()-1)) {
                buffer.append("|");
            }
        }
        return buffer.toString();
    }
    /*    public Rule(Rule copy){
        super(copy);
        this.lhs = copy.lhs;
        this.recursive = copy.recursive;
        this.minimumDepth = copy.minimumDepth;
    }*/
    
/*    public boolean getRecursive() {
        return recursive;
    }*/
    
/*    public void setRecursive(boolean newRecursive){
        recursive=newRecursive;
    }*/
    
/*    public int getMinimumDepth() {
        return minimumDepth;
    }*/
    
/*    public void setMinimumDepth(int newMinimumDepth){
        minimumDepth=newMinimumDepth;
    }*/

/*    public void setLHS(Symbol s) {
        this.lhs = s;
    }*/

/*    public Symbol getLHS() {
        return this.lhs;
    }*/

    
    
}