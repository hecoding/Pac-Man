package jeco.core.util.bnf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class BnfReader {

    protected ArrayList<Rule> rules = new ArrayList<Rule>();

    public BnfReader() {
    }

    public boolean load(String pathToBnfFile) {
        boolean res = false;
        try {
            StringBuilder contents = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(new File(pathToBnfFile)));
            String line;
            while ((line = br.readLine()) != null) {
                contents.append(line);
                //readLine removes the line-separator from http://www.javapractices.com/Topic42.cjp
                contents.append(System.getProperty("line.separator"));
            }
            br.close();
            contents.append("\n");
            res = readBNFString(contents.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean readBNFString(String bnfString) {
        Rule newRule = new Rule(); // Used to create new rules for grammar
        boolean insertRule = false;// If newRule is to be inserted onto grammar
        Rule currentRule = null;// Used in pass 2 to add productions to current rule
        Production newProduction = new Production();// Used to create new productions for grammar
        Symbol newSymbol = new Symbol();// Used to create new symbols for grammar
        String symbolString;
        Symbol newTokenSeparator = new Symbol();// Used to create token separators for grammar
        int bnfString_size = bnfString.length();
        char currentChar;// Current char of input
        char separated = 0;// If there was a separator between previous token and current one
        boolean skip = false;// Skip an iteration on parser (for escaped newlines)
        boolean quoted = false;// If current char is quoted
        boolean non_terminal = false;// If current text is a non-terminal symbol
        StringBuffer currentBuffer = new StringBuffer(bnfString_size);// Buffer used to add new symbols to grammar
        // States of parser
        final int START = 0;
        final int START_RULE = 1;
        final int LHS_READ = 2;
        final int PRODUCTION = 3;
        final int START_OF_LINE = 4;
        int state = START;// Current state of parser

        int i;
        try {
            for (int pass = 0; pass < 2; pass++) { //Do 2 passes over the string
                i = 0;
                while (i < bnfString_size) {
                    if (i < bnfString_size) {
                        currentChar = bnfString.charAt(i);
                    } else { // Simulate presence of endl at end of grammar
                        currentChar = '\n';
                    }
                    if (bnfString.charAt(i) == '\\') { // Escape sequence
                        i++;
                        if (i >= bnfString_size) {// Escape sequence as last char is invalid
                            throw new Exception("Escape sequence as last char is invalid");
                        } else {
                            if ((non_terminal) && (bnfString.charAt(i) != '\n')) {
                                // Only escaped newline allowed inside non-terminal
                                throw new Exception("Only escaped newline allowed inside non-terminal");
                            }
                        }
                        if (bnfString.charAt(i) == '\'') {// Single quote
                            currentChar = '\'';
                        } else if (bnfString.charAt(i) == '\'') {// Double quote
                            currentChar = '\'';
                        } else if (bnfString.charAt(i) == '\\') {// Backslash
                            currentChar = '\\';
                        } else if (bnfString.charAt(i) == '0') {// Null character
                            currentChar = '\0';
                        } else if (bnfString.charAt(i) == 'a') {// Audible bell
                            currentChar = '\007';
                        } else if (bnfString.charAt(i) == 'b') {// Backspace
                            currentChar = '\b';
                        } else if (bnfString.charAt(i) == 'f') {// Formfeed
                            currentChar = '\f';
                        } else if (bnfString.charAt(i) == 'n') {// Newline
                            currentChar = '\n';
                        } else if (bnfString.charAt(i) == 'r') {// Carriage return
                            currentChar = '\r';
                        } else if (bnfString.charAt(i) == 't') {// Horizontal tab
                            currentChar = '\t';
                        } else if (bnfString.charAt(i) == 'v') {// Vertical tab
                            currentChar = '\013';
                        } else if (bnfString.charAt(i) == '\n') {// Escaped newline
                            skip = true;// Ignore newline
                        } else if (bnfString.charAt(i) == '\r') {// Escaped DOS return
                            skip = true;// Ignore newline
                            if (bnfString.charAt(++i) != '\n') {
                                throw new Exception("No newline");
                            }
                        } else {// Normal character
                            currentChar = bnfString.charAt(i);
                        }
                        if ((!skip) && (pass > 0)) {
                            if (currentBuffer.length() == 0) {//Empty
                                newSymbol = new Symbol(Symbol.SYMBOL_TYPE.T_SYMBOL);
                            }
                            currentBuffer.append(currentChar);
                        }
                    } else {
                        switch (state) {
                            case (START):
                                if (currentChar == '\r') {
                                    break;// Ignore DOS newline first char
                                }
                                if (currentChar == '#') {
                                    // this line is a comment in the grammar so skip to end of line
                                    while (i < bnfString_size && bnfString.charAt(i) != '\n') {
                                        //System.out.println("charAt:" + bnfString.charAt(i));
                                        i++;
                                    }
							// we have skipped to end of line, so exit the switch
                                    // next time round, it will see the "\n" (or "\r\n") at end of line
                                    break;
                                }
                                switch (currentChar) {
                                    case ' ':// Ignore whitespaces
                                    case '\t':// Ignore tabs
                                    case '\n':// Ignore newlines
                                        break;
                                    case '<':// START OF RULE
                                        newSymbol = new Symbol(Symbol.SYMBOL_TYPE.NT_SYMBOL);
                                        currentBuffer.append(currentChar);
                                        state = START_RULE;
                                        break;
                                    default: // Illigal
                                        throw new Exception("Illegal");
                                }
                                break;
                            case (START_RULE):// Read the lhs Non-terminal symbol
                                if (currentChar == '\r') {
                                    break;// Ignore DOS newline first char
                                }
                                switch (currentChar) {
                                    case '\n':// Newlines are illigal here
                                        throw new Exception("Newlines are illigal here");
                                    case '>': // Possible end of non-terminal symbol
                                        currentBuffer.append(currentChar);
                                        symbolString = currentBuffer.toString();
                                        if (pass == 0) {// First pass
                                            // Check if new symbol definition
                                            if (findRule(newSymbol) == null) {// Create new rule for symbol
                                                insertRule = true;//We will add the newRule to Grammar.Rules
                                                newRule.lhs = new Symbol(symbolString, Symbol.SYMBOL_TYPE.NT_SYMBOL);
                                            } else {
                                                insertRule = true;//We will not add a rule this time
                                            }
                                        } else {
								// Second pass
                                            // Point currentRule to previously defined rule
                                            currentRule = findRule(symbolString);
                                            if (currentRule == null) {
                                                throw new Exception("Current rule is null: " + symbolString);
                                            }
                                        }
                                        currentBuffer.delete(0, currentBuffer.length());// Reset the buffer
                                        state = LHS_READ;// lhs for this rule has been read
                                        break;
                                    default:// Check for non-escaped special characters
                                        if (((currentChar == '"') || (currentChar == '|') || (currentChar == '<'))) {
                                            throw new Exception("Non escaped special character");
                                        }
                                        currentBuffer.append(currentChar);
                                }
                                break;
                            case (LHS_READ):// Must read ::= token
                                if (currentChar == '\r') {
                                    break;// Ignore DOS newline first char
                                }
                                switch (currentChar) {
                                    case ' ':// Ignore whitespaces
                                    case '\t':// Ignore tabs
                                    case '\n':// Ignore newlines
                                        break;
                                    case ':':// Part of ::= token
                                        currentBuffer.append(currentChar);
                                        break;
                                    case '=':// Should be end of ::= token
                                        currentBuffer.append(currentChar);
                                        String s = currentBuffer.toString();
                                        if (s.compareTo("::=") != 0) {// Something other than ::= was read
                                            throw new Exception("Something other than ::= was read");
                                        }
                                        currentBuffer.delete(0, currentBuffer.length());
                                        // START OF PRODUCTION
                                        newProduction.clear();
                                        state = PRODUCTION;
                                        break;
                                    default: // Illigal
                                        throw new Exception("Illigal:" + currentChar);
                                }
                                break;
                            case (PRODUCTION):// Read everything until | token or \n, or EOL
                                if (currentChar == '\r') {
                                    break;// Ignore DOS newline first char
                                }
                                if (pass == 0) {
                                    if (currentChar == '\n') {
                                        state = START_OF_LINE;
                                    }
                                    break;
                                } else {
                                    switch (currentChar) {
                                        case '|':// Possible end of production
                                            if (quoted) {// Normal character
                                                currentBuffer.append(currentChar);
                                                break;
                                            }
                                        case '\n':// End of production (and possibly rule)
                                            separated = 0;// Reset separator marker
                                            if ((currentBuffer.length() != 0) || (newProduction.size() == 0)) {// There is a symbol to add
                                                if (currentBuffer.length() == 0) {
                                                    // No symbol exists; create terminal empty symbol
                                                    newSymbol.type = Symbol.SYMBOL_TYPE.T_SYMBOL;
                                                }
                                                if (non_terminal) {// Current non-terminal symbol isn't finished
                                                    symbolString = currentBuffer.toString();
                                                    throw new Exception("Current non-terminal symbol isn't finished: "+symbolString);
                                                }
                                                symbolString = currentBuffer.toString();
                                                newSymbol.symbolString = symbolString;
                                                if (newSymbol.type == Symbol.SYMBOL_TYPE.NT_SYMBOL) {
                                                    // Find rule that defines this symbol
                                                    Rule tempRule = findRule(newSymbol);
                                                    if (tempRule != null) {
                                                        newProduction.add(newSymbol.clone());
                                                    } else {// Undefined symbol, insert anyway
                                                        newProduction.add(newSymbol.clone());
                                                    }
                                                } else {// Add terminal symbol
                                                    newProduction.add(newSymbol.clone());
                                                }
                                                newSymbol.symbolString = null;
                                                newSymbol.type = null;
                                            }
								// END OF PRODUCTION
                                            // Add production to current rule
                                            currentRule.add(newProduction.clone());
                                            currentBuffer.delete(0, currentBuffer.length());// Reset the buffer
                                            if (currentChar == '\n') {
                                                state = START_OF_LINE;
                                            } else {
                                                // START OF PRODUCTION
                                                newProduction.clear();
                                            }
                                            break;
                                        case '<':// Possible start of non-terminal symbol
                                        case '>':// Possible end of non-terminal symbol
                                        case ' ':// Possible token separator
                                        case '\t':// Possible token separator
                                            if ((quoted) || (((currentChar == ' ') || (currentChar == '\t')) && (non_terminal))) {// Spaces inside non-terminals are accepted
                                                currentBuffer.append(currentChar);
                                                if (!non_terminal) {
                                                    newSymbol.type = Symbol.SYMBOL_TYPE.T_SYMBOL;
                                                }
                                                break;
                                            }
                                            if (currentChar == '>') {// This is also the end of a non-terminal symbol
                                                currentBuffer.append(currentChar);
                                                non_terminal = false;
                                            }
                                            if (currentBuffer.length() != 0) {
                                                if (non_terminal) {// Current non-terminal symbol isn't finished
                                                    symbolString = currentBuffer.toString();
                                                    throw new Exception("Current non-terminal symbol isn't finished: "+symbolString);
                                                }
                                                if ((currentChar == ' ') || (currentChar == '\t')) {// Token separator
                                                    separated = 1;
                                                }
                                                symbolString = currentBuffer.toString();
                                                newSymbol.symbolString = symbolString;
                                                if (newSymbol.type == Symbol.SYMBOL_TYPE.NT_SYMBOL) {
                                                    // Find rule that defines this symbol
                                                    Rule tempRule = findRule(newSymbol);
                                                    if (tempRule != null) {
                                                        newProduction.add(newSymbol.clone());
                                                    } else {
                                                        // Undefined symbol, insert anyway
                                                        newProduction.add(newSymbol.clone());
                                                    }
                                                } else {// Add terminal symbol
                                                    newProduction.add(newSymbol.clone());
                                                }
                                                newSymbol.symbolString = null;
                                                newSymbol.type = null;
                                            } else {// Empty buffer
                                                if (((currentChar == ' ') || (currentChar == '\t')) && (newProduction.size() != 0)) {
                                                    // Probably a token separator after a non-terminal symbol
                                                    separated = 1;
                                                }
                                            }
                                            currentBuffer.delete(0, currentBuffer.length());// Reset the buffer
                                            if (currentChar == '<') {// This is also the start of a non-terminal symbol
                                                // Special case; must create new Symbol here
                                                newSymbol.symbolString = null;
                                                newSymbol.type = Symbol.SYMBOL_TYPE.NT_SYMBOL;
                                                currentBuffer.append(currentChar);
                                                non_terminal = true;// Now reading a non-terminal symbol
                                                if (separated == '1') {// Insert a token separator
                                                    separated = 0;
                                                    newTokenSeparator.symbolString = " ";
                                                    newTokenSeparator.type = Symbol.SYMBOL_TYPE.T_SYMBOL;
                                                    newProduction.add(newTokenSeparator.clone());
                                                }
                                            }
                                            break;
                                        default: // Add character to current buffer
                                            if (separated == '1') {// Insert a token separator
                                                separated = 0;
                                                newTokenSeparator.symbolString = " ";
                                                newTokenSeparator.type = Symbol.SYMBOL_TYPE.T_SYMBOL;
                                                newProduction.add(newTokenSeparator.clone());
                                            }
                                            if (currentChar == '"') {// Start (or end) quoted section
                                                quoted = !quoted;
                                                newSymbol.type = Symbol.SYMBOL_TYPE.T_SYMBOL;
                                                break;
                                            }
                                            if (currentBuffer.length() == 0) {
                                                newSymbol.type = Symbol.SYMBOL_TYPE.T_SYMBOL;
                                            }
                                            currentBuffer.append(currentChar);
                                    }
                                    break;

                                }
                            case (START_OF_LINE):
                                if (currentChar == '#') {
                                    // this line is a comment in the grammar so skip to end of line
                                    while (i < bnfString_size && bnfString.charAt(i) != '\n') {
                                        //System.out.println("charAt:" + bnfString.charAt(i));
                                        i++;
                                    }
								// we have skipped to end of line, so exit the switch
                                    // next time round, it will see the "\n" (or "\r\n") at end of line
                                    break;
                                }
                                if (currentChar == '\r') {
                                    break;// Ignore DOS newline first char
                                }
                                switch (currentChar) {
                                    case ' ':// Ignore whitespaces
                                    case '\t':// Ignore tabs
                                    case '\n':// Ignore newlines
                                        break;
                                    case '|':// Start of new production
                                        state = PRODUCTION;
                                        if (pass == 1) {
                                            // START OF PRODUCTION
                                            newProduction.clear();
                                        }
                                        break;
                                    case '<':// Start of lhs non-terminal symbol
                                        // END OF RULE
                                        if (pass == 0) {
                                            // Add current rule
                                            if (insertRule) {
                                                rules.add(newRule.clone());
                                            }
                                        }
                                        // START OF RULE
                                        newSymbol.type = Symbol.SYMBOL_TYPE.NT_SYMBOL;
                                        currentBuffer.append(currentChar);
                                        state = START_RULE;
                                        break;
                                    default: // Illigal
                                        throw new Exception("Illigal:" + currentChar);
                                }
                                break;
                            default://Impossible error, quit the program now!
                                throw new Exception("Impossible error, quit the program now!");
                        }
                    }
                    skip = false;
                    i++;
                }
                // END OF PASS
                if (state != START_OF_LINE) {// This must be the state of the parser
                    throw new Exception("START_OF_LINE must be the state of the parser");
                }
                if (pass == 0) {
                    // Add current rule
                    if (insertRule) {
                        this.rules.add(newRule.clone());
                    }
                }
            }
            checkInfiniteRecursion();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
//			ex.printStackTrace();
            return false;
        }
        updateRuleFields();
        //genotype2Phenotype();
        return true;
    }

    public Rule findRule(Symbol symbol) {
        for (Rule rule : rules) {
            if (rule.lhs.equals(symbol)) {
                return rule;
            }
        }
        return null;
    }

    public Rule findRule(String symbolString) {
        for (Rule rule : rules) {
            if (rule.lhs.symbolString.equals(symbolString)) {
                return rule;
            }
        }
        return null;
    }

    public void checkInfiniteRecursion() throws Exception {
        for (Rule rule : rules) {
            if (isInfinitlyRecursive(rule)) {
                throw new Exception("Infinite recursion: " + rule.toString());
            }

        }
    }

    public boolean isInfinitlyRecursive(Rule startRule) throws Exception {
        LinkedList<Rule> rulesToVisit = new LinkedList<Rule>();
        ArrayList<Rule> visitedRules = new ArrayList<Rule>();

        Rule currentRule;

        rulesToVisit.add(startRule);
        while (!rulesToVisit.isEmpty()) {
            currentRule = rulesToVisit.remove();
            visitedRules.add(currentRule);
            for (Production production : currentRule) {
                for (Symbol symbol : production) {
                    if (symbol.type == Symbol.SYMBOL_TYPE.NT_SYMBOL
                            && !symbol.symbolString.startsWith("<JecoCodonValue")) {
                        currentRule = this.findRule(symbol);
                        if (currentRule == null) {
                            throw new Exception("No rule found for symbol " + symbol.toString());
                        }
                        if (!visitedRules.contains(currentRule)) {
                            rulesToVisit.add(currentRule);
                        }
                    } else {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    void updateRuleFields() {
        ArrayList<Rule> visitedRules = new ArrayList<Rule>();
        clearRuleFields();
        for (Rule rule : rules) {
            visitedRules.clear();
            rule.recursive = isRecursive(visitedRules, rule);
        }

        for (Rule rule : rules) {
            visitedRules.clear();
            calculateMinimumDepthRecursive(rule, visitedRules);
        }

        for (Rule rule : rules) {
            setProductionMinimumDepth(rule);
        }
    }

    public void clearRuleFields() {
        for (Rule rule : rules) {
            rule.minimumDepth = Integer.MAX_VALUE >> 1;
            rule.recursive = false;
        }
    }

    boolean isRecursive(ArrayList<Rule> visitedRules, Rule currentRule) {
        ArrayList<Production> prodIt;
        Rule definingRule;

        if (visitedRules.size() == 0) {
            prodIt = currentRule;
        } else {
            prodIt = visitedRules.get(visitedRules.size() - 1);
        }

        // Check if this is a recursive call to a previously visited rule
        if (visitedRules.contains(findRule(currentRule.lhs))) {
            currentRule.recursive = true;
            return true;
        }

        // Go through each production in the rule
        for (Production production : prodIt) {
            for (Symbol symbol : production) {
                if (symbol.type == Symbol.SYMBOL_TYPE.NT_SYMBOL) {
                    definingRule = findRule(symbol);
                    if (definingRule != null) {
                        if (!visitedRules.contains(definingRule)) {
                            visitedRules.add(definingRule);
                            if (isRecursive(visitedRules, currentRule)) {
                                production.recursive = true;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void calculateMinimumDepthRecursive(Rule startRule, ArrayList<Rule> visitedRules) {

        if (!visitedRules.contains(startRule)) {
            for (Production production : startRule) {
                production.minimumDepth = 0;
                for (Symbol symbol : production) {
                    if (symbol.type == Symbol.SYMBOL_TYPE.NT_SYMBOL) {
                        Rule currentRule = findRule(symbol);
                        if (currentRule != null) {
                            visitedRules.add(startRule);
                            calculateMinimumDepthRecursive(currentRule, visitedRules);
                            if (production.minimumDepth < (currentRule.minimumDepth + 1)) {
                                production.minimumDepth = currentRule.minimumDepth + 1;
                            }
                        }
                    } else {
                        if (production.minimumDepth < 1) {
                            production.minimumDepth = 1;
                        }
                    }
                }
                if (startRule.minimumDepth > production.minimumDepth) {
                    startRule.minimumDepth = production.minimumDepth;
                }
            }
        }
    }

    public void setProductionMinimumDepth(Rule rule) {
        int minDepth;
        for (Production production : rule) {
            minDepth = 0;
            for (Symbol symbol : production) {
                if (symbol.type == Symbol.SYMBOL_TYPE.NT_SYMBOL) {
                    Rule ruleAux = this.findRule(symbol);
                    if (ruleAux != null) {
                        if (ruleAux.minimumDepth > minDepth) {
                            minDepth = ruleAux.minimumDepth;
                        }
                    }
                }
            }
            production.minimumDepth = minDepth;
        }
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public static void main(String[] args) {
        BnfReader bnfReader = new BnfReader();
        bnfReader.load("test/grammar.bnf");
        for (Rule rule : bnfReader.rules) {
            System.out.println(rule.toString());
        }
    }

    //Returns the index of the symbol in the BNF
	public Integer indexOf(Symbol symbol) {
		int i = 0;
		for (Rule rule : rules) {
	        	if (rule.lhs.equals(symbol)) {
	            		return i;
	        	}
	        	i++;
		  }
	      	return null;
	}

}
