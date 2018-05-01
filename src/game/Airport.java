package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;


import autopilotLibrary.Vector;
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
	
	private Pakket packageGate0=null;
	
	private Pakket packageGate1=null;
	
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
		this.id=id;
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
				0f,
				Color.BLACK,
				false,
				1);
		
		Mesh airportmesh = new Mesh(airport1.positions(),null,airport1.indices(),airport1.textCoordsAirport(),this.texture);
		this.mesh = airportmesh;

	}
	
	public void generateGameItem() {
		Mesh airportmesh = mesh;
		GameItem airportItem = new GameItem(airportmesh, false, true, false);
		airportItem.setId(texture.id);
		this.airportItem = airportItem;
	}
	
	//FOR TESTING
	public static void main(String[] args) {
		Airport a = new Airport(200,200,(float) (2*Math.PI/2), 30, 60, 0);
		
		float[] cord = a.getStartRunway0Corner();
		System.out.println("X0start: " + cord[0] + " Z0start: " + cord[1]);
		
		float[] cord1 = a.getEndRunway1Corner();
		System.out.println("X1end: " + cord1[0] + " Z1end: " + cord1[1]);
		
		
		
		Vector vector1 = new Vector(a.getL() + (a.getW()/2),0,a.getW()); 
		Vector vector2 = new Vector (a.getCenterToRunway0X(),0,a.getCenterToRunway0Z());
		float radian = (float) Math.acos(Vector.scalairProd(vector1, vector2)/(Vector.norm(vector1)*Vector.norm(vector2)));
		System.out.println("centertox: " + a.getCenterToRunway0X() + " centertoZ: " +a.getCenterToRunway0Z());
		System.out.println("real radian" + a.getOrientation() + " calc radian: " + radian);
 	}
	
	
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
		float[] point = getInvertedRotatedPoint(x, z);
		float[] a = getInvertedRotatedPoint(getStartRunway0Corner()[0], getStartRunway0Corner()[1]);
		float[] b = getInvertedRotatedPoint(getEndRunway1Corner()[0], getEndRunway1Corner()[1]);

		boolean onAirportX = false;
		boolean onAirportZ = false;
		if(a[0] < b[0]){
			if(point[0] >= a[0] && point[0] <= b[0]) onAirportX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onAirportX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onAirportZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onAirportZ = true;
		
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
	 * Calculates invertedrotated position of a position according to the orientation.
	 */
	public float[] getInvertedRotatedPoint(float x, float z){
		float angle = -getOrientation();
		
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
		return getX() -  getStartRunway0Corner()[0];
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
		return packageGate1!=null;
	}

	public void setPackageGate1(Pakket pakket) {
		this.packageGate1 = pakket;
	}
	
	public Pakket getPackageGate1() {
		return this.packageGate1;
	}
	
	public boolean isPackageGate0() {
		return packageGate0!=null;
	}

	public void setPackageGate0(Pakket pakket) {
		this.packageGate0 = pakket;
	}
	
	public Pakket getPackageGate0() {
		return this.packageGate0;
	}
	
	public void setPackageGate(Pakket packageGate, int id){
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
