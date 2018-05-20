package Physics;

import org.lwjgl.util.vector.Vector3f;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import drone.Airfoil;
import drone.Drone;
import drone.DronePart;
import drone.Engine;
import drone.State;
import drone.Wheel;

import java.util.concurrent.SynchronousQueue;

import org.lwjgl.util.vector.Matrix3f;

public class DronePhysics {
	
	public final static float gravity = (float) 9.81;
	private Vector3f moment;
	private Vector3f force;
	private Vector3f acceleration;
	
	public DronePhysics() {
		this.moment = new Vector3f(0,0,0);
		this.force = new Vector3f(0,0,0);
		this.acceleration = new Vector3f(0,0,0);

	}
	
	
	public Vector3f getMoment() {
		return this.moment;
	}
	
	public Vector3f getForce() {
		return this.force;
	}
	
	
	public State getNewState(Drone drone, float time) {
		State nextState = new State(drone);
		
		nextState.setPosition(this.getNewPositionInWorld(drone, time));
		nextState.setVelocity(this.getNewVelocityInWorld(drone, time));
		
		Vector3f resultMoment = this.getDroneResultingMomentInWorld(drone, time);
		
		
		//TEMP FIX
		// WHEN DRONE ON GROUND, NO PITCH MOMENT
		if(drone.getFrontWheel().isGround() && drone.getRightWheel().isGround() && drone.getLeftWheel().isGround()) {
			Vector3f resultMomentInDrone = this.convertToDroneCords(drone, resultMoment);
			resultMomentInDrone.setX(0f);
			resultMomentInDrone.setZ(0f);
			resultMoment = this.convertToWorld(drone, resultMomentInDrone);		
		}
		
		//resultMoment = new Vector3f(0,0,0);
		this.setMoment(resultMoment);
		
		//Gebruik dit om Moment te testen 
	
		
		//Vector3f MomentVectorAccToDrone = new Vector3f(600000,0,0);
		//resultMoment = this.convertToWorld(drone, MomentVectorAccToDrone);
		 
		
		Vector3f angularAcc = this.getAngularAccelerationInWorld(drone, time, resultMoment);
		
		Vector3f angularVel = this.getNewAngularVelocityInWorld(drone, time, angularAcc);
		
		nextState.setAngularRotation(angularVel);
		Vector3f hprRates = this.getHPRVelocity(drone, time, angularVel);
				
		this.print("HPRrates are: " + hprRates, 1800);
		nextState.setHPRrates(hprRates);
		
		
		float newHeadingRate = hprRates.getX();
		float newPitchRate = hprRates.getY();
		float newRollRate = hprRates.getZ();
		
        float newHeading = drone.getState().getHeading() + (newHeadingRate * time);		
		float newPitch = drone.getState().getPitch() + newPitchRate * time;		
		float newRoll = drone.getState().getRoll() + newRollRate * time;		
		
		newHeading = this.clean(newHeading);
		newPitch = this.clean(newPitch);
		newRoll = this.clean(newRoll);
        Vector3f hpr = new Vector3f(newHeading, newPitch, newRoll);
		
        nextState.setHPR(hpr);
        
		return nextState;
	}


	
	
	
	
	//------------------------------------------
	
	private void setMoment(Vector3f resultMoment) {
		this.moment = resultMoment;
	}


	public Matrix3f getRotationMatrix(Drone drone){
		float heading = drone.getHeading();
		float pitch = drone.getPitch();
		float roll = drone.getRoll();
		
		
		this.print("Roll", 200);
		this.print(Rotation_matrix_Roll(roll), 200);
		
		this.print("Pitch", 200);
		this.print(Rotation_matrix_Pitch(pitch), 200);
		
		this.print("Heading", 200);
		this.print(Rotation_matrix_Heading(heading), 200);
		
		Matrix3f I = new Matrix3f();
		I.setIdentity();
		
		/*
		Matrix3f conversionMatrix = new Matrix3f();
		conversionMatrix.setIdentity();

		//Matrix3f.mul(I, I, conversionMatrix);
		//Matrix3f.mul(Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);

		conversionMatrix.m00 = (float) (cos(heading)*cos(roll) + sin(heading)*sin(pitch)*sin(roll));
		conversionMatrix.m01 = (float) (cos(roll)*sin(heading)*sin(pitch)-cos(heading)*sin(roll));
		conversionMatrix.m02 =          (float) (cos(pitch)*sin(heading));
	        
		conversionMatrix.m10 =             (float) (cos(pitch)*sin(roll));
		conversionMatrix.m11 =           (float) (cos(pitch)*cos(roll));
		conversionMatrix.m12 =           (float) -sin(pitch);
	                
		conversionMatrix.m20 =          (float) (cos(heading)*sin(pitch)*sin(roll) - cos(roll)*sin(heading));
		conversionMatrix.m21 =           (float) (sin(heading)*sin(roll)+cos(heading)*cos(roll)*sin(pitch));
		conversionMatrix.m22 =            (float) (cos(heading)*cos(pitch));
	     */
		
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
		
		float roll = drone.getState().getRoll();
		this.print("rotation matrix to world is: " + conversionMatrix,00);
		this.print("rotation matrix roll to world  is: " + this.Rotation_matrix_Roll(roll),20);

		Matrix3f.transform(conversionMatrix,Drone_vector,worldVector);
		
		return worldVector;
	}
	
	public Matrix3f wtd(Drone drone) {
		
		Matrix3f c = new Matrix3f();
		float heading = drone.getHeading();
		float pitch = drone.getPitch();
		float roll = drone.getRoll();
		 
		c.m00 =       (float) (cos(heading)*cos(roll) + sin(heading)*sin(pitch)*sin(roll));
		c.m01 =			(float) (cos(pitch)*sin(roll));
		c.m02 =			(float) (cos(heading)*sin(pitch)*sin(roll) - cos(roll)*sin(heading));
		c.m10 =            (float) (cos(roll)*sin(heading)*sin(pitch) - cos(heading)*sin(roll));
		c.m11 =            (float) (cos(pitch)*cos(roll));
		c.m12 =            (float) (sin(heading)*sin(roll) + cos(heading)*cos(roll)*sin(pitch));
	                
		c.m20 =           (float) (cos(pitch)*sin(heading));
		c.m21 =            (float)(-sin(pitch));
		c.m22 =            (float)(cos(heading)*cos(pitch));
	                

		return c;
	}
	
	
	public Vector3f convertToDroneCords(Drone drone, Vector3f worldVector){
	
		Matrix3f conversionMatrixInverse = new Matrix3f();
		Matrix3f.transpose(this.getRotationMatrix(drone), conversionMatrixInverse);

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
			new_matrix.m12=(float) Math.sin(pitch);
			new_matrix.m20=(float) 0;
			new_matrix.m21=(float) -Math.sin(pitch);
			new_matrix.m22=(float) Math.cos(pitch);
			
			return new_matrix;
		}
	
	
	public Matrix3f Rotation_matrix_Roll(float roll){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) Math.cos(roll);
		new_matrix.m01=(float) Math.sin(roll);
		new_matrix.m02=(float) 0;
		new_matrix.m10=(float) -Math.sin(roll);
		new_matrix.m11=(float) Math.cos(roll);
		new_matrix.m12=(float) 0;
		new_matrix.m20=(float) 0;
		new_matrix.m21=(float) 0;
		new_matrix.m22=(float) 1;
		
		return new_matrix;
	}
	
	
	
	
	
	//DRONE FORCES --------------------------------------------------------------
	
	public void print(Object obj, int priority){
		if(priority >= 4000){
			System.out.println("PRINTLOG: "+ obj);
		}
	}
	
	public Vector3f getTotalForceOnDroneInWorld(Drone drone, float time) {
		print("TOTAL FORCE CALC ", 50);
		
		DronePart[] partArray = drone.getDroneParts();
		Vector3f v = new Vector3f(0,0,0);
		for (DronePart part: partArray) {
			print("TOTAL FORCE of dronepart at  " + part.getRelativePosition() +" is " + part.getTotalForceInWorld(time), 50000);
			v = sum(part.getTotalForceInWorld(time), v);
		}
		
		Vector3f prevV = v;
		
		
		Vector3f frontWheelForce = drone.getFrontWheel().getDronePartForce(prevV, time);
		Vector3f leftWheelForce = drone.getLeftWheel().getDronePartForce(prevV, time);
		Vector3f rightWheelForce = drone.getRightWheel().getDronePartForce(prevV, time);
		
		print("FRONT WHEEL FORCE IS: " + frontWheelForce, 1600);
		print("LEFT WHEEL FORCE IS: " + leftWheelForce, 50);
		print("RIGHT WHEEL FORCE IS: " + rightWheelForce, 50);


		v = sum(drone.getFrontWheel().getDronePartForce(prevV, time),v);
		v = sum(drone.getLeftWheel().getDronePartForce(prevV, time),v);
		v = sum(drone.getRightWheel().getDronePartForce(prevV, time),v);
		
		drone.getFrontWheel().setPrevWheelForce(drone.getFrontWheel().getDronePartForce(prevV,time));
		drone.getLeftWheel().setPrevWheelForce(drone.getLeftWheel().getDronePartForce(prevV, time));
		drone.getRightWheel().setPrevWheelForce(drone.getRightWheel().getDronePartForce(prevV, time));
				
		print(" v is " + v, 1);
		
		v = this.convertToDroneCords(drone, v);
		print(" vv is " + v, 1);
		//v.setX(0f);
		
		v = this.convertToWorld(drone, v);
		print(" vvv is " + v, 1);
		this.force = v;
		return v;
	}
	
	public Vector3f getAccelerationInWorld(Drone drone, float time) {
		Vector3f force = this.getTotalForceOnDroneInWorld(drone, time);
		float mass = drone.getTotalMass();
		Vector3f acceleration = product(1/mass,force);
		
		print("ACC is " + acceleration, 50);

		this.setAcceleration(acceleration);
		return acceleration;
	}
	
	private void setAcceleration(Vector3f a) {
		this.acceleration = a;
	}
	
	public Vector3f getAcceleration() {
		return this.acceleration;
	}
	
	public Vector3f getNewVelocityInWorld(Drone drone, float time) {

		Vector3f acc = getAccelerationInWorld(drone, time);
		Vector3f at = new Vector3f(acc.getX()*time,acc.getY()*time,acc.getZ()*time);
		Vector3f droneVelocityInWorld = drone.getState().getVelocity();
		Vector3f v = sum(droneVelocityInWorld, at);		

		return v;
	}
	
	public Vector3f getNewPositionInWorld(Drone drone, float time) {
		
		Vector3f droneVelocityInWorld = drone.getState().getVelocity();
		Vector3f dronePositionInWorld= drone.getState().getPosition();
		Vector3f newPositionInWorld = sum(product(time, droneVelocityInWorld), dronePositionInWorld);
		
		return newPositionInWorld;
	}
	
	
	public Vector3f zeroVec() {
		return new Vector3f(0,0,0); 
	}
	


	
// DRONE MOMENTS --------------------------------------------------------------------
	
	public Vector3f getMoment(Vector3f posVector, Vector3f forceVector){
		Vector3f momentVector = new Vector3f();
		Vector3f.cross(posVector, forceVector, momentVector);
		
		return momentVector;	
	}
	
	public Vector3f getDroneResultingMomentInWorld(Drone drone, float time){
		Vector3f momentVector = new Vector3f(0, 0, 0);
		DronePart[] partArray = drone.getDroneParts();		
		print("START RESULTNG MOMENT CALC: " + momentVector, 1400);		

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
				
				print(" MOMENT of dronepart at "+ posVector + "whith force " + forceVector + " IS: " + moment, 1400);		
				print(" MOMENT of dronepart at "+ posVector + "whith bforce " + bf + " IS: " + this.getMoment(posVectorInWorld, bf), 1400);		

			}


			momentVector = sum(momentVector, moment);
			
		}
		
		
		
		//drone.setMoment(momentVector);
		print("RESULTING MOMENT IS: " + momentVector, 1400);	
		//return(new Vector3f(00000.0f,50000.0f,0.0f));
		return momentVector;
	}
	
	public Vector3f getResultingMoment() {
		
		return moment;
	}
	
	
	
	public Vector3f getDroneResultingMomentLocal(Drone drone, float time){
		Vector3f momentVector = new Vector3f(0, 0, 0);
		DronePart[] partArray = drone.getDroneParts();		
		print("START RESULTNG MOMENT CALC: " + momentVector, 1400);		

		for (DronePart part: partArray) {
			if(part instanceof Airfoil && part.getRelativePosition().z == 0){
			Vector3f posVector = part.getRelativePosition();
			Vector3f forceVector = part.getDronePartForce();

			Vector3f moment = this.getMoment(posVector, forceVector);

			momentVector = sum(momentVector, moment);
			}
			
		}
		// momentVector = new Vector3f(0, 0, 0);

		return momentVector;
				
	}
		
	public Vector3f getAngularAccelerationOnDrone(Drone drone, float time, Vector3f resultMoment){
		Vector3f angularAcceleration = new Vector3f();
		
		Vector3f ResultingMomentOnDrone = getDroneResultingMomentLocal(drone, time);
		//ResultingMomentOnDrone.setX(0f);
		//ResultingMomentOnDrone.setY(0f);

		Matrix3f inverseMomentOfInertia = new Matrix3f();
		Matrix3f MomentOfInertia = (Matrix3f) drone.getIneriaMatrixInWorld();
		Matrix3f.invert(MomentOfInertia, inverseMomentOfInertia);
		
		
		Matrix3f inverseMomentOfInertiaOnDrone = new Matrix3f();
		Matrix3f MomentOfInertiaOnDrone = (Matrix3f) drone.getIneriaMatrixInWorld();
		Matrix3f.invert(MomentOfInertiaOnDrone, inverseMomentOfInertiaOnDrone);
		
		//Matrix3f MomentOfInertiaDrone = (Matrix3f) drone.getInertiaMatrix();
		//Matrix3f MomentOfInertiaDrone = .invert();

		angularAcceleration.setX(ResultingMomentOnDrone.x * inverseMomentOfInertiaOnDrone.m00);
		angularAcceleration.setY(ResultingMomentOnDrone.y * inverseMomentOfInertiaOnDrone.m11);
		angularAcceleration.setZ(ResultingMomentOnDrone.z * inverseMomentOfInertiaOnDrone.m22);
		
		print("RESULTING ANG ACC IS" + angularAcceleration, 30);
		this.acceleration = angularAcceleration;
		return angularAcceleration; 	
		
		
	}

	
	public Vector3f getAngularAccelerationInWorld(Drone drone, float time, Vector3f resultMoment){
		
		Vector3f angularAcceleration = new Vector3f();
		
		Vector3f droneResultingMoment = resultMoment;
		
		Vector3f angularVelocity = drone.getState().getAngularRotation();
		
		Matrix3f momentOfInertia = drone.getInertiaMatrix();
		Matrix3f inertiaMatrixInWorld = new Matrix3f();
		
		Matrix3f.mul(this.getRotationMatrix(drone), momentOfInertia, inertiaMatrixInWorld);
		//Matrix3f.mul(inertiaMatrixInWorld, conversionMatrixInverted, inertiaMatrixInWorld);
		
		Matrix3f inverseMomentOfInertia = (Matrix3f) inertiaMatrixInWorld.invert();
		
		
		Vector3f impulsmoment = getImpulseMoment(drone);
		Vector3f term = new Vector3f();
		Vector3f.cross(angularVelocity, impulsmoment, term);
		
		Vector3f MomentDiff = sum(droneResultingMoment, product(-1,term));
		MomentDiff = droneResultingMoment;
		
		
		Matrix3f.transform(inverseMomentOfInertia,MomentDiff,angularAcceleration);
		

		
		print("RESULTING ANG ACC IS" + angularAcceleration, 30);
	
		
		Vector3f aod = this.getAngularAccelerationOnDrone(drone, time, resultMoment);
		return this.convertToWorld(drone, aod); 	
		
	}
	
	public Vector3f getImpulseMoment(Drone drone){
		Matrix3f inertia = drone.getIneriaMatrixInWorld();
		Vector3f angularVelocity = drone.getState().getAngularRotation();
		Vector3f impulseMoment = new Vector3f();
		
		Matrix3f.transform(inertia, angularVelocity, impulseMoment);
		
		return impulseMoment;
	}

	
	public Vector3f getNewAngularVelocityInWorld(Drone drone, float time, Vector3f angularAcc ){
		
		Vector3f angularAccelerationInWorld = angularAcc;
		
		Vector3f at = new Vector3f(angularAccelerationInWorld.getX()*time,angularAccelerationInWorld.getY()*time,angularAccelerationInWorld.getZ()*time);
		
		Vector3f droneAngularRotationInWorld = drone.getState().getAngularRotation();
		Vector3f v = sum(droneAngularRotationInWorld, at);
		
		print("-- total droneAngularRotationInWorld is: " + droneAngularRotationInWorld, 3);
		print("-- total at is: " + at, 3);
		print("RESULTING ANG VEL IS: " + v, 30);
		return v;
	}
	
	
	private Vector3f getHPRVelocity(Drone drone, float time, Vector3f angularVel){
		//TODO controleren
		Vector3f angularVelocityInWorld = angularVel;
				
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
		
		float headingRate = (wy + wz*cosh*sinp/cosp +wx*sinp*sinh/(cosp));
		float pitchRate = 0; //(wx*cosh - wz*sinh);
		
	//	float rollRate = (wz*cosh/cosp + wx*sinh/cosp);
		float rollRate = (wz*cosh/cosp + wx*sinh/cosp);
		
		return new Vector3f(headingRate, pitchRate, rollRate);
	}
	
	/*public float getNewHeadingRate(Drone drone, float time){
		return this.getHPRVelocity(drone, time)[0];
	}
	
	public float getNewPitchRate(Drone drone, float time){
		return this.getHPRVelocity(drone, time)[1];
	}
	
	public float getNewRollRate(Drone drone, float time){
		return this.getHPRVelocity(drone, time)[2];

	}
	*/
	
	
	
	
	// --HULPFUNCTIES ---
	
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

