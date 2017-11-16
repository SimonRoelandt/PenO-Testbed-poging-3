package drone;

import org.lwjgl.util.vector.Vector3f;
public class Engine extends DronePart{

	private float thrust;
	private static final float maxThrust= 5000;
	
	public Engine(
			double thrust, 
			double mass, 
			Vector3f relativePosition) {
		
		this.thrust = (float) thrust;
		this.mass = (float) mass;
		
		this.setRelativePosition(relativePosition);
	}
	
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

	public static float getMaxthrust() {
		return maxThrust;
	}
	

	
	
}
