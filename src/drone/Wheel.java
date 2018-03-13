package drone;

import org.lwjgl.util.vector.Vector3f;

public class Wheel extends DronePart {

	private float tyreRadius;
	private float tyreslope;
	private float dampslope;
	private float BrakeForce;
	private float maxWrijving;
	private float D = 0;
	private float lastD = 0;
	private Vector3f wheelForce;
	private Vector3f wrijvingForce = new Vector3f(0.0f,0.0f,0.0f);

	public Wheel(float tyreRadius, float tyreSlope , float dampSlope,  float maxWrijving, Vector3f relativePosition, Drone drone) {		
		setTyreRadius(tyreRadius);
		setDampslope(dampSlope);
		setTyreslope(tyreSlope);
		setDrone(drone);
		setRelativePosition(relativePosition);
		setMaxWrijvingsCoeff(maxWrijving);
		setBrakeForce(0); // standaard in de lucht
		setWheelForce(new Vector3f(0.0f,0.0f,0.0f));
		setWrijvingForce(new Vector3f(0.0f,0.0f,0.0f));
		setD(0);
	}
	
	public void update(float brakeForce, float time){
		setBrakeForce(brakeForce);
		setLastD(getD());
		System.out.println("OLD D: " + getD());
		setD(calcNewD());
		System.out.println("NEW D: " + getD());
		setWheelForce(getNewWheelForce(time));
		//setWrijvingForce(getNewWrijvingForce(time));
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
	
	public float getMaxWrijvingsCoeff() {
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
		if(isGround()){
//			return (this.drone.getState().getY()+this.getRelativePosition().getY());
			return getAbsolutePositionInWorld().getY() - getTyreRadius();
//			float gravForce = getDrone().getGravity()*getDrone().getTotalMass();
//			return - (gravForce / getDampSlope());
		}
		else return 0.0f;
	}
	
	
	private Vector3f getNewWheelForce(float time) {
//			if (this.drone.getYPos() < -this.getRelativePosition().getY())
//				return null; //CRASH
//			else
		if(isGround()){
			if(time == 0.0) return new Vector3f(0,0,0);
			float force = Math.abs((this.getTyreSlope()*D) 
						+ this.getDampSlope()*(D-lastD)/time);
			return new Vector3f(0,force,0); //afgeleide nog doen
		}
		else return new Vector3f(0,0,0);
	}

	public Vector3f getNewWrijvingForce(float time) {
		if(isGround()){
			Vector3f normaliseSpeed = null;
			
			
			float x_speed = this.getDrone().getState().getVelocity().getX(); // TODO draaining rond y-as meerekening
			
			
			System.out.println("SPEEEd" + this.getWheelForce());
			System.out.println(this.getWheelForce().length());
			System.out.println(x_speed + "X_SPEED");
			
			
			float scalar = this.getWheelForce().length()*this.getMaxWrijvingsCoeff()*x_speed;
			
			System.out.println("scalar " + scalar);
			
			normaliseSpeed = this.getDrone().getState().getVelocity().normalise(normaliseSpeed); //.normalise(normaliseSpeed);
			
			System.out.println(normaliseSpeed);
			
			return fysica.product(scalar,normaliseSpeed);
		}
		else return new Vector3f(0,0,0);
	}

	@Override
	public Vector3f getDronePartForce() {
		if(isGround()){
			float brake = this.getBrakeForce();
			Vector3f wrijving = this.getWrijvingForce();
			Vector3f wheelforce = this.getWheelForce();
			
			Vector3f total = new Vector3f(0,0,0);
			
			//TODO WELKE RICHTING GAAT DE BRAKEFORCE UIT
			// HOEK KAN NOG NEGATIEF ZIJN -> FOUTE KANT
			float heading = getDrone().getHeading();
			Vector3f brakeForce = new Vector3f((float) (brake*Math.sin(heading)),0.0f,(float) (brake*Math.cos(heading)));
			
			System.out.println("BRAKEFORCE: " +brakeForce);
			
			Vector3f.add(brakeForce, wrijving, total);
			Vector3f.add(total, wheelforce, total);
			
			System.out.println("TOTAL WHEEL FORCE " + wheelforce);
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