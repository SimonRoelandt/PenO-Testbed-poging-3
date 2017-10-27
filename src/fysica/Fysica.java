package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;
import drone.Engine;

public class Fysica {
	
	final static float gravity = (float) 9.81;
	
	private float[] velocity= {0,25,0};

	private float liftSlope = (float) 1.5;
	
	private static Vector3f vector = new Vector3f(0,0,0); //Voor berekeningen
	
	public float getGravity() {
		return this.gravity;
	}
	

	public Vector3f gravitationForce(DroneObject obj) {
		float gravitationForce = (float) (obj.getMass() * gravity);
		return new Vector3f(0,-gravitationForce,0);
	}
	
	//liftSlope ?????

	public Vector3f liftForce(Airfoil air) {
		Vector3f normal = crossProduct(air.getAxisVector(),air.getAttackVector());
		System.out.print("Normal: ");
		//print(normal);
		Vector3f airspeed = air.getVelocityAirfoil();
		//print(airspeed);
		Vector3f projectedAirspeed = product(scalarProduct(airspeed,air.getAxisVector()) / scalarProduct(air.getAxisVector(), air.getAxisVector()), air.getAxisVector());
		System.out.print("ProjectedAirspeed: ");
		//print(projectedAirspeed);
		System.out.print("AttackVector: ");
		//print(air.getAttackVector());
		float angleOfAttack = (float) -Math.atan2(scalarProduct(projectedAirspeed,normal), scalarProduct(projectedAirspeed,air.getAttackVector()));
		if (air.isVertical()) {
			angleOfAttack = 0;
		}
		else angleOfAttack = (float) 1.2;
		System.out.println("ScalarProduct: " + scalarProduct(projectedAirspeed,air.getAttackVector()));
		Vector3f liftForce = product(angleOfAttack * scalarProduct(projectedAirspeed, projectedAirspeed), product(liftSlope, normal));
		System.out.print("liftforce: ");
		//print(liftForce);
		return liftForce;
	}

	public Vector3f totalForce(DroneObject obj) {
		if(obj instanceof Engine) {
			sum(obj.getGraviation(), ((Engine) obj).getThrust());
		}
		return sum(liftForce((Airfoil) obj), gravitationForce(obj));
	}
	
	

	public Vector3f totalForceDrone(Drone drone) {
		DroneObject[] objArray = drone.getDroneObj();
		Vector3f v = new Vector3f(0,0,0);
		for (DroneObject obj: objArray) {
			v = sum(obj.getTotalForce(), v);
			//print(obj.getTotalForce());
		}
		return v;
	}
	
	public Vector3f acceleration(Drone drone) {
		Vector3f force = drone.getTotalForceDrone();
		DroneObject[] objArray = drone.getDroneObj();
		float mass = 0;
		for (DroneObject obj : objArray) {
			mass += obj.getMass();
		}
		Vector3f acceleration = product((1/mass), force);
		return acceleration;
	}
	
	public Vector3f nextPosition(Drone drone, float time) {
		Vector3f acc = acceleration(drone);
		Vector3f pos = sum(sum(drone.getPos(),product(time, drone.getVelocity())),
				          product((float) (Math.pow(time, 2)/2),acc ));
		return pos;
	}
	
	public Vector3f velocity(Drone drone, float time) {
		return sum(drone.getVelocity(), product(time, acceleration(drone)));
	}
	
	//Hulpfuncties
	
	//vector1.xcoordinaat*vector2.xcoordinaat+vector1.ycoordinaat*vector2.ycoordinaat+vector1.zcoordinaat*vector2.zcoordinaat;	
	
	public Vector3f crossProduct(Vector3f v1, Vector3f v2) {
		Vector3f v = vector.cross(v1,v2,null);
		return v;
	}
	
	public Vector3f product(float s, Vector3f v) {
		v.setX(v.getX() * s);
		v.setY(v.getY() * s);
		v.setZ(v.getZ() * s);
		return v;
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
