package fysica;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;
import drone.Engine;
import javax.vecmath.Matrix3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Matrix3f;


public class Fysica implements IFysica {
	
	final static float gravity = (float) 9.81;
	
	private Vector3f velocity;

	private float liftSlope;
	
	public float getGravity() {
		return this.gravity;
	}
	
	
	
	
	private Matrix3f getRollMatrix(float roll){
		
		return new Matrix3f(
				 (float) Math.cos(roll),
				(float)  Math.sin(roll),
				(float) 0.0,
				
				(float) Math.sin(roll),
				(float) Math.cos(roll),
				(float)0.0,
				
				(float) 0.0,
				(float)0.0,
				(float)1.0);
	}
	
	private Matrix3f getPitchMatrix(float Pitch){
		
		return new Matrix3f(
				(float) 1.0,
				(float)0.0,
				(float)0.0,
				
				(float)0,
				(float) Math.cos(Pitch),
				(float)-Math.sin(Pitch),
				
				(float) 0.0,
				(float)Math.sin(Pitch),
				(float)Math.cos(Pitch));
	}
	
	private Matrix3f getHeadingMatrix(float Heading){
		
		return new Matrix3f(
				 (float) Math.cos(Heading),
				(float)  Math.sin(Heading),
				(float) 0.0,
				
				(float) Math.sin(Heading),
				(float) Math.cos(Heading),
				(float)0.0,
				
				(float) 0.0,
				(float)0.0,
				(float)1.0);
	}
	
	private Vector3f convertToWorldAxes(Vector3f vector,float Roll, float Pitch, float Heading){
		Matrix3f temp= new Matrix3f();
		Matrix3f temp1= new Matrix3f();
		temp.mul(this.getRollMatrix(Roll),this.getPitchMatrix(Pitch));
		temp1.mul(this.getHeadingMatrix(Heading),temp);
		
		return temp
	}
	
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
	
	public void print(float[] force) {
		System.out.println(force[0] + " " + force[1] + " " + force[2]);
	}

	
}
