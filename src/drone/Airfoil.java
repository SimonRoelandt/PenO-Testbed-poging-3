package drone;

import org.lwjgl.util.vector.Vector3f;

import fysica.Fysica;

public class Airfoil implements DroneObject {

	private float inclination;
	private float mass;
	private Vector3f velocity = new Vector3f(0,0,9);
	private boolean vertical;
	
	private Vector3f pos;
	
	private Vector3f axisVector = new Vector3f(0,0,0);
	private Vector3f attackVector = new Vector3f(0,0,0);
	
	private Fysica fysica;
	
	public Airfoil(double inclination, double mass, boolean vertical, Vector3f position) {
		this.inclination = (float) inclination;
		this.mass = (float) mass;
		this.fysica = new Fysica();
		setAxisVector(vertical);
		setAttackVector(inclination, vertical);
		this.pos = position;
	}
	
	public Vector3f getLiftForce() {
		return fysica.liftForce(this);
	}
	
	public Vector3f getTotalForce() {
		return fysica.totalForce(this);
	}
	
//	public Vector3f getNewVelocity() {
//		return fysica.velocity(drone, time)
//	}
	
	public Vector3f getPos() {
		return this.pos;
	}
	
	public void setPos(Vector3f pos) {
		this.pos = pos;
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
	
	public void setAxisVector(boolean vertical) {
		if (!vertical) {
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
	
	
	
	public void setAttackVector(double incl, boolean vertical) {
		if (!vertical) { 
			attackVector.x = (float) Math.sin(0);
			attackVector.y = (float) Math.sin(incl);
			attackVector.z = (float) -Math.cos(incl);
		}
		else {
			attackVector.x = (float) -Math.sin(incl);
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
		return this.vertical; 
	}
	
	
	public float getLiftSlope() {
		if (isVertical()) {
			return (float) 1.0;
		}
		return (float) 1.0;
	}
	
	

}
