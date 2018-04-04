package game;

public class Airport {
	
	/**
	 * Airport id.
	 */
	private int id;
	/**
	 * Airport width.
	 */
	private float w = 60;
	/**
	 * Airport length.
	 */
	private float l = 30;
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
	
	public Airport(float x, float z, float ori) {
		setX(x);
		setZ(z);
		setOrientation(ori);
	}	
	
	//FOR TESTING
	public static void main(String[] args) {
		Airport a = new Airport(200,200,(float) Math.PI/2);
		
		float[] cord = a.getStartRunway0Corner();
		System.out.println("X0start: " + cord[0] + " Y0start: " + cord[1]);
		
		float[] cord1 = a.getEndRunway0Corner();
		System.out.println("X0end: " + cord1[0] + " Y0end: " + cord1[1]);
 	}
	
	/**
	 * Calculates rotated position of the runway according to the orientation.
	 */
	public float[] getRotatedPoint(float x, float z){
		float angle = getOrientation();
		
		float rotatedX = (float) (getX() + (x  * Math.cos(angle)) - (z * Math.sin(angle)));
		float rotatedZ = (float) (getZ() + (x  * Math.sin(angle)) + (z * Math.cos(angle)));

		return new float []{rotatedX, rotatedZ};
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

}
