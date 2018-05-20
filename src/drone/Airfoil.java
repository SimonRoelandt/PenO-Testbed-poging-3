package drone;

import org.lwjgl.util.vector.Vector3f;

import Physics.DronePhysics;

import java.lang.*;

public class Airfoil extends DronePart {

	private float inclination;
	private boolean vertical;	
	private Vector3f attackVector = new Vector3f(0,0,0);
	private float liftslope;
	DronePhysics fysica;
	public Vector3f liftForce = new Vector3f(0,0,0);
	
	
	public Airfoil(float inclination, float mass, boolean vertical, float liftslope,Vector3f relativePosition,Drone drone) {		
		setDrone(drone);

		setInclination(inclination);
		setMass(mass);
		setVertical(vertical);
		setLiftslope(liftslope);
		setAttackVector(inclination);
		setRelativePosition(relativePosition);
		fysica = this.getDrone().p;

		
	}
	
	public Vector3f getLiftForce() {
		Vector3f normal = fysica.crossProduct(this.getAxisVector(),this.getAttackVector());
		//airspeed = this.getDrone().p.convertToDroneCords(getDrone(), airspeed);
		Vector3f projectedAirspeed = this.getProjectedAirspeed();
				
		float angleOfAttack = getAngleOfAttack();
		
		float speedSquared = Math.min(10e2f, projectedAirspeed.lengthSquared());
		speedSquared = projectedAirspeed.lengthSquared(); 
		
		this.getDrone().p.print("OUTPUT LIFT FORCE:", 3000);
		this.getDrone().p.print("AOA: " + angleOfAttack, 3000);
		this.getDrone().p.print("Speedsquared: " + speedSquared, 3000);

		Vector3f liftForce = this.getDrone().p.product((float)(angleOfAttack *speedSquared * this.getLiftSlope()), 
				normal);

		return liftForce;
	}
	
	public Vector3f getProjectedAirspeed() {
		Vector3f airspeed = this.getVelocityAirfoil();
		Vector3f axis = this.getAxisVector();
		axis = this.getDrone().p.convertToWorld(getDrone(), axis);
			
		Vector3f projectedAirspeed = fysica.sum(airspeed,fysica.product(-1*fysica.scalarProduct(axis, airspeed)/axis.lengthSquared(), axis));
		return projectedAirspeed;
	}
	
	public float getAngleOfAttack() {
		Vector3f normal = this.getDrone().p.crossProduct(this.getAxisVector(),this.getAttackVector());
		Vector3f normalInWorld = this.getDrone().p.convertToWorld(getDrone(), normal);
		
		Vector3f attackVector = this.getAttackVector();
		Vector3f attackVectorInWorld = this.getDrone().p.convertToWorld(getDrone(), attackVector);
		
		Vector3f projectedAirspeed = this.getProjectedAirspeed();
		
		float angleOfAttack = (float) -Math.atan2(this.getDrone().p.scalarProduct(projectedAirspeed,normalInWorld), 
				this.getDrone().p.scalarProduct(projectedAirspeed,attackVectorInWorld));
		
		return angleOfAttack;
	}
	
	private Vector3f getVelocityAirfoil() {
		Vector3f angularVelocity = this.getDrone().getState().getAngularRotation();
		Vector3f relativeDistance = this.getDrone().p.convertToWorld(this.getDrone(), this.getRelativePosition());
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
		return attackVector;
	}
	
	public void setInclination(float incl) {
		this.inclination = this.getDrone().p.clean(incl);
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