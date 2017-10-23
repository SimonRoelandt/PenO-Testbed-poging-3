package drone;

public class Test {

	public static void main(String[] args) {
		Drone drone = new Drone(5, 0, 0, 0, 0, 0, 0, 0, null);
		float[] force = drone.getTotalForceDrone();
		System.out.println(force);

	}

}
