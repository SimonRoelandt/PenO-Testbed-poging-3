package engine;

import java.util.ArrayList;
import java.util.List;

import graph.Mesh;
import graph.Texture;

public class Ground {
	
	public Texture texture= new Texture("res/grass.png");
	
	
	private float width;
	private float lenght;
	private float widthPiece = 200;
	private float xMaxCoordinate;
	private float zMaxCoordinate;
	private float xMinCoordinate;
	private float zMinCoordinate;
	private List<GroundItem> listGroundItems = new ArrayList<GroundItem>();
	
	public Ground() {	
	}
	
	public void generateStart(float xmax, float zmax) {
		for (int i=0; i < xmax/widthPiece; i++) {
			for (int j = 0; j < zmax/widthPiece; j++) {
				Balk groundPiece = new Balk(-xmax/2+i*widthPiece,  0, -zmax/2+j*widthPiece, widthPiece, -2f, widthPiece, null, true);
				Mesh groundMesh = new Mesh(groundPiece.positions(), null, groundPiece.indices(), groundPiece.textCoords(), this.texture);
				GroundItem groundItem = new GroundItem(groundMesh, false, true,-xmax/2+i*widthPiece + widthPiece/2 , -zmax/2+j*widthPiece + widthPiece/2);
				groundItem.setId(texture.id);
				listGroundItems.add(groundItem);
				
			}
		}
		width = xmax;
		lenght = zmax;
		xMaxCoordinate = xmax/2 - widthPiece/2;
		xMinCoordinate = -xmax/2 + widthPiece/2;
		zMaxCoordinate = zmax/2 - widthPiece/2;
		zMinCoordinate = -zmax/2 + widthPiece/2;
		System.out.println("AAAAAAAAAAAAAAAAA " + listGroundItems.size() + " BEGIN VLAKKEN");
	}
	
	public List<GroundItem> generateNewRowAndDeleteOldRow(String row) {
		List<GroundItem> list = new ArrayList<GroundItem>();
		switch (row) {
			case "xmax":
				for (int i=0; i < lenght/widthPiece; i++) {
					Balk groundPiece = new Balk(xMaxCoordinate + widthPiece/2,  0, -zMaxCoordinate/2+i*widthPiece, widthPiece, 0.5f, widthPiece, null, true);
					Mesh groundMesh = new Mesh(groundPiece.positions(), null, groundPiece.indices(), groundPiece.textCoords(), this.texture);
					GroundItem groundItem = new GroundItem(groundMesh, false, true, xMaxCoordinate + widthPiece , -zMaxCoordinate/2+i*widthPiece + widthPiece/2);
					groundItem.setId(texture.id);
					list.add(groundItem);
				}
				listGroundItems.removeAll(getXMinRow());
			case "xmin":
				for (int i=0; i < lenght/widthPiece; i++) {
					Balk groundPiece = new Balk(xMinCoordinate - widthPiece/2 - widthPiece,  0, -zMaxCoordinate/2+i*widthPiece, widthPiece, 0.5f, widthPiece, null, true);
					Mesh groundMesh = new Mesh(groundPiece.positions(), null, groundPiece.indices(), groundPiece.textCoords(), this.texture);
					GroundItem groundItem = new GroundItem(groundMesh, false, true, xMinCoordinate - widthPiece , -zMaxCoordinate/2+i*widthPiece + widthPiece/2);
					groundItem.setId(texture.id);
					list.add(groundItem);
				}
				listGroundItems.removeAll(getXMaxRow());	
			case "zmax":
				for (int i=0; i < width/widthPiece; i++) {
					Balk groundPiece = new Balk(-xMaxCoordinate/2+i*widthPiece,  0, zMaxCoordinate + widthPiece/2, widthPiece, 0.5f, widthPiece, null, true);
					Mesh groundMesh = new Mesh(groundPiece.positions(), null, groundPiece.indices(), groundPiece.textCoords(), this.texture);
					GroundItem groundItem = new GroundItem(groundMesh, false, true, -xMaxCoordinate/2+i*widthPiece + widthPiece/2 , zMaxCoordinate + widthPiece);
					groundItem.setId(texture.id);
					list.add(groundItem);
				}
				listGroundItems.removeAll(getZMinRow());
			case "zmin":
				for (int i=0; i < width/widthPiece; i++) {
					Balk groundPiece = new Balk(-xMaxCoordinate/2+i*widthPiece,  0, zMinCoordinate - widthPiece/2 - widthPiece, widthPiece, 0.5f, widthPiece, null, true);
					Mesh groundMesh = new Mesh(groundPiece.positions(), null, groundPiece.indices(), groundPiece.textCoords(), this.texture);
					GroundItem groundItem = new GroundItem(groundMesh, false, true, -xMaxCoordinate/2+i*widthPiece + widthPiece/2 , zMinCoordinate-widthPiece);
					groundItem.setId(texture.id);
					list.add(groundItem);
				}
				listGroundItems.removeAll(getZMaxRow());	
			}
		listGroundItems.addAll(list);
		return list;
	}
	
	public List<GroundItem> getXMaxRow() {
		List<GroundItem> list = new ArrayList<GroundItem>();
		for (GroundItem groundItem: listGroundItems) {
			if (groundItem.getXpos() == xMaxCoordinate)
				list.add(groundItem);
		}		System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC ZMIN MET " + list.size() + "ELEMENTEN");
		return list;
	}
	
	public List<GroundItem> getXMinRow() {
		List<GroundItem> list = new ArrayList<GroundItem>();
		for (GroundItem groundItem: listGroundItems) {
			if (groundItem.getXpos() == xMinCoordinate)
				list.add(groundItem);
		}
		System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB ZMIN MET " + list.size() + "ELEMENTEN");
		return list;
	}
	
	public List<GroundItem> getZMaxRow() {
		List<GroundItem> list = new ArrayList<GroundItem>();
		for (GroundItem groundItem: listGroundItems) {
			System.out.println("ZPOS " + groundItem.getZpos());
			System.out.println("ZMAXCOORDINATE" + zMaxCoordinate);
			if (groundItem.getZpos() == zMaxCoordinate)
				list.add(groundItem);
		}
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA ZMIN MET " + list.size() + "ELEMENTEN");
		return list;
	}

	public List<GroundItem> getZMinRow() {
		List<GroundItem> list = new ArrayList<GroundItem>();
		for (GroundItem groundItem: listGroundItems) {
			if (groundItem.getZpos() == zMinCoordinate)
				list.add(groundItem);
		}
		System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDD ZMIN MET " + list.size() + "ELEMENTEN");
		return list;
	}
	
	
	
	public List<GroundItem> getListGroundItems() {
		return this.listGroundItems;
	}
	
	public float getXMaxCoordinate() {
		return this.xMaxCoordinate;
	}
	
	public float getZMaxCoordinate() {
		return this.zMaxCoordinate;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getWidthPiece() {
		return this.widthPiece;
	}
	
	
}
