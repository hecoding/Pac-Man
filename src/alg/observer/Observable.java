package model.observer;

public interface Observable<T> {
	public void addObserver(T o);
	public void removeObserver(T o);
	public void makeIncrement();
}
