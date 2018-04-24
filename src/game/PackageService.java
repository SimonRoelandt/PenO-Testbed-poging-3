package game;

import interfaces.AutopilotModule;

public class PackageService {
	
	AirportController apController;
	
	public PackageService(AirportController apC) {
		this.apController = apC;
	}
	
	public void newPackage(AutopilotModule apModule, int fromAp, int toAp){
		int fromGate = 0;
		int toGate = 0;
		
		if(!isPackageGate(fromAp,0)) fromGate = 0;
		if(!isPackageGate(fromAp,1)) fromGate = 1;
		else return;
		
		apController.deliverPackage(fromAp, fromGate, toAp, toGate);
		apModule.deliverPackage(fromAp, fromGate, toAp, toGate);
	}
	
	
	public boolean isPackageGate(int ap, int gate){
		return apController.getAirport(ap).isPackageGate(gate);
	}
}
