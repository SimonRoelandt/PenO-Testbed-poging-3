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
	
	public Wheel frontWheel;
	public Wheel leftWheel;
	public Wheel rightWheel;
	
	//Wheel waarden
	public float wheelY = -1.12f;
	public float frontWheelZ = -2.079f;
	public float rearWheelZ = 0.959f;
	public float rearWheelX = 1.24f;
	public float wheelRadius = 0.2f;
	
	public float tyreSlope = 50000;
	public float dampSlope = 5000;
	public float maxWrijving = 2486;
	
	//Waarden van de drone
	public float wingX = 4.2f;
	public float tailSize = 4.2f;
	
	public float engineMass = 180f;
	public float wingMass = 100f;
	public float tailMass = 100f;
	
	public float maxThrust = 100000;
	public float maxAOA = (float) (Math.PI /12);
	
	
	private Vector3f positionInWorld = new Vector3f(0,0,0);
	
	private Vector3f velocityInWorld = new Vector3f(0,0,0);


	private Vector3f angularPositionInWorld = new Vector3f(0,0,0);
	private Vector3f angularVelocityInWorld = new Vector3f(0,0,0);
	

	private static float wingLiftSlope = 10f;
	private static float horStabLiftSlope = 5f;
	private static float verStabLiftSlope = 5f;
	
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
	
	private State state;
	
	
	//-1.97864475 voor 1 wing
	
	
	public Drone(float xPos, float yPos, float zPos, Vector3f velocity ) {	
		//SETTING DRONE PARTS
		this.leftWing = new Airfoil(0, wingMass,   false, wingLiftSlope, new Vector3f(-wingX,0,0),this);
		this.rightWing = new Airfoil(0, wingMass,   false, wingLiftSlope, new Vector3f(wingX,0,0),this);
		this.horStabilization = new Airfoil(0, tailMass/2, false, horStabLiftSlope, new Vector3f(0,0,tailSize),this);
		this.verStabilization = new Airfoil(0, tailMass/2, true, verStabLiftSlope,  new Vector3f(0,0,tailSize),this);
		
		this.frontWheel = new Wheel(wheelRadius,tyreSlope,dampSlope,maxWrijving,new Vector3f(0, wheelY, frontWheelZ),this);
		this.leftWheel = new Wheel(wheelRadius,tyreSlope,dampSlope,maxWrijving,new Vector3f(-rearWheelX, wheelY, rearWheelZ),this);
		this.rightWheel = new Wheel(wheelRadius,tyreSlope,dampSlope,maxWrijving,new Vector3f(rearWheelX, wheelY, rearWheelZ),this);
		
		
		Vector3f engineRelLocation= this.getEngineLocation();
		this.engine = new Engine(0,  engineMass, engineRelLocation, this, this.maxThrust);
		
		//SETTING INERTIA MATRIX
		this.setInertiaMatrix();
		
		//SETTING POSITION AND VELOCITY STATE IN WORLD
		State initState = new State();
		state.setNextPosition(new Vector3f(xPos, yPos, zPos));
		state.setNextVelocity(velocity);
		this.state = initState;
		this.goToNextState();
	}
	
	
	
	//UPDATE THE DRONE AT EVERY TIME STEP
	public void update(AutopilotOutputs outputs,float time){
		
		System.out.println("========================================================================");
		System.out.println("======================== UPDATE DRONE ==================================");
		System.out.println("========================================================================");
		
        //float scaledThrust = Math.max(0,Math.min(this.maxThrust, outputs.getThrust()));
        float scaledThrust = 0;
        
        //OM TE TESTEN LATER WEGDOEN:
        
		this.fysica.print("UPDATE with time= " +time, 10);
		this.fysica.print("Autopilot outputs are: " 
		+ ", scaled thrust:" + scaledThrust
		+ ", thrust:" +  outputs.getThrust()
		+ ", leftwing:" +  outputs.getLeftWingInclination()
		+ ", rightwing:" +  outputs.getRightWingInclination()
		+ ", hor:" +  outputs.getHorStabInclination()
		+ ", ver:" +  outputs.getVerStabInclination(), 10);

        setThrust(scaledThrust);
        
        this.getLeftWing().updateInclinationAngle(this.getLeftWingInclination());
        this.getRightWing().updateInclinationAngle(this.getRightWingInclination());
        this.getHorStabilizator().updateInclinationAngle(this.getHorStabInclination());        
        this.getVerStabilizator().updateInclinationAngle(this.getVerStabInclination());
        this.getEngine().setThrust(0f);
		
        
 
        
        
        // TOT HIER
        System.out.println("thrust is " + scaledThrust);
        
        this.fysica.print("resulting force is:" + 
        		this.fysica.totalForceOnDroneInWorld, 10);
        
        this.fysica.print("resulting acceleration is:" + 
        		this.fysica.accelerationInWorld, 10);
        
        this.fysica.print("old speed is:" + 
        		this.getState().getVelocity(), 10);
        
        this.fysica.print("a*t is:" + 
        		this.fysica.product(time, this.fysica.accelerationInWorld), 10);
       
        this.fysica.print("resulting speed is:" + 
        		this.fysica.newVelocityInWorld, 10);
        
        
        //UPDATE STATE
        this.state.setNextVelocity(fysica.getNewVelocityInWorld(this, time));
        this.state.setNextPosition(fysica.getNewPositionInWorld(this, time));
        
        this.state.setNextAngularRotation(fysica.getNewAngularVelocityInWorld(this, time));
      //  this.state.setNextAngularOrienation(fysica.get);
        
       /* this.fysica.print("updating new pos and vel --- ", 12);
        //UPDATE POSITION AND VELOCITY IN WORLD
		Vector3f p = fysica.getNewPositionInWorld(this, time);
		
		
		Vector3f v = fysica.getNewVelocityInWorld(this, time);
		
		
        this.fysica.print("stop updating new pos and vel", 12);
*/
        
        //UPDATE HEAD PITCH ROLL POSITION AND RATE 
		//this.setAngularVelocityInWorld(fysica.getNewAngularVelocityInWorld(this, time));
		
		//HEADING
		
		float newHeading = this.getHeading() + (this.getHeadingVel() * time);
		this.setHeading(newHeading);
		
		float newHeadingRate = fysica.getNewHeadingRate(this, time);
		this.setHeadingVel(newHeadingRate);
		
		
		//PITCH
		
		float newPitch = this.getPitch() + this.getPitchVel() * time;
		this.setPitch(newPitch);
		
		float newPitchRate = fysica.getNewPitchRate(this, time);
		this.setPitchVel(newPitchRate);
		
		System.out.println("PITCH AND PITCHRATE");
		System.out.println(fysica.getNewPitchRate(this, time));
		System.out.println(this.getPitchVel());
		
		//ROLL
		
		float newRoll = this.getRoll() + this.getRollVel() * time;
		this.setRoll(newRoll);
		
		float newRollRate = fysica.getNewRollRate(this, time);
		this.setRollVel(newRollRate);
		
		
		this.fysica.print("HPR: " + newHeading + newPitch + newRoll, 10);
		

		//WHEELS TODO NIEUWE AUTOPILOT NODIG
		this.frontWheel.update(0.0f, time);
	    this.leftWheel.update(0.0f, time);
	    this.rightWheel.update(0.0f, time);
		
	    
        this.goToNextState();

	}
	
	
	private void goToNextState() {
		this.state = this.getState().getNextState();
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
	
	//
	public float getThrust() {
		return this.getEngine().getThrustScalar();
	}
	
	public float getGravity() {
		return Fysica.gravity;
	}
	
	
	
	
	//POSITION IN WORLD ---------------------------
	
	public Vector3f getPositionInWorld() {
		Vector3f v = new Vector3f(getXPos(), getYPos(), getZPos());
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
		DronePart[] droneParts = {getLeftWing(), getRightWing(), getHorStabilizator(), getVerStabilizator(), getEngine()};
		return droneParts;
	}
		
	public void setHeading(float heading) {
		this.heading = fysica.clean(heading);
	}

	public float getHeading() {
		return this.heading;
	}
	
	public void setPitch(float pitch) {
		this.pitch = fysica.clean(pitch);
		System.out.println("PITCH LOL NEGATIEF MAAR NAAR BOVEN" + pitch);
	}

	public float getPitch() {
		
		//TODO getters 0
		
		return this.pitch;
		
		
		//return 0;
	}
	
	public void setRoll(float roll) {
		this.roll = fysica.clean(roll);
	}

	public float getRoll() {
		
		
		//return this.roll;
		
		
		return 0;
	}
	
	public void setHeadingVel(float vel) {
		this.headingVel = vel;
	}
	
	public float getHeadingVel() {
		
		
		return this.headingVel;
		
		
		//return 0;
		
		
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
