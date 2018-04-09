package drone;

import org.lwjgl.util.vector.Vector3f;

/**
 * 
 * A class of engine drone parts.
 *
 */
public class Engine extends DronePart{

	private float thrust;
	private float maxThrust = 5000;
	
	public Engine(float thrust, float mass, Vector3f relativePosition, Drone drone, float maxThrust) {	
		setThrust(thrust);
		setMass(mass);
		setMaxThrust(maxThrust);
		setDrone(drone);
		setRelativePosition(relativePosition);
	}
	
	/**
	 * Gets the thrust force of the engine.
	 */
	private Vector3f getThrust() {
		Vector3f v1 = new Vector3f(0, 0 ,(float) - this.getThrustScalar());
		return v1;
	}
	
	@Override
	public Vector3f getDronePartForce() {
		return this.getThrust();
	}
	
	public float getThrustScalar() {
		return this.thrust;
	}
	
	public void setThrust(float thrust) {
		this.thrust = thrust;
	}

	public float getMaxthrust() {
		return maxThrust;
	}
	
	public void setMaxThrust(float maxthrust) {
		this.maxThrust = maxthrust;
	}
}
