package model.observer;

public interface GeneticAlgorithmObserver {
	public void onStartRun();
	public void onEndRun();
	public void onIncrement(int n);
}
