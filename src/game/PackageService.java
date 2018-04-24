package game;

import java.util.ArrayList;

import drone.Drone;
import interfaces.AutopilotModule;

public class PackageService {
	
	AirportController apController;
	DronesController droneController;
	
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
				if( ap.onGate0(d.getX(), d.getZ())) System.out.println("ON GATE 0");
				if( ap.onGate1(d.getX(), d.getZ())) System.out.println("ON GATE 1");
			 }
		 }
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
		
		apController.deliverPackage(fromAp, fromGate, toAp, toGate);
		apModule.deliverPackage(fromAp, fromGate, toAp, toGate);
	}
	
	public boolean isPackageGate(int ap, int gate){
		return apController.getAirport(ap).isPackageGate(gate);
	}


}
