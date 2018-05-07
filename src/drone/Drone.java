 package drone;

import java.awt.Color;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import engine.GameItem;
import engine.Square;
import interfaces.AutopilotOutputs;
import fysica.Fysica;
import Physics.DronePhysics;
import game.Airport;
import game.Pakket;
import graph.Mesh;
import graph.Texture;


public class Drone {
	//Icon
	public Texture texture;
	public float droneIconLength = 200f;
	public float hoogte = 20f;
	private Mesh droneIconMesh;
	private GameItem droneIconGameItem;
	
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
	public float wheelY = 0f; //-1.12f;
	//changed this
	public float frontWheelZ = -1.918f;
	public float rearWheelZ = 0.959f;
	public float rearWheelX = 1.24f;
	public float wheelRadius = 0.2f;
	
	public float tyreSlope = 50000f;
	public float dampSlope = 5000f;
	
	public float maxWrijving = 0.075f;
	public float maxRem = 2500;
	
	public final float Y_START_POS = -(wheelY) + wheelRadius;
	
	//Waarden van de drone
	public float wingX = 4.2f;
	public float tailSize = 4.2f;
	
	public float engineMass = 180f;
	public float wingMass = 100f;
	public float tailMass = 100f;
	
	public float maxThrust = 100000;
	public float maxAOA = (float) (Math.PI /12);

	private static float wingLiftSlope = 10f;
	private static float horStabLiftSlope = 0.01f;
	private static float verStabLiftSlope = 0f;

	private Matrix3f inertiaMatrix;
	
	private State state;
	
	private GameItem gameItem;

	private int id;
	
	private Airport startingAirport;
	private int startingGate;
	
	private Pakket carryingPackage;
	
	public DronePhysics p = new DronePhysics();
	
	
	public float tempTurningForce = 400000f;
	/**
	 * Create a new drone with the given id, at the given gate of the given airport, pointing to the given runway.
	 * @param id
	 * @param ap
	 * @param gate
	 * @param pointingToRunway
	 */
	public Drone(int id,Airport ap, int gate, int pointingToRunway){
		
		setId(id);
		setStartingAirport(ap);
		setStartingGate(gate);

		float xPos = ap.getMiddleGate(gate)[0];
		float yPos = Y_START_POS;

		float zPos = ap.getMiddleGate(gate)[1];
		
		Vector3f velocity = new Vector3f(0,0,0);
		
		//TODO ROTATIE VAN DRONE AFHANKELIJK VAN AIRPORT ORIENTATIE!!
		
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
		
		initState.setHPR(new Vector3f((float) 0, (float) 0, (float) 0));
		initState.setVelocity(velocity);
		this.state = initState;
	}
	
	//DEPRECATED
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
	
	/**
	 * Checks in every drone part if the drone is crashed.
	 */
	public boolean checkCrash(){
		for(DronePart dp : this.getDroneParts()){
			if(dp.checkCrash()) return true;
		}
		return false;
	}
	
	
	/**
	 * Updates the drone with the given outputs and the given elapsed time.
	 * @param outputs
	 * @param time
	 */
	public void update(AutopilotOutputs outputs,float time){
		
		System.out.println("========================================================================");
		System.out.println("======================== - UPDATE DRONE - ==================================");
		System.out.println("========================================================================");
		
        float scaledThrust = tempTurningForce; //Math.min(tempTurningForce, outputs.getThrust());
        
        //OM TE TESTEN LATER WEGDOEN:
        
		this.fysica.print("UPDATE with time= " +time, 1400);
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
		
		1600);
 
        this.getLeftWing().updateInclinationAngle((float) (Math.PI/12));
        this.getRightWing().updateInclinationAngle((float) (Math.PI/12));
        this.getHorStabilizator().updateInclinationAngle(outputs.getHorStabInclination());
        this.getVerStabilizator().updateInclinationAngle(outputs.getVerStabInclination());
        this.getEngine().setThrust(scaledThrust);

        
        this.frontWheel.update(0, time);
	    this.leftWheel.update(0, time);
	    this.rightWheel.update(tempTurningForce, time);
        
        //UPDATE STATE
      
        State newState = p.getNewState(this, time);
        this.state = newState;
        

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
	
	public void visualise() {
		texture = new Texture("res/droneIcon.png");
	}
	
	public Mesh generateMesh() {
		
		Square droneIcon = new Square(droneIconLength/2,droneIconLength/2,
				-droneIconLength/2,droneIconLength/2,
				-droneIconLength/2,-droneIconLength/2,
				droneIconLength/2,-droneIconLength/2,
				hoogte,
				Color.BLACK,
				false,
				1);
		
		Mesh droneIconMesh = new Mesh(droneIcon.positions(),null,droneIcon.indices(),droneIcon.textCoords(),this.texture);
		this.droneIconMesh = droneIconMesh;
		return droneIconMesh;

	}
	
	public GameItem generateGameItem() {
		GameItem droneIconItem = new GameItem(droneIconMesh, false, true, true);
		droneIconItem.setId(texture.id);
		this.droneIconGameItem = droneIconItem;
		return droneIconGameItem;
	}
	
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

	public float getHeading() {
		return this.getState().getHeading();
	}

	public float getPitch() {
		return this.getState().getPitch();
		}

	public float getRoll() {		
		return this.getState().getRoll();
		}
	
	public float getHeadingVel() {	
		return this.getState().getHeadingRate();
	}
	
	public float getPitchVel() {
		return this.getState().getPitchRate();
	}
	
	public float getRollVel() {
		return this.getState().getRollRate();
	}
	
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

	public Airport getStartingAirport() {
		return startingAirport;
	}

	public void setStartingAirport(Airport startingAirport) {
		this.startingAirport = startingAirport;
	}

	public int getStartingGate() {
		return startingGate;
	}

	public void setStartingGate(int startingGate) {
		this.startingGate = startingGate;
	}
	
	public void setGameItem(GameItem gameItem) {
		this.gameItem = gameItem;
	}
	
	public GameItem getGameItem() {
		return this.gameItem;
	}
	
	public void setIconGameItem(GameItem gameItem) {
		this.droneIconGameItem = gameItem;
	}
	
	public GameItem getIconGameItem() {
		return this.droneIconGameItem;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public State getState() {
		return this.state;
	}
	
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
	
	public float getThrust() {
		return this.getEngine().getThrustScalar();
	}
	
	public float getGravity() {
		return Fysica.gravity;
	}
	
	public float getWingX() {
		return this.wingX;
	}
	
	public float getTailSize() {
		return tailSize;
	}
	
	public float getX(){
		return getState().getPosition().getX();
	}
	
	public float getY(){
		return getState().getPosition().getY();
	}
	
	public float getZ(){
		return getState().getPosition().getZ();
	}

	public boolean isCarryingPackage() {
		return carryingPackage!=null;
	}

	public void setCarryingPackage(Pakket carryingPackage) {
		this.carryingPackage = carryingPackage;
	}
	
	public Pakket getCarryingPackage() {
		return this.carryingPackage;
	}

}