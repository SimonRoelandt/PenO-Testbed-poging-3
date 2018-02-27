package engine;

public class Ground {

	public Ground() {	
	}
	
	public float[] vertices() {
		float[] vertices = new float[]{
				
		-5000, -0.00f, -5000, //1 ,
		-5000, -0.00f, 5000, //0 ,
		5000, -0.00f, 5000, //0 ,
		5000, -0.00f, -5000, //0,
		
		
		};
		
		return vertices;
	}
	
	public int[] indices() {
		int[] indices = new int[]{
				
			0,1,2, 0,2,3
				
				
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
}
