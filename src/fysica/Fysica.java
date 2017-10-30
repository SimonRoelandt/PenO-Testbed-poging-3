package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;
import drone.Engine;

public class Fysica {
	
	final static float gravity = (float) 9.81;
	
	//private float[] velocity= {0,25,0};

	private float liftSlope = (float) 1.5;
	
	public float getGravity() {
		return this.gravity;
	}
	

	public Vector3f gravitationForce(DroneObject obj) {
		float gravitationForce = (float) (obj.getMass() * gravity);
		return new Vector3f(0,-gravitationForce,0);
	}
	
	//liftSlope ?????

	public Vector3f liftForce(Airfoil air) {
		//System.out.println(air.getClass().getName());
		Vector3f normal = crossProduct(air.getAxisVector(),air.getAttackVector());
		//System.out.print("Normal: ");
		//print(normal);
		Vector3f airspeed = air.getVelocityAirfoil();
		//System.out.print("Airspeed: ");
		//print(airspeed);
		//System.out.print("Axis Vector: ");
		Vector3f axis = air.getAxisVector();
		//print(air.getAxisVector());
		//Vector3f projectedAirspeed = Vector3f.sub(airspeed, product(Vector3f.dot(axis,airspeed),axis),null);
		//Vector3f projectedAirspeed = product(scalarProduct(airspeed,air.getAxisVector()) / scalarProduct(air.getAxisVector(), air.getAxisVector()), air.getAxisVector());
		Vector3f projectedAirspeed = Vector3f.sub(airspeed, mul( mul(airspeed,axis),axis),null);
		//System.out.print("ProjectedAirspeed: ");
		//print(projectedAirspeed);
		//System.out.print("AttackVector: ");
		//print(air.getAttackVector());
		float angleOfAttack = (float) -Math.atan2(scalarProduct(projectedAirspeed,normal), scalarProduct(projectedAirspeed,air.getAttackVector()));
		//if (air.isVertical()) {
			//angleOfAttack = 0;
		//}
		//else angleOfAttack = (float) -1.2;
		System.out.println("AOA: " + angleOfAttack);
		//System.out.println("ScalarProduct: " + scalarProduct(projectedAirspeed,air.getAttackVector()));
		Vector3f proj = new Vector3f(0,0,projectedAirspeed.getZ());
		//Vector3f liftForce = product((float) (angleOfAttack * Math.pow(projectedAirspeed.getZ(),2)),product(air.getLiftSlope(),normal));
		Vector3f liftForce = product((float)(angleOfAttack *Math.pow(proj.getZ(),2)),product(air.getLiftSlope(),normal));
		System.out.print("liftforce: ");
		System.out.print(normal);
		print(liftForce);
		return liftForce;
	}

	public Vector3f totalForce(DroneObject obj) {
		if(obj instanceof Engine) {
			
			return sum(obj.getGraviation(), ((Engine) obj).getThrust());
		}
		return sum(liftForce((Airfoil) obj), gravitationForce(obj));
		//return gravitationForce(obj);
	}
	
	

	public Vector3f totalForceDrone(Drone drone) {
		DroneObject[] objArray = drone.getDroneObj();
		Vector3f v = new Vector3f(0,0,0);
		for (DroneObject obj: objArray) {
			v = sum(obj.getTotalForce(), v);
			//System.out.print("Force: ");
			//print(obj.getTotalForce());
		}
		System.out.print("Total Force:" );
		print(v);
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
		Vector3f vel = product((float) (Math.pow(time,2)/2),acc);
		Vector3f pos = sum(sum(drone.getPos(),product(time, drone.getVelocity())),
				          vel);
		return pos;
	}
	
	
	public Vector3f velocity(Drone drone, float time) {
		Vector3f acc = acceleration(drone);
		Vector3f at = new Vector3f(acc.getX()*time,acc.getY()*time,acc.getZ()*time);
		Vector3f v = sum(drone.getVelocity(), at);
		//System.out.println("Pro: " + product(time, acc));
		return v;
	}
	
	//Hulpfuncties
	
	//vector1.xcoordinaat*vector2.xcoordinaat+vector1.ycoordinaat*vector2.ycoordinaat+vector1.zcoordinaat*vector2.zcoordinaat;	
	
	public Vector3f crossProduct(Vector3f v1, Vector3f v2) {
		Vector3f v = Vector3f.cross(v1,v2,null);
		return v;
	}
	
	public Vector3f mul (Vector3f v1, Vector3f v2) {
		Vector3f v = new Vector3f(0,0,0);
		v.setX(v1.getX() * v2.getX());
		v.setY(v1.getY() * v2.getY());
		v.setZ(v1.getZ() * v2.getZ());
		return v;
	}
	
	public Vector3f product(float s, Vector3f v) {
		Vector3f v1 = new Vector3f(0,0,0);
		v1.setX(v.getX() * s);
		v1.setY(v.getY() * s);
		v1.setZ(v.getZ() * s);
		return v1;
	}
	
	public float scalarProduct(Vector3f v1, Vector3f v2) {
		float v = Vector3f.dot(v1,v2);
		return v;
	}
	
	public Vector3f sum(Vector3f v1, Vector3f v2) {
		Vector3f sum = Vector3f.add(v1, v2, null);
		return sum;
	}
	
	public void print(Vector3f force) {
		System.out.println(force.getX() + " " + force.getY()+ " " + force.getZ());
	}

	
}
