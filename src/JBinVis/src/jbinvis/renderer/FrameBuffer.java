/*
 *  
 */
package jbinvis.renderer;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Billy
 */
public class FrameBuffer {
    private int[] fbo = {0};
    private CanvasTexture texture;
    
    private int width, height;
    
    /**
     * Gets the texture to which rendering is done
     * @return 
     */
    public CanvasTexture getTexture() {
        return texture;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    
    public FrameBuffer(GL2 gl, int width, int height) {
        this.width = width;
        this.height = height;
        texture = CanvasTexture.create2D(gl, width, height);
        
        gl.glGenFramebuffers(1, fbo, 0);
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fbo[0]);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, 
                GL2.GL_TEXTURE_2D, texture.getTextureIndex(), 0);
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
        
    }
    
    public void begin(GL2 gl) {
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fbo[0]);
    }
    
    public void end(GL2 gl) {
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
    }
    
    public void dispose(GL2 gl) {
        texture.dispose(gl);
        gl.glDeleteFramebuffers(1,fbo,0);
    }
}
