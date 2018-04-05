package game;

import java.util.ArrayList;

import autopilotLibrary.MyAutopilotModule;

public class AirportController {

	private ArrayList<Airport> airports = new ArrayList<Airport>();  
    private final float AIRPORT_W = 50;  
    private final float AIRPORT_L = 100;
	
	public AirportController() {
        Airport air0 = new Airport(0,0,0,AIRPORT_W,AIRPORT_L,0);
        Airport air1 = new Airport(2000,1000,(float) (Math.PI/4),AIRPORT_W,AIRPORT_L,1);
        airports.add(air0);
        airports.add(air1);
	}

	public ArrayList<Airport> getAirports() {
		return airports;
	}

	public void setAirports(ArrayList<Airport> airports) {
		this.airports = airports;
	}

	public void defineAirports(MyAutopilotModule autopilotModule) {
		autopilotModule.defineAirportParams(AIRPORT_L, AIRPORT_W);
		for(Airport ap : getAirports()){
			autopilotModule.defineAirport(ap.getX(), ap.getZ(), ap.getCenterToRunway0X(), ap.getCenterToRunway0Z());
		}
	}

}
