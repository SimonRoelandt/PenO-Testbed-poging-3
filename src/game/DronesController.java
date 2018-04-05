package game;

import drone.Drone;

public class DronesController {


	private static Drone drone1 = new Drone(0, 0, 0, null);
	private static Drone drone2 = new Drone(0, 0, 0, null);


	public static Drone[] getDrones() {
		//returns all the drones
		Drone[] drones = {drone1, drone2};

		return drones;
	}


}
