package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CubeLoader {
	
	private List<Float> xpos = new ArrayList<Float>();
	private List<Float> ypos = new ArrayList<Float>();
	private List<Float> zpos = new ArrayList<Float>();
	
	public void generatePositions(String filename) {
		FileReader fr = null;
		try {
			fr = new FileReader(new File("src\\" + filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fr);
		String line;
		
		try {
			while(true){
				line = reader.readLine();
				if (line==null)
					break;
				String[] currentLine = line.split(" ");
	
				xpos.add(Float.parseFloat(currentLine[1]));
				ypos.add(Float.parseFloat(currentLine[2]));
				zpos.add(Float.parseFloat(currentLine[3]));			
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	public List<Float> getXpos() {
		return this.xpos;
	}
	
	public List<Float> getYpos() {
		return this.ypos;
	}
	
	public List<Float> getZpos() {
		return this.zpos;
	}
}

