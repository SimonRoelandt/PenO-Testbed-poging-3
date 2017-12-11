package drone;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;
import interfaces.AutopilotOutputs;
import fysica.Fysica;


public class Drone {
	
	//Fysica
	public Fysica fysica = new Fysica();

	//Onderdelen van de drone
	public Airfoil leftWing;
	public Airfoil rightWing;
	public Airfoil horStabilization;
	public Airfoil verStabilization;
	public Engine engine;
	
	//Waarden van de drone
	public float wingX = 0.5f;
	public float tailSize = 0.5f;
	
	public float engineMass = 0.25f;
	public float wingMass = 0.25f;
	public float tailMass = 0.125f;
	
	public float maxThrust = 1000;
	public float maxAOA = (float) (Math.PI /12);
	
	
	private Vector3f positionInWorld = new Vector3f(0,0,0);
	private Vector3f velocityInWorld = new Vector3f(0,0,0);


	private Vector3f angularPositionInWorld = new Vector3f(0,0,0);
	private Vector3f angularRotationInWorld = new Vector3f(0,0,0);
	

	private static float wingLiftSlope = 0.1f;
	private static float horStabLiftSlope = 0.05f;
	private static float verStabLiftSlope = 0.05f;
	
	private float xPos;
	private float yPos;
	private float zPos;
	
	private float heading;
	private float pitch;
	private float roll;
	
	private float headingVel;
	private float pitchVel;
	private float rollVel;

	private Matrix3f inertiaMatrix;
	
	
	//-1.97864475 voor 1 wing
	
	
	public Drone(float xPos, float yPos, float zPos, Vector3f velocity ) {
		//float incl = (float) -2.08343282;
		
		//SETTING POSITION AND VELOCITY IN WORLD
		this.setPositionInWorld(xPos, yPos, zPos);
		this.setVelocityInWorld(velocity);

		
		this.leftWing         = new Airfoil(0, wingMass,   false, 0.1f, new Vector3f(-wingX,0,0));
		this.leftWing.setDrone(this);

		this.rightWing        = new Airfoil(0, wingMass,   false, 0.1f, new Vector3f(wingX,0,0));
		this.rightWing.setDrone(this);

		this.horStabilization = new Airfoil(0, tailMass/2, false, 0f, new Vector3f(0,0,tailSize));
		this.horStabilization.setDrone(this);
		
		this.verStabilization = new Airfoil(0, tailMass/2, false, 0f,  new Vector3f(0,0,tailSize));
		this.verStabilization.setDrone(this);
		
		Vector3f engineRelLocation= this.getEngineLocation();

		this.engine           = new Engine(0,  engineMass, engineRelLocation);
		this.engine.setDrone(this);
		
		this.engine.setMaxThrust(maxThrust);
		//setting inertia matrix
		this.setInertiaMatrix();
		
		
	}
	
	//Bepaalt de verandering die elke stap gebeurt
	

	public void update(AutopilotOutputs outputs,float time){
		
        float scaledThrust = Math.max(0,Math.min(5, outputs.getThrust()));

		
		this.fysica.print("UPDATE with time= " +time, 10);
		this.fysica.print("Autopilot outputs are: " 
		+ ", scaled thrust:" + scaledThrust
		+ ", thrust:" +  outputs.getThrust()
		+ ", wings:" +  outputs.getLeftWingInclination()
		+ ", hor:" +  outputs.getHorStabInclination()
		+ ", ver:" +  outputs.getVerStabInclination(), 10);

        
        this.getEngine().setThrust(scaledThrust);

        this.getLeftWing().updateInclinationAngle(outputs.getLeftWingInclination());
        this.getRightWing().updateInclinationAngle(outputs.getRightWingInclination());
        this.getHorStabilizator().updateInclinationAngle(outputs.getHorStabInclination());        
        this.getVerStabilizator().updateInclinationAngle(outputs.getVerStabInclination());

        
        this.fysica.print("resulting force is:" + 
        		this.fysica.getTotalForceOnDroneInWorld(this), 10);
        
        this.fysica.print("resulting acceleration is:" + 
        		this.fysica.getAccelerationInWorld(this), 10);
        
        this.fysica.print("old speed is:" + 
        		this.getVelocityInWorld(), 10);
        
        this.fysica.print("a*t is:" + 
        		this.fysica.product(time, this.fysica.getAccelerationInWorld(this)), 10);
       
        this.fysica.print("resulting speed is:" + 
        		this.fysica.getNewVelocityInWorld(this, time), 10);
        
        //UPDATE POSITION AND VELOCITY IN WORLD
		Vector3f p = fysica.getNewPositionInWorld(this, time);
		this.setPositionInWorld(p);
		
		Vector3f v = fysica.getNewVelocityInWorld(this, time);
		this.setVelocityInWorld(v);
		
		 
        //UPDATE HEAD PITCH ROLL POSITION AND RATE
		
		
		//HEADING
		
		float newHeading = this.getHeading() + this.getHeadingVel() * time;
		this.setHeading(newHeading);
		
		float newHeadingRate = fysica.getNewHeadingRate(this, time);
		this.setHeadingVel(newHeadingRate);
		
		
		//HEADING
		
		float newPitch = this.getPitch() + this.getPitchVel() * time;
		this.setPitch(newPitch);
		
		float newPitchRate = fysica.getNewPitchRate(this, time);
		this.setHeadingVel(newPitchRate);
		
		
		//ROLL
		
		float newRoll = this.getRoll() + this.getRollVel() * time;
		this.setRoll(newRoll);
		
		float newRollRate = fysica.getNewRollRate(this, time);
		this.setRollVel(newRollRate);
		
		
		this.fysica.print("HPR: " + newHeading + newPitch + newRoll, 10);
        
	}
	
	
	//geeft alle airfoils + engine
	public Airfoil getLeftWing() {
		return this.leftWing;
	}
	
	public Airfoil getRightWing() {
		return this.rightWing;
	}
	
	public Airfoil getHorStabilizator() {
		return this.horStabilization;
	}
	
	public Airfoil getVerStabilizator() {
		return this.verStabilization;
	}
	
	public Engine getEngine() {
		return this.engine;
	}
	
	//
	public float getThrust() {
		return this.getEngine().getThrustScalar();
	}
	
	public float getGravity() {
		return fysica.getGravity();
	}
	
	
	
	
	//POSITION IN WORLD ---------------------------
	
	public Vector3f getPositionInWorld() {
		Vector3f v = new Vector3f(getXPos(), getYPos(), getZPos());
		Vector3f n = new Vector3f(0,0,0);
		return v;
	}
	
	public void setPositionInWorld(Vector3f pos) {
		this.xPos = pos.getX();
		this.yPos = pos.getY();
		this.zPos = pos.getZ();
	}
	
	public void setPositionInWorld(float x, float y, float z){
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
	}
	
	public void setXPos(float x) {
		this.xPos = x;
	}
	
	public float getXPos() {
		return this.xPos;
	}
	
	public void setYPos(float y) {
		this.yPos = y;
	}
	
	public float getYPos() {
		return this.yPos;
	}
	
	public void setZPos(float z) {
		this.zPos = z;
	}
	
	public float getZPos() {
		return this.zPos;
	}
	
	
	//VELOCITY IN WORLD ---------------------------------------------------
	public void setVelocityInWorld(Vector3f vel) {
		this.velocityInWorld = vel;
		
	/*	Niet overtuigd van deze manier van werken
	 * this.velocity = vel;
		this.getLeftWing().setVelocityAirfoil(vel);
		this.getRightWing().setVelocityAirfoil(vel);
		this.getHorStabilizator().setVelocityAirfoil(vel);
		this.getVerStabilizator().setVelocityAirfoil(vel);
		
		*/
	}
	
	public Vector3f getVelocityInWorld() {
		return this.velocityInWorld;
	}
	
	
	//ANGULAR POSITION IN WORLD

	public Vector3f getAngularPositionInWorld() {
		// TODO Auto-generated method stub
		return this.angularPositionInWorld;
	}
	
	public void setAngularPositionInWorld(Vector3f angularPosition) {
		this.angularPositionInWorld = angularPosition;
	}
	
	
	//ANGULAR ROTATION IN WORLD
	
	public Vector3f getAngularRotationInWorld() {
		// TODO Auto-generated method stub
		return this.angularRotationInWorld;
	}
	
	public void setAngularRotationInWorld(Vector3f angularRotation) {
		this.angularRotationInWorld = angularRotation;
	}
	

	//
	
	public void setThrust(float thrust) {
		this.getEngine().setThrust(thrust);
	}
	
	public void setLeftWingInclination(float incl) {
		this.getLeftWing().setInclination(incl);
	}
	
	public float getLeftWingInclination() {
		return this.getLeftWing().getInclination();
	}
	
	public void setRightWingInclination(float incl) {
		this.getRightWing().setInclination(incl);
	}
	
	public float getRightWingInclination() {
		return this.getRightWing().getInclination();
	}
	
	public void setHorStabInclination(float incl) {
		this.getHorStabilizator().setInclination(incl);
	}
	
	public float getHorStabInclination() {
		return this.getHorStabilizator().getInclination();
	}
	
	public void setVerStabInclination(float incl) {
		this.getVerStabilizator().setInclination(incl);
	}
	
	public float getVerStabInclination() {
		return this.getVerStabilizator().getInclination();
	}
	

	public DronePart[] getDroneParts() {
		DronePart[] droneParts = {getLeftWing(), getRightWing(), getHorStabilizator(), getVerStabilizator(), getEngine()};
		return droneParts;
	}

		
	public void setHeading(float heading) {
		this.heading = heading;
	}

	public float getHeading() {
		return this.heading;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getPitch() {
		return this.pitch;
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getRoll() {
		return this.roll;
	}
	
	public void setHeadingVel(float vel) {
		this.headingVel = vel;
	}
	
	public float getHeadingVel() {
		return this.headingVel;
	}
	
	public void setPitchVel(float vel) {
		this.pitchVel = vel;
	}
	
	public float getPitchVel() {
		return this.pitchVel;
	}
	
	public void setRollVel(float vel) {
		this.rollVel = vel;
	}
	
	public float getRollVel() {
		return this.rollVel;
	}
	
	//Getters van alle finals
	
	public float getWingX() {
		return this.wingX;
	}
	
	public float getTailSize() {
		return tailSize;
	}
	
	
	//MASS FUNCTIONS
	
	public float getTotalMass(){
		return this.getEngineMass() + 2*this.getWingMass()+this.getTailMass();
	}
	
	public float getEngineMass() {
		return this.getEngine().getMass();
	}

	public Vector3f getEngineLocation() {
		Vector3f EngineLocation= new Vector3f(0,0,-this.tailSize*this.tailMass/this.engineMass);
		return EngineLocation;
	}
	
	//Alle vleugels hebben dezelfde massa
	public float getWingMass() {
		return this.getLeftWing().getMass();
	}
		
	public float getTailMass() {
		return this.getHorStabilizator().getMass() + this.getVerStabilizator().getMass();
	}
	
	public float getMaxThrust() {
		return this.getEngine().getMaxthrust();
	}
	
	public float getMaxAOA() {
		return this.maxAOA;
	}
	
	public float getWingLiftSlope() {
		return this.wingLiftSlope;
	}

	public float getHorStabLiftSlope() {
		return this.horStabLiftSlope;
	}

	public float getVerStabLiftSlope() {
		return this.verStabLiftSlope;
	}
	
	
	
	//INERTIA MATRICES

	public void setInertiaMatrix() { // bij berekeningen transformeren naar wereldassenstelsel
		Matrix3f inertiaMatrix = new Matrix3f();
		
		inertiaMatrix.m00= 
				(float) (this.tailMass*Math.pow(this.tailSize,2) + 
						this.engineMass*Math.pow(this.getEngineLocation().getZ(),2));//nieuwe functie aangemaakt met engineplace op basis van zwaartepunt
		inertiaMatrix.m01=0f;
		inertiaMatrix.m02=0f;
		
		inertiaMatrix.m10=0f;
		inertiaMatrix.m11=(float) ((2*this.wingMass*Math.pow(this.wingX,2))+ (this.tailMass*Math.pow(this.tailSize,2)+this.engineMass*Math.pow(this.getEngineLocation().getZ(),2)));
		inertiaMatrix.m12=0f;
		
		inertiaMatrix.m20=0f;
		inertiaMatrix.m21=0f;
		inertiaMatrix.m22=(float)(2*this.wingMass*Math.pow(this.wingX,2));
		
		this.inertiaMatrix=inertiaMatrix;
		
	}


	//INERTIA MATRIX
	
	private Matrix3f getInertiaMatrix(){
		return this.inertiaMatrix;
	}
	
	public Matrix3f getIneriaMatrixInWorld(){
		
		Matrix3f inertiaMatrix = this.getInertiaMatrix();
		
		Matrix3f conversionMatrix = this.fysica.getRotationMatrix();
		Matrix3f conversionMatrixInverted = new Matrix3f();
		Matrix3f.transpose(conversionMatrix, conversionMatrixInverted);
		
		Matrix3f inertiaMatrixInWorld = new Matrix3f();
		Matrix3f.mul(conversionMatrix, inertiaMatrix, inertiaMatrixInWorld);
		Matrix3f.mul(inertiaMatrixInWorld, conversionMatrixInverted, inertiaMatrixInWorld);
		
		return inertiaMatrixInWorld;
		
	}
	

}
