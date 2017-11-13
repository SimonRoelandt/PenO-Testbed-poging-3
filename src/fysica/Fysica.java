
package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.DroneObject;
import drone.Engine;
import org.lwjgl.util.vector.Matrix3f;

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
	
	public Vector3f Drone_vector_to_world(Vector3f Drone_vector, float pitch, float heading, float roll ){
		Matrix3f new_matrix = this.Rotation_matrix_Roll(roll);
		Matrix3f Total_matrix=new Matrix3f();
		Vector3f World_vector=new Vector3f();
		Matrix3f.mul(new_matrix, this.Rotation_matrix_Heading(heading), new_matrix);
		Matrix3f.mul(new_matrix,this.Rotation_matrix_Pitch(pitch), Total_matrix);
		Matrix3f.transform(Total_matrix,Drone_vector,World_vector);
		return World_vector;
	}
	
	
public Matrix3f Rotation_matrix_Pitch(float pitch){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) 1;
		new_matrix.m01=(float) 0;
		new_matrix.m02=(float) 0;
		new_matrix.m10=(float) 0;
		new_matrix.m11=(float) Math.cos(pitch);
		new_matrix.m12=(float) Math.sin(pitch);
		new_matrix.m20=(float) 0;
		new_matrix.m21=(float) -Math.sin(pitch);
		new_matrix.m22=(float) Math.cos(pitch);
		
		return new_matrix;
	}
	
	public Matrix3f Rotation_matrix_Heading(float heading){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) Math.cos(heading);
		new_matrix.m01=(float) 0;
		new_matrix.m02=(float) -Math.sin(heading);
		new_matrix.m10=(float) 0;
		new_matrix.m11=(float) 1;
		new_matrix.m12=(float) 0;
		new_matrix.m20=(float) -Math.sin(heading);
		new_matrix.m21=(float) 0;
		new_matrix.m22=(float) Math.cos(heading);
		
		return new_matrix;
	}
	
	public Matrix3f Rotation_matrix_Roll(float roll){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) Math.cos(roll);
		new_matrix.m01=(float) -Math.sin(roll);
		new_matrix.m02=(float) 0;
		new_matrix.m10=(float) Math.sin(roll);
		new_matrix.m11=(float) Math.cos(roll);
		new_matrix.m12=(float) 0;
		new_matrix.m20=(float) 0;
		new_matrix.m21=(float) 0;
		new_matrix.m22=(float) 1;
		
		return new_matrix;
	}
	
	
	
	public Vector3f liftForce(Airfoil air) {
		Vector3f normal = crossProduct(air.getAxisVector(),air.getAttackVector());
		Vector3f airspeed = air.getVelocityAirfoil();
		Vector3f axis = air.getAxisVector();
		Vector3f projectedAirspeed = Vector3f.sub(airspeed, mul( mul(airspeed,axis),axis),null);
		float angleOfAttack = (float) -Math.atan2(scalarProduct(projectedAirspeed,normal), scalarProduct(projectedAirspeed,air.getAttackVector()));
		Vector3f proj = new Vector3f(0,0,projectedAirspeed.getZ());
		Vector3f liftForce = product((float)(angleOfAttack *Math.pow(proj.getZ(),2)),product(air.getLiftSlope(),normal));
		//System.out.println("Lift: " + liftForce);
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
		}
//		System.out.print("Total Force:" );
//		print(v);
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
		//System.out.println("Pos: " + pos);
		Vector3f posInWorld = Drone_vector_to_world(pos,drone.getPitch(),drone.getHeading(),drone.getRoll());
		//System.out.println("PosW: " + posInWorld);
		return posInWorld;
	}
	
	
	public Vector3f velocity(Drone drone, float time) {
		Vector3f acc = acceleration(drone);
		Vector3f at = new Vector3f(acc.getX()*time,acc.getY()*time,acc.getZ()*time);
		Vector3f v = sum(drone.getVelocity(), at);
		Vector3f vInWorld = Drone_vector_to_world(v,drone.getPitch(),drone.getHeading(),drone.getRoll());
		return vInWorld;
	}
	
	public Vector3f accelerationAirfoil(Airfoil air) {
		Vector3f force = air.getTotalForce();
		Vector3f acceleration = product((1/air.getMass()),force);
		return acceleration;
	}
	
	public Vector3f nextPositionAirfoil(Airfoil air, float time) {
		Vector3f acc = accelerationAirfoil(air);
		Vector3f vel = product((float) (Math.pow(time, 2)/2),acc);
		Vector3f pos = sum(sum(air.getPos(),product(time,air.getVelocityAirfoil())),
						vel);
		return pos;
	}
	
	public Vector3f velocityAirfoil(Airfoil air, float time) {
		Vector3f acc = accelerationAirfoil(air);
		Vector3f at = new Vector3f(acc.getX()*time,acc.getY()*time,acc.getZ()*time);
		Vector3f v = sum(air.getVelocityAirfoil(),at);
		return v;
	}
	
	
	
	//Hulpfuncties
	
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
