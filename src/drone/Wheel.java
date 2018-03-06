package drone;

import org.lwjgl.util.vector.Vector3f;

public class Wheel extends DronePart {

	private float tyreRadius;
	private float tyreslope;
	private float dampslope;
	private float BrakeForce;
	private float maxWrijving;
	private float D;
	private float lastD;
	private Vector3f wheelForce;
	private Vector3f wrijvingForce;

	public Wheel(float tyreRadius, float tyreSlope , float dampSlope,  float maxWrijving, Vector3f relativePosition, Drone drone) {		
		setTyreRadius(tyreRadius);
		setDampslope(dampSlope);
		setTyreslope(tyreSlope);
		setDrone(drone);
		setRelativePosition(relativePosition);
		setMaxWrijvingsCoeff(maxWrijving);
		setBrakeForce(0); // standaard in de lucht
	}
	
	public void update(float brakeForce, float time){
		setBrakeForce(brakeForce);
		setLastD(getD());
		setD(calcNewD());
		setWheelForce(getNewWheelForce(time));
		setWrijvingForce(getNewWrijvingForce(time));
	}
	
	private void setD(float D) {
		this.D=D;
	}
	
	public float getD() {
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
	
	public boolean isGround(){
		if(getAbsolutePositionInWorld().getY() <= getTyreRadius()) return true;
		else return false;
	}
	
	public float calcNewD(){
			float gravForce = getDrone().getGravity()*getDrone().getTotalMass();
			return - (gravForce / getDampSlope());
	}
	
	
	private Vector3f getNewWheelForce(float time) {
			if (this.drone.getYPos()<-this.getRelativePosition().getY())
				return null; //CRASH
			else
				return new Vector3f(0,this.getTyreSlope()*(this.drone.getYPos()+this.getRelativePosition().getY())+this.getDampSlope()*Math.abs((D-lastD)/time),0); //afgeleide nog doen
		
	}

	public Vector3f getNewWrijvingForce(float time) {
			Vector3f normaliseSpeed = null;
			
			float x_speed = this.getDrone().getVelocityInWorld().getX(); // TODO draaining rond y-as meerekening
			
			float scalar = this.getWheelForce().length()*this.getMawWrijvingsCoeff()*x_speed;											//richting
			
			this.getDrone().getVelocityInWorld().normalise(normaliseSpeed);
			
			return fysica.product(scalar,normaliseSpeed);

	}

	@Override
	public Vector3f getDronePartForce() {
		if(isGround()){
			float brake = this.getBrakeForce();
			Vector3f wrijving = this.getWrijvingForce();
			Vector3f wheelforce = this.getWheelForce();
			
			Vector3f total = null;
			
			//TODO WELKE RICHTING GAAT DE BRAKEFORCE UIT
			// HOEK KAN NOG NEGATIEF ZIJN -> FOUTE KANT
			float heading = getDrone().getHeading();
			Vector3f brakeForce = new Vector3f((float) (brake*Math.cos(heading)),0.0f,(float) (brake*Math.sin(heading)));
			
			Vector3f.add(brakeForce, wrijving, total);
			Vector3f.add(total, wheelforce, total);
			
			return total;
		}
		else return new Vector3f(0,0,0);
	}

	public float getTyreRadius() {
		return tyreRadius;
	}

	public void setTyreRadius(float tyreRadius) {
		this.tyreRadius = tyreRadius;
	}

	public Vector3f getWheelForce() {
		return wheelForce;
	}

	public void setWheelForce(Vector3f wheelForce) {
		this.wheelForce = wheelForce;
	}

	public Vector3f getWrijvingForce() {
		return wrijvingForce;
	}

	public void setWrijvingForce(Vector3f wrijvingForce) {
		this.wrijvingForce = wrijvingForce;
	}
	
	// TO DO 
	// D updaten 
	// tijd gebruiken
	// draaining rond y-as meerekening bij x-as

	
	
	
}