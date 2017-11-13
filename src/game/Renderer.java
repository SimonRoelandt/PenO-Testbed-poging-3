package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

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
    private int renderbufferSide;
    private int framebufferSide;
    private int depthbufferSide;
    private int renderbufferTop;
    private int framebufferTop;
    private int depthbufferTop;
    float fov = (float) Math.toRadians(120.0f);
    private static float z_near = 0.01f;
    private static float z_far = 1000.f;
    private final Transformation transformation;
    int imageWidth;
    int imageHeight;
    int imageWidthAutopilot;
    int imageHeightAutopilot;
    private ByteBuffer pixels;
    private byte[] pixelsarray;

    public Renderer() {
    	transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
    	this.imageHeight = window.getHeight();
    	this.imageWidth = window.getWidth();
    	this.imageWidthAutopilot = 200;
    	this.imageHeightAutopilot = 200;
    	this.imageHeight = 500;
    	this.imageWidth = 500;
        this.pixels = ByteBuffer.allocateDirect(imageWidthAutopilot*imageHeightAutopilot*3);  
    	this.pixelsarray = new byte[imageWidthAutopilot*imageHeightAutopilot*3];

    	
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
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        //create the renderbuffer
        this.renderbuffer = glGenRenderbuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,framebuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA, imageWidthAutopilot, imageHeightAutopilot);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderbuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
       //create the depthbuffer
        this.depthbuffer = glGenRenderbuffers();  
        glBindFramebuffer(GL_FRAMEBUFFER,framebuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, depthbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, imageWidthAutopilot, imageHeightAutopilot);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
        
        //create the framebuffer
        this.framebufferSide = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferSide);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glDrawBuffer(GL_DEPTH_ATTACHMENT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        //create the renderbuffer
        this.renderbufferSide = glGenRenderbuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,framebufferSide);
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferSide);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA, imageWidth, imageHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderbufferSide);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
       //create the depthbuffer
        this.depthbufferSide = glGenRenderbuffers();  
        glBindFramebuffer(GL_FRAMEBUFFER,framebufferSide);
        glBindRenderbuffer(GL_RENDERBUFFER, depthbufferSide);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, imageWidth, imageHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbufferSide);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
        
        //create the framebuffer
        this.framebufferTop = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferTop);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glDrawBuffer(GL_DEPTH_ATTACHMENT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        //create the renderbuffer
        this.renderbufferTop = glGenRenderbuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,framebufferTop);
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferTop);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA, imageWidth, imageHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderbufferTop);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
       //create the depthbuffer
        this.depthbufferTop = glGenRenderbuffers();  
        glBindFramebuffer(GL_FRAMEBUFFER,framebufferTop);
        glBindRenderbuffer(GL_RENDERBUFFER, depthbufferTop);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, imageWidth, imageHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbufferTop);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
        //create the pixel buffer
        

        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Camera cameraPlane, Camera cameraSide, Camera cameraTop, List<GameItem> gameItems) throws Exception {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        
        shaderProgram.bind();
        
        //update projectionMatrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);
        //projectionMatrix = transformation.getProjectionMatrixOrthogonal(window.getWidth(), window.getHeight(), z_near, z_far);
        shaderProgram.setUniform("projectionMatrix",projectionMatrix);
        System.out.println(projectionMatrix);
        
        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        
        //Render each gameItem
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        window.setClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        //CHASE
        for(GameItem gameItem : gameItems) {
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
        	shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        	gameItem.getMesh().render(imageWidth, imageHeight, window.getWidth(), window.getHeight());
        }
        
        
        
        
       	glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
       	clear();
       	//glBindFramebuffer(GL_FRAMEBUFFER, 0);
  
        //PLANE
        viewMatrix = transformation.getViewMatrix(cameraPlane);
        for(GameItem gameItem : gameItems) {
        	if(gameItem.getRenderOnPlaneView()){
        		Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            	shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            	gameItem.getMesh().renderCameraPlane(framebuffer, imageWidthAutopilot, imageHeightAutopilot, window.getWidth(), window.getHeight());
        	}
        } 
        
        
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferSide);
       	clear();
       	//glBindFramebuffer(GL_FRAMEBUFFER, 0);
  
       	//Verander naar orthogonaal
       	
       	
       	projectionMatrix = transformation.getProjectionMatrixOrthogonal(100,100, z_near, z_far);
       	shaderProgram.setUniform("projectionMatrix",projectionMatrix);
       	// orthogonale projectiematrix werkt nog niet
       	
       	
       	
        
       	System.out.println(projectionMatrix);
       	
        //SIDE
        viewMatrix = transformation.getViewMatrix(cameraSide);
        for(GameItem gameItem : gameItems) {
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            gameItem.getMesh().renderCameraSide(framebufferSide, imageWidth, imageHeight, window.getWidth(), window.getHeight());	
        } 
        
        
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferTop);
       	clear();
       	//glBindFramebuffer(GL_FRAMEBUFFER, 0);
  
        //TOP
        viewMatrix = transformation.getViewMatrix(cameraTop);
        for(GameItem gameItem : gameItems) {
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            gameItem.getMesh().renderCameraTop(framebufferTop, imageWidth, imageHeight, window.getWidth(), window.getHeight());
        } 
        //glViewport(0, 0, window.getWidth(), window.getHeight());
        
   
       
        
        
        /*
      	//Scrijf naar een file om te testen voor de input/output tussen tesbed en autopilot gemaakt is
        File file = new File("C:\\Image\\pixels.txt");
        boolean append = false;
        FileChannel wChannel = new FileOutputStream(file, append).getChannel();
        wChannel.write(pixels);
        wChannel.close(); 
        */
     

       
        //TESTEN

        
      glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
      glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
      glBlitFramebuffer(0, 0,imageWidthAutopilot, imageHeightAutopilot, 0, 0 ,imageWidthAutopilot, imageHeightAutopilot, GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        
      //Maak byte[] aan van het zicht van de drone
      glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
      glReadPixels(0, 0, imageWidthAutopilot, imageHeightAutopilot, GL_RGB, GL_BYTE, pixels);   
      for (int i = 0; i < imageWidthAutopilot*imageHeightAutopilot*3; i++)
      	pixelsarray[i] = pixels.get(i);
      System.out.println(pixelsarray.length);
      glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
      glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
      glBlitFramebuffer(0, 0,imageWidthAutopilot, imageHeightAutopilot, 0, 0 ,imageWidth, imageHeight, GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT, GL_NEAREST);
      
      glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferSide);
      glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
      glBlitFramebuffer(0,0,imageWidth,imageHeight, window.getWidth()-imageWidth,0,window.getWidth(),imageHeight, GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT, GL_NEAREST);
      
      glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferTop);
      glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
      glBlitFramebuffer(0,0,imageWidth,imageHeight, window.getWidth()-imageWidth,window.getHeight()-imageHeight,window.getWidth(),window.getHeight(), GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT, GL_NEAREST);

//    glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
//    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
//    glBlitFramebuffer(0, 0,imageWidth, imageHeight, 0, 0,window.getWidth(), window.getHeight(), GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT, GL_NEAREST);

        
        //TESTEN GEDAAN
        
       
        
        //glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
        //glViewport(0,0,window.getWidth(), window.getHeight());
        //window.setClearColor(1.0f, 1.0f, 1.0f, 1.0f);
     
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
       
        shaderProgram.unbind();

    }

    public byte[] getPixelsarray() {
    	return this.pixelsarray;
    }
    
    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
            glDeleteFramebuffers(framebuffer);
            glDeleteRenderbuffers(renderbuffer);
            glDeleteRenderbuffers(depthbuffer);
            glDeleteFramebuffers(framebufferSide);
            glDeleteRenderbuffers(renderbufferSide);
            glDeleteRenderbuffers(depthbufferSide);
            glDeleteFramebuffers(framebufferTop);
            glDeleteRenderbuffers(renderbufferTop);
            glDeleteRenderbuffers(depthbufferTop);
            
        }
    }
}