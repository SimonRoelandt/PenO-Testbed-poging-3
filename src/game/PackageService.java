package game;

import java.util.ArrayList;
import drone.Drone;
import interfaces.AutopilotModule;

public class PackageService {
	
	private AirportController apController;
	private DronesController droneController;
	private ArrayList<Delivery> freePackages = new ArrayList<Delivery>();
	private ArrayList<Delivery> takenPackages = new ArrayList<Delivery>();
	
	public class Delivery{
		float x,z;
		public Delivery(float x,float z){
			this.x = x;
			this.z = z;
		}
		public Delivery(float[] pos){
			this.x = pos[0];
			this.z = pos[1];
		}
		
		public float getX(){
			return x;
		}
		
		public float getY(){
			return z;
		}
	}
	
	public PackageService(AirportController apC, DronesController dC) {
		this.apController = apC;
		this.droneController = dC;
	}
	
	/**
	 * Check for pickup of any packages.
	 */
	public void checkPickup() {
		System.out.println("CHECK PICKUP");
		 for(Drone d : droneController.getDrones()){
			 for(Airport ap : apController.getAirports()){
				if(ap.isPackageGate0() && ap.onGate0(d.getX(), d.getZ())){
					System.out.println("ON GATE 0 + PACKAGE AVAILABLE");
					if(d.getState().getVelocity().length() < 1.0) pickup(d, ap, 0);
				}
				if(ap.isPackageGate1() && ap.onGate1(d.getX(), d.getZ())){
					System.out.println("ON GATE 1 + PACKAGE AVAILABLE");
					if(d.getState().getVelocity().length() < 1.0) pickup(d, ap, 1);
				}
			 }
		 }
	}
	
	/**
	 * Picks up a package at the given airport and gate with the given drone.
	 */
	private void pickup(Drone d, Airport ap, int gate){
		System.out.println("PICKUP!!!!");
		ap.setPackageGate(false, gate);
		d.setCarryingPackage(true);
		//freePackages.remove(o)
	}
	
	
	/**
	 * Create a new package deliver event from one airport to another.
	 */
	public void newPackage(AutopilotModule apModule, int fromAp, int toAp){
		int fromGate = 0;
		int toGate = 0;
		
		if(!isPackageGate(fromAp,1)) fromGate = 1;
		if(!isPackageGate(fromAp,0)) fromGate = 0;
		else{
			System.out.println("NO GATE FREE");
			return;
		}
		
		freePackages.add(new Delivery(apController.getAirport(fromAp).getMiddleGate0()));
		apController.deliverPackage(fromAp, fromGate, toAp, toGate);
		apModule.deliverPackage(fromAp, fromGate, toAp, toGate);
	}
	
	public boolean isPackageGate(int ap, int gate){
		return apController.getAirport(ap).isPackageGate(gate);
	}

	public ArrayList<Delivery> getFreePackages() {
		return freePackages;
	}

	public void setFreePackages(ArrayList<Delivery> freePackages) {
		this.freePackages = freePackages;
	}

	public ArrayList<Delivery> getTakenPackages() {
		return takenPackages;
	}

	public void setTakenPackages(ArrayList<Delivery> takenPackages) {
		this.takenPackages = takenPackages;
	}
}
