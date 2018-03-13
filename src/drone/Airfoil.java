package drone;

import org.lwjgl.util.vector.Vector3f;

public class Airfoil extends DronePart {

	private float inclination;
	private boolean vertical;	
	private Vector3f attackVector = new Vector3f(0,0,0);
	private float liftslope;
	
	
	public Airfoil(float inclination, float mass, boolean vertical, float liftslope,Vector3f relativePosition,Drone drone) {		
		setInclination(inclination);
		setMass(mass);
		setVertical(vertical);
		setLiftslope(liftslope);
		setAttackVector(inclination);
		setDrone(drone);
		setRelativePosition(relativePosition);
		
	}
	
	public Vector3f getLiftForce() {
		Vector3f normal = fysica.crossProduct(this.getAxisVector(),this.getAttackVector());
		Vector3f airspeed = this.getVelocityAirfoil();
		Vector3f axis = this.getAxisVector();
			
		Vector3f projectedAirspeed = fysica.sum(airspeed,fysica.product(-1*fysica.scalarProduct(axis, airspeed)/axis.lengthSquared(), axis));
		
		float angleOfAttack = getAngleOfAttack();
		
		float speedSquared = projectedAirspeed.lengthSquared();
		
		Vector3f liftForce = fysica.product((float)(angleOfAttack * speedSquared), 
				fysica.product(this.getLiftSlope(),normal));
		
		System.out.println("Airfoil lift force" + liftForce);
		return liftForce;
	}
	
	public float getAngleOfAttack() {
		Vector3f normal = this.fysica.crossProduct(this.getAxisVector(),this.getAttackVector());
		Vector3f airspeed = this.getVelocityAirfoil();
		Vector3f axis = this.getAxisVector();
		
		Vector3f projectedAirspeed = fysica.sum(airspeed,fysica.product(-1*fysica.scalarProduct(axis, airspeed)/axis.lengthSquared(), axis));
		
		float angleOfAttack = (float) -Math.atan2(fysica.scalarProduct(projectedAirspeed,normal), 
				fysica.scalarProduct(projectedAirspeed,this.getAttackVector()));
		
		return angleOfAttack;
	}
	
	private Vector3f getVelocityAirfoil() {
		Vector3f angularVelocity = this.getDrone().getState().getAngularRotation();
		Vector3f relativeDistance = fysica.convertToWorld(this.getDrone(), this.getRelativePosition());
		Vector3f relSpeed = new Vector3f();
		Vector3f.cross(angularVelocity, relativeDistance, relSpeed);
		
		Vector3f droneCenterVel = this.getDrone().getState().getVelocity();
		
		return fysica.sum(relSpeed, droneCenterVel);
	}
	
	@Override
	public Vector3f getDronePartForce(){
		return this.getLiftForce();		
	}
	
	public Vector3f getAxisVector() {
		Vector3f axisVector = new Vector3f(0,0,0);
		if (!isVertical()) {
			axisVector.x = 1;
			axisVector.y = 0;
			axisVector.z = 0;
		}
		else { 
			axisVector.x = 0;
			axisVector.y = 1;
			axisVector.z = 0;
		}
		return axisVector;
	}
	
	public void setAttackVector(double incl) {
		if (!isVertical()) { 
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
	
	public void setInclination(float incl) {
		this.inclination = fysica.clean(incl);
	}
	
	public float getInclination() {
		return this.inclination;
	}
	
	public void updateInclinationAngle(float angle){
		setInclination(angle);
		this.setAttackVector(this.inclination);
	}
	
	public void setInclinationAngle(float angle) {
		setInclination(angle);
		this.setAttackVector(this.inclination);
	}
	
	public void setVertical(boolean newVertical){
		this.vertical = newVertical;
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
