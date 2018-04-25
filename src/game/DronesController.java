package game;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;
import drone.Drone;
import interfaces.AutopilotModule;
import interfaces.AutopilotOutputs;
import interfaces.Config;
import interfaces.Inputs;

/**
 * 
 * A class of drone controllers.
 *
 */
public class DronesController {
	
	/**
	 * List of all drones.
	 */
	private ArrayList<Drone> drones = new ArrayList<Drone>();
	/**
	 * The airportController of the world.
	 */
	private AirportController apController;
	
	public DronesController(AirportController apController){
		this.apController = apController;
		addDrone(new Drone(0,apController.getAirports().get(0), 0, 0));
    	//addDrone(new Drone(1,apController.getAirports().get(1), 0, 0));
	}
	
	/**
	 * Defines the drones to the given autopilotModule.
	 */
	public void defineDrones(Renderer renderer, AutopilotModule apModule){
		for(Drone drone : getDrones()){
			Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
				drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
				drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(),
				renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeight, "", drone.wheelY,
				drone.frontWheelZ, drone.rearWheelZ, drone.rearWheelX, drone.tyreSlope, drone.dampSlope, drone.wheelRadius,
				drone.maxRem, drone.maxWrijving);
			apModule.defineDrone(drone.getStartingAirport().getId(), drone.getStartingGate(), 0, config);
		}
		//TODO VOOR NU EVEN DEZE TWEE DRONES AANMAKEN
//		Drone drone = getDrones().get(0);
//		Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
//				drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
//				drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(),
//				renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeight, "", drone.wheelY,
//				drone.frontWheelZ, drone.rearWheelZ, drone.rearWheelX, drone.tyreSlope, drone.dampSlope, drone.wheelRadius,
//				drone.maxRem, drone.maxWrijving);
//		apModule.defineDrone(0, 0, 0, config);
//		
//		drone = getDrones().get(1);
//		config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
//				drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
//				drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(),
//				renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeight, "", drone.wheelY,
//				drone.frontWheelZ, drone.rearWheelZ, drone.rearWheelX, drone.tyreSlope, drone.dampSlope, drone.wheelRadius,
//				drone.maxRem, drone.maxWrijving);
//		
//		apModule.defineDrone(1, 0, 0, config);
	}
	
    /**
     * Checks if the given drone is crashed.
     */
	public boolean checkCrash(Drone drone){
		if(drone.checkCrash()) return true;
		else if(drone.getLeftWheel().isGround() || drone.getRightWheel().isGround() || drone.getFrontWheel().isGround()){
			if(onTarmac(drone)){
				System.out.println("TARMAC");
				return false;
			}
			else{
				System.out.println("GRASS");
				return true;
			}
		}
		return false;
	}
	
    /**
     * Checks if the two given drones have crashed.
     */
	public boolean checkCrash(Drone d, Drone d2){	
		if(Math.sqrt(Math.pow(d2.getX() - d.getX(), 2))
				+ Math.sqrt(Math.pow(d2.getY() - d.getY(), 2))
				+ Math.sqrt(Math.pow(d2.getZ() - d.getZ(), 2))
		    < 5) return true;
		else return false;
	}
	
	/**
	 * Checks if the given drone is on tarmac - in an airport.
	 */
	private boolean onTarmac(Drone drone){
		for (Airport ap : apController.getAirports()){
			if(ap.onAirport(drone.getState().getX(), drone.getState().getZ())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Starts a time passed for every drone in the given autopilotModule.
	 */
	public void startTimePassed(AutopilotModule autopilotModule, Renderer renderer, float time){
		for(Drone drone : getDrones()){
			Inputs input = new Inputs(
	        		renderer.getPixelsarray(),
	        		drone.getState().getX(),
	        		drone.getState().getY(),
	        		drone.getState().getZ(),

	        		drone.getHeading(),
	        		drone.getPitch(),
	        		drone.getRoll(),

	        		time);
			autopilotModule.startTimeHasPassed(drone.getId(), input);
		}
	}
	
	/**
	 * Completes a time passed for every drone in the given autopilotModule.
	 */
	public void completeTimePassed(AutopilotModule autopilotModule, float time){
		for(Drone drone : getDrones()){
			AutopilotOutputs output = autopilotModule.completeTimeHasPassed(drone.getId());
			drone.update(output, time);
		}
	}
	
	/**
	 * Removes given drone from the droneController.
	 * @param d
	 */
	public void remove(Drone d) {
		this.getDrones().remove(d);
	}
	
	public ArrayList<Drone> getDrones() {
		return drones;
	}
	
	public void addDrone(Drone d){
		drones.add(d);
	}

	public AirportController getApController() {
		return apController;
	}

	public void setApController(AirportController apController) {
		this.apController = apController;
	}

	public boolean isEmpty() {
		return getDrones().isEmpty();
	}
}
