package jeco.core.operator.selection;

import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public abstract class SelectionOperator<T extends Variable<?>> {
    abstract public Solutions<T> execute(Solutions<T> solutions);
}
