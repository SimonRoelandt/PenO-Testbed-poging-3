package drone;

import org.lwjgl.util.vector.Vector3f;

public class Airfoil extends DronePart {

	private float inclination;
	private boolean vertical;	
	private Vector3f axisVector = new Vector3f(0,0,0);
	private Vector3f attackVector = new Vector3f(0,0,0);
	private float liftslope;
	
	
	public Airfoil(
			double inclination, 
			double mass, 
			boolean vertical, 
			float liftslope,
			Vector3f relativePosition) {		
		
		this.inclination = (float) inclination;
		this.mass = (float) mass;
		this.setAxisVector(vertical);
		this.setLiftslope(liftslope);
		this.setAttackVector(inclination, vertical);
		
		this.setRelativePosition(relativePosition);

		
	}
	
	private Vector3f getLiftForce() {
		
		Vector3f normal = this.fysica.crossProduct(this.getAxisVector(),this.getAttackVector());
		Vector3f airspeed = this.getVelocityAirfoil();
		Vector3f axis = this.getAxisVector();
		
		Vector3f projectedAirspeed = airspeed;
		
		float angleOfAttack = (float) -Math.atan2(fysica.scalarProduct(projectedAirspeed,normal), 
				fysica.scalarProduct(projectedAirspeed,this.getAttackVector()));
		
		float speedSquared = airspeed.lengthSquared();
		
		Vector3f liftForce = fysica.product((float)(angleOfAttack * speedSquared), 
				fysica.product(this.getLiftSlope(),normal));
		
		fysica.print("liftforce is: " + liftForce, 4);
		
		//return new Vector3f(0,0,0);
		
		return liftForce;
	}
	
	private Vector3f getVelocityAirfoil() {
		// TODO Auto-generated method stub
		return this.getDrone().getVelocityInWorld();
	}

	public Vector3f getDronePartForce(){
		return this.getLiftForce();		
	}
	
	
	public void setInclination(float incl) {
		this.inclination = incl;
	}
	
	public float getInclination() {
		return this.inclination;
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
	
	public void updateInclinationAngle(float angle){
		this.inclination = angle;
		this.setAttackVector(this.inclination, vertical);
	}
	
	public void setInclinationAngle(float angle) {
		this.inclination = angle;
		this.setAttackVector(this.inclination, vertical);
	}
	
	public boolean isVertical() {
		return this.vertical; 
	}
	
	public void setLiftslope(float liftslope){
		this.liftslope = liftslope;
	}
	
	
	public float getLiftSlope() {
		return this.liftslope;
	}

	
	

}
