package drone;

import org.lwjgl.util.vector.Vector3f;

import fysica.Fysica;

public class Engine implements DroneObject{

	private float thrust;
	private float mass;
	private Vector3f gravation;
	private Vector3f position;
	private float maxThrust; //Misschien nog aanpassen
	
	private Fysica fysica;
	
	public Engine(double thrust, double mass, Vector3f pos) {
		this.thrust = (float) thrust;
		this.mass = (float) mass;
		this.position = pos;
		fysica = new Fysica();
		this.gravation = fysica.gravitationForce(this);
		this.maxThrust = 5000; //Misschien nog aanpassen
	}
	
	public Vector3f getThrust() {
		Vector3f v1 = new Vector3f(0, 0 ,(float) - this.thrust);
		return v1;
	}
	
	public float getThrustScalar() {
		return this.thrust;
	}
	
	public float getMass() {
		return this.mass;
	}
	
	@Override
	public Vector3f getTotalForce() {
		return fysica.totalForce(this);
	}

	@Override
	public Vector3f getGraviation() {
		return fysica.gravitationForce(this); 
	}
	
	public void setPos(Vector3f pos) {
		this.position = pos;
	}
	
	public Vector3f getPos() {
		return this.position;
	}

	public void setThrust(float thrust) {
		this.thrust = thrust;
	}
	
	public float getMaxThrust() {
		return this.maxThrust;
	}

	
}
