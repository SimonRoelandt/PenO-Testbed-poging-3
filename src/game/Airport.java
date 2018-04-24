package game;

import java.awt.Color;
import engine.GameItem;
import engine.Square;
import graph.Mesh;
import graph.Texture;

/**
 * 
 * A class of airports.
 *
 */
public class Airport {
	
	/**
	 * Airport id.
	 */
	private int id;
	/**
	 * Airport width.
	 */
	private float w;
	/**
	 * Airport length.
	 */
	private float l;
	/**
	 * Airport center x position in the world.
	 */
	private float centerX;
	/**
	 * Airport center z position in the world.
	 */
	private float centerZ;
	/**
	 * The orientation of the airport in the world.
	 */
	private float orientation;
	
	private Texture texture;
	
	private Mesh mesh;
	
	private Mesh mesh2;
	
	private GameItem airportItem;
	
	private GameItem airportItem2;
	
	private boolean packageGate0 = false;
	
	private boolean packageGate1 = false;
	
	// AIRPORT CONFIGURATION
	//
	//      l  w    l
	//    |--|----|--|  
	//    |  |    |  |  gate 1
	//    |  |____|  |
	// 2w |  |    |  |
	//    |  |    |  |  gate 0
	//    |--|----|--|
	//    
	//   lane 0   lane 1
	
	public Airport(float x, float z, float ori, float w, float l, int id) {
		setX(x);
		setZ(z);
		setOrientation(ori);
		setW(w);
		setL(l);
	}	
	
	public void visualise() {
		texture = new Texture("res/airport.png");
		generateMesh();
		generateGameItem();
	}
	
	public void generateMesh() {
		
		Square airport1 = new Square(getStartRunway0Corner()[0],getStartRunway0Corner()[1],
				getEndRunway0Corner()[0],getEndRunway0Corner()[1],
				getEndRunway1Corner()[0],getEndRunway1Corner()[1],
				getStartRunway1Corner()[0],getStartRunway1Corner()[1],
				Color.BLACK,
				false,
				1);
		
		Mesh airportmesh = new Mesh(airport1.positions(),null,airport1.indices(),airport1.textCoordsAirport(),this.texture);
		this.mesh = airportmesh;

	}
	
	public void generateGameItem() {
		Mesh airportmesh = mesh;
		GameItem airportItem = new GameItem(airportmesh, false, true);
		airportItem.setId(texture.id);
		this.airportItem = airportItem;
	}
	
	//FOR TESTING
	/*
	public static void main(String[] args) {
		Airport a = new Airport(200,200,(float) Math.PI/2, 30, 60, 0);
		
		float[] cord = a.getStartRunway0Corner();
		System.out.println("X0start: " + cord[0] + " Y0start: " + cord[1]);
		
		float[] cord1 = a.getEndRunway0Corner();
		System.out.println("X0end: " + cord1[0] + " Y0end: " + cord1[1]);
 	}
	*/
	
	/**
	 * Checks if a position is on gate 0.
	 */
	public boolean onGate0(float x, float z){
		boolean onGateX = false;
		boolean onGateZ = false;
		float[] a = getStartGate0();
		float[] b = getEndGate0();
		if(a[0] < b[0]){
			if(x >= a[0] && x <= b[0]) onGateX = true;
		}
		else if(x <= a[0] && x >= b[0]) onGateX = true;
		
		if(a[1] < b[1]){
			if(z >= a[1] && z <= b[1]) onGateZ = true;
		}
		else if(z <= a[1] && z >= b[1]) onGateZ = true;
		
		return (onGateX && onGateZ);
	}
	
	/**
	 * Checks if a position is on gate 1.
	 */
	public boolean onGate1(float x, float z){
		boolean onGateX = false;
		boolean onGateZ = false;
		float[] a = getStartGate1();
		float[] b = getEndGate1();
		if(a[0] < b[0]){
			if(x >= a[0] && x <= b[0]) onGateX = true;
		}
		else if(x <= a[0] && x >= b[0]) onGateX = true;
		
		if(a[1] < b[1]){
			if(z >= a[1] && z <= b[1]) onGateZ = true;
		}
		else if(z <= a[1] && z >= b[1]) onGateZ = true;
		
		return (onGateX && onGateZ);
	}
	
	/**
	 * Checks if a position is on the airport.
	 */
	public boolean onAirport(float x, float z){
		boolean onAirportX = false;
		boolean onAirportZ = false;
		float[] a = getStartRunway0Corner();
		float[] b = getEndRunway1Corner();
		if(a[0] < b[0]){
			if(x >= a[0] && x <= b[0]) onAirportX = true;
		}
		else if(x <= a[0] && x >= b[0]) onAirportX = true;
		
		if(a[1] < b[1]){
			if(z >= a[1] && z <= b[1]) onAirportZ = true;
		}
		else if(z <= a[1] && z >= b[1]) onAirportZ = true;
		
		return (onAirportX && onAirportZ);
	}
	
	/**
	 * Calculates rotated position of a position according to the orientation.
	 */
	public float[] getRotatedPoint(float x, float z){
		float angle = getOrientation();
		
		float rotatedX = (float) (getX() + (x  * Math.cos(angle)) - (z * Math.sin(angle)));
		float rotatedZ = (float) (getZ() + (x  * Math.sin(angle)) + (z * Math.cos(angle)));

		return new float []{rotatedX, rotatedZ};
	}
	
	/**
	 * Gets the start position corner of gate 0.
	 */
	public float[] getStartGate0(){
		return getRotatedPoint(-getW()/2,-getW());
	}
	
	/**
	 * Gets the start position corner of gate 1.
	 */
	public float[] getStartGate1(){
		return getRotatedPoint(-getW()/2, 0);
	}
	
	/**
	 * Gets the end position corner of gate 0.
	 */
	public float[] getEndGate0(){
		return getRotatedPoint(getW()/2,0);
	}
	
	/**
	 * Gets the end position corner of gate 1.
	 */
	public float[] getEndGate1(){
		return getRotatedPoint(getW()/2,getW());
	}
	
	/**
	 * Gets the middle position of gate 0.
	 */
	public float[] getMiddleGate0(){
		return getRotatedPoint(0,-getW()/2);
	}
	
	/**
	 * Gets the middle position of gate 1.
	 */
	public float[] getMiddleGate1(){
		return getRotatedPoint(0,getW()/2);
	}
	
	/**
	 * Gets start of runway 0 corner (bottom left).
	 */
	public float[] getStartRunway0Corner(){
		return getRotatedPoint(-(getL()+(getW()/2)), -getW());
	}
	/**
	 * Gets end of runway 0 corner (top left).
	 */
	public float[] getEndRunway0Corner(){
		return getRotatedPoint(-(getL()+(getW()/2)), getW());
	}
	/**
	 * Gets start of runway 1 corner (bottom right).
	 */
	public float[] getStartRunway1Corner(){
		return getRotatedPoint((getL()+(getW()/2)), -getW());
	}
	/**
	 * Gets end of runway 1 corner (top right).
	 */
	public float[] getEndRunway1Corner(){
		return getRotatedPoint((getL()+(getW()/2)), getW());
	}
	
	/**
	 * Returns the x distance from the center of the airport to runway 0.
	 * 	(to middle of runway 0)
	 */
	public float getCenterToRunway0X(){
		return getX() - getRotatedPoint(-((getL()/2) + (getW()/2)), -getW())[0];
	}
	
	/**
	 * Returns the z distance from the center of the airport to runway 0.
	 * 	(to start of runway 0)
	 */
	public float getCenterToRunway0Z(){
		return getZ() - getStartRunway0Corner()[1];
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getL() {
		return l;
	}

	public void setL(float l) {
		this.l = l;
	}

	public float getX() {
		return centerX;
	}

	public void setX(float x) {
		this.centerX = x;
	}

	public float getZ() {
		return centerZ;
	}

	public void setZ(float y) {
		this.centerZ = y;
	}

	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}
	
	public Mesh getMesh() {
		return this.mesh;
	}

	public GameItem getItem() {
		return this.airportItem;
	}
	
	public GameItem getItem2() {
		return this.airportItem2;
	}

	public boolean isPackageGate1() {
		return packageGate1;
	}

	public void setPackageGate1(boolean packageGate1) {
		this.packageGate1 = packageGate1;
	}

	public boolean isPackageGate0() {
		return packageGate0;
	}

	public void setPackageGate0(boolean packageGate0) {
		this.packageGate0 = packageGate0;
	}
	
	public void setPackageGate(boolean packageGate, int id){
		if(id == 0) setPackageGate0(packageGate);
		if(id == 1) setPackageGate1(packageGate);
		System.out.println("DELIVER PACKAGE AT GATE " + id);
	}
	
	public boolean isPackageGate(int id){
		if(id == 0) return isPackageGate0();
		if(id == 1) return isPackageGate1();
		else return true;
	}
	
}