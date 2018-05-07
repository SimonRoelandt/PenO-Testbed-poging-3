package graph;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.GameItem;

public class Transformation {

	private final Matrix4f projectionMatrix;
	
	private final Matrix4f modelViewMatrix;
	
	private final Matrix4f viewMatrix;
	
	private final Vector3f xAxis = new Vector3f(1,0,0);
	private final Vector3f yAxis = new Vector3f(0,1,0);
	private final Vector3f zAxis = new Vector3f(0,0,1);
	
	public Transformation() {
		modelViewMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
	}
	
	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width/height;
        float zp = zFar+zNear;
		float zm = zFar-zNear;
		projectionMatrix.setIdentity();
        projectionMatrix.m00 = (float) ((1.0f/Math.tan(fov*0.5f))/aspectRatio);
		projectionMatrix.m11 = (float) (1.0f/Math.tan(fov*0.5f));
		projectionMatrix.m22 = -zp/zm;
		projectionMatrix.m32 = -((zFar+zFar)*zNear)/zm;
		projectionMatrix.m23 = -1.0f;
		projectionMatrix.m33 = 0.0f;
		return projectionMatrix;
	}
	
	public final Matrix4f getProjectionMatrixOrthogonal(float width, float height, float zNear, float zFar) {
		float aspectRatio = width/height;
        float zp = zFar+zNear;
		float zm = zFar-zNear;
		projectionMatrix.setIdentity();
        projectionMatrix.m00 = 2f/width;
		projectionMatrix.m11 = 2f/height;
		projectionMatrix.m22 = 1/zm;
		projectionMatrix.m32 = -(zNear)/zm;
		projectionMatrix.m23 = 0;
		projectionMatrix.m33 = 1f;
		return projectionMatrix;
	}
	
	
	public Matrix4f getViewMatrix(Camera camera) {
		Vector3f cameraPos = camera.getPosition();
		Vector3f rotation = camera.getRotation();
		viewMatrix.setIdentity();
		viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0)).rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0));
		viewMatrix.translate(new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z));
		return viewMatrix;
	}
	
	public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
		Vector3f rotation = gameItem.getRotation();
	    modelViewMatrix.setIdentity();
	    modelViewMatrix.translate(gameItem.getPosition()).
	    				rotate((float)Math.toRadians(-rotation.z), new Vector3f(0,0,1)).
	    				rotate((float)Math.toRadians(-rotation.x), new Vector3f(1,0,0)).
	    				rotate((float)Math.toRadians(-rotation.y), new Vector3f(0,1,0)).
	    				scale(new Vector3f(gameItem.getScale(),gameItem.getScale(),gameItem.getScale()));
	    Matrix4f viewCurr = new Matrix4f(viewMatrix);
	    Matrix4f.mul(viewCurr, modelViewMatrix, viewCurr);
	    return viewCurr;
	}
}
