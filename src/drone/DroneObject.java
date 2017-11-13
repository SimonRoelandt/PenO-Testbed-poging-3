package drone;

import org.lwjgl.util.vector.Vector3f;

//Onderdeel van een drone -> airfoil of engine
public interface DroneObject {
	
	public float getMass();
	
	public Vector3f getPos();
	
	public Vector3f getGraviation();
	
	public Vector3f getTotalForce();
	
	
}
