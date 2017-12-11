package engine;

public class Timer {

	private double firstLoopTime;
    private double lastLoopTime;
    private float elapTime;
    private boolean first = false;
    
    public void init() {
    	firstLoopTime = getTime();
        lastLoopTime = getTime();
        first = true;
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - firstLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }
    
    public float getTot() {
    	return elapTime;
    }

    public double getLastLoopTime() {
    	double time = getTime();
        return (float) (time - lastLoopTime);
    }
}