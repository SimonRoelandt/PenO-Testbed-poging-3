package engine;

import graph.Mesh;

public class GroundItem extends GameItem {

	private float xpos; 
	private float zpos;//the center of the piece of ground
	
	public GroundItem(Mesh mesh, boolean render, boolean texture, float xpos, float zpos) {
		super(mesh, render, texture);
		this.xpos = xpos;
		this.zpos = zpos;
	}
	
	public float getXpos() {
		return xpos;
	}
	
	public float getZpos() {
		return zpos;
	}
}
