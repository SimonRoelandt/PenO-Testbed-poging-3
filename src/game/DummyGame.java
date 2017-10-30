package game;


import static org.lwjgl.glfw.GLFW.*;

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
    
    private static final float CAMERA_POS_STEP = 0.5f;
    
    private GameItem[] gameItems;
    
    private GameItem droneItem;
    
    private Vector3f afstand = new Vector3f();
    
    private Drone drone;
    
    private Timer timer;
    


    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(20, 0, -25);
        camera.setRotation(0, -90f, 0);
        cameraInc = new Vector3f(0,0,0);
        cameraPlane = new Camera();
        cameraPlane.setPosition(0, 0, 0);
        timer = new Timer();
        drone = new Drone(0, 0, 0, new Vector3f(0,0,-8));
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        timer.init();
        this.window = window;
        
        // Maak de gameItem meshes aan
        Balk droneVisual = new Balk(drone.getXPos()-0.5f, drone.getYPos()-0.5f, drone.getZPos()-0.5f, 1f, 1f, 1f, new float[]{0f,0f,0f}, new float[]{0f,0f,0f},  new float[]{0f,0f,0f},  new float[]{0f,0f,0f},  new float[]{0f,0f,0f},  new float[]{0f,0f,0f});
        Balk balk = new Balk(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, new float[]{(179f/255),0f,0f}, new float[]{115f/255,0f,0f},  new float[]{77f/255,0f,0f},  new float[]{217f/255,0f,0f},  new float[]{255f/255,0f,0f},  new float[]{38f/255,0f,0f});
        Mesh mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());
        //Mesh meshDrone = new Mesh(droneVisual.positions(), droneVisual.colours(), droneVisual.indices());
        Mesh meshDrone = OBJLoader.loadOBJModel("Eurofighter");
        GameItem gameItem = new GameItem(mesh,true);
        GameItem gameItem2 = new GameItem(mesh,true);
        GameItem gameItem3 = new GameItem(mesh,true);
        GameItem gameItem4 = new GameItem(mesh,true);
        GameItem droneItem = new GameItem(meshDrone,false);
        droneItem.setScale(0.2f);
        this.droneItem = droneItem;
        gameItem4.setPosition(-2, -2, -2);
        gameItem4.setRotation(-60f, 20f, 40f);
        gameItem3.setPosition(-1, -1, -3);
        gameItem3.setRotation(34f, 53f, 45f);
        gameItem2.setPosition(1, -2, -5);
        gameItem.setPosition(0, 20, -200);
        //gameItem.setPosition(0, -10, -200);
        //gameItem.setPosition(0, 0, -50);
        //gameItem.setPosition(0, 38, -200);
        gameItem.setRotation(-60f, 20f, 40f);
        gameItems = new GameItem[] { gameItem, droneItem};

        //Maak config file aan voor de autopilot
        Config config = new Config(drone.getGravity(), drone.getWingX(), drone.getTailSize(), drone.getEngineMass(),
        							drone.getWingMass(), drone.getTailMass(), drone.getMaxThrust(), drone.getMaxAOA(),
        							drone.getWingLiftSlope(), drone.getHorStabLiftSlope(), drone.getVerStabLiftSlope(), 
        							renderer.fov, renderer.fov, renderer.imageWidth, renderer.imageHeight);
        
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
        //droneItem.setRotation(drone.getXRot(), drone.getYRot, drone.getZRot);
        
        cameraPlane.setPosition(drone.getXPos(), drone.getYPos(), drone.getZPos());
        cameraPlane.setRotation(0f, 0f, 0f);
        
       // System.out.println(drone.getZPos() - gameItems[0].getPosition().z);
        
        
        //bepaalt wanneer de simulatie stopt
        Vector3f.sub(drone.getPos(), gameItems[0].getPosition(), afstand);
        if (afstand.length()<4f) {
       	System.out.println(timer.getTot() + "end");
        	window.simulationEnded = true;
        }
        if (timer.getTot() > 2000000) {
        	System.out.println(timer.getTot());
        	window.simulationEnded = true;
        }
    }

    @Override
    public void render(Window window) throws Exception {
        renderer.render(window, camera, cameraPlane, gameItems);
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