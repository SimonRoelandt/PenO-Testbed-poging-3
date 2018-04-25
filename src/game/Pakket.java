package game;

import java.awt.Color;

import drone.Drone;
import engine.GameItem;
import engine.Square;
import graph.Mesh;
import graph.Texture;

public class Pakket{
	
	public float hoogte = 30f;
	private GameItem packageIconGameItem;
	private Drone drone;
	
	float x,z;
	public Pakket(float x,float z){
		this.x = x;
		this.z = z;
	}
	
	public Pakket() {
		
	}
	public Pakket(float[] pos){
		this.x = pos[0];
		this.z = pos[1];
	}

	
	public GameItem getPackageIconGameItem() {
		return this.packageIconGameItem;
	}
	
	public void setPackageIconGameItem(GameItem packageIcon) {
		this.packageIconGameItem = packageIcon;
	}
	
	public void setDrone(Drone d) {
		this.drone = d;
	}
	
	public Drone getDrone() {
		return this.drone;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return z;
	}
	
	
}