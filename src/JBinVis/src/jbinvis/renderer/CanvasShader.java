/*
 *  
 */
package jbinvis.renderer;

import com.jogamp.opengl.GL2;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Billy
 */
public class CanvasShader {
    private static CanvasShader currentAppliedShader = null;
    
    private boolean initialised = false;
    private int vshader, fshader, shaderprog;
    private boolean vfloaded, sploaded;
    
    public CanvasShader(GL2 gl, String shaderName) {
        vfloaded = sploaded = false;
        
        // load up shaders
        InputStream stream = null;
        BufferedReader reader = null;
        String vshaderRaw = "";
        String fshaderRaw = "";
        String line;
        
        /* Read vshader */
        try {
            stream = new FileInputStream("shaders/" + shaderName + ".vsh");
            reader = new BufferedReader(new InputStreamReader(stream));
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: Vertex Shader not found!");
            return;
        }
        
        do {
            line = null;
            try {
                line = reader.readLine();
            } catch(IOException e) {
                System.out.println("ERROR: Something wrong reading a line from file.");
                break;
            }
            
            if(line == null) 
                break;
            
            vshaderRaw += line + "\n";
        } while(line!=null);
        
        try {
            stream.close();
        }catch(IOException e) {}
        
        /* Read fshader */
        try {
            stream = new FileInputStream("shaders/" + shaderName + ".fsh");
            reader = new BufferedReader(new InputStreamReader(stream));
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: Fragment Shader not found!");
            return;
        }
        
        do {
            line = null;
            try {
                line = reader.readLine();
            } catch(IOException e) {
                System.out.println("ERROR: Something wrong reading a line from file.");
                break;
            }
            
            if(line == null) 
                break;
            
            fshaderRaw += line + "\n";
        } while(line!=null);
        
        try {
            stream.close();
        }catch(IOException e) {}
        
        /* Compile shaders */
        vshader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        fshader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        vfloaded = true;
        
        gl.glShaderSource(vshader, 1, new String[] { vshaderRaw }, 
                new int[] { vshaderRaw.length() }, 0);
        gl.glShaderSource(fshader, 1, new String[] { fshaderRaw }, 
                new int[] { fshaderRaw.length() }, 0);
        
        if(!compileShader(gl,vshader)) {
            printShaderLog(gl, vshader);
            dispose(gl);
            return;
        }
        
        if(!compileShader(gl,fshader)) {
            printShaderLog(gl, fshader);
            dispose(gl);
            return;
        }
        
        /* Create shader program */
        int[] params = {0};
        shaderprog = gl.glCreateProgram();
        sploaded = true;
        
        gl.glAttachShader(shaderprog, vshader);
        gl.glAttachShader(shaderprog, fshader);
        gl.glLinkProgram(shaderprog);
        
        gl.glGetProgramiv(shaderprog, GL2.GL_LINK_STATUS, params, 0);
        if(params[0] == GL2.GL_FALSE) {
            printProgramLog(gl);
            dispose(gl);
            return;
        }
        
        /*
        gl.glValidateProgram(shaderprog);
        gl.glGetProgramiv(shaderprog, GL2.GL_VALIDATE_STATUS, params, 0);
        if(params[0] == GL2.GL_FALSE) {
            printProgramLog(gl);
            dispose(gl);
            return;
        }
        */
        
        initialised = true; 
    }
    
    /**
     * Disposes all created resources
     * @param gl 
     */
    public final void dispose(GL2 gl) {
        if(sploaded) {
            gl.glDeleteProgram(shaderprog);
            sploaded = false;
        }
        if(vfloaded) {
            gl.glDeleteShader(vshader);
            gl.glDeleteShader(fshader);
            vfloaded = false;
        }
    }
    
    public void begin(GL2 gl) {
        if(!initialised) {
            throw new IllegalStateException("Shader has not been initialised properly.");
        }
        if(currentAppliedShader != null) {
            throw new IllegalStateException("A shader has already been attached.");
        }
        currentAppliedShader = this;
        gl.glUseProgram(shaderprog);
    }
    
    public void end(GL2 gl) {
        if(!initialised) {
            throw new IllegalStateException("Shader has not been initialised properly.");
        }
        
        if(currentAppliedShader != this) {
            throw new IllegalStateException("Ending a shader that is different from the one being used.");
        }
        
        currentAppliedShader = null;
        gl.glUseProgram(0);
    }
    
    private void printProgramLog(GL2 gl) {
        int[] length = {0};
        byte[] data = new byte[256];
        gl.glGetProgramInfoLog(shaderprog, 255, length, 0, data, 0);
        StringBuilder builder = new StringBuilder();
        
        for(int i=0;i<length[0];i++) {
            builder.append((char)data[i]);
        }
        System.out.println("Shader Program Info Log: " + builder.toString());
    }
    
    private void printShaderLog(GL2 gl, int shader) {
        int[] length = {0};
        byte[] data = new byte[256];
        gl.glGetShaderInfoLog(shader, 255, length, 0, data, 0);
        StringBuilder builder = new StringBuilder();
        
        for(int i=0;i<length[0];i++) {
            builder.append((char)data[i]);
        }
        System.out.println("Shader Info Log: " + builder.toString());
    }
    
    private boolean compileShader(GL2 gl, int shader) {
        int[] params = {0};
        gl.glCompileShader(shader);
        gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, params, 0);
        return params[0] == GL2.GL_TRUE;
    }
    
    /**
     * Gets whether this shader has been initialised properly
     * @return 
     */
    public boolean isInitialised() {
        return initialised;
    }
    
    public int getUniformLocation(GL2 gl,String name) {
        return gl.glGetUniformLocation(this.shaderprog, name);
    }
    
}
