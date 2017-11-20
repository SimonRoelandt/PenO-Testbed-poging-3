package drone;

import org.lwjgl.util.vector.Vector3f;

import engine.Timer;

public class Test {

	public static void main(String[] args) {
		Vector3f vel = new Vector3f(0,0,(float) -8);
		Drone drone = new Drone(0,0,0,vel);
		Timer f = new Timer();
		f.init();
		
		//System.out.println("Left  : " + drone.getLeftWing().getTotalForce());
		//System.out.println("Right : " + drone.getRightWing().getTotalForce());
		//System.out.println("Hor   : " + drone.getHorStabilizator().getTotalForce());
		//System.out.println("Ver   : " + drone.getVerStabilizator().getTotalForce());
		//System.out.println("Engine: " + drone.getEngine().getTotalForce());
//		while (f.getTot() < 1) {
//			Vector3f force = drone.getNewPosition(f.getElapsedTime());
//			drone.setPosition(force);
//			System.out.println("Pos: " + drone.getPos());
//			System.out.println("Vel: " + drone.getVelocity());
////			Vector3f vel1 = drone.getNewVelocity(1);
////			System.out.println("Vel: " + vel1);
////			drone.setVelocity(vel1);
//			//System.out.println(f.getTot());
//		}
		//System.out.println(f.getTot());
		

	}

}
