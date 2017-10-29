package drone;

import org.lwjgl.util.vector.Vector3f;

public interface DroneObject {
	
	public float getMass();
	
	public Vector3f getGraviation();
	
	public Vector3f getTotalForce();
	
	
}
