package drone;

public class Test {

	public static void main(String[] args) {
		float[] vel = {0,0,0};
		Drone drone = new Drone(0,0,0,vel);
		float[] force = drone.getTotalForceDrone();
		System.out.println(force);

	}

}
