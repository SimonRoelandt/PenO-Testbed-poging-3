package drone;

import org.lwjgl.util.vector.Vector3f;
import api.AutopilotOutputs;
import fysica.Fysica;

public class Drone {
	
	//Fysica
	private Fysica fysica = new Fysica();

	//Onderdelen van de drone
	private Airfoil leftWing;
	private Airfoil rightWing;
	private Airfoil horStabilization;
	private Airfoil verStabilization;
	private Engine engine;
	
	//Waarden van de drone
	private static float wingX = 4;
	private static float tailSize = 4;
	
	private static float engineMass = 2;
	private static float wingMass = 2;
	private static float tailMass = 2;
	
	private static float maxThrust = 1000;
	private static float maxAOA = (float) (Math.PI /12);
	
	private Vector3f velocity = new Vector3f(0,0,9);
	
	private static float wingLiftSlope = 1;
	private static float horStabLiftSlope = 1;
	private static float verStabLiftSlope = 1;
	
	private float xPos;
	private float yPos;
	private float zPos;
	
	private float heading;
	private float pitch;
	private float roll;
	
	private float headingVel;
	private float pitchVel;
	private float rollVel;
	
	//-1.97864475 voor 1 wing
	
	
	public Drone(float xPos, float yPos, float zPos, Vector3f velocity ) {
		//float incl = (float) -2.08343282;
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		
		this.leftWing         = new Airfoil(0, wingMass,   false, new Vector3f(-wingX,0,0));
		this.leftWing.setDrone(this);

		this.rightWing        = new Airfoil(0, wingMass,   false, new Vector3f(wingX,0,0));
		this.rightWing.setDrone(this);

		
		this.horStabilization = new Airfoil(0, tailMass/2, false, new Vector3f(0,0,tailSize));
		this.horStabilization.setDrone(this);
		
		this.verStabilization = new Airfoil(0, tailMass/2, true,  new Vector3f(0,0,tailSize));
		this.verStabilization.setDrone(this);
		
		this.engine           = new Engine(0,  engineMass,        new Vector3f(0,0,-1));
		this.engine.setDrone(this);
		
		
		
		setVelocity(velocity);
	}
	
	//Bepaalt de verandering die elke stap gebeurt
	public void update(AutopilotOutputs outputs,float time){
		
		//System.out.println("thrust: " + outputs.getThrust());
		
		//Schrijf outputs
        this.getEngine().setThrust(outputs.getThrust());
        this.getLeftWing().setInclinationAngle(outputs.getLeftWingInclination());
        this.getRightWing().setInclinationAngle(outputs.getRightWingInclination());
        this.getHorStabilizator().setInclinationAngle(outputs.getHorStabInclination());        
        this.getVerStabilizator().setInclinationAngle(outputs.getVerStabInclination());
		
		Vector3f p = this.getNewPosition(time);
		this.setPosition(p);
		
		Vector3f v = this.getNewVelocity(time);
		this.setVelocity(v);
		
		leftWing.setAttackVector(outputs.getLeftWingInclination(), false);
		rightWing.setAttackVector(outputs.getRightWingInclination(),false);
		horStabilization.setAttackVector(outputs.getHorStabInclination(), false);
		verStabilization.setAttackVector(outputs.getVerStabInclination(), true);
		
	}
	
	//Twee belangrijkste functies
	public Vector3f getNewPosition(float time) {
		Vector3f vel = getNewVelocity(time);
		this.setVelocity(vel);
		return fysica.nextPosition(this, time);
	}
	
	public Vector3f getNewVelocity(float time) {
		//System.out.println("New vel drone: " + fysica.velocity(this, 1));
		Vector3f v = fysica.velocity(this, time);
		return v;
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
	
	public void setPosition(Vector3f pos) {
		this.xPos = pos.getX();
		this.yPos = pos.getY();
		this.zPos = pos.getZ();
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
		this.getHorStabilizator().setVelocityAirfoil(vel);
		this.getVerStabilizator().setVelocityAirfoil(vel);
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
	
	public float getPitchingVel() {
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

}
