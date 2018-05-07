package drone;

import org.lwjgl.util.vector.Vector3f;
import java.lang.Math.*;

/**
 * 
 * A class of wheel drone parts.
 *
 */
public class Wheel extends DronePart {

	private float tyreRadius;
	private float tyreslope;
	private float dampslope;
	private float BrakeForce;
	private float maxWrijving;
	private float D = 0;
	private float maxRem;
	private float lastD = 0;
	private Vector3f wheelForce;
	private Vector3f wrijvingForce = new Vector3f(0.0f,0.0f,0.0f);
	private boolean front;
	private Vector3f prevWheelForce;
	
	public Wheel(boolean front, float tyreRadius, float tyreSlope , float dampSlope,  float maxWrijving,float maxRem, Vector3f relativePosition, Drone drone) {		
		setFront(front);
		setTyreRadius(tyreRadius);
		setDampslope(dampSlope);
		setTyreslope(tyreSlope);
		setDrone(drone);
		setRelativePosition(relativePosition);
		setMaxWrijvingsCoeff(maxWrijving);
		setMaxRem(maxRem);
		setBrakeForce(0);
		setWheelForce(new Vector3f(0.0f,0.0f,0.0f));
		setWrijvingForce(new Vector3f(0.0f,0.0f,0.0f));
		setD(0);
	}
	
	/**
	 * Updates the wheel parameters.
	 */
	public void update(float brakeForce, float time){
		setBrakeForce(brakeForce);
		setLastD(getD());
		setD(calcNewD());
		setWheelForce(getNewWheelForce(time));
		setWrijvingForce(getNewFrictionForce(time));
	}
	
	@Override
	public boolean checkCrash(){
		if(getAbsolutePositionInWorld().y < 0) return true;
		else if(getD() > getTyreRadius()) return true;
		else return false;
	}
	
	/**
	 * Checks if the tire is on the ground.
	 */
	public boolean isGround(){
		if(getAbsolutePositionInWorld().getY() <= getTyreRadius() + 0.005) return true;
		else return false;
	}
	
	/**
	 * Calculates the new D (amount tire has been pressed in)
	 */
	public float calcNewD(){
		if(isGround()){
			return getAbsolutePositionInWorld().getY() - getTyreRadius();
		}
		else return 0.0f;
	}
	
	/**
	 * Gets the new wheel force, the force the wheel exerts in the positive y direction.
	 */
	private Vector3f getNewWheelForce(float time) {
		if(isGround()){
			if(time == 0.0) return new Vector3f(0,0,0);
			float force = Math.abs((this.getTyreSlope()*D) 
						+ this.getDampSlope()*(D-lastD)/time);
			
			//WORKAROUND
			//return new Vector3f(0f, 1569.5907f, 0f);
			return new Vector3f(0,force,0);
		}
		else return new Vector3f(0,0,0);
	}

	/**
	 * Gets the new friction force.
	 */
	public Vector3f getNewFrictionForce(float time) {
		if(isFront()){
			return new Vector3f(0,0,0);
		}
		if(isGround() ){
			Vector3f speed = this.getDrone().getState().getVelocity();
			Vector3f convertedSpeed = this.getDrone().p.convertToDroneCords(this.getDrone(), speed);
			
			float xComponent = convertedSpeed.getX();
			float max = this.getMaxWrijvingsCoeff();
			float N = this.getWheelForce().length();
			
			float frictionScalar = xComponent*max*N;
			
			Vector3f friction = new  Vector3f(-frictionScalar,0.0f,0f);
			Vector3f fInWorld = this.getDrone().p.convertToWorld(this.getDrone(), friction);
			
			this.getDrone().p.print("world speed is:" + speed + " so xComp of drone is: " + xComponent
			+ " so friction force according to drone is " + friction + " so in world is :" + fInWorld, 105);
			
			return fInWorld;
			
			//return new Vector3f((float) (scalar*Math.sin(heading)),0.0f,(float) -(scalar*Math.cos(heading)));
			
			//return new Vector3f(0,0,0);
			
		}
		
		else {
			return new Vector3f(0,0,0);
		}
	}
	
	/**
	 * Gets the total brake force in the right directions: a factor of the previous total force of the drone.
	 */
	
	private Vector3f brakeForce = new Vector3f();
	
	public void setBF(Vector3f v) {
		this.brakeForce = v;
	}
	
	public Vector3f getBf() {
		return this.brakeForce;
	}
	public Vector3f getTotBrakeForce(Vector3f prevTotForce){
	
		float requestedBrakeForce = this.getBrakeForce();

		Vector3f speed = this.getDrone().getState().getVelocity();
		Vector3f convertedSpeed = this.getDrone().p.convertToDroneCords(this.getDrone(), speed);
		
		
		
			
		//DIT MOET ZOIETS ZIJN
		
		
		
		///CLEAN
		
		float brakeForceNeeded = this.getDrone().tempTurningForce;
		float actualBrakeForce = Math.signum(brakeForceNeeded)*Math.min(Math.abs(brakeForceNeeded), requestedBrakeForce);

		Vector3f brakeForceInDroneCords = new Vector3f(0f, 0f, actualBrakeForce);
		Vector3f brakeForceInWorld = this.getDrone().p.convertToWorld(this.getDrone(), brakeForceInDroneCords);
		
		
		this.getDrone().p.print("--Brakef in drone is: " + brakeForceInDroneCords + " so in world is :" +
				brakeForceInWorld, 140);
				
		
		this.setBF(brakeForceInWorld);

		return brakeForceInWorld;
		//wat is dit?
				
	}
	
	@Override
	public Vector3f getDronePartForce() {
		return new Vector3f(0,0,0);
	}
	
	/**
	 * Gets the total wheel part force: wheel force, friction force and brake force.
	 */
	public Vector3f getDronePartForce(Vector3f prevTotForce) {
		if(isGround()){
			Vector3f total = new Vector3f(0,0,0);
			Vector3f.add(this.getWheelForce(), this.getWrijvingForce(), total);
			Vector3f.add(total, getTotBrakeForce(prevTotForce), total);
			return total;
		}
		else return new Vector3f(0,0,0);
	}
	
	
	public Vector3f getAbsolutePositionInWorld() {
		Vector3f sum = new Vector3f(0,0,0);
		Vector3f relPosInWorld = this.getDrone().p.convertToWorld(this.getDrone(), this.getRelativePosition());
		Vector3f dronePos = this.getDrone().getState().getPosition();
		
		Vector3f.add(relPosInWorld, dronePos, sum);
		
		return sum;

	}
	
//------------GETTERS/SETTERS--------------------------------------------	

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

	public boolean isFront() {
		return front;
	}

	public void setFront(boolean front) {
		this.front = front;
	}

	public float getMaxRem() {
		return maxRem;
	}

	public void setMaxRem(float maxRem) {
		this.maxRem = maxRem;
	}

	public Vector3f getPrevWheelForce() {
		return prevWheelForce;
	}

	public void setPrevWheelForce(Vector3f prevWheelForce) {
		this.prevWheelForce = prevWheelForce;
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
}