package fysica;

import org.lwjgl.util.vector.Vector3f;

import drone.Drone;
import drone.DronePart;
import drone.Wheel;

import java.util.concurrent.SynchronousQueue;

import org.lwjgl.util.vector.Matrix3f;

public class Fysica {

	public final static float gravity = (float) 9.81;
	public Vector3f totalForceOnDroneInWorld = new Vector3f(0,0,0);
	public Vector3f accelerationInWorld = new Vector3f(0,0,0);
	public Vector3f newVelocityInWorld = new Vector3f(0,0,0);
	public Vector3f newAngularVelocityInWorld = new Vector3f(0,0,0);
	
	
	public Matrix3f getRotationMatrix(Drone drone){
		float heading = drone.getHeading();
		float pitch = drone.getPitch();
		float roll = drone.getRoll();
		
		Matrix3f conversionMatrix = Rotation_matrix_Roll(roll);
		Matrix3f.mul(Rotation_matrix_Pitch(pitch), Rotation_matrix_Roll(roll), conversionMatrix);
		Matrix3f.mul(Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);
		/*
		Matrix3f.mul(Rotation_matrix_Roll(roll), Rotation_matrix_Heading(heading), conversionMatrix);
		Matrix3f.mul(conversionMatrix, Rotation_matrix_Pitch(pitch), conversionMatrix);
		*/
		return conversionMatrix;
	}
	
	public Vector3f convertToWorld(Drone drone, Vector3f Drone_vector){
		
		Vector3f worldVector = new Vector3f();
		Matrix3f conversionMatrix = getRotationMatrix(drone);
		Matrix3f.transform(conversionMatrix,Drone_vector,worldVector);
		
		return worldVector;
	}
	
	
	public Vector3f convertToDroneCords(Drone drone, Vector3f worldVector){
	
		Matrix3f conversionMatrixInverse = (Matrix3f) getRotationMatrix(drone).invert();

		Vector3f droneVector = new Vector3f();
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
		if(priority >= 10000){
			System.out.println("PRINTLOG: "+ obj);
		}
	}
	/*
	public Vector3f getTotalForceOnDroneInWorld(Drone drone, float time) {
		print("TOTAL FORCE CALC ", 50);
		
		DronePart[] partArray = drone.getDroneParts();
		Vector3f v = new Vector3f(0,0,0);
		for (DronePart part: partArray) {
			print("TOTAL FORCE of dronepart at  " + part.getRelativePosition() +" is " + part.getTotalForceInWorld(time), 50);
			v = sum(part.getTotalForceInWorld(time), v);
		}
		
		Vector3f prevV = v;
		
		
		Vector3f frontWheelForce = drone.getFrontWheel().getDronePartForce(prevV, time);
		Vector3f leftWheelForce = drone.getLeftWheel().getDronePartForce(prevV, time);
		Vector3f rightWheelForce = drone.getRightWheel().getDronePartForce(prevV, time);
		
		print("FRONT WHEEL FORCE IS: " + frontWheelForce, 50);
		print("LEFT WHEEL FORCE IS: " + leftWheelForce, 50);
		print("RIGHT WHEEL FORCE IS: " + rightWheelForce, 50);

		v = sum(drone.getFrontWheel().getDronePartForce(prevV, time),v);
		v = sum(drone.getLeftWheel().getDronePartForce(prevV, time),v);
		v = sum(drone.getRightWheel().getDronePartForce(prevV, time),v);
		
		drone.getFrontWheel().setPrevWheelForce(drone.getFrontWheel().getDronePartForce(prevV, time));
		drone.getLeftWheel().setPrevWheelForce(drone.getLeftWheel().getDronePartForce(prevV, time));
		drone.getRightWheel().setPrevWheelForce(drone.getRightWheel().getDronePartForce(prevV, time));
		
		totalForceOnDroneInWorld = v;
		
		//v.setX(0f);
		v.setY(0f);
		print("TOTAL FORCE ON DRONE is " + v, 50);
		return v;
	}
	
	public Vector3f getAccelerationInWorld(Drone drone, float time) {
		Vector3f force = this.getTotalForceOnDroneInWorld(drone, time);
		float mass = drone.getTotalMass();
		Vector3f acceleration = product(1/mass,force);
		
		accelerationInWorld = acceleration;
		print("ACC is " + acceleration, 50);

		return acceleration;
	}
	
	public Vector3f getNewVelocityInWorld(Drone drone, float time) {

		Vector3f acc = getAccelerationInWorld(drone, time);
		Vector3f at = new Vector3f(acc.getX()*time,acc.getY()*time,acc.getZ()*time);
		Vector3f droneVelocityInWorld = drone.getState().getVelocity();
		Vector3f v = sum(droneVelocityInWorld, at);		
		newVelocityInWorld = v;
		print("VEL is " + newVelocityInWorld, 50);

		return v;
	}
	*/
	public Vector3f getNewPositionInWorld(Drone drone, float time) {
		
		Vector3f droneVelocityInWorld = drone.getState().getVelocity();
		Vector3f dronePositionInWorld= drone.getState().getPosition();
		Vector3f newPositionInWorld = sum(product(time, droneVelocityInWorld), dronePositionInWorld);
		
		return newPositionInWorld;
	}
	

	// DRONE MOMENT -----------------------------------------------------------------------------------
	
	public Vector3f getMoment(Vector3f posVector, Vector3f forceVector){
		Vector3f momentVector = new Vector3f();
		Vector3f.cross(posVector, forceVector, momentVector);
		
		return momentVector;	
	}
	
	
	public Vector3f getDroneResultingMomentInWorld(Drone drone, float time){
		Vector3f momentVector = new Vector3f(0, 0, 0);
		DronePart[] partArray = drone.getDroneParts();		
		print("START RESULTNG MOMENT CALC: " + momentVector, 40);		

		for (DronePart part: partArray) {
			Vector3f posVector = part.getRelativePosition();
			Vector3f posVectorInWorld = this.convertToWorld(drone, posVector);

			Vector3f forceVector = part.getTotalForceInWorld(time);
			if(part instanceof Wheel){
				forceVector = ((Wheel) part).getPrevWheelForce();
			}
			
			Vector3f moment = this.getMoment(posVectorInWorld, forceVector);
			
			if(part instanceof Wheel){
				Wheel wheel = (Wheel) part;
				Boolean isground = wheel.isGround();
				Boolean isCrash = wheel.checkCrash();
				Vector3f bf = wheel.getBf();
				
				print(" MOMENT of dronepart at "+ posVector + "whith bforce " + bf + " IS: " + moment, 140);		
			}
			

			momentVector = sum(momentVector, moment);
			
		}
		
		momentVector.setX(0);
		momentVector.setY(3000f);
		//drone.setMoment(momentVector);
		print("RESULTING MOMENT IS: " + momentVector, 140);	
		//return(new Vector3f(00000.0f,50000.0f,0.0f));
		return momentVector;
	}
	
	public Vector3f zeroVec() {
		return new Vector3f(0,0,0); 
	}
	
		
	public Vector3f getAngularAccelerationInWorld(Drone drone, float time){
				
		Vector3f angularAcceleration = new Vector3f();
		
		Vector3f droneResultingMoment = this.getDroneResultingMomentInWorld(drone,time);
		
		Vector3f angularVelocity = drone.getState().getAngularRotation();
		
		Vector3f impulsmoment = getImpulseMoment(drone);
		Vector3f term = new Vector3f();
		Vector3f.cross(angularVelocity, impulsmoment, term);
		
		Vector3f MomentDiff = droneResultingMoment; // sum(droneResultingMoment, product(-1,term));
		
		Matrix3f momentOfInertia = drone.getIneriaMatrixInWorld();
		Matrix3f inverseMomentOfInertia = (Matrix3f) momentOfInertia.invert();
		
		Matrix3f.transform(inverseMomentOfInertia,MomentDiff,angularAcceleration);
		print("RESULTING ANG ACC IS" + angularAcceleration, 30);
	
		return angularAcceleration; 	
	}
	
	public Vector3f getImpulseMoment(Drone drone){
		Matrix3f inertia = drone.getIneriaMatrixInWorld();
		Vector3f angularVelocity = drone.getState().getAngularRotation();
		Vector3f impulseMoment = new Vector3f();
		
		Matrix3f.transform(inertia, angularVelocity, impulseMoment);
		
		return impulseMoment;
	}

	
	public Vector3f getNewAngularVelocityInWorld(Drone drone, float time){
		
		Vector3f angularAccelerationInWorld = this.getAngularAccelerationInWorld(drone, time);
		
		Vector3f at = new Vector3f(angularAccelerationInWorld.getX()*time,angularAccelerationInWorld.getY()*time,angularAccelerationInWorld.getZ()*time);
		
		Vector3f droneAngularRotationInWorld = drone.getState().getAngularRotation();
		Vector3f v = sum(droneAngularRotationInWorld, at);
		
		print("-- total droneAngularRotationInWorld is: " + droneAngularRotationInWorld, 3);
		print("-- total at is: " + at, 3);
		print("RESULTING ANG VEL IS: " + v, 30);
		
		newAngularVelocityInWorld = v;
		return v;
	}
	
	
	private float[] getHPRVelocity(Drone drone, float time){
		//TODO controleren
		Vector3f angularVelocityInWorld = this.newAngularVelocityInWorld;
				
		float wx = angularVelocityInWorld.getX();
		float wy = angularVelocityInWorld.getY();
		float wz = angularVelocityInWorld.getZ();
		
		float heading = drone.getHeading();
		float pitch = drone.getPitch();
		
		float sinh = (float) Math.sin(heading);
		float cosh = (float) Math.cos(heading);
		
		float sinp = (float) Math.sin(pitch);
		float cosp = (float) Math.cos(pitch);
		
		//TODO verandert:
//		float headingRate = wy - wz*cosh +wx*sinp*sinh/(cosp);
//		float pitchRate =  wx*cosh + wz*sinh;
//		float rollRate = wz*cosh/cosp - wx*sinh/cosp;
		
		float headingRate = wy + wz*cosh*sinp/cosp +wx*sinp*sinh/(cosp);
		float pitchRate = wx*cosh - wz*sinh;
		float rollRate = wz*cosh/cosp + wx*sinh/cosp;
		
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

	
	public float clean(float f){
		//TODO
		
		while( f > 2*Math.PI){
			f = (float) (f -2*Math.PI);
		}
		
		while(f < -2*Math.PI){
			f = (float) (f+2*Math.PI);
		}
		
		
		return f;
	}
}
