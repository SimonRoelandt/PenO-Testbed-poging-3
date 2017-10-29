package drone;

import org.lwjgl.util.vector.Vector3f;

import fysica.Fysica;

public class Airfoil implements DroneObject {

	private float inclination;
	private float mass;
	private Vector3f velocity = new Vector3f(0,0,100);
	private int vertical;
	
	private Vector3f axisVector = new Vector3f(0,0,0);
	private Vector3f attackVector = new Vector3f(0,0,0);
	
	private Fysica fysica;
	
	public Airfoil(double inclination, double mass, int vertical) {
		this.inclination = (float) inclination;
		this.mass = (float) mass;
		this.fysica = new Fysica();
		setAxisVector(vertical);
		setAttackVector(inclination, vertical);
	}
	
	public Vector3f getTotalForce() {
		return fysica.totalForce(this);
	}
	
	public float getMass() {
		return this.mass;
	}
	
	public void setInclination(float incl) {
		this.inclination = incl;
	}
	
	public float getInclination() {
		return this.inclination;
	}

	@Override
	public Vector3f getGraviation() {
		return fysica.gravitationForce(this);
	}
	
	public void setAxisVector(int vertical) {
		if (vertical == 0) {
			axisVector.x = 1;
			axisVector.y = 0;
			axisVector.z = 0;
		}
		else {
			axisVector.x = 0;
			axisVector.y = 1;
			axisVector.z = 0;
		}
	}
	
	public Vector3f getAxisVector() {
		return this.axisVector;
	}
	
	
	
	public void setAttackVector(double incl, int vertical) {
		if (vertical == 0) { 
			attackVector.x = (float) Math.sin(0);
			attackVector.y = (float) Math.sin(incl);
			attackVector.z = (float) -Math.cos(incl);
		}
		else {
			attackVector.x = (float) Math.sin(incl);
			attackVector.y = (float) Math.sin(0);
			attackVector.z = (float) -Math.cos(incl);
		}
	}
	
	public Vector3f getAttackVector() {
		return this.attackVector;
	}
	
	public void setVelocityAirfoil(Vector3f vel) {
		this.velocity = vel;
	}
	
	public Vector3f getVelocityAirfoil() {
		return this.velocity;
	}
	
	public void setInclinationAngle(float angle) {
		this.inclination = angle;
	}
	
	public boolean isVertical() {
		if (this.vertical == 1) {
			return true;
		}
		else return false;
	}
	
	

}
