
package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.DronePart;
import drone.Engine;
import org.lwjgl.util.vector.Matrix3f;

public class Fysica {
	

	final static float gravity = (float) 9.81;
	
	public float getGravity() {
		return this.gravity;
	}
	
	//private float[] velocity= {0,25,0};

	
	
	//HEAD PITCH ROLL
	
	private float heading;
	private float pitch;
	private float roll;
	
	
	public Matrix3f getRotationMatrix(){
		
		Matrix3f conversionMatrix = this.Rotation_matrix_Roll(roll);
		Matrix3f.mul(this.Rotation_matrix_Pitch(pitch), this.Rotation_matrix_Roll(roll), conversionMatrix);
		Matrix3f.mul(this.Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);
		
		return conversionMatrix;
		
	}
	
	public Vector3f convertToWorld(Vector3f Drone_vector){
		
		float heading = this.getHeading();
		float pitch = this.getPitch();
		float roll = this.getRoll();
		
	
		Matrix3f conversionMatrix = this.Rotation_matrix_Roll(roll);
		Vector3f worldVector=new Vector3f();
		Matrix3f.mul(this.Rotation_matrix_Pitch(pitch), this.Rotation_matrix_Roll(roll), conversionMatrix);
		Matrix3f.mul(this.Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);
		Matrix3f.transform(conversionMatrix,Drone_vector,worldVector);
		
		return worldVector;
	}
	
	
	public Vector3f convertToDroneCords(Vector3f worldVector){
		float heading = this.getHeading();
		float pitch = this.getPitch();
		float roll = this.getRoll();
		
	
		Matrix3f conversionMatrix = this.Rotation_matrix_Roll(roll);
		Matrix3f conversionMatrixInverse = new Matrix3f();

		Vector3f droneVector=new Vector3f();
		Matrix3f.mul(this.Rotation_matrix_Pitch(pitch), this.Rotation_matrix_Roll(roll), conversionMatrix);
		Matrix3f.mul(this.Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);
		
		conversionMatrixInverse = (Matrix3f) conversionMatrix.invert();
		Matrix3f.transform(conversionMatrixInverse,worldVector,droneVector);
		
		return droneVector;
		
	}
	
public Matrix3f Rotation_matrix_Heading(float heading){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) Math.cos(heading);
		new_matrix.m01=(float) 0;
		new_matrix.m02=(float) Math.sin(heading);
		new_matrix.m10=(float) 0;
		new_matrix.m11=(float) 1;
		new_matrix.m12=(float) 0;
		new_matrix.m20=(float) -Math.sin(heading);
		new_matrix.m21=(float) 0;
		new_matrix.m22=(float) Math.cos(heading);
		
		return new_matrix;
	}
	
	
	public Matrix3f Rotation_matrix_Pitch(float pitch){
			
			Matrix3f new_matrix = new Matrix3f();
			new_matrix.m00=(float) 1;
			new_matrix.m01=(float) 0;
			new_matrix.m02=(float) 0;
			new_matrix.m10=(float) 0;
			new_matrix.m11=(float) Math.cos(pitch);
			new_matrix.m12=(float) -Math.sin(pitch);
			new_matrix.m20=(float) 0;
			new_matrix.m21=(float) Math.sin(pitch);
			new_matrix.m22=(float) Math.cos(pitch);
			
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
	
	
	//TOTAL DRONE FORCES --------------------------------------------------------------
	
	public void print(Object obj, int priority){
		if(priority >= 10){
			System.out.println("PRINTLOG: "+ obj);
		}
	}
	
	public Vector3f getTotalForceOnDroneInWorld(Drone drone) {
		DronePart[] partArray = drone.getDroneParts();
		Vector3f v = new Vector3f(0,0,0);
		for (DronePart part: partArray) {
			v = sum(part.getTotalForceInWorld(), v);
		}
		print("total force is: " + v, 10);

		return v;
	}
	
	public Vector3f getAccelerationInWorld(Drone drone) {
		Vector3f force = this.getTotalForceOnDroneInWorld(drone);
		float mass = drone.getTotalMass();
		Vector3f acceleration = product((1/mass), force);
		System.out.println("++++++++++++++++++++++++++++++++++++" + acceleration);
		return acceleration;
		
	}
	
	public Vector3f getNewVelocityInWorld(Drone drone, float time) {
		Vector3f acc = getAccelerationInWorld(drone);
		Vector3f at = new Vector3f(acc.getX()*time,acc.getY()*time,acc.getZ()*time);
		Vector3f droneVelocityInWorld = drone.getVelocityInWorld();
		Vector3f v = sum(droneVelocityInWorld, at);
	
		return v;
	}
	
	public Vector3f getNewPositionInWorld(Drone drone, float time) {
		
		Vector3f droneVelocityInWorld = drone.getVelocityInWorld();
		Vector3f dronePositionInWorld= drone.getPositionInWorld();
		Vector3f newPositionInWorld = sum(product(time, droneVelocityInWorld), dronePositionInWorld);
		
		return newPositionInWorld;
	}
	

	// DRONE MOMENT -----------------------------------------------------------------------------------
	
	public Vector3f getMoment(Vector3f posVector, Vector3f forceVector){
		Vector3f momentVector = new Vector3f();
		Vector3f.cross(posVector, forceVector, momentVector);
		
		return momentVector;	
	}
	
	
	public Vector3f getDroneResultingMomentInWorld(Drone drone){
		
		Vector3f momentVector = new Vector3f(0, 0, 0);
		DronePart[] partArray = drone.getDroneParts();
		
		print("--Start calc moment", 3);
		
		for (DronePart part: partArray) {
			Vector3f posVector = part.getRelativePosition();
			Vector3f posVectorInWorld = this.convertToWorld(posVector);
			Vector3f forceVector = part.getTotalForceInWorld();
			
			Vector3f moment = this.getMoment(posVectorInWorld, forceVector);
			
			print("calc moment: " + posVectorInWorld + " " + forceVector +" resulted in " + moment, 3);
			momentVector = sum(momentVector, moment);
		}
		
		print("---Resulting Moment: " + momentVector, 3);		
		return momentVector;
	}
	
	
		
	public Vector3f getAngularAccelerationInWorld(Drone drone){
		
		
		print("--calc angular acc ", 3);
		
		Vector3f angularAcceleration = new Vector3f();
		
		Vector3f droneResultingMoment = this.getDroneResultingMomentInWorld(drone);
		Matrix3f momentOfInertia = drone.getIneriaMatrixInWorld();
		Matrix3f inverseMomentOfInertia = (Matrix3f) momentOfInertia.invert();
		
		Matrix3f.transform(inverseMomentOfInertia,droneResultingMoment,angularAcceleration);
		
		
		print("moment is " + droneResultingMoment, 3);
		print("imoi is " + inverseMomentOfInertia, 3);
		
		print("ang acc is " + angularAcceleration, 3);
		
		return angularAcceleration; 
		
	}

	
	private Vector3f getNewAngularVelocityInWorld(Drone drone, float time){
		
		Vector3f angularAccelerationInWorld = this.getAngularAccelerationInWorld(drone);
		Vector3f at = new Vector3f(angularAccelerationInWorld.getX()*time,angularAccelerationInWorld.getY()*time,angularAccelerationInWorld.getZ()*time);
		
		Vector3f droneAngularRotationInWorld = drone.getAngularRotationInWorld();
		Vector3f v = sum(droneAngularRotationInWorld, at);
		
		print("-- total droneAngularRotationInWorld is: " + droneAngularRotationInWorld, 3);
		print("-- total at is: " + at, 3);
		print("-- total ang vel is: " + v, 3);

		return v;
	}
	
	
	private float[] getHPRVelocity(Drone drone, float time){
		
		
		Vector3f angularVelocityInWorld = this.getNewAngularVelocityInWorld(drone, time);
	
		
		float wx = angularVelocityInWorld.getX();
		float wy = angularVelocityInWorld.getY();
		float wz = angularVelocityInWorld.getZ();
		
		float heading = this.getHeading();
		float pitch = this.getPitch();
		
		float sinh = (float) Math.sin(heading);
		float cosh = (float) Math.cos(heading);
		
		float sinp = (float) Math.sin(pitch);
		float cosp = (float) Math.cos(pitch);
		
		float headingRate = wy - wz*cosh +wx*sinp*sinh/(cosp);
		float pitchRate = wx*cosh + wz*sinh;
		float rollRate = wz*cosh/cosp - wx*sinh/cosp;
		
		float[] rates = new float[] {headingRate, pitchRate, rollRate};
		return rates;
	}
	
	public float getNewHeadingRate(Drone drone, float time){
		return this.getHPRVelocity(drone, time)[0];
	}
	
	public float getNewPitchRate(Drone drone, float time){
		return this.getHPRVelocity(drone, time)[1];
	}
	
	
	public float getNewRollRate(Drone drone, float time){
		return this.getHPRVelocity(drone, time)[2];

	}
	
	
	
	
	
	
	//HULPFUNCTIES
	
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


	public float getHeading() {
		return heading;
	}


	public void setHeading(float heading) {
		this.heading = heading;
	}


	public float getPitch() {
		return pitch;
	}


	public void setPitch(float pitch) {
		this.pitch = pitch;
	}


	public float getRoll() {
		return roll;
	}


	public void setRoll(float roll) {
		this.roll = roll;
	}

	
}
