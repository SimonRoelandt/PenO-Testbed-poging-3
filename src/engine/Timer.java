package engine;

public class Timer {

    private double lastLoopTime;
    private float elapTime = 0.0f;
    private double firstLoopTime;
    
    public void init() {
        lastLoopTime = getTime();
        firstLoopTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        //elapTime = elapTime + elapsedTime;
        lastLoopTime = time;
        System.out.println("ELAPPP" + elapsedTime);
        return elapsedTime;
    }
    
    public float getTot() {
    	//return elapTime;
    	return (float) (getTime() - firstLoopTime);
    }

    public double getLastLoopTime() {
        return lastLoopTime;
    }
}
