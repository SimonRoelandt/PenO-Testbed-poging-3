package drone;

import org.lwjgl.util.vector.Vector3f;

public class State {
	
	public State() {
		
	}
	
	//POSITION
	public Vector3f getPosition() {
		return this.position;
	}
	
	public void setPosition(Vector3f position) {
		this.nextState = new State();

		this.position = position;
	}
	
	public float getX(){
		return getPosition().getX();
	}
	
	public float getY(){
		return getPosition().getY();
	}
	
	public float getZ(){
		return getPosition().getZ();
	}
	
	
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
	
	//ANGULAR ROTATION
	public Vector3f getAngularRotation() {
		return new Vector3f();
		//return this.angularRotation;
	}
	
	public void setAngularRotation(Vector3f angularRotation) {
		this.angularRotation = angularRotation;
	}
	
	
	
	private Vector3f position;
	private Vector3f velocity;
	
	private Vector3f angularOrientation;
	private Vector3f angularRotation;

	private State nextState;
	
	

}
