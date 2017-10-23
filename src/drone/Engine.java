package drone;

import fysica.Fysica;

public class Engine implements DroneObject{

	private double thrust;
	private double mass;
	private float[] gravation;
	
	private Fysica fysica;
	
	public Engine(double thrust, double mass) {
		this.thrust = thrust;
		this.mass = mass;
		fysica = new Fysica();
		this.gravation = fysica.gravitationForce();
	}
	
	public float[] getThrust() {
		float[] v1 = {0, 0 ,(float) - this.thrust};
		return v1;
	}
	
	public double getMass() {
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

	
}
