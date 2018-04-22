package engine;

import java.util.ArrayList;
import java.util.List;

import graph.Mesh;
import graph.Texture;

public class Ground {
	
	public Texture texture= new Texture("res/grass.png");
	
	private float xStart = -4000f;
	private float zStart = -4000f;
	private float width = 8000f;
	private float length = 8000f;
	private float scale = 100;

	private List<GroundItem> listGroundItems = new ArrayList<GroundItem>();
	
	public Ground() {	
	}
	
	public void generateStart() {

		Balk groundPiece = new Balk(xStart,  0, zStart, width, -2f, length, null, true, 100);
		Mesh groundMesh = new Mesh(groundPiece.positions(), null, groundPiece.indices(), groundPiece.textCoords(), this.texture);
		GroundItem groundItem = new GroundItem(groundMesh, false, true);
		groundItem.setId(texture.id);
		listGroundItems.add(groundItem);

	}

	public List<GroundItem> getListGroundItems() {
		return this.listGroundItems;
	}

}
