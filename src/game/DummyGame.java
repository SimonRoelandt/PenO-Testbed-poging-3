package game;


import static org.lwjgl.glfw.GLFW.*;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
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
import engine.Window;
import graph.Camera;
import graph.Mesh;
import graph.Texture;
import autopilotLibrary.MyAutopilotModule;


public class DummyGame implements IGameLogic {

	
	private static final float MOUSE_SENSITIVITY = 0.01f;
	
    public final Renderer renderer;
    
    public Window window;
    
    private final Vector3f cameraInc;

    private final Camera camera;
    
    private final Camera cameraFree;
    
    private final Camera cameraPlane;
    
    private final Camera cameraSide;
    
    public final Camera cameraTop;
    
    private static final float CAMERA_POS_STEP = 10f;
    
    private ArrayList<GameItem> gameItems = new ArrayList<GameItem>();
    
    private final float cubeScale = 5f;
    
    public DronesController droneController;
     
    private AirportController apController;
    
    private float totTime = 0;

 
        //INITIAL SETUPS
   
    private Balk balk;
    private Mesh mesh;
    private List<Mesh> meshList = new ArrayList<Mesh>();
    private Ground ground;
        
    private GUI gui;
    
    public boolean startSimulation = false;
    public boolean simulationEnded = false;
    public boolean sendConfig = false;
    
    //TODO
    private float xRenderDistance = 5000;
    
    private float zRenderDistance = 5000;
    
    private float groundPieceWidth;
    
    private float xMaxCoordinate;
    
    private float zMaxCoordinate;
    
    private float xMinCoordinate;
    
    private float zMinCoordinate;
    
    private MyAutopilotModule autopilotModule;
    
    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0, 1, 2);
        camera.setRotation(0,0,0);
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
        
        autopilotModule = new MyAutopilotModule();
        apController = new AirportController();
        droneController = new DronesController(apController);
        
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
        for( Drone drone : droneController.getDrones()) {
            Balk droneVisual = new Balk(drone.getState().getX()-0.5f, drone.getState().getY()-0.5f, drone.getState().getZ()-0.5f, 1f, 1f, 1f, Color.black, false);
            Mesh meshDrone = OBJLoader.loadOBJModel("Eurofighter");
            GameItem droneItem = new GameItem(meshDrone,false, false);
            droneItem.setScale(0.5f);
            droneItem.setRotation(drone.getState().getPitch(), drone.getState().getHeading(), drone.getState().getRoll()); //TODO JUIST?
            droneItem.setPosition(drone.getState().getX(), drone.getState().getY(), drone.getState().getZ());
            drone.setGameItem(droneItem);
            //droneItems.add(droneItem);
        }

      //CUBES VISUAL
        balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, Color.red, false);
        Texture balkTexture = null;
        mesh = new Mesh(balk.positions(), balk.colours(), balk.indices(), new float[]{}, balkTexture);
        createMesh(Color.red);
        createMesh(Color.green);
        createMesh(Color.blue);
        createMesh(Color.yellow);
        createMesh(Color.cyan);
        createMesh(Color.magenta);

        GameItem gameItem = new GameItem(mesh,true, false);
        GameItem gameItem2 = new GameItem(mesh,true, false);
        GameItem gameItem3 = new GameItem(mesh,true, false);
        GameItem gameItem4 = new GameItem(mesh,true, false);
        
        gameItem4.setPosition(-10, 0, 0);
        gameItem4.setRotation(-60f, 20f, 40f);
        gameItem3.setPosition(10, 10, 0);
        gameItem3.setRotation(34f, 53f, 45f);
        gameItem2.setPosition(0, 10 , -100);
        gameItem.setPosition(0, 5, -50);
        //gameItem.setPosition(0, -30, -100);
        //gameItem.setPosition(0, 0, -50);
        //gameItem.setPosition(0, 38, -200);
        gameItem.setRotation(-60f, 20f, 40f);
        
        gameItems = new ArrayList<GameItem>();
        //gameItems = new ArrayList<GameItem>(Arrays.asList(gameItem,gameItem2,gameItem3,gameItem4, droneItem));
        
        for( Drone drone : droneController.getDrones()) {
            gameItems.add(drone.getGameItem());
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
       ground.generateStart(2*xRenderDistance, 2*zRenderDistance);
       gameItems.addAll(ground.getListGroundItems());
       groundPieceWidth = ground.getWidthPiece();
       xMaxCoordinate = xRenderDistance - groundPieceWidth/2;
       zMaxCoordinate = zRenderDistance - groundPieceWidth/2;
       xMinCoordinate = -xRenderDistance + groundPieceWidth/2;
       zMinCoordinate = -zRenderDistance + groundPieceWidth/2; 
       //gameItems.add(ground.getGroundGameItem()); 
       
       //AIRPORT VISUAL
       apController.visualise();
       gameItems.addAll(apController.getAirportItems());
       
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
    		
    		//Clean-up crashed drones.
    		for(Drone d : droneController.getDrones()){
    			//Crash on ground
    			if(droneController.checkCrash(d)) removeDrone(d);
    			//Crash between two drones
    			for(Drone d2 : droneController.getDrones()){
    				if(!d.equals(d2) && droneController.checkCrash(d,d2)){
    					removeDrone(d);
    					removeDrone(d2);
    				}
    			}
    		}
    		
    		//Update visual drone objects
    		for(Drone drone: droneController.getDrones()){
    			drone.getGameItem().setPosition(drone.getState().getX(), drone.getState().getY()-1, drone.getState().getZ());
    			drone.getGameItem().setRotation(drone.getPitch(), drone.getHeading(), drone.getRoll());
    			System.out.println("PITCH: " + drone.getPitch());
    		}
    		
    		camera.setPosition(droneController.getDrones().get(0).getState().getX()-5, droneController.getDrones().get(0).getState().getY(), droneController.getDrones().get(0).getState().getZ()+5);
    		camera.setRotation((float )Math.PI/2, 0.0f, 0.0f);
    	}
    }
    
    /**
     * Removes the given drone from the world.
     * @param d
     * 		  The given drone.
     */
    private void removeDrone(Drone d) {
		this.getGameItems().remove(d.getGameItem());
		droneController.remove(d);
	}

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
        	GameItem gameItem = new GameItem(randomMesh(),true, false);
        	
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
    	GameItem gameItem = new GameItem(randomMesh(), true, false);
    	gameItem.setPosition(xPos, yPos, zPos);
    	gameItem.setScale(cubeScale);
    	this.gameItems.add(gameItem);
    }
    
    public void readCubesFromFile(File file) {
    	CubeLoader cl = new CubeLoader();
    	cl.generatePositions(file);
    	for (int i = 0; i < cl.xpos.size(); i++) {
    		GameItem gameItem = new GameItem(randomMesh(), true, false);
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
    	Balk createbalk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, color, false);
    	meshList.add(new Mesh(createbalk.positions(), createbalk.colours(), createbalk.indices(),  new float[]{} , null));	
    }
    
    public List<GameItem> getGameItems() {
    	return gameItems;
    }
  
    public float totTime(){
    	return totTime;
    }
    
    @Override
    public void render(Window window) throws Exception {
        renderer.render(window, camera, cameraFree, cameraPlane, cameraSide, cameraTop, gameItems);
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