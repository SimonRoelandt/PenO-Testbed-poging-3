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
	
	public Vector3f getAbsolutePositionInWorld(){
		Vector3f droneCenterPositionInWorld = this.getDrone().getState().getPosition();
		Vector3f relativePositionInWorld = fysica.convertToWorld(this.getDrone(), this.getRelativePosition());		
		return fysica.sum(droneCenterPositionInWorld, relativePositionInWorld);
	}
	
	public Vector3f getGraviationForceInWorld() {
		float gravitationForce = (float) (this.getMass() * Fysica.gravity);
		return new Vector3f(0,-gravitationForce,0);
	}
	
	public abstract Vector3f getDronePartForce();
	
	private Vector3f getDronePartForceInWorld() {
		return fysica.convertToWorld(this.getDrone(), getDronePartForce());
	}
	
	public Vector3f getTotalForceInWorld(float time) {	
		Vector3f dronePartForceInWorld = getDronePartForceInWorld();
		Vector3f totalForce = fysica.sum(dronePartForceInWorld, getGraviationForceInWorld());
		System.out.println("lift " + dronePartForceInWorld + " grav " + getGraviationForceInWorld());
		System.out.println();
		System.out.println("******************** VELOCITY " + drone.getLeftWing().getVelocityAirfoil() + "**********************");
		System.out.println();
		return totalForce;
	}
}
