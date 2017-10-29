package drone;

import org.lwjgl.util.vector.Vector3f;

import api.AutopilotOutputs;
import fysica.Fysica;

public class Drone {
	
private Fysica  fysica;
	
	private static float gravity = (float) 9.81;

	
	private Airfoil leftWing;
	private Airfoil rightWing;
	private Airfoil horStabilization;
	private Airfoil verStabilization;
	
	
	private Engine  engine;
	
	
	private float xPos;
	private float yPos;
	private float zPos;
	
	private Vector3f velocity = new Vector3f(0,0,100);
	
	private static float wingX = 4;
	private static float tailSize = 4;
	
	private static float engineMass = 50;
	private static float wingMass = 50;
	private static float tailMass = 50;
	
	private static float maxThrust = 1000;
	private static float maxAOA = (float) (Math.PI /12);
	
	private static float wingLiftSlope = 1;
	private static float horStabLiftSlope = 1;
	private static float verStabLiftSlope = 1;
	
	
	private float heading;
	private float pitch;
	private float roll;
	
	
	public Drone(float xPos, float yPos, float zPos, Vector3f velocity ) {
		this.leftWing         = new Airfoil(0, wingMass, 0);
		this.rightWing        = new Airfoil(0,wingMass, 0);
		this.horStabilization = new Airfoil(0, tailMass/2, 0);
		this.verStabilization = new Airfoil(0, tailMass/2, 1);
		this.engine           = new Engine(0, engineMass);
		
		fysica = new Fysica();
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.velocity = velocity;
		
	}
	
	//Bepaalt de verandering die elke stap gebeurt
	public void update(AutopilotOutputs outputs){
		
		//Schrijf Output
        this.getEngine().setThrust(outputs.getThrust());
        this.getLeftWing().setInclinationAngle(outputs.getLeftWingInclination());
        this.getRightWing().setInclinationAngle(outputs.getRightWingInclination());
        this.getHorStabilization().setInclinationAngle(outputs.getHorStabInclination());        
        this.getVerStabilization().setInclinationAngle(outputs.getVerStabInclination());
		
		
		this.setPos(this.getNewPosition().x,this.getNewPosition().y,this.getNewPosition().z);
		
		Vector3f v = this.getNewVelocity();
		this.setVelocity(v);
		
		leftWing.setVelocityAirfoil(v);
		rightWing.setVelocityAirfoil(v);
		horStabilization.setVelocityAirfoil(v);
		verStabilization.setVelocityAirfoil(v);
		
	}
	
	
	//Twee belangrijkste functies
	public Vector3f getNewPosition() {
		return fysica.nextPosition(this, 1);
	}
	
	public Vector3f getNewVelocity() {
		return fysica.velocity(this, 1);
	}
	
	public Vector3f getTotalForceDrone() {
		return fysica.totalForceDrone(this);
	}
	
	//geeft alle airfoils + engine
	public Airfoil getLeftWing() {
		return this.leftWing;
	}
	
	public Airfoil getRightWing() {
		return this.rightWing;
	}
	
	public Airfoil getHorStabilization() {
		return this.horStabilization;
	}
	
	public Airfoil getVerStabilization() {
		return this.verStabilization;
	}
	
	public Engine getEngine() {
		return this.engine;
	}
	
	
	public float getThrust() {
		return this.getEngine().getThrustScalar();
	}
	
	public float getGravity() {
		return fysica.getGravity();
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
	
	public Vector3f getPos() {
		Vector3f v = new Vector3f(getXPos(), getYPos(), getZPos());
		return v;
	}
	
	public void setPos(float x, float y, float z){
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
	}
	
	public void setVelocity(Vector3f vel) {
		this.velocity = vel;
		this.getLeftWing().setVelocityAirfoil(vel);
		this.getRightWing().setVelocityAirfoil(vel);
		this.getHorStabilization().setVelocityAirfoil(vel);
		this.getVerStabilization().setVelocityAirfoil(vel);
	}
	
	public Vector3f getVelocity() {
		return this.velocity;
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
		this.getHorStabilization().setInclination(incl);
	}
	
	public float getHorStabInclination() {
		return this.getHorStabilization().getInclination();
	}
	
	public void setVerStabInclination(float incl) {
		this.getVerStabilization().setInclination(incl);
	}
	
	public float getVerStabInclination() {
		return this.getVerStabilization().getInclination();
	}
	
	public DroneObject[] getDroneObj() {
		DroneObject[] droneObj = {getLeftWing(), getRightWing(), getHorStabilization(), getVerStabilization(), getEngine()};
		return droneObj;
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
	
	//getters van alle finals
	
	public float getWingX() {
		return this.wingX;
	}
	
	public float getTailSize() {
		return tailSize;
	}
	
	public float getEngineMass() {
		return this.getEngine().getMass();
	}
	
	//Alle vleugels hebben dezelfde massa
	public float getWingMass() {
		return this.getLeftWing().getMass();
	}
		
	public float getTailMass() {
		return this.getHorStabilization().getMass() + this.getVerStabilization().getMass();
	}
	
	public float getMaxThrust() {
		return this.getEngine().getMaxThrust();
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
	
	
	
	
	
}
