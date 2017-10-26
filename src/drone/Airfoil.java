package drone;

import fysica.Fysica;

public class Airfoil implements DroneObject {

	private float inclination;
	private float mass;
	
	private float[] axisVector = {0,0,0};
	private float[] attackVector = {0,0,0};
	
	private Fysica fysica;
	
	public Airfoil(double inclination, double mass, int vertical) {
		this.inclination = (float) inclination;
		this.mass = (float) mass;
		this.fysica = new Fysica();
		setAxisVector(vertical);
		setAttackVector(inclination, vertical);
	}
	
	public float[] getTotalForce() {
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
	public float[] getGraviation() {
		return fysica.gravitationForce(this);
	}
	
	public void setAxisVector(int vertical) {
		if (vertical == 0) {
			axisVector[0] = 1;
			axisVector[1] = 0;
			axisVector[2] = 0;
		}
		else {
			axisVector[0] = 0;
			axisVector[1] = 1;
			axisVector[2] = 0;
		}
	}
	
	public float[] getAxisVector() {
		return this.axisVector;
	}
	
	
	
	public void setAttackVector(double incl, int vertical) {
		if (vertical == 0) { 
			attackVector[0] = (float) Math.sin(0);
			attackVector[1] = (float) Math.sin(incl);
			attackVector[2] = (float) -Math.cos(incl);
		}
		else {
			float [] v = {(float) Math.sin(incl),(float) Math.sin(0), (float) -Math.cos(incl)};
			attackVector[0] = (float) Math.sin(incl);
			attackVector[1] = (float) Math.sin(0);
			attackVector[2] = (float) -Math.cos(incl);
		}
	}
	
	public float[] getAttackVector() {
		return this.attackVector;
	}
	
	

}
