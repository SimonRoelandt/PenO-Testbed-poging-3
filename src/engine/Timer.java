package engine;

public class Timer {

    private double lastLoopTime;
    private float elapTime;
    
    public void init() {
        lastLoopTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        elapTime = elapTime + elapsedTime;
        lastLoopTime = time;
        return elapsedTime;
    }
    
    public float getTot() {
    	return elapTime;
    }

    public double getLastLoopTime() {
        return lastLoopTime;
    }
}