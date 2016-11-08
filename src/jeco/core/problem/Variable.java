package jeco.core.problem;

public class Variable<T> {
    protected T value;

    public Variable(T value) {
        this.value = value;
    }

    public T getValue() { return value; }

    public void setValue(T value) { this.value = value; }
    
    @Override
    public Variable<T> clone() {
        return new Variable<T>(value);
    }

    @SuppressWarnings("unchecked")
		@Override
    public boolean equals(Object right) {
        Variable<T> var = (Variable<T>)right;
        return this.value.equals(var.value);
    }
}
