package fysica;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;

public interface IFysica {
	

	public float[] gravitationForce(DroneObject obj);
	
	public float[] liftForce(Airfoil air);
	
	public float[] totalForce(DroneObject obj);
	
	public float[] totalForceDrone(Drone drone);
}
