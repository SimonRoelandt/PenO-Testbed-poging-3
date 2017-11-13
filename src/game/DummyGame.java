package game;


import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


import drone.Drone;
import engine.Balk;
import engine.GameItem;
import engine.IGameLogic;
import engine.MouseInput;
import engine.OBJLoader;
import engine.Timer;
import engine.Window;
import graph.Camera;
import graph.Mesh;

import api.*;
import autopilotLibrary.CommunicatieTestbed;


public class DummyGame implements IGameLogic {

	private static final float MOUSE_SENSITIVITY = 0.4f;
	
    private final Renderer renderer;
    
    private Window window;
    
    private final Vector3f cameraInc;

    private final Camera camera;
    
    private final Camera cameraPlane;
    
    private final Camera cameraSide;
    
    private final Camera cameraTop;
    
    private static final float CAMERA_POS_STEP = 0.5f;
    
    private List<GameItem> gameItems;

    private GameItem droneItem;
    
    private Vector3f afstand = new Vector3f();
    
    private Drone drone;
    
    private Timer timer;
    
    private GUI gui;
    


    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraSide = new Camera();
        cameraSide.setPosition(-20, 0, -60);
        cameraSide.setRotation(0, -90f, 0);
        camera.setPosition(20, 0, -50);
        camera.setRotation(0, -90f, 0);
        cameraInc = new Vector3f(0,0,0);
        cameraPlane = new Camera();
        cameraPlane.setPosition(0, 0, 0);
        cameraTop = new Camera();
        cameraTop.setPosition(0, 10, -60);
        cameraTop.setRotation(90f, 0, 0);
        timer = new Timer();
        drone = new Drone(0, 0, 0, new Vector3f(0,0,-8));
        //gui = new GUI();
    }


    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        timer.init();
        //gui.init();
        //gui.run();
        this.window = window;
        
        // Maak de gameItem meshes aan
        
        //Drone Item
        Balk droneVisual = new Balk(drone.getXPos()-0.5f, drone.getYPos()-0.5f, drone.getZPos()-0.5f, 1f, 1f, 1f, new float[]{0f,0f,0f}, new float[]{0f,0f,0f},  new float[]{0f,0f,0f},  new float[]{0f,0f,0f},  new float[]{0f,0f,0f},  new float[]{0f,0f,0f});
        //Mesh meshDrone = new Mesh(droneVisual.positions(), droneVisual.colours(), droneVisual.indices());
        Mesh meshDrone = OBJLoader.loadOBJModel("Eurofighter");
        GameItem droneItem = new GameItem(meshDrone,false);
        droneItem.setScale(0.2f);
        droneItem.setRotation(0f, 180f, 0f);
        this.droneItem = droneItem;
        
        //Kubussen
        Balk balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, new float[]{(179f/255),0f,0f}, new float[]{115f/255,0f,0f},  new float[]{77f/255,0f,0f},  new float[]{217f/255,0f,0f},  new float[]{255f/255,0f,0f},  new float[]{38f/255,0f,0f});
        Mesh mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());

        GameItem gameItem = new GameItem(mesh,true);
        GameItem gameItem2 = new GameItem(mesh,true);
        GameItem gameItem3 = new GameItem(mesh,true);
        GameItem gameItem4 = new GameItem(mesh,true);
        
        gameItem4.setPosition(-2, -2, -30);
        gameItem4.setRotation(-60f, 20f, 40f);
        gameItem3.setPosition(-10, -5, -200);
        gameItem3.setRotation(34f, 53f, 45f);
        gameItem2.setPosition(0,0 , -50);
        gameItem.setPosition(10, -20, -100);
        //gameItem.setPosition(0, -30, -100);
        //gameItem.setPosition(0, 0, -50);
        //gameItem.setPosition(0, 38, -200);
        gameItem.setRotation(-60f, 20f, 40f);
          
        gameItems = new ArrayList<GameItem>(Arrays.asList(gameItem,gameItem2,gameItem3,gameItem4, droneItem));

//        gameItems = worldGenerator(5);
//        gameItems.add(drone);
        
        //Maak config file aan voor de autopilot
        Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
        							drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
        							drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(), 
        							renderer.fov, renderer.fov, renderer.imageWidthAutopilot, renderer.imageHeightAutopilot);
        
        //Maak eerste input aan voor autopilot
        Inputs input = new Inputs(renderer.getPixelsarray(), drone.getXPos(), drone.getYPos(), drone.getZPos(), drone.getHeading(), drone.getPitch(), drone.getRoll(), timer.getElapsedTime());
        //Start de simulatie in autopilot
        AutopilotOutputs outputs = CommunicatieTestbed.simulationStarted((AutopilotConfig)config,(AutopilotInputs)input);
        
        //Schrijf output
        drone.getEngine().setThrust(outputs.getThrust());
        drone.getLeftWing().setInclinationAngle(outputs.getLeftWingInclination());
        drone.getRightWing().setInclinationAngle(outputs.getRightWingInclination());
        drone.getHorStabilizator().setInclinationAngle(outputs.getHorStabInclination());        
        drone.getVerStabilizator().setInclinationAngle(outputs.getVerStabInclination());
        
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
        // Update camera positie
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
            cameraInc.y * CAMERA_POS_STEP,
            cameraInc.z * CAMERA_POS_STEP);
        // Update camera door muis            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
        
        //Roep een timePassed op in Autopilot
        float time = timer.getElapsedTime();
        Outputs outputs =  (Outputs) CommunicatieTestbed.timePassed((AutopilotInputs) new Inputs(renderer.getPixelsarray(), drone.getXPos(), drone.getYPos(), drone.getZPos(), drone.getHeading(), drone.getPitch(), drone.getRoll(), time));
        
        //Update de drone
        drone.update(outputs,time);
        
//        drone.setLeftWingInclination((float) Math.PI/6);
//        drone.setRightWingInclination((float) Math.PI/6);
//        drone.setHorStabInclination((float) Math.PI/6);

        //Vlieg recht door + gravitatie
        //System.out.println("Pos: " + drone.getPos());
        //System.out.println("Vel: " + drone.getVelocity());
        //drone.setVelocity(drone.getNewVelocity(timer.getElapsedTime()));
        droneItem.setPosition(drone.getXPos(), drone.getYPos(), drone.getZPos());
        droneItem.setRotation(drone.getPitch(), 180 + drone.getHeading(), drone.getRoll());
        
        camera.setPosition(drone.getXPos(), drone.getYPos()+1f, drone.getZPos()+0.4f);
        camera.setRotation(0,0,0);
        
        
        cameraPlane.setPosition(drone.getXPos(), drone.getYPos(), drone.getZPos());
        cameraPlane.setRotation(0f, 0f, 0f);
        
       // System.out.println(drone.getZPos() - gameItems[0].getPosition().z);
       //System.out.println("Vel: " + drone.getVelocity()); 
        
        //bepaalt wanneer de simulatie stopt
        boolean end = true;
        for(int i = 0 ; i < gameItems.size() -1 ; i++){
        	if(gameItems.get(i) != null){
		        Vector3f.sub(drone.getPos(), gameItems.get(i).getPosition(), afstand);
		        if (afstand.length()<4f) {
		        	//System.out.println(timer.getTot() + "end");
		       	    gameItems.remove(i);
		       	    System.out.println("TARGET HIT");
		        }
        	}
        	if(gameItems.get(i).getRenderOnPlaneView() == true) end = false;
        }
        if (end == true) window.simulationEnded = true;
        
        
//        if (timer.getTot() > 2000000) {
//        	System.out.println(timer.getTot());
//        	window.simulationEnded = true;
//        }
    }
    
    //Genereert n willekeurige kubussen
    public List<GameItem> worldGenerator(int n){
    	Random rand = new Random();
    	List<GameItem> gameItems = new ArrayList<GameItem>();
    	Balk balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, new float[]{(179f/255),0f,0f}, new float[]{115f/255,0f,0f},  new float[]{77f/255,0f,0f},  new float[]{217f/255,0f,0f},  new float[]{255f/255,0f,0f},  new float[]{38f/255,0f,0f});
        Mesh mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());
        
        for(int i=0;i<n;i++){
        	GameItem gameItem = new GameItem(mesh,true);
        	
        	float z = i * -40f;
        	float x = rand.nextInt(20) -10;
            float y = rand.nextInt(20) -10;
            while(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) > 10){
            	x = rand.nextInt(20) -10;
                y = rand.nextInt(20) -10;
            }
        	
        	gameItem.setPosition(x, y, z);
            gameItem.setRotation(0, 0, 0);
            gameItems.add(gameItem);
        }
        return gameItems;
    }
    
    
    @Override
    public void render(Window window) throws Exception {
        renderer.render(window, camera, cameraPlane, cameraSide, cameraTop, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
        CommunicatieTestbed.simulationEnded();
    }

}