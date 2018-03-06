package drone;

import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector3f;

public class Wheel extends DronePart {

	
	private float tyreslope;
	private float dampslope;
	private float radius;
	private float BrakeForce;
	private float maxWrijving;
	private float D;
	private float lastD;

	public Wheel(float radius, float tyreSlope , float dampSlope, float tyreRadius, float maxWrijving, Vector3f relativePosition, Drone drone) {		
		setDampslope(dampSlope);
		setTyreslope(tyreSlope);
		setDrone(drone);
		setRelativePosition(relativePosition);
		setRadius(radius);
		setMaxWrijvingsCoeff(maxWrijving);
		setBrakeForce(0); // standaard in de lucht
	}
	
	private void setD(float D) {
		this.D=D;
	}
	
	public float getD(float D ) {
		return this.D;
	}
	
	private void setLastD(float lastD) {
		this.lastD=lastD;
	}
	
	public float getLastD(float lastD ) {
		return this.lastD;
	}
	
	private void setMaxWrijvingsCoeff(float maxWrijving) {
		this.maxWrijving=maxWrijving;
	}
	
	public float getMawWrijvingsCoeff() {
		return this.maxWrijving;
	}

	public void setRadius(float radius){
		this.radius = radius;
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	public void setTyreslope(float liftslope){
		this.tyreslope = liftslope;
	}
	
	public float getTyreSlope() {
		return this.tyreslope;
	}
	

	public void setDampslope(float dampSlope){
		this.dampslope = dampSlope;
	}
	
	public float getDampSlope() {
		return this.dampslope;
	}
	
	public void setBrakeForce(float brakeForce) {
		this.BrakeForce=brakeForce;
	}
	
	public float getBrakeForce() {
		return this.BrakeForce;
}
	
	
	private Vector3f getwheelForce(float time) {
		
		if (this.drone.getYPos() > this.getTyreSlope()+this.getRelativePosition().getY())  //kan problemen geven bij machine nauwkeurigheid
			return new Vector3f (0,0,0) ;
		else {
			if (this.drone.getYPos()<-this.getRelativePosition().getY())
				return null; //CRASH
			else
				return new Vector3f(0,this.getTyreSlope()*(this.drone.getYPos()+this.getRelativePosition().getY())+this.getDampSlope()*Math.abs((D-lastD)/time),0); //afgeleide nog doen
	}
		
	}

	public Vector3f getWrijvingForce(float time) {
		Vector3f normaliseSpeed = null;
		
		float x_speed = this.getDrone().getVelocityInWorld().getX(); // draaining rond y-as meerekening
		
		float scalar = this.getwheelForce(time).length()*this.getMawWrijvingsCoeff()*x_speed;											//richting
		
		this.getDrone().getVelocityInWorld().normalise(normaliseSpeed);
		
		return fysica.product(scalar,normaliseSpeed);
			
	}

	@Override
	public Vector3f getDronePartForce() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// TO DO 
	// D updaten 
	// tijd gebruiken
	// draaining rond y-as meerekening bij x-as

	
	
	
}