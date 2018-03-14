package engine;

import org.lwjgl.util.vector.Vector3f;

import graph.Mesh;

public class GameItem {
	
	private final Mesh mesh;
	private final Vector3f position;
	private float scale;
	private final Vector3f rotation;
	private boolean renderOnPlaneView;
	public boolean texture;
	public int textureId;
	
	public GameItem(Mesh mesh,boolean render, boolean texture) {
		this.mesh = mesh;
		position = new Vector3f(0,0,0);
		scale = 1;
		rotation = new Vector3f(0,0,0);
		this.renderOnPlaneView = render;
		this.texture = texture;
	}
	
	public boolean getRenderOnPlaneView(){
		return this.renderOnPlaneView;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
        this.scale = scale;
    }
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public void setId(int id) {
		this.textureId = id;
	}
}
