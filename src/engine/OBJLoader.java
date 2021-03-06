package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import org.lwjgl.util.vector.Vector3f;

import graph.Mesh;

public class OBJLoader {

	public static Mesh loadOBJModel(String fileName, Color color) {
		FileReader fr = null;
		FileReader fr2 = null;
		try {
			String machineOS = System.getProperty("os.name");
			
			
			if(machineOS.length() == 8){
				System.out.println("OS of machine is: " + machineOS);

				fr = new FileReader(new File("src/"+fileName+".obj"));
				fr2 = new FileReader(new File("src/"+fileName+".obj"));
			} else {
				System.out.println("OS of machine is not Mac ");

				fr = new FileReader(new File("src\\"+fileName+".obj"));
				fr2 = new FileReader(new File("src\\"+fileName+".obj"));
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.print("File not found");
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fr);
	
		String line;
		String line2;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		int[] indicesArray = null;
		float[] colourArray = null;
		try{
			while(true){
				line = reader.readLine();
				if (line==null) {
					break;
				}
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")){
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				}	
			}
			
			BufferedReader reader2 = new BufferedReader(fr2);
			line2 = reader2.readLine();
			while(true) {	
				if (line2==null)
					break;
				if (!line2.startsWith("f ")){
					line2 = reader2.readLine();
					continue;
				}
				String[] currentLine = line2.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				processVertex(vertex1, indices);
				processVertex(vertex2, indices);
				processVertex(vertex3, indices);
				line2 = reader2.readLine();
				
	
			}
			reader.close();
			reader2.close();
				
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		colourArray = new float[indices.size()];
		
		int vertexPointer = 0;
		for (Vector3f vertex : vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		for (int i = 0; i < indices.size();i++){
			indicesArray[i] = indices.get(i);
		}
		for (int i = 0; i < indices.size();i+=3){
			float grijs = (float) Math.random()/8;
			colourArray[i] = grijs;
			colourArray[i+1] = grijs;
			colourArray[i+2] = grijs;
		}
		
		
		return new Mesh(verticesArray, colourArray, indicesArray, new float[]{}, null);
		
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices) {
		int currentVertexPointer = Integer.parseInt(vertexData[0]) -1;
		indices.add(currentVertexPointer);
	}
}
 