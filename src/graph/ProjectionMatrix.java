package graph;

import org.lwjgl.util.vector.Matrix4f;

public class ProjectionMatrix extends Matrix4f {
	public ProjectionMatrix(float AspectRatio, float fov, float Z_near, float Z_far) {
		this.AspectRatio = AspectRatio;
		this.fov = fov;
		this.z_near = Z_near;
		this.z_far = Z_far;
		Matrix4f projectionmatrix = new Matrix4f();
		float zp = Z_far+Z_near;
		float zm = Z_far-Z_near;
		/*projectionmatrix.m00 = (float) ((1/Math.tan(fov/2))/AspectRatio);
		projectionmatrix.m10 = 0;
		projectionmatrix.m20 = 0;
		projectionmatrix.m30 = 0;
		projectionmatrix.m01 = 0;
		projectionmatrix.m11 = (float) (1/Math.tan(fov/2));
		projectionmatrix.m21 = 0;
		projectionmatrix.m31 = 0;
		projectionmatrix.m02 = 0;
		projectionmatrix.m12 = 0;
		projectionmatrix.m22 = -zp/zm;
		projectionmatrix.m32 = -(2*Z_far*Z_near)/zm;
		projectionmatrix.m03 = 0;
		projectionmatrix.m13 = 0;
		projectionmatrix.m23 = -1;
		projectionmatrix.m33 = 0;*/
		
		projectionmatrix.m00 = 0.5f;
		projectionmatrix.m10 = 0f;
		projectionmatrix.m20 = 0f;
		projectionmatrix.m30 = 0f;
		projectionmatrix.m01 = 0f;
		projectionmatrix.m11 = 0.5f;
		projectionmatrix.m21 = 0f;
		projectionmatrix.m31 = 0f;
		projectionmatrix.m02 = 0f;
		projectionmatrix.m12 = 0f;
		projectionmatrix.m22 = 0.5f;
		projectionmatrix.m32 = 0f;
		projectionmatrix.m03 = 0f;
		projectionmatrix.m13 = 0f;
		projectionmatrix.m23 = 0f;
		projectionmatrix.m33 = 0.5f;
		//System.out.println(projectionmatrix);
		
	}
	
	public float AspectRatio;
	public float fov;
	public float z_near;
	public float z_far;
	
	
	public float getAspectRatio() {
		return AspectRatio;
	}
	public void setAspectRatio(float aspectRatio) {
		AspectRatio = aspectRatio;
	}
	public float getFov() {
		return fov;
	}
	public void setFov(float fov) {
		this.fov = fov;
	}
	public float getZ_near() {
		return z_near;
	}
	public void setZ_near(float z_near) {
		this.z_near = z_near;
	}
	public float getZ_far() {
		return z_far;
	}
	public void setZ_far(float z_far) {
		this.z_far = z_far;
	}


	
}
