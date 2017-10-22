package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;


import org.lwjgl.util.vector.Matrix4f;

import engine.GameItem;
import engine.Utils;
import engine.Window;
import graph.Camera;
import graph.Mesh;
import graph.ShaderProgram;
import graph.Transformation;
import graph.ProjectionMatrix;

public class Renderer {

    private ShaderProgram shaderProgram;
    private int renderbuffer;
    private int framebuffer;
    private int depthbuffer;
    private static float fov = (float) Math.toRadians(120.0f);
    private static float z_near = 0.01f;
    private static float z_far = 1000.f;
    private Matrix4f projectionMatrix = new Matrix4f();
    private final Transformation transformation;
    private int imageWidth;
    private int imageHeight;
    private ByteBuffer pixels;
    private Byte[] pixelsarray;
    

    public Renderer() {
    	transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
    	this.imageHeight = window.getHeight();
    	this.imageWidth = window.getWidth();
    	this.imageHeight = 100;
    	this.imageWidth = 100;
    	ByteBuffer pixels = ByteBuffer.allocateDirect(imageWidth*imageHeight*4);  
    	this.pixels = pixels;
    	Byte[] pixelsarray = new Byte[imageWidth*imageHeight*4];
    	this.pixelsarray = pixelsarray;
    	
    	
    	
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();
        
        /*//create projectionmatrix
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        float zp = z_far+z_near;
		float zm = z_far-z_near;
        projectionMatrix.m00 = (float) ((1.0f/Math.tan(fov*0.5f))/aspectRatio);
		projectionMatrix.m11 = (float) (1.0f/Math.tan(fov*0.5f));
		projectionMatrix.m22 = -zp/zm;
		projectionMatrix.m32 = -((z_far+z_far)*z_near)/zm;
		projectionMatrix.m23 = -1.0f;
		projectionMatrix.m33 = 0.0f;*/
        //System.out.println(projectionMatrix);
        
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
   
        
        //create the framebuffer
        this.framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glDrawBuffer(GL_DEPTH_ATTACHMENT);
        //glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        //create the renderbuffer
        this.renderbuffer = glGenRenderbuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,framebuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA, imageWidth, imageHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderbuffer);
        //glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
       //create the depthbuffer
        this.depthbuffer = glGenRenderbuffers();  
        glBindFramebuffer(GL_FRAMEBUFFER,framebuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, depthbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, imageWidth, imageHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbuffer);
        //glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
        //create the pixel buffer
        
       
        
        
        
        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Camera cameraPlane, GameItem[] gameItems) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        
        shaderProgram.bind();
        
        //update projectionMatrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);
        shaderProgram.setUniform("projectionMatrix",projectionMatrix);
        //System.out.println(projectionMatrix);
        
        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        
        //Render each gameItem
        
        for(GameItem gameItem : gameItems) {
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
        	shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        	gameItem.getMesh().render();
        }
        
       	glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
       	clear();
     	glClearColor(0f, 0f, 0f, 1f);
       	glBindFramebuffer(GL_FRAMEBUFFER, 0);
  
        
        viewMatrix = transformation.getViewMatrix(cameraPlane);
        for(GameItem gameItem : gameItems) {
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
        	shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        	gameItem.getMesh().renderCameraPlane(framebuffer, imageWidth, imageHeight);
        } 
        //glViewport(0, 0, window.getWidth(), window.getHeight());
        
        
        
        
        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);     
        glReadPixels(0, 0, imageWidth, imageHeight, GL_RGBA, GL_BYTE, pixels);

       
        for (int i = 0; i<imageHeight*imageWidth*4; i++)
        	pixelsarray[i] = pixels.get(i);
        
        //TESTEN

        
        /*
        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0,imageWidth, imageHeight, 0, 0,window.getWidth(), window.getHeight(), GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        */
       
        //TESTEN GEDAAN
        
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
        glViewport(0,0,window.getWidth(), window.getHeight());
        window.setClearColor(0.0f, 0.5f, 0.0f, 1.0f);
     
        
        //glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        
        shaderProgram.unbind();
        
        

    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
            glDeleteFramebuffers(framebuffer);
            glDeleteRenderbuffers(renderbuffer);
            glDeleteRenderbuffers(depthbuffer);
            
            for (int i = 0; i<imageHeight*imageWidth*4; i++)
            System.out.println(pixelsarray[i]);
        }
    }
}