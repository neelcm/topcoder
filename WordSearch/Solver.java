package neelcm.puzzle;

public interface Solver {

	public void registerObserver(SolverObserver obj);

	public void deregisterObserver(SolverObserver obj);

	public void notifyObservers();

	public Object getUpdate();
}