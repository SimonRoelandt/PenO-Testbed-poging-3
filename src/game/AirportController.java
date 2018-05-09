package game;

import java.util.ArrayList;
import java.util.List;
import autopilotLibrary.MyAutopilotModule;
import engine.GameItem;

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
    private final float AIRPORT_W = 250;
    /**
     * The L value of airports - depends on landing accuracy - 1L = width of runway.
     */
    private final float AIRPORT_L = 50;
	
    public List<GameItem> airportItems = new ArrayList<GameItem>();
    
    
    /**
     * Creates a new airportController with a number of airports.
     */
	public AirportController() {
        Airport air0 = new Airport(0,0,0,AIRPORT_W,AIRPORT_L,0);
        Airport air1 = new Airport(0,-2500,0,AIRPORT_W,AIRPORT_L,1);
        Airport air2 = new Airport(0,2500,0,AIRPORT_W,AIRPORT_L,2);
        Airport air3 = new Airport(-400,400,(float)Math.PI/2,AIRPORT_W,AIRPORT_L,3);
        Airport air4 = new Airport(1000,-500,(float)Math.PI/4,AIRPORT_W,AIRPORT_L,4);
        Airport air5 = new Airport(-500,-1000,-(float)Math.PI/5,AIRPORT_W,AIRPORT_L,5);
        airports.add(air0);
        airports.add(air1);
        airports.add(air2);
//        airports.add(air3);
//        airports.add(air4);
//        airports.add(air5);

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
	
	public void visualise() {
		for (Airport airport : airports) {
			airport.visualise();
			airportItems.add(airport.getItem());
		}
	}
	
	/**
	 * Gets the airport of the given id.
	 * @param id
	 */
	public Airport getAirport(int id){
		for(Airport ap : getAirports()){
			if(ap.getId() == id) return ap;
		}
		return null;
	}

	
	public ArrayList<Airport> getAirports() {
		return airports;
	}

	public void setAirports(ArrayList<Airport> airports) {
		this.airports = airports;
	}
	
	public List<GameItem> getAirportItems() {
		return this.airportItems;
	}

	public void deliverPackage(int fromAp, int fromGate, int toAp, int toGate, Pakket pakket) {
		getAirport(fromAp).setPackageGate(pakket, fromGate);
	}
}
