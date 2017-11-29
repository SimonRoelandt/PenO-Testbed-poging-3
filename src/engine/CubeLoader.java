package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CubeLoader {
	
	public List<Float> xpos = new ArrayList<Float>();
	public List<Float> ypos = new ArrayList<Float>();
	public List<Float> zpos = new ArrayList<Float>();
	
	public CubeLoader() {
		
	}
	public void generatePositions(File file) {
		FileReader fr = null;
		try {
			fr = new FileReader(file);
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
	
				xpos.add(Float.parseFloat(currentLine[0]));
				ypos.add(Float.parseFloat(currentLine[1]));
				zpos.add(Float.parseFloat(currentLine[2]));	
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

