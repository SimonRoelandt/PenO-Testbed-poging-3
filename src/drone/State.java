package drone;

import org.lwjgl.util.vector.Vector3f;

public class State {
	
	public State() {
		
	}
	
	//POSITION
	public Vector3f getPosition() {
		return this.position;
	}
	
	public void setNextPosition(Vector3f position) {
		this.nextState = new State();

		this.getNextState().position = position;
	}
	
	//VELOCITY
	public Vector3f getVelocity() {
		return this.velocity;
	}
	
	public void setNextVelocity(Vector3f velocity) {
		this.getNextState().velocity = velocity;
	}
	
	//ANGULAR ORIENTATION
	public Vector3f getAngularOrientation() {
		return this.angularOrientation;
	}
	
	public void setNextAngularOrienation(Vector3f angularorientation) {
		this.getNextState().angularOrientation = angularOrientation;
	}
	
	//ANGULAR ROTATION
	public Vector3f getAngularRotation() {
		return this.angularRotation;
	}
	
	public void setNextAngularRotation(Vector3f angularRotation) {
		this.getNextState().angularRotation = angularRotation;
	}
	
	
	//NEXT STATE
	public State getNextState() {
		return this.nextState;
	}
	
	public void createNextState() {
		this.nextState = new State();
	}
	
	
	private Vector3f position;
	private Vector3f velocity;
	
	private Vector3f angularOrientation;
	private Vector3f angularRotation;

	private State nextState;
	
	

}
