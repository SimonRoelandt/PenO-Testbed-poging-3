package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import drone.Drone;
import engine.GameItem;
import engine.Square;
import graph.Mesh;
import graph.Texture;
import interfaces.AutopilotModule;

public class PackageService {
	
	private Texture texture;
	private Mesh packageIconMesh;
	private float packageIconLength = 120;
	private float hoogte = 22f;
	
	private AirportController apController;
	private DronesController droneController;
	private List<Pakket> freePackages = new ArrayList<Pakket>();
	private List<Pakket> takenPackages = new ArrayList<Pakket>();
	private Pakket pickedUpPackage;
	
	public PackageService(AirportController apC, DronesController dC) {
		this.apController = apC;
		this.droneController = dC;
	}
	
	/**
	 * Check for pickup of any packages.
	 */
	public void checkPickup() {
		 for(Drone d : droneController.getDrones()){
			 for(Airport ap : apController.getAirports()){
				if(ap.isPackageGate0() && ap.onGate0(d.getX(), d.getZ())){
					System.out.println("ON GATE 0 + PACKAGE AVAILABLE");
					if(d.getState().getVelocity().length() < 1.0) {
						pickup(d, ap, 0, ap.getPackageGate0());
						return;
					}
				}
				if(ap.isPackageGate1() && ap.onGate1(d.getX(), d.getZ())){
					System.out.println("ON GATE 1 + PACKAGE AVAILABLE");
					if(d.getState().getVelocity().length() < 1.0) {
						pickup(d, ap, 1, ap.getPackageGate1());
						return;
					}
				}
			 }
		 }
	}
	
	/**
	 * Picks up a package at the given airport and gate with the given drone.
	 */
	private void pickup(Drone d, Airport ap, int gate, Pakket pakket){
		System.out.println("PICKUP!!!!");
		pickedUpPackage = pakket;
		ap.setPackageGate(null, gate);
		d.setCarryingPackage(pakket);
		pakket.setDrone(d);
		freePackages.remove(pakket);
		takenPackages.add(pakket);
	}
	
	
	/**
	 * Create a new package deliver event from one airport to another.
	 */
	public Pakket newPackage(AutopilotModule apModule, int fromAp, int toAp){
		Pakket pakket = new Pakket();
		
		int fromGate = 0;
		int toGate = 0;
		
		if(!isPackageGate(fromAp,0)) fromGate = 0;
		else if(!isPackageGate(fromAp,1)) fromGate = 1;
		else{
			System.out.println("NO GATE FREE AT SENDING AIRPORT");
			return null;
		}
		if(!isPackageGate(toAp,0)) toGate = 0;
		else if(!isPackageGate(toAp,1)) toGate = 1;
		else{
			System.out.println("NO GATE FREE AT RECEIVING AIRPORT");
			return null;
		}
		if (fromGate == 0) {
			pakket.x=apController.getAirport(fromAp).getMiddleGate0()[0];
			pakket.z=apController.getAirport(fromAp).getMiddleGate0()[1];
			freePackages.add(pakket);
		}
		if (fromGate == 1) {
			pakket.x=apController.getAirport(fromAp).getMiddleGate1()[0];
			pakket.z=apController.getAirport(fromAp).getMiddleGate1()[1];
			freePackages.add(pakket);
		}
		
		generatePackageGameItem(pakket);
		
		apController.deliverPackage(fromAp, fromGate, toAp, toGate, pakket);
		apModule.deliverPackage(fromAp, fromGate, toAp, toGate);
		return pakket;
	}
	
	public void visualise() {
		texture = new Texture("res/packageIcon.png");
		generatePackageMesh();
	}
	
	public void generatePackageMesh() {
		Square packageIcon = new Square(packageIconLength/2,packageIconLength/2,
				-packageIconLength/2,packageIconLength/2,
				-packageIconLength/2,-packageIconLength/2,
				packageIconLength/2,-packageIconLength/2,
				hoogte,
				Color.BLACK,
				true,
				1);
		
		Mesh packageIconMesh = new Mesh(packageIcon.positions(),null,packageIcon.indices(),packageIcon.textCoords(),this.texture);
		this.packageIconMesh = packageIconMesh;
	}
	
	public void generatePackageGameItem(Pakket pakket) {
		GameItem packageIconItem = new GameItem(packageIconMesh, false, true, true);
		packageIconItem.setId(texture.id);
		packageIconItem.setPosition(pakket.x, hoogte, pakket.z);
		pakket.setPackageIconGameItem(packageIconItem);
	}
	
	public Pakket getPickedUpPackage() {
		return pickedUpPackage;
	}
	
	public boolean isPackageGate(int ap, int gate){
		return apController.getAirport(ap).isPackageGate(gate);
	}

	public List<Pakket> getFreePackages() {
		return freePackages;
	}

	public void setFreePackages(List<Pakket> freePackages) {
		this.freePackages = freePackages;
	}

	public List<Pakket> getTakenPackages() {
		return takenPackages;
	}

	public void setTakenPackages(List<Pakket> takenPackages) {
		this.takenPackages = takenPackages;
	}
}
