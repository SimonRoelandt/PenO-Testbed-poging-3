package engine;

import graph.Mesh;

public class GroundItem extends GameItem {

	private float xpos; 
	private float zpos;//the center of the piece of ground
	
	public GroundItem(Mesh mesh, boolean render, boolean texture) {
		super(mesh, render, texture);
	}
	
	public float getXpos() {
		return xpos;
	}
	
	public float getZpos() {
		return zpos;
	}
}
