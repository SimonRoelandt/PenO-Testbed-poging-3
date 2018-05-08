package drone;

import org.lwjgl.util.vector.Vector3f;

public class State {
	
	private Vector3f position;
	private Vector3f hpr = new Vector3f(0,0,0);
	private Vector3f hprRates = new Vector3f(0,0,0);;

	private Vector3f velocity;
	
	private Vector3f angularOrientation;
	private Vector3f angularRotation = new Vector3f(0,0,0);	
	
	public State() {
		
	}
	
	//POSITION
	public Vector3f getPosition() {
		return this.position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}


	//DRONE MOMENT
	
	Vector3f moment = new Vector3f();
	
	
	
	//VELOCITY
	public Vector3f getVelocity() {
		return this.velocity;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	//ANGULAR ORIENTATION
	public Vector3f getAngularOrientation() {
		return this.angularOrientation;
	}
	
	public void setAngularOrientation(Vector3f angularOrientation) {
		this.angularOrientation = angularOrientation;
	}
	
	//ANGULAR HPR
	
	public Vector3f getHPR() {
		
		Vector3f hpr = this.hpr;
		//hpr.setY(0f);
		//hpr.setZ(0f);
		
		return hpr;
	}
	
	public void setHPR(Vector3f hpr) {
		this.hpr = hpr;
	}
	
	public float getHeading(){
		return getHPR().getX();
	}
	
	public float getPitch(){
		return getHPR().getY();
	}
	
	public float getRoll(){
		return getHPR().getZ();
	}
	
	
	
	//ANGULAR HPR rates
	
	public Vector3f getHPRrates() {
		return  this.hprRates;
	}
	
	public void setHPRrates(Vector3f hprRates) {
		this.hprRates = hprRates;
	}
	
	public float getHeadingRate(){
		return getHPRrates().getX();
	}
	
	public float getPitchRate(){
		return getHPRrates().getY();
	}
	
	public float getRollRate(){
		return getHPRrates().getZ();
	}

	
	
	//ANGULAR ROTATION
	public Vector3f getAngularRotation() {
		return this.angularRotation;
	}
	
	public void setAngularRotation(Vector3f angularRotation) {
		this.angularRotation = angularRotation;
	}


	
}
