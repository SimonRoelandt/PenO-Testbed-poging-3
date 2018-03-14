package engine;

import graph.Texture;

public class Ground {
	
	public Texture texture;

	public Ground() {	
		this.texture = new Texture("res/texture.png");
	}
	
	public float[] vertices() {
		float[] vertices = new float[]{
				
		-500, -0.00f, -500, //1 ,
		-500, -0.00f, 500, //0 ,
		500, -0.00f, 500, //0 ,
		500, -0.00f, -500 //0,
		
		
		};
		
		return vertices;
	}
	
	public int[] indices() {
		int[] indices = new int[]{
				
			0,1,3, 3,1,2
				
				
		};
		
		return indices;
	}
	
	public float[] colours() {
		float[] colours = new float[] {
				
		        0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f,
		        
		        0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f,
		         
				
		};
		return colours;
	}
	
	public float[] textCoords() {
		float[] textCoords = new float[] {
				

				0, 0,
                1, 0,
                1, 1,
                0, 1
		};
		return textCoords;
	}
}
