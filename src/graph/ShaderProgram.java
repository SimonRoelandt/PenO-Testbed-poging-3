package graph;

import static org.lwjgl.opengl.GL20.*;


import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;
    
    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
    }
    
    public void createUniform(String uniformName) {
    	int uniformLocation = glGetUniformLocation(programId, uniformName);
    	if (uniformLocation < 0) 
    		System.out.println("Uniform Error");
    	uniforms.put(uniformName, uniformLocation);
    }
    
    //put the matrix in a float buffer
    public void setUniform(String uniformName, Matrix4f value) {
    	try (MemoryStack stack = MemoryStack.stackPush()) {
    		FloatBuffer fb = stack.mallocFloat(16);
    		value.store(fb);
    		fb.flip();
    		glUniformMatrix4fv(uniforms.get(uniformName),false, fb);
    		//for (int i = 0; i < 16; i++)
    		//	System.out.println(fb.get(i));
    		//System.out.println(uniforms.get(uniformName));
    		//float[] test = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
    		//glGetUniformfv(programId, uniforms.get(uniformName), test);
    		//for (int i = 0; i < 16; i++)
        	//	System.out.println(test[i]);
    		
    	}
    }
    
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.out.println("Warning validating Shader code: " + glGetShaderInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}