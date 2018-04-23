package drone;

import org.lwjgl.util.vector.Vector3f;

import fysica.Fysica;

public abstract class DronePart {
	
	Fysica fysica = new Fysica();
	Drone drone;
	float mass;
	Vector3f relativePostion;
	Vector3f velocityInWorld;
	
	public Drone getDrone(){
		return this.drone;
	}
	
	/**
	 * Checks if a part has crashed (is under the ground).
	 */
	public boolean checkCrash(){
		if(getAbsolutePositionInWorld().y < 0) return true;
		else return false;
	}
	
	/**
	 * Gets the absolute position of the part in the world.
	 */
	public Vector3f getAbsolutePositionInWorld(){
		Vector3f droneCenterPositionInWorld = this.getDrone().getState().getPosition();
		Vector3f relativePositionInWorld = fysica.convertToWorld(this.getDrone(), this.getRelativePosition());		
		return fysica.sum(droneCenterPositionInWorld, relativePositionInWorld);
	}
	
	/**
	 * Gets the gravitation force of the drone part.
	 */
	public Vector3f getGravitationForceInWorld() {
		float gravitationForce = (float) (this.getMass() * Fysica.gravity);
		return new Vector3f(0,-gravitationForce,0);
	}
	
	/**
	 * Gets the drone part force (all forces except gravitation).
	 */
	public abstract Vector3f getDronePartForce();
	
	/**
	 * Converts the drone part force to the world coordinates. 
	 */
	public Vector3f getDronePartForceInWorld() {
		return fysica.convertToWorld(this.getDrone(), getDronePartForce());
	}
	
	/**
	 * Gets the total force on the drone part.
	 */
	public Vector3f getTotalForceInWorld(float time) {	
		Vector3f dronePartForceInWorld = getDronePartForceInWorld();
		Vector3f totalForce = fysica.sum(dronePartForceInWorld, getGravitationForceInWorld());
		return totalForce;
	}
	
//-----------GETTERS/SETTERS---------------------------------------	
	
	public void setDrone(Drone drone){
		this.drone = drone;
	}
	
	public void setMass(float newMass){
		this.mass = newMass;
	}
	
	public float getMass(){
		return this.mass;
	}
	
	public void setRelativePosition(Vector3f relativePosition){
		this.relativePostion = relativePosition;
	}
	
	public Vector3f getRelativePosition(){
		return this.relativePostion;
	}
}
