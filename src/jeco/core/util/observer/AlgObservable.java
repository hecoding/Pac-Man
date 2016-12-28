package jeco.core.util.observer;

import java.util.ArrayList;
import java.util.Observer;

public class AlgObservable {
	protected ArrayList<AlgObserver> observers;
	
	public AlgObservable() {
		this.observers = new ArrayList<AlgObserver>();
	}
	
	public void notifyObservers(int n) {
		for (AlgObserver obs : this.observers) {
			obs.onIncrement(n);
		}
	}
	
	public void notifyStart() {
		for (AlgObserver obs : this.observers) {
			obs.onStart();
		}
	}
	
	public void notifyEnd() {
		for (AlgObserver obs : this.observers) {
			obs.onEnd();
		}
	}
	
	public void addObserver(AlgObserver o) {
		this.observers.add(o);
	}
	
	public void removeObserver(AlgObserver o) {
		this.observers.remove(o);
	}
	
	public ArrayList<AlgObserver> getObservers() {
		return this.observers;
	}
	
	public void setObservers(ArrayList<AlgObserver> observers) {
		this.observers = observers;
	}
	
	public int countObservers() {
		return this.observers.size();
	}
	
	public void addObserver(Observer o) {}
	public void notifyObservers() {}
	public void notifyObservers(Object arg) {}
	public void setChanged() {}
	
}
