package engine;

public class Balk {
	
	private float xpos;
	private float ypos;
	private float zpos;
	private float length;
	private float width;
	private float heigth;
	private float[] colorfront;
	private float[] colorback;
	private float[] colorleft;
	private float[] colorright;
	private float[] colorup;
	private float[] colordown;

	public Balk(float xpos, float ypos, float zpos, float length, float width, float heigth,
			float[] colorfront,float[] colorback,float[] colorleft,float[] colorright,float[] colorup,float[] colordown ) {
	this.xpos = xpos;
	this.ypos = ypos;
	this.zpos = zpos;
	this.length = length;
	this.width = width;
	this.heigth = heigth;
	this.colorfront = colorfront;
	this.colorback = colorback;
	this.colorleft = colorleft;
	this.colorright = colorright;
	this.colorup = colorup;
	this.colordown = colordown;
	}
	
	public float[] positions() {
		float[] positions = new float[]{
				
				//front 
		        xpos,  ypos+heigth,  zpos,
		        xpos,  ypos,  zpos,
		        xpos+length,  ypos,  zpos,
		        xpos+length,  ypos+heigth,  zpos,
		         
		       //back 
		        xpos,  ypos+heigth,  zpos+width,
		        xpos,  ypos,  zpos+width,
		        xpos+length,  ypos,  zpos+width,
		        xpos+length,  ypos+heigth,  zpos+width,
			         
			    //left 
		        xpos,  ypos+heigth,  zpos+width,
		        xpos,  ypos,  zpos+width,
		        xpos,  ypos,  zpos,
		        xpos,  ypos+heigth,  zpos,
			         
				//right 
		        xpos+length,  ypos+heigth,  zpos+width,
		        xpos+length,  ypos,  zpos+width,
		        xpos+length,  ypos,  zpos,
		        xpos+length,  ypos+heigth,  zpos,
			         
			     //up 
		        xpos,  ypos+heigth,  zpos+width,
		        xpos,  ypos+heigth,  zpos,
		        xpos+length,  ypos+heigth,  zpos,
		        xpos+length,  ypos+heigth,  zpos+width,
				         
				  //down 
		        xpos,  ypos,  zpos+width,
		        xpos,  ypos,  zpos,
		        xpos+length,  ypos,  zpos,
		        xpos+length,  ypos,  zpos+width,
		        
		
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
		         
		       //back 
		        colorback[0], colorback[1], colorback[2],
		        colorback[0], colorback[1], colorback[2],
		        colorback[0], colorback[1], colorback[2],
		        colorback[0], colorback[1], colorback[2],
			         
			    //left 
		        colorleft[0], colorleft[1], colorleft[2],
		        colorleft[0], colorleft[1], colorleft[2],
		        colorleft[0], colorleft[1], colorleft[2],
		        colorleft[0], colorleft[1], colorleft[2],
			         
				//right 
		        colorright[0], colorright[1], colorright[2],
		        colorright[0], colorright[1], colorright[2],
		        colorright[0], colorright[1], colorright[2],
		        colorright[0], colorright[1], colorright[2],
			         
			     //up 
		        colorup[0], colorup[1], colorup[2],
		        colorup[0], colorup[1], colorup[2],
		        colorup[0], colorup[1], colorup[2],
		        colorup[0], colorup[1], colorup[2],
				         
				  //down 
		        colordown[0], colordown[1], colordown[2],
		        colordown[0], colordown[1], colordown[2],
		        colordown[0], colordown[1], colordown[2],
		        colordown[0], colordown[1], colordown[2],
		};
		return colours;
	}
	
	public int[] indices() {
		int[] indices = new int[] {
				//front
				0, 1, 3, 3, 1, 2,
				
				//back
				4, 5, 7, 7, 5, 6,
				
				//left
				8, 9, 11, 11, 9, 10,
				
				//right
				12, 13, 15, 15, 13, 14,
				
				//up
				16, 17, 19, 19, 17, 18,
				
				//down
				20, 21, 23, 23, 21, 22,
				
		};
		
		return indices;
	}
}
