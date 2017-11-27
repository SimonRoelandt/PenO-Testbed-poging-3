package drone;

import org.lwjgl.util.vector.Vector3f;

import fysica.Fysica;

public abstract class DronePart {
	
	Fysica fysica = new Fysica();
	
	Drone drone;
	
	float mass;
	Vector3f relativePostion;
	Vector3f velocityInWorld;
	
	
	//DRONE
	
	public Drone getDrone(){
		return this.drone;
	}
	
	public void setDrone(Drone drone){
		this.drone = drone;
	}
	
	//MASS
	public float getMass(){
		return this.mass;
	};
	
	//RELATIVE POSITION
	public void setRelativePosition(Vector3f relativePosition){
		this.relativePostion = relativePosition;
	}
	
	public Vector3f getRelativePosition(){
		return this.relativePostion;
	};
	
	//ABSOLUTE POSITION
	public Vector3f getAbsolutePositionInWorld(){
		
		Vector3f droneCenterPositionInWorld = this.getDrone().getPositionInWorld();
		Vector3f relativePositionInWorld = fysica.convertToWorld(this.getRelativePosition());
		
		return fysica.sum(droneCenterPositionInWorld, relativePositionInWorld);
	}
	
	//SPEED
	public Vector3f getSpeedInWorld(){
		return null;
	}

	public Vector3f getGraviationForceInWorld() {
		float gravitationForce = 
				(float) (this.getMass() * fysica.getGravity());
		return new Vector3f(0,-gravitationForce,0);
		
	}
	
	public abstract Vector3f getDronePartForce();
	
	
	private Vector3f getDronePartForceInWorld() {
		return fysica.convertToWorld(this.getDronePartForce());
	}
	
	public Vector3f getTotalForceInWorld() {
		
		Vector3f dronePartForceInWorld = this.getDronePartForceInWorld();
		Vector3f gravitationForce = this.getGraviationForceInWorld();

		return fysica.sum(dronePartForceInWorld, gravitationForce);
	}

	
}
