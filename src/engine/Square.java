package engine;

import java.awt.Color;

public class Square {
	
	private float hoogte=0f;
	private float x1;
	private float y1;
	private float x2;
	private float y2;
	private float x3;
	private float y3;
	private float x4;
	private float y4;
	private float[] colorfront;
	private Color color; 
	private float textureScale;
	

	public Square(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Color color, boolean texture, float textureScale) {
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
	this.x3 = x3;
	this.y3 = y3;
	this.x4 = x4;
	this.y4 = y4;
	this.color = color;
	this.textureScale = textureScale;
	
	if (!texture)
		getAllColorFaces(color);
	}
	
	public void getAllColorFaces(Color color) {
		colorfront = getColorFace(color, 1f);	
	}
	
	public float[] getColorFace(Color color, float value) {
		return new float[]{color.getRed()/255f*value,color.getGreen()/255f*value,color.getBlue()/255f*value};
	}
	
	public float[] positions() {
		float[] positions = new float[]{

		         
		       //back 
		        x1,hoogte,y1,
		        x2,hoogte,y2,
		        x3,hoogte,y3,
		        x4,hoogte,y4,
		        
		    };
		return positions;
	}
	
	public float[] colours() {
		float[] colours = new float[] {
				//front 
		        colorfront[0], colorfront[1], colorfront[2],
		        colorfront[0], colorfront[1], colorfront[2],
		        colorfront[0], colorfront[1], colorfront[2],
		        colorfront[0], colorfront[1], colorfront[2],
		         
		};
		return colours;
	}
	
	public int[] indices() {
		int[] indices = new int[] {
				//front
				0, 1, 3, 3, 1, 2,
				
		};
		
		return indices;
	}
	
	public float[] textCoords() {
		float[] textCoords = new float[] {
				0,0,
				0,textureScale,
				textureScale,textureScale,
				textureScale,0,
				
				
		};
		return textCoords;
	}
	
	public float[] textCoordsAirport() {
		float[] textCoords = new float[] {
				0.20f,0.1133f,
				0.20f,1-0.1133f,
				1-0.20f,1-0.1133f,
				1-0.20f,0.1133f,
				
				
		};
		return textCoords;
	}
}
