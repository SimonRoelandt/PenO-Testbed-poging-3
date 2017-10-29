package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import graph.Mesh;

public class OBJLoader {

	public static Mesh loadOBJModel(String fileName) {
		FileReader fr = null;
		try {
			fr = new FileReader(new File("C:\\Users\\simon\\git\\PenO-Testbed-poging-3\\src\\bunny.obj"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.print("File not found");
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		int[] indicesArray = null;
		float[] colourArray = null;
		try{
			while(true){
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v")){
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				}else if (line.startsWith("f")){
					break;
				}
			}
			
			while(line!=null) {
				if (!line.startsWith("f")){
					line = reader.readLine();
					continue;
				}
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				processVertex(vertex1, indices);
				processVertex(vertex2, indices);
				processVertex(vertex3, indices);
				
				line = reader.readLine();
			}
			reader.close();
				
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
		for (int i = 0; i < indices.size();i++){
			colourArray[i] = (float) Math.random()/3;
		}
		
		
		return new Mesh(verticesArray, colourArray, indicesArray);
		
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices) {
		int currentVertexPointer = Integer.parseInt(vertexData[0]) -1;
		indices.add(currentVertexPointer);
	}
}
 