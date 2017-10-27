package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;
import drone.Engine;

public class Fysica implements IFysica {
	
	final static float gravity = (float) 9.81;
	
	private float[] velocity= {0,25,0};

	private float liftSlope = (float) 1.5;
	
	private static Vector3f vector = new Vector3f(0,0,0); //Voor berekeningen
	
	public float getGravity() {
		return this.gravity;
	}
	
	@Override
	public Vector3f gravitationForce(DroneObject obj) {
		float gravitationForce = (float) (obj.getMass() * gravity);
		return new Vector3f(0,-gravitationForce,0);
	}
	
	//liftSlope ?????

	@Override
	public float[] liftForce(Airfoil air) {
		float[] normal = crossProduct(air.getAxisVector(),air.getAttackVector());
		System.out.print("Normal: ");
		print(normal);
		float[] airspeed = air.getVelocityAirfoil();
		print(airspeed);
		float[] projectedAirspeed = product(scalarProduct(airspeed,air.getAxisVector()) / scalarProduct(air.getAxisVector(), air.getAxisVector()), air.getAxisVector());
		System.out.print("ProjectedAirspeed: ");
		print(projectedAirspeed);
		System.out.print("AttackVector: ");
		print(air.getAttackVector());
		float angleOfAttack = (float) -Math.atan2(scalarProduct(projectedAirspeed,normal), scalarProduct(projectedAirspeed,air.getAttackVector()));
		if (air.isVertical()) {
			angleOfAttack = 0;
		}
		else angleOfAttack = (float) 1.2;
		System.out.println("ScalarProduct: " + scalarProduct(projectedAirspeed,air.getAttackVector()));
		float[] liftForce = product(angleOfAttack * scalarProduct(projectedAirspeed, projectedAirspeed), product(liftSlope, normal));
		System.out.print("liftforce: ");
		print(liftForce);
		return liftForce;
	}

	@Override
	public float[] totalForce(DroneObject obj) {
		if(obj instanceof Engine) {
			sum(obj.getGraviation(), ((Engine) obj).getThrust());
		}
		return sum(liftForce((Airfoil) obj), gravitationForce(obj));
	}
	
	

	@Override
	public float[] totalForceDrone(Drone drone) {
		DroneObject[] objArray = drone.getDroneObj();
		float[] v = {0,0,0};
		for (DroneObject obj: objArray) {
			v = sum(obj.getTotalForce(), v);
			//print(obj.getTotalForce());
		}
		return v;
	}
	
	public float[] acceleration(Drone drone) {
		Vector3f force = drone.getTotalForceDrone();
		DroneObject[] objArray = drone.getDroneObj();
		float mass = 0;
		for (DroneObject obj : objArray) {
			mass += obj.getMass();
		}
		float[] acceleration = product((1/mass), force);
		return acceleration;
	}
	
	public float[] nextPosition(Drone drone, float time) {
		float[] acc = acceleration(drone);
		float[] pos = sum(sum(drone.getPos(),product(time, velocity)),
				          product((float) (Math.pow(time, 2)/2),acc ));
		return pos;
	}
	
	public float[] velocity(Drone drone, float time) {
		return sum(drone.getVelocity(), product(time, acceleration(drone)));
	}
	
	//Hulpfuncties
	
	//vector1.xcoordinaat*vector2.xcoordinaat+vector1.ycoordinaat*vector2.ycoordinaat+vector1.zcoordinaat*vector2.zcoordinaat;	
	
	public Vector3f crossProduct(Vector3f v1, Vector3f v2) {
		Vector3f v = vector.cross(v1,v2,null);
		return v;
	}
	
	public float[] product(float s, float[] v) {
		float[] p = {s*v[0], s*v[1], s*v[2]};
		return p;
	}
	
	public float scalarProduct(Vector3f v1, Vector3f v2) {
		return vector.dot(v1,v2);
	}
	
	public Vector3f sum(Vector3f v1, Vector3f v2) {
		Vector3f sum = vector.add(v1, v2, null);
		return sum;
	}
	
	public void print(float[] force) {
		System.out.println(force[0] + " " + force[1] + " " + force[2]);
	}

	
}
