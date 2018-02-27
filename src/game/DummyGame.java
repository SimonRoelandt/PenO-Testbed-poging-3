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
import engine.Timer;
import engine.Window;
import graph.Camera;
import graph.Mesh;
import interfaces.*;
import autopilotLibrary.CommunicatieTestbed;


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
    
    private static final float CAMERA_POS_STEP = 0.01f;
    
    private List<GameItem> gameItems;

    private GameItem droneItem;
    
    private Balk balk;
    
    private Mesh mesh;
    
    private Vector3f afstand = new Vector3f();
    
    public Drone drone;
    
    private Timer timer;
    
    private GUI gui;
    
    private CommunicatieTestbed comm;
    
    private List<Mesh> meshList = new ArrayList<Mesh>();;
    
    public boolean startSimulation = false;
    
    public boolean simulationEnded = false;
    
    public boolean sendConfig = false;


    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0, 1, 2);
        camera.setRotation(0,0,0);
        cameraFree = new Camera();
        cameraFree.setPosition(0, 1, 2);
        cameraSide = new Camera();
        cameraSide.setPosition(30, 30, -50);
        cameraSide.setRotation(0, -90f, 0);
        cameraInc = new Vector3f(0,0,0);
        cameraPlane = new Camera();
        cameraPlane.setPosition(0, 0, 0);
        cameraTop = new Camera();
        cameraTop.setPosition(-20, 300, -50);
        cameraTop.setRotation(90f, -90f, 0);
        timer = new Timer();
        drone = new Drone(0, 0, 0, new Vector3f(0,0,0));
        gui = new GUI(this);
    }


    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        gui.init();
        gui.run();
        this.window = window;
        comm = new CommunicatieTestbed();
        sendConfig = true;
        
        // Maak de gameItem meshes aan
        
        //Drone Item
        
        Balk droneVisual = new Balk(drone.getXPos()-0.5f, drone.getYPos()-0.5f, drone.getZPos()-0.5f, 1f, 1f, 1f, Color.black);
        //Mesh meshDrone = new Mesh(droneVisual.positions(), droneVisual.colours(), droneVisual.indices());
        Mesh meshDrone = OBJLoader.loadOBJModel("Eurofighter");
        GameItem droneItem = new GameItem(meshDrone,false);
        droneItem.setScale(0.2f);
        droneItem.setRotation(0f, 0f, 0f);
        this.droneItem = droneItem;
        
       
       
        
        //Kubussen
        balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, Color.red);
        mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());
        createMesh(Color.red);
        createMesh(Color.green);
        createMesh(Color.blue);
        createMesh(Color.yellow);
        createMesh(Color.cyan);
        createMesh(Color.magenta);

        //--------------------------overbodig vanaf hier
        
        GameItem gameItem = new GameItem(mesh,true);
        GameItem gameItem2 = new GameItem(mesh,true);
        GameItem gameItem3 = new GameItem(mesh,true);
        GameItem gameItem4 = new GameItem(mesh,true);
        
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

        
        //---------------------------------tot hier
        
       //gameItems = worldGenerator(5);
       gameItems.add(droneItem);
      
       
       //wereld
       Ground ground = new Ground();
       Mesh groundMesh = new Mesh(ground.vertices(), ground.colours(), ground.indices());
       GameItem groundItem = new GameItem(groundMesh, false);
       //gameItems.add(groundItem);
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
	    	
    		if(sendConfig == true){
    			timer.init();
    			//Maak config file aan voor de autopilot
    	        Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
    	        							drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
    	        							drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(), 
    	        							renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeightAutopilot);
    	        //Maak eerste input aan voor autopilot
    	        Inputs input = new Inputs(renderer.getPixelsarray(), drone.getXPos(), drone.getYPos(), drone.getZPos(), drone.getHeading(), drone.getPitch(), drone.getRoll(), timer.getTot());
    	        //Start de simulatie in autopilot
    	        AutopilotOutputs outputs = comm.simulationStarted((AutopilotConfig)config,(AutopilotInputs)input);
    	        //Update de drone
    	        drone.update(outputs,0);
    	        sendConfig = false;
    		}
    		
	        //Roep een timePassed op in Autopilot

	        float time = timer.getTot();
	        AutopilotOutputs outputs =  (Outputs) comm.timePassed((AutopilotInputs) new Inputs(renderer.getPixelsarray(), drone.getXPos(), drone.getYPos(), drone.getZPos(), drone.getHeading(), drone.getPitch(), drone.getRoll(), time));
	        
	        System.out.println("TIME" + time);
	        
	        //Update de drone
	        //System.out.println("LASTLOOPTIME" + timer.getElapsedTime());
	        
	        
	        
	        
	        
	        drone.update(outputs,timer.getElapsedTime());
	        
	        
	        
	        
	        
	        
	        drone.setLeftWingInclination((float) 0.0);
	        drone.setRightWingInclination((float) 0.0);
	        drone.setHorStabInclination((float) 0.0);
	        drone.setVerStabInclination((float) 0.0);
	        drone.setThrust((float) 0.0);
	
	        //Vlieg recht door + gravitatie
	        //System.out.println("Pos: " + drone.getPos());
	        //System.out.println("Vel: " + drone.getVelocity());
	        //drone.setVelocity(drone.getNewVelocity(timer.getElapsedTime()));
	        droneItem.setPosition(drone.getXPos(), drone.getYPos(), drone.getZPos());
	        System.out.println("positie " + drone.getXPos() + drone.getYPos() + drone.getZPos());
	        droneItem.setRotation(drone.getPitch(), drone.getHeading(), drone.getRoll());
	        
	        //meer impressionant:
	        //camera.setPosition(drone.getXPos(), drone.getYPos()+1f, drone.getZPos()+0.4f);
	        
	        //volgens opgave:
	        camera.setPosition(drone.getXPos(), drone.getYPos()+1, drone.getZPos()+2);
	        camera.setRotation(0,0,0);
	        
	        //cameraSide.setPosition(cameraSide.getPosition().x, cameraSide.getPosition().y, drone.getZPos());
	        //cameraTop.setPosition(cameraTop.getPosition().x, cameraTop.getPosition().y, drone.getZPos());
	        
	        cameraPlane.setPosition(drone.getXPos(), drone.getYPos(), drone.getZPos());
	        cameraPlane.setRotation(0f, 0f, 0f);
	        
	       // System.out.println(drone.getZPos() - gameItems[0].getPosition().z);
	       //System.out.println("Vel: " + drone.getVelocity()); 
	     
	        
	        //bepaalt wanneer de simulatie stopt
	        boolean end = true;
	        for(int i = 0 ; i < gameItems.size(); i++){
	        	if(gameItems.get(i) != null && gameItems.get(i)!=droneItem){
			        Vector3f.sub(drone.getPositionInWorld(), gameItems.get(i).getPosition(), afstand);
			        if (afstand.length()<4f) {
			        	//System.out.println(timer.getTot() + "end");
			       	    gameItems.remove(i);
			       	    System.out.println("TARGET HIT");
			        }
	        	}
	        	if(gameItems.get(i).getRenderOnPlaneView() == true) end = false;
	        }
	        //if (end == true) window.simulationEnded = true;
	        //if (end == true) simulationEnded = true;
	        
	        
	//        if (timer.getTot() > 2000000) {
	//        	System.out.println(timer.getTot());
	//        	window.simulationEnded = true;
	//        }
	    	}
    	else {
    		float f = timer.getElapsedTime();
    	}
    }
    
    //Genereert n willekeurige kubussen
    public List<GameItem> worldGenerator(int n){
    	Random rand = new Random();
    	List<GameItem> gameItems = new ArrayList<GameItem>();
    	//Balk balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, new float[]{(179f/255),0f,0f}, new float[]{115f/255,0f,0f},  new float[]{77f/255,0f,0f},  new float[]{217f/255,0f,0f},  new float[]{255f/255,0f,0f},  new float[]{38f/255,0f,0f});
    	//Mesh mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());
        
        for(int i=0;i<n;i++){
        	GameItem gameItem = new GameItem(randomMesh(),true);
        	
        	float z = i * -40f - 30;
        	float x = rand.nextInt(20) -10;
            float y = rand.nextInt(10);
            while(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) > 10){
            	x = rand.nextInt(20) -10;
                y = rand.nextInt(10) ;
            }
        	
        	gameItem.setPosition(x, y, z);
            gameItem.setRotation(0, 0, 0);
            gameItems.add(gameItem);
        }
        return gameItems;
    }
    
    public void addGameItem(GameItem gameItem){
    	this.gameItems.add(gameItem);
    }
    
    public void addGameItemAtPos(float xPos, float yPos, float zPos) {
    	GameItem gameItem = new GameItem(randomMesh(), true);
    	gameItem.setPosition(xPos, yPos, zPos);
    	this.gameItems.add(gameItem);
    }
    
    public void readCubesFromFile(File file) {
    	gameItems.clear();
    	gameItems.add(droneItem);
    	CubeLoader cl = new CubeLoader();
    	cl.generatePositions(file);
    	for (int i = 0; i < cl.xpos.size(); i++) {
    		GameItem gameItem = new GameItem(randomMesh(), true);
    		gameItem.setPosition(cl.xpos.get(i), cl.ypos.get(i), cl.zpos.get(i));
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
    	Balk createbalk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, color);
    	meshList.add(new Mesh(createbalk.positions(), createbalk.colours(), createbalk.indices()));	
    }
    
    public List<GameItem> getGameItems() {
    	return gameItems;
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
        comm.simulationEnded();
    }
}