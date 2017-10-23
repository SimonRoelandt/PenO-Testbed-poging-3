package drone;

import fysica.Fysica;

public class Drone {
	
	private Airfoil leftWing;
	private Airfoil rightWing;
	private Airfoil horStabilization;
	private Airfoil verStabilization;
	private Engine  engine;
	private Fysica  fysica;
	
	private float xPos;
	private float yPos;
	private float zPos;
	
	private float[] velocity;
	
	
	public Drone(double thrust, double leftwingi, double rightwingi, double horStabilization, double verStabilization, double xPos, double yPos, double zPos, float[] velocity ) {
		this.leftWing         = new Airfoil(leftwingi, 5, 0);
		this.rightWing        = new Airfoil(rightwingi,5, 0);
		this.horStabilization = new Airfoil(horStabilization, 5, 0);
		this.verStabilization = new Airfoil(verStabilization, 5, 0);
		this.engine           = new Engine(thrust, 5);
		
		fysica = new Fysica();
		
		this.xPos = (float) xPos;
		this.yPos = (float) yPos;
		this.zPos = (float) zPos;
		this.velocity = velocity;
	}
	
	public float[] getTotalForceDrone() {
		return fysica.totalForceDrone(this);
	}
	
	public float[] getNewPosition() {
		return fysica.nextPosition(this, 1);
	}
	
	public float[] getNewVelocity() {
		return fysica.velocity(this, 1);
	}
	
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
	
	public float[] getPos() {
		float[] v = {getXPos(), getYPos(), getZPos()};
		return v;
	}
	
	public void setVelocity(float[] vel) {
		this.velocity = vel;
	}
	
	public float[] getVelocity() {
		return this.velocity;
	}
	
	public DroneObject[] getDroneObj() {
		DroneObject[] droneObj = {getLeftWing(), getRightWing(), getHorStabilization(), getVerStabilization(), getEngine()};
		return droneObj;
	}
	
	
	
}
