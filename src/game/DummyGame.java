package game;


import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import engine.Balk;
import engine.GameItem;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Window;
import graph.Camera;
import graph.Mesh;

public class DummyGame implements IGameLogic {

	private static final float MOUSE_SENSITIVITY = 0.4f;
	
    private final Renderer renderer;
    
    private final Vector3f cameraInc;

    private final Camera camera;
    
    private final Camera cameraPlane;
    
    private static final float CAMERA_POS_STEP = 0.05f;
    
    private GameItem[] gameItems;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        cameraPlane = new Camera();
        cameraPlane.setPosition(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        // Create the Mesh
        
        
        Balk balk = new Balk(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, new float[]{1f,0f,0f}, new float[]{1f,0f,0f},  new float[]{0f,1f,0f},  new float[]{0f,1f,0f},  new float[]{0f,0f,1f},  new float[]{0f,0f,1f});
        Balk balk = new Balk(0f, 0f, 0f, 1f, 1f, 1f, new float[]{1f,0f,0f}, new float[]{1f,0f,0f},  new float[]{1f,0f,0f},  new float[]{1f,0f,0f},  new float[]{1f,0f,0f},  new float[]{1f,0f,0f});
        Mesh mesh = new Mesh(balk.positions(), balk.colours(), balk.indices());
        GameItem gameItem = new GameItem(mesh);
        GameItem gameItem2 = new GameItem(mesh);
        GameItem gameItem3 = new GameItem(mesh);
        GameItem gameItem4 = new GameItem(mesh);
        gameItem4.setPosition(-2, -2, -2);
        gameItem4.setRotation(-60f, 20f, 40f);
        gameItem3.setPosition(-1, -1, -3);
        gameItem3.setRotation(34f, 53f, 45f);
        gameItem2.setPosition(1, -2, -5);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[] { gameItem , gameItem2, gameItem3, gameItem4};
        gameItem.setPosition(0, 0, -10);
        gameItems = new GameItem[] { gameItem};
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
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
            cameraInc.y * CAMERA_POS_STEP,
            cameraInc.z * CAMERA_POS_STEP);

        cameraPlane.movePosition(0, 0.01f, 0.01f);
        cameraPlane.moveRotation(0f, 0.3f, 0f);
        // Update camera based on mouse            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, cameraPlane, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}