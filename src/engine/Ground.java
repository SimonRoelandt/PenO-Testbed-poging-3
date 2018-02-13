package engine;

public class Ground {

	public Ground() {	
	}
	
	public float[] vertices() {
		float[] vertices = new float[]{
				
		0, 0, 0, //1 ,
		1, 0, 0, //0 ,
		0, 1, 0, //0 ,
		-1, 0, 0, //0,
		0,-1, 0, //0
		
		};
		
		return vertices;
	}
	
	public int[] indices() {
		int[] indices = new int[]{
				
			0,1,2, 0,2,3, 0,3,4, 0,4,1
				
				
		};
		
		return indices;
	}
	
	public float[] colours() {
		float[] colours = new float[] {
				
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		         
		     
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
			         
		
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
		        0.5f, 0.5f, 0.5f, 0.5f,
			         
				
		};
		return colours;
	}
}
