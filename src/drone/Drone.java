 package drone;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import engine.GameItem;
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
	
	public Wheel frontWheel;
	public Wheel leftWheel;
	public Wheel rightWheel;
	
	//Wheel waarden
	public float wheelY = -1.12f;
	public float frontWheelZ = -2.079f;
	public float rearWheelZ = 0.959f;
	public float rearWheelX = 1.24f;
	public float wheelRadius = 0.2f;
	
	public float tyreSlope = 50000f;
	public float dampSlope = 5000f;
	
	
	public float maxWrijving = 0.075f;
	public float maxRem = 2500;
	
	//Waarden van de drone
	public float wingX = 4.2f;
	public float tailSize = 4.2f;
	
	public float engineMass = 180f;
	public float wingMass = 100f;
	public float tailMass = 100f;
	
	public float maxThrust = 100000;
	public float maxAOA = (float) (Math.PI /12);
	
	
//	private Vector3f positionInWorld = new Vector3f(0,0,0);
//	
//	private Vector3f velocityInWorld = new Vector3f(0,0,0);
//
//
//	private Vector3f angularPositionInWorld = new Vector3f(0,0,0);
//	private Vector3f angularVelocityInWorld = new Vector3f(0,0,0);
	

	private static float wingLiftSlope = 10f;
	private static float horStabLiftSlope = 5f;
	private static float verStabLiftSlope = 0f;
	
//	private float xPos;
//	private float yPos;
//	private float zPos;
	
	private float heading;
	private float pitch;
	private float roll;
	
	private float headingVel;
	private float pitchVel;
	private float rollVel;

	private Matrix3f inertiaMatrix;
	
	private State state;
	
	private GameItem gameItem;

	private int id;
	
	//-1.97864475 voor 1 wing
	
	
	public Drone(float xPos, float yPos, float zPos, Vector3f velocity ) {	
		//SETTING DRONE PARTS
		this.leftWing = new Airfoil(0, wingMass,   false, wingLiftSlope, new Vector3f(-wingX,0,0),this);
		this.rightWing = new Airfoil(0, wingMass,   false, wingLiftSlope, new Vector3f(wingX,0,0),this);
		this.horStabilization = new Airfoil(0, tailMass/2, false, horStabLiftSlope, new Vector3f(0,0,tailSize),this);
		this.verStabilization = new Airfoil(0, tailMass/2, true, verStabLiftSlope,  new Vector3f(0,0,tailSize),this);
		
		this.frontWheel = new Wheel(true,wheelRadius,tyreSlope,dampSlope,maxWrijving,maxRem,new Vector3f(0, wheelY, frontWheelZ),this);
		this.leftWheel = new Wheel(false,wheelRadius,tyreSlope,dampSlope,maxWrijving,maxRem,new Vector3f(-rearWheelX, wheelY, rearWheelZ),this);
		this.rightWheel = new Wheel(false,wheelRadius,tyreSlope,dampSlope,maxWrijving,maxRem,new Vector3f(rearWheelX, wheelY, rearWheelZ),this);
		
		
		Vector3f engineRelLocation= this.getEngineLocation();
		this.engine = new Engine(0,  engineMass, engineRelLocation, this, this.maxThrust);
		
		//SETTING INERTIA MATRIX
		this.setInertiaMatrix();
		
		//SETTING POSITION AND VELOCITY STATE IN WORLD
		State initState = new State();
		initState.setPosition(new Vector3f(xPos, yPos, zPos));
		initState.setVelocity(velocity);
		this.state = initState;
	}
	
	public void setGameItem(GameItem gameItem) {
		this.gameItem = gameItem;
	}
	
	public GameItem getGameItem() {
		return this.gameItem;
	}
	
	public int getId() {
		return this.id;
	}
	
	/**
	 * Checks if the drone is crashed.
	 */
	public boolean checkCrash(){
		for(DronePart dp : this.getDroneParts()){
			if(dp.checkCrash()) return true;
		}
		return false;
	}
	
	
	//UPDATE THE DRONE AT EVERY TIME STEP
	public void update(AutopilotOutputs outputs,float time){
		
		System.out.println("========================================================================");
		System.out.println("======================== - UPDATE DRONE - ==================================");
		System.out.println("========================================================================");
		
        float scaledThrust = Math.max(0,Math.min(this.maxThrust, outputs.getThrust()));
        
        //OM TE TESTEN LATER WEGDOEN:
        
		this.fysica.print("UPDATE with time= " +time, 10);
		this.fysica.print("Autopilot outputs are: " 
		+ "\n scaled thrust:" + scaledThrust
		+ "\n thrust:" +  outputs.getThrust()
		+ "\n leftwing:" +  outputs.getLeftWingInclination()
		+ "\n rightwing:" +  outputs.getRightWingInclination()
		+ "\n hor:" +  outputs.getHorStabInclination()
		+ "\n ver:" +  outputs.getVerStabInclination()
		+ "\n FRONT BRAKE FORECE: " + outputs.getFrontBrakeForce() 
		+ "\n LEFT BRAKE FORECE: " + outputs.getLeftBrakeForce() 
		+ "\n RIGHT BRAKE FORECE: " + outputs.getRightBrakeForce(), 
		
		40);

 
        this.getLeftWing().updateInclinationAngle(outputs.getLeftWingInclination());
        this.getRightWing().updateInclinationAngle(outputs.getRightWingInclination());
        this.getHorStabilizator().updateInclinationAngle(outputs.getHorStabInclination());
        this.getVerStabilizator().updateInclinationAngle(outputs.getVerStabInclination());
        this.getEngine().setThrust(scaledThrust);

        
        this.frontWheel.update(outputs.getFrontBrakeForce(), time);
	    this.leftWheel.update(outputs.getLeftBrakeForce(), time);
	    this.rightWheel.update(outputs.getRightBrakeForce(), time);
	    

        
        //UPDATE STATE
      
        State newState = new State();
        
        Vector3f newVelocity = fysica.getNewVelocityInWorld(this, time);
 
        if(newVelocity.getZ()>0) {
        	newVelocity = new Vector3f(0,0,0);
        }
        newState.setVelocity(fysica.getNewVelocityInWorld(this, time));
        
        newState.setPosition(fysica.getNewPositionInWorld(this, time));
        
        newState.setAngularRotation(fysica.getNewAngularVelocityInWorld(this, time));
        
        
        float newHeading = this.getHeading() + (this.getHeadingVel() * time);
		this.fysica.print("-- NEW HEADING IS:" + newHeading, 30);
		
		float newPitch = this.getPitch() + this.getPitchVel() * time;
		this.setPitch(newPitch);
		this.fysica.print("-- NEW PITCH IS:" + time + " . " + this.getPitchVel()  + " = " + newPitch, 30);
		
		float newRoll = this.getRoll() + this.getRollVel() * time;
		this.setRoll(newRoll);
		this.fysica.print("-- NEW ROLL IS:" + time + " . " + this.getRollVel() +  " = " + newRoll, 30);
		
		
		newHeading = this.fysica.clean(newHeading);
		newPitch = this.fysica.clean(newPitch);
		newRoll = this.fysica.clean(newRoll);

		//UPDATE STATE->HPR
        Vector3f hpr = new Vector3f(newHeading, newPitch, newRoll);
        newState.setHPR(hpr);
		
		
		//UPDATE STATE->HPRRATES
		
		float newHeadingRate = fysica.getNewHeadingRate(this, time);
		this.fysica.print("-- NEW HEADINGRATE IS:" + newHeadingRate, 30);
		
		float newPitchRate = fysica.getNewPitchRate(this, time);
		this.fysica.print("-- NEW PITCHRATE IS:" + newPitchRate, 30);
		
		float newRollRate = fysica.getNewRollRate(this, time);
		this.fysica.print("-- NEW ROLLRATE IS:" + newRollRate, 30);
		
		Vector3f hprRates = new Vector3f(newHeadingRate, newPitchRate, newRollRate);
		newState.setHPRrates(hprRates);
        this.state = newState;

	}
	
	
	public State getState() {
		return this.state;
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
	
	public Wheel getFrontWheel(){
		return this.frontWheel;
	}
	
	public Wheel getLeftWheel(){
		return this.leftWheel;
	}
	
	public Wheel getRightWheel(){
		return this.rightWheel;
	}
	
	//
	public float getThrust() {
		return this.getEngine().getThrustScalar();
	}
	
	public float getGravity() {
		return Fysica.gravity;
	}
	
	
	
	
	//POSITION IN WORLD ---------------------------
	
//	public Vector3f getPositionInWorld() {
//		Vector3f v = new Vector3f(getXPos(), getYPos(), getZPos());
//		return v;
//	}
	
//	public void setPositionInWorld(Vector3f pos) {
//		this.xPos = pos.getX();
//		this.yPos = pos.getY();
//		this.zPos = pos.getZ();
//	}
//	
//	public void setPositionInWorld(float x, float y, float z){
//		this.xPos = x;
//		this.yPos = y;
//		this.zPos = z;
//	}
//	
//	public void setXPos(float x) {
//		this.xPos = x;
//	}
//	
//	public float getXPos() {
//		return this.xPos;
//	}
//	
//	public void setYPos(float y) {
//		this.yPos = y;
//	}
//	
//	public float getYPos() {
//		return this.yPos;
//	}
//	
//	public void setZPos(float z) {
//		this.zPos = z;
//	}
//	
//	public float getZPos() {
//		return this.zPos;
//	}
	
	
	//VELOCITY IN WORLD ---------------------------------------------------
//	public void setVelocityInWorld(Vector3f vel) {
//		this.velocityInWorld = vel;
//	}
//	
//	public Vector3f getVelocityInWorld() {
//		return this.velocityInWorld;
//	}
//	
	
	//ANGULAR POSITION IN WORLD

//	public Vector3f getAngularPositionInWorld() {
//		return this.angularPositionInWorld;
//	}
//	
//	public void setAngularPositionInWorld(Vector3f angularPosition) {
//		this.angularPositionInWorld = angularPosition;
//	}
	
	
	//ANGULAR ROTATION IN WORLD
	
//	public Vector3f getAngularVelocityInWorld() {
//		return this.angularVelocityInWorld;
//	}
//	
//	public void setAngularVelocityInWorld(Vector3f angularRotation) {
//		this.angularVelocityInWorld = angularRotation;
//	}
	
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
		DronePart[] droneParts = {
				getLeftWing(), 
				getRightWing(), 
				getHorStabilizator(), 
				getVerStabilizator(), getEngine(),getFrontWheel(),getLeftWheel(),getRightWheel()};
		return droneParts;
	}
		
	public void setHeading(float heading) {
		this.heading = fysica.clean(heading);
	}

	public float getHeading() {
		return this.getState().getHeading();
	}
	
	public void setPitch(float pitch) {
		this.pitch = fysica.clean(pitch);
	}

	public float getPitch() {
		
		//TODO getters 0
		
		return this.getState().getPitch();
		
		
		//return 0;
	}
	
	public void setRoll(float roll) {
		this.roll = fysica.clean(roll);
	}

	public float getRoll() {
		
		
		return this.getState().getRoll();
		
		
		//return 0;
	}
	
	public void setHeadingVel(float vel) {
		this.headingVel = vel;
	}
	
	public float getHeadingVel() {
		
		
		return this.getState().getHeadingRate();
		
		
		//return 0;
		
		
	}
	
	public void setPitchVel(float vel) {
		this.pitchVel = vel;
	}
	
	public float getPitchVel() {
		return this.getState().getPitchRate();
	}
	
	public void setRollVel(float vel) {
		this.rollVel = vel;
	}
	
	public float getRollVel() {
		return this.getState().getRollRate();
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
		return Drone.wingLiftSlope;
	}

	public float getHorStabLiftSlope() {
		return Drone.horStabLiftSlope;
	}

	public float getVerStabLiftSlope() {
		return Drone.verStabLiftSlope;
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
		
		Matrix3f conversionMatrix = this.fysica.getRotationMatrix(this);
		Matrix3f conversionMatrixInverted = new Matrix3f();
		Matrix3f.transpose(conversionMatrix, conversionMatrixInverted);
		
		Matrix3f inertiaMatrixInWorld = new Matrix3f();
		Matrix3f.mul(conversionMatrix, inertiaMatrix, inertiaMatrixInWorld);
		Matrix3f.mul(inertiaMatrixInWorld, conversionMatrixInverted, inertiaMatrixInWorld);
		
		return inertiaMatrixInWorld;
		
	}
	

}
