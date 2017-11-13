package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;

public interface IFysica {
	

	public Vector3f gravitationForce(DroneObject obj);
	
	public float[] liftForce(Airfoil air);
	
	public float[] totalForce(DroneObject obj);
	
	public float[] totalForceDrone(Drone drone);
}
