package fysica;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;
import drone.Engine;

public class Fysica implements IFysica {
	
	final static float gravity = (float) 9.81;
	
	private float[] velocity= null;

	private float liftSlope;
	
	@Override
	public float[] gravitationForce(DroneObject obj) {
		float gravitationForce = (float) (obj.getMass() * gravity);
		float[] gravitationVector = {0, -gravitationForce,0};
		return gravitationVector;
	}
	
	//liftSlope ?????

	@Override
	public float[] liftForce(Airfoil air) {
		float[] normal = crossProduct(air.getAxisVector(),air.getAttackVector());
		float[] airspeed = velocity;
		float[] projectedAirspeed = product(scalarProduct(airspeed,air.getAxisVector()) / scalarProduct(air.getAxisVector(), air.getAxisVector()), air.getAxisVector());
		float   angleOfAttack = (float) -Math.atan2(scalarProduct(projectedAirspeed,normal), scalarProduct(airspeed,air.getAxisVector()));
		float[] liftForce = product(angleOfAttack * scalarProduct(projectedAirspeed, projectedAirspeed), product(liftSlope, normal));
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
			sum(obj.getTotalForce(), v);
		}
		return v;
	}
	
	public float[] acceleration(Drone drone) {
		float[] force = drone.getTotalForceDrone();
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
	
	public float[] crossProduct(float[] v1, float[] v2) {
		float[] v = {v1[1] * v2[2] - v1[2] * v2[1],
					 v1[2] * v2[0] - v1[0] * v2[2],
					 v1[0] * v2[1] - v1[2] * v2[0] };
		return v;
	}
	
	public float[] product(float s, float[] v) {
		float[] p = {s*v[0], s*v[1], s*v[2]};
		return p;
	}
	
	public float scalarProduct(float[] v1, float[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}
	
	public float[] sum(float[] v1, float[] v2) {
		float[] sum = {v1[0]+v2[0], v1[1]+v2[1], v1[2]+v2[2]};
		return sum;
	}

	
}
