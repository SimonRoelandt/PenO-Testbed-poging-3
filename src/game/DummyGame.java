package game;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import GUI.GUI;
import drone.Drone;
import engine.Balk;
import engine.CubeLoader;
import engine.GameItem;
import engine.Ground;
import engine.IGameLogic;
import engine.MouseInput;
import engine.OBJLoader;
import engine.Square;
import engine.Timer;
import engine.Window;
import graph.Camera;
import graph.Mesh;
import graph.Texture;
import autopilotLibrary.MyAutopilotModule;


public class DummyGame implements IGameLogic {

	
	private static final float MOUSE_SENSITIVITY = 0.1f;
	
    public final Renderer renderer;
    
    public Window window;
    
    private final Vector3f cameraInc;
    
    private List<Camera> chaseCameras = new ArrayList<Camera>();
    
    private final Camera cameraFree;
    
    private final Camera cameraPlane;
    
    private final Camera cameraSide;
    
    public final Camera cameraTop;
    
    private static final float CAMERA_POS_STEP = 1f;
    
    private ArrayList<GameItem> gameItems = new ArrayList<GameItem>();
    
    private final float cubeScale = 5f;
    
    public DronesController droneController; 
    private AirportController apController;
    private PackageService packageService;
    
    private float totTime = 0;

 
        //INITIAL SETUPS
   
    private Balk balk;

    private Mesh mesh;

    private GUI gui;
        
    private List<Mesh> meshList = new ArrayList<Mesh>();;
    
    public boolean startSimulation = false;
    
    public boolean simulationEnded = false;
    
    public boolean sendConfig = false;
    
    private Ground ground;
    
    public int currentDroneId;
   
    private MyAutopilotModule autopilotModule;
    
    public DummyGame() {
        renderer = new Renderer();
        autopilotModule = new MyAutopilotModule();
        apController = new AirportController();
        droneController = new DronesController(apController);
        packageService = new PackageService(apController,droneController);
        
        for (Drone drone : droneController.getDrones().values()) {
        	Camera camera = new Camera();
            camera.setPosition(drone.getState().getPosition().x-2*(float)Math.sin((double)(drone.getState().getHeading())),drone.getState().getPosition().y+1,drone.getState().getPosition().z+2*(float)Math.cos((double)drone.getState().getHeading()));
            camera.setRotation(0,(float)Math.toDegrees(drone.getState().getHeading()),0);
        	chaseCameras.add(camera);
        }

        cameraFree = new Camera();
        cameraFree.setPosition(0, 1, 5);
        cameraSide = new Camera();
        cameraSide.setPosition(30, 30, -50);
        cameraSide.setRotation(0, -90f, 0);
        cameraInc = new Vector3f(0,0,0);
        cameraPlane = new Camera();
        cameraPlane.setPosition(0, 0, 0);
        cameraTop = new Camera();
        cameraTop.setPosition(20, 300, -50);
        cameraTop.setRotation(-90f, -90f, 0);

        
        
        
        
        gui = new GUI(this);
    }
    
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        gui.init();
        gui.run();
        this.window = window;
        
        //INIT autopilotModule
        apController.defineAirports(autopilotModule);
        droneController.defineDrones(renderer,autopilotModule);
        
      //DRONES VISUAL
        for( Drone drone : droneController.getDrones().values()) {
        	Color[] colors = {Color.RED, Color.blue};
            //Balk droneVisual = new Balk(drone.getState().getX()-0.5f, drone.getState().getY()-0.5f, drone.getState().getZ()-0.5f, 1f, 1f, 1f, Color.black, false,1);
            //drone
        	Mesh meshDrone = OBJLoader.loadOBJModel("p39", Color.GREEN);
            GameItem droneItem = new GameItem(meshDrone,false, false, false);
            droneItem.setScale(0.5f);
            droneItem.setRotation(drone.getState().getPitch(), drone.getState().getHeading(), drone.getState().getRoll()); //TODO JUIST?
            droneItem.setPosition(drone.getState().getX(), drone.getState().getY()-1, drone.getState().getZ());
            drone.setGameItem(droneItem);
            //Icon
        	drone.visualise();
            Mesh meshDroneIcon = drone.generateMesh();
            GameItem droneIconItem = drone.generateGameItem();
    		droneIconItem.setPosition(drone.getState().getX(), drone.hoogte, drone.getState().getZ());
    		droneIconItem.setRotation(0f, drone.getState().getHeading(), 0f);
    		drone.setIconGameItem(droneIconItem);
            //droneItems.add(droneItem);
        }

      //CUBES VISUAL
        balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, Color.red, false,1);
        Texture balkTexture = null;
        mesh = new Mesh(balk.positions(), balk.colours(), balk.indices(), new float[]{}, balkTexture);
        createMesh(Color.red);
        createMesh(Color.green);
        createMesh(Color.blue);
        createMesh(Color.yellow);
        createMesh(Color.cyan);
        createMesh(Color.magenta);

        GameItem gameItem = new GameItem(mesh,true, false, false);
        GameItem gameItem2 = new GameItem(mesh,true, false, false);
        GameItem gameItem3 = new GameItem(mesh,true, false, false);
        GameItem gameItem4 = new GameItem(mesh,true, false, false);
        
        gameItem4.setPosition(-10, 0, 0);
        gameItem4.setRotation(-60f, 20f, 40f);
        gameItem3.setPosition(10, 10, 0);
        gameItem3.setRotation(34f, 53f, 45f);
        gameItem2.setPosition(0, 10 , -100);
        gameItem.setPosition(1000, 50, 2000);
        gameItem.setScale(10f);
        //gameItem.setPosition(0, -30, -100);
        //gameItem.setPosition(0, 0, -50);
        //gameItem.setPosition(0, 38, -200);
        //gameItem.setRotation(-60f, 20f, 40f);
        
        gameItems = new ArrayList<GameItem>();
        //gameItems = new ArrayList<GameItem>(Arrays.asList(gameItem,gameItem2,gameItem3,gameItem4, droneItem));
        
        for( Drone drone : droneController.getDrones().values()) {
            gameItems.add(drone.getGameItem());
            gameItems.add(drone.getIconGameItem());
        }
      
    	        
     //WORLD VISUAL

       /*
       Ground ground = new Ground();
       Mesh groundMesh = new Mesh(ground.vertices(), ground.colours(), ground.indices(), ground.textCoords(), ground.texture);
       GameItem groundItem = new GameItem(groundMesh, false, true);
       groundItem.setId(ground.texture.id);
       //System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ                   "  +  + "          QQQQQQQQQQQQQQQQQQQQQQQ");
       gameItems.add(groundItem);
       */
       
       //TODO
       ground = new Ground();
       ground.generateStart();
       gameItems.addAll(ground.getListGroundItems());
       
       
       //AIRPORT VISUAL
       apController.visualise();
       gameItems.addAll(apController.getAirportItems());
       
       //PACKAGE VISUAL
       packageService.visualise();
       
       
       
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -2;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -2;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -2;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 2;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
    	totTime += interval;
	    gui.update();
	    // Update camera positie
	    cameraFree.movePosition(cameraInc.x * CAMERA_POS_STEP,
	        cameraInc.y * CAMERA_POS_STEP,
	        cameraInc.z * CAMERA_POS_STEP);
	    // Update camera door muis            
	    if (mouseInput.isRightButtonPressed()) {
	        Vector2f rotVec = mouseInput.getDisplVec();
	        cameraFree.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
	    }
	        
    	if (startSimulation && simulationEnded == false) {
    		
    		//Update Drones
    		droneController.startTimePassed(autopilotModule,renderer,totTime());
    		System.out.println("");
    		droneController.completeTimePassed(autopilotModule,interval);
    		
    		//Update visual drone objects
    		for(Drone drone: droneController.getDrones().values()){
    			drone.getGameItem().setPosition(drone.getState().getX(), drone.getState().getY()-1, drone.getState().getZ());
    			drone.getGameItem().setRotation((float)(drone.getPitch()*Math.cos(drone.getHeading())-drone.getRoll()*Math.sin(drone.getHeading())), drone.getHeading(), (float)(drone.getPitch()*Math.sin(drone.getHeading())+drone.getRoll()*Math.cos(drone.getHeading())));
    			drone.getIconGameItem().setPosition(drone.getState().getX(), drone.hoogte , drone.getState().getZ());
    			//drone.getIconGameItem().setRotation(0f, drone.getState().getHeading(), 0f);
    		}
    		
    		
    		//Check package pick-up
    		packageService.checkPickup();
    		
    		//Check package delivery
    		if (packageService.checkDelivery()) {
        		//remove delivered packages
        		gameItems.remove(packageService.getDeliveredPackage().getPackageIconGameItem());
    		}
    		
    		//update visual packages
    		for (Pakket pakket : packageService.getTakenPackages()) {
    			pakket.getPackageIconGameItem().setPosition(pakket.getDrone().getState().getPosition().getX()-pakket.getDrone().droneIconLength/2.5f,
    					pakket.hoogte, 
    					pakket.getDrone().getState().getPosition().getZ()-pakket.getDrone().droneIconLength*(-1/2f+1/1.1f));
    			pakket.getPackageIconGameItem().setScale(0.7f);
    		}
    		

    		
    		
    		//Update chase-cameras
    		if(!droneController.isEmpty() && droneController.getDrones().get(currentDroneId) != null){
    			Vector3f currentDronePos = droneController.getDrones().get(currentDroneId).getState().getPosition();
    			float currentDroneHeading = droneController.getDrones().get(currentDroneId).getState().getHeading();
    			chaseCameras.get(currentDroneId).setPosition(currentDronePos.x-2*(float)Math.sin((double)(currentDroneHeading)),currentDronePos.y+1,currentDronePos.z+2*(float)Math.cos((double)currentDroneHeading));
    			chaseCameras.get(currentDroneId).setRotation(0,(float)Math.toDegrees(currentDroneHeading),0);

    		}
    		
    		//Clean-up crashed drones.
    		ArrayList<Drone> toRemove = new ArrayList<Drone>();
    		for(Drone d : droneController.getDrones().values()){
    	 		//Crash on ground
    			if(droneController.checkCrash(d)) toRemove.add(d);
    			//Crash between two drones
    			for(Drone d2 : droneController.getDrones().values()){
    				if(!d.equals(d2) && droneController.checkCrash(d,d2)){
    					toRemove.add(d);
    					toRemove.add(d2);
    				}
    			}
    		}
    		for(Drone d : toRemove){
    			removeDrone(d);
    		}
    	}
    }
    
    /**
     * Adds a package request from one airport to another.
     * @param fromAirport
     * @param toAirport
     */
    public void addPackage(int fromAirport, int toAirport){
    	Pakket pakket = packageService.newPackage(autopilotModule, fromAirport, toAirport);
    	if (pakket != null) {
    		System.out.println("PAKKET GEADD IN GAMEITEMS");
    		gameItems.add(pakket.getPackageIconGameItem());
    	}
    	
    }
    
    /**
     * Removes the given drone from the world.
     * @param d
     * 		  The given drone.
     */
    private void removeDrone(Drone d) {
		this.getGameItems().remove(d.getGameItem());
		this.getGameItems().remove(d.getIconGameItem());
		droneController.remove(d);
	}
    /*
    private void removePackagItem(Package p) {
    	gameItems.remove(p.getGameItem());
    }
     */
    
    
	/**
	 * Generates the given number of cubes at random (in a pre-defined area) position.
	 * @param n
	 * 		  The nb of cubes.
	 * @return list of cube gameItems.
	 */
    public List<GameItem> worldGenerator(int n){
    	Random rand = new Random();
    	List<GameItem> gameItems = new ArrayList<GameItem>();
    	//Balk balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, new float[]{(179f/255),0f,0f}, new float[]{115f/255,0f,0f},  new float[]{77f/255,0f,0f},  new float[]{217f/255,0f,0f},  new float[]{255f/255,0f,0f},  new float[]{38f/255,0f,0f});
    	//Mesh mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());
        
        for(int i=0;i<n;i++){
        	GameItem gameItem = new GameItem(randomMesh(),true, false, false);
        	
        	float z = i * -40f - 30;
        	float x = rand.nextInt(20) -10;
            float y = rand.nextInt(10);
            while(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) > 10){
            	x = rand.nextInt(20) -10;
                y = rand.nextInt(10) ;
            }
        	
        	gameItem.setPosition(x, y, z);
            gameItem.setRotation(0, 0, 0);
            gameItem.setScale(cubeScale);
            gameItems.add(gameItem);
        }
        return gameItems;
    }
    
    public void addGameItem(GameItem gameItem){
    	this.gameItems.add(gameItem);
    }
    
    public void addGameItemAtPos(float xPos, float yPos, float zPos) {
    	GameItem gameItem = new GameItem(randomMesh(), true, false, false);
    	gameItem.setPosition(xPos, yPos, zPos);
    	gameItem.setScale(cubeScale);
    	this.gameItems.add(gameItem);
    }
    
    public void readCubesFromFile(File file) {
    	CubeLoader cl = new CubeLoader();
    	cl.generatePositions(file);
    	for (int i = 0; i < cl.xpos.size(); i++) {
    		GameItem gameItem = new GameItem(randomMesh(), true, false, false);
    		gameItem.setPosition(cl.xpos.get(i), cl.ypos.get(i), cl.zpos.get(i));
    		gameItem.setScale(cubeScale);
    		addGameItem(gameItem);
    	}
    }
    
    public void addGameItems(List<GameItem> gameItems) {
    	for (GameItem gameItem: gameItems) {
    		this.gameItems.add(gameItem);
    	}
    }
    
    public Mesh randomMesh() {
    	int rnd = new Random().nextInt(meshList.size());
        return meshList.get(rnd);
     
    }
    
    public void createMesh(Color color) {
    	Balk createbalk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, color, false,1);
    	meshList.add(new Mesh(createbalk.positions(), createbalk.colours(), createbalk.indices(),  new float[]{} , null));	
    }
    
    public List<GameItem> getGameItems() {
    	return gameItems;
    }
    @Override
    public void render(Window window) throws Exception {
    	List<GameItem> gameItemsCopy = new ArrayList<GameItem>(gameItems);
    	Collections.copy(gameItemsCopy, gameItems);
        renderer.render(window, chaseCameras.get(currentDroneId), cameraFree, cameraPlane, cameraSide, cameraTop, gameItemsCopy);
    }
    
    public float totTime(){
    	return totTime;
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
        autopilotModule.simulationEnded();
    }
  
}