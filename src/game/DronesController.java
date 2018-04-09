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
        float[] air0 = apController.getAirports().get(0).getMiddleGate0();
        float[] air1 = apController.getAirports().get(1).getMiddleGate0();
        addDrone(new Drone(air0[0], 0, air0[1], new Vector3f(0,0,0)));
    	addDrone(new Drone(air1[0], 0, air1[1], new Vector3f(0,0,0)));
	}
	
	/**
	 * Defines the drones to the given autopilotModule.
	 */
	public void defineDrones(Renderer renderer, AutopilotModule apModule){
//		for(Drone drone : getDrones()){
//			Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
//				drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
//				drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(),
//				renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeight, "", drone.wheelY,
//				drone.frontWheelZ, drone.rearWheelZ, drone.rearWheelX, drone.tyreSlope, drone.dampSlope, drone.wheelRadius,
//				drone.maxRem, drone.maxWrijving);
//			apModule.defineDrone(0, 0, 0, config); // TODO DRONE ZOU BEGIN AIRPORT MOETEN BIJHOUDEN
//		}
		//TODO VOOR NU EVEN DEZE TWEE DRONES AANMAKEN
		Drone drone = getDrones().get(0);
		Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
				drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
				drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(),
				renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeight, "", drone.wheelY,
				drone.frontWheelZ, drone.rearWheelZ, drone.rearWheelX, drone.tyreSlope, drone.dampSlope, drone.wheelRadius,
				drone.maxRem, drone.maxWrijving);
		apModule.defineDrone(0, 0, 0, config);
		
		drone = getDrones().get(1);
		config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
				drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
				drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(),
				renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeight, "", drone.wheelY,
				drone.frontWheelZ, drone.rearWheelZ, drone.rearWheelX, drone.tyreSlope, drone.dampSlope, drone.wheelRadius,
				drone.maxRem, drone.maxWrijving);
		
		apModule.defineDrone(1, 0, 0, config);
	}
	
    /**
     * Checks if the given drone is crashed.
     */
	public boolean checkCrash(Drone drone){
		if(drone.getLeftWheel().isGround() || drone.getRightWheel().isGround() || drone.getFrontWheel().isGround()){
			if(onTarmac(drone)){
				if(drone.checkCrash()) return true;
				else return false;
			}
			else return false;
		}
		return false;
	}
	
	/**
	 * Checks if the given drone is on tarmac.
	 */
	private boolean onTarmac(Drone drone){
		for (Airport ap : apController.getAirports()){
			if(ap.onAirport(drone.getState().getX(), drone.getState().getY())){
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
	 * Completes a time passed in the given autopilotModule.
	 */
	public void completeTimePassed(AutopilotModule autopilotModule, float time){
		for(Drone drone : getDrones()){
			AutopilotOutputs output = autopilotModule.completeTimeHasPassed(drone.getId());
			drone.update(output, time);
		}
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

}
