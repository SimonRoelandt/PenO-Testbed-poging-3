package drone;

import fysica.Fysica;

public class Engine implements DroneObject{

	private float thrust;
	private float mass;
	private float[] gravation;
	private float maxThrust; //Misschien nog aanpassen
	
	private Fysica fysica;
	
	public Engine(double thrust, double mass) {
		this.thrust = (float) thrust;
		this.mass = (float) mass;
		fysica = new Fysica();
		this.gravation = fysica.gravitationForce(this);
		this.maxThrust = 5000; //Misschien nog aanpassen
	}
	
	public float[] getThrust() {
		float[] v1 = {0, 0 ,(float) - this.thrust};
		return v1;
	}
	
	public float getThrustScalar() {
		return this.thrust;
	}
	
	public float getMass() {
		return this.mass;
	}
	
	@Override
	public float[] getTotalForce() {
		return fysica.totalForce(this);
	}

	@Override
	public float[] getGraviation() {
		return fysica.gravitationForce(this); 
	}

	public void setThrust(float thrust) {
		this.thrust = thrust;
	}
	
	public float getMaxThrust() {
		return this.maxThrust;
	}

	
}
