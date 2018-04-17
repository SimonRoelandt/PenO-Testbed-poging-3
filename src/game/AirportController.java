package game;

import java.util.ArrayList;

import autopilotLibrary.MyAutopilotModule;

/**
 * 
 * A class of airportControllers that control all airports in a world.
 *
 */
public class AirportController {
	
	/**
	 * The list of airports.
	 */
	private ArrayList<Airport> airports = new ArrayList<Airport>();
	/**
	 * The W value of airports - depends on take-off/landing length - 2W = length of runway.
	 */
    private final float AIRPORT_W = 175;
    /**
     * The L value of airports - depends on landing accuracy - 1L = width of runway.
     */
    private final float AIRPORT_L = 50;
	
    /**
     * Creates a new airportController with a number of airports.
     */
	public AirportController() {
        Airport air0 = new Airport(0,0,0,AIRPORT_W,AIRPORT_L,0);
        Airport air1 = new Airport(20,20,(float) (Math.PI/4),AIRPORT_W,AIRPORT_L,1);
        airports.add(air0);
        airports.add(air1);
	}
	
	/**
	 * Defines both the airport parameters and the airports for the given autopilotModule.
	 * @param autopilotModule
	 */
	public void defineAirports(MyAutopilotModule autopilotModule) {
		autopilotModule.defineAirportParams(AIRPORT_L, AIRPORT_W);
		for(Airport ap : getAirports()){
			autopilotModule.defineAirport(ap.getX(), ap.getZ(), ap.getCenterToRunway0X(), ap.getCenterToRunway0Z());
		}
	}
	
	public ArrayList<Airport> getAirports() {
		return airports;
	}

	public void setAirports(ArrayList<Airport> airports) {
		this.airports = airports;
	}
}
