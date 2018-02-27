package jbinvis.renderer.camera;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Encapsulates the abstract camera class
 * @author Billy
 */
public abstract class Camera {
    protected GLU glu = new GLU();
    protected int viewportWidth, viewportHeight;
    
    protected Camera(GL2 gl) {
        int[] params = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, params, 0);
        viewportWidth = params[2];
        viewportHeight = params[3];
    }
    
    /**
     * Updates this camera with the given viewport width and height
     * @param viewWidth
     * @param viewHeight 
     */
    public void setViewportDimensions(int viewWidth, int viewHeight) {
        viewportWidth = viewWidth;
        viewportHeight = viewHeight;
    }
    
    public int getViewportWidth() { return viewportWidth; }
    public int getViewportHeight() { return viewportHeight; }
    
    public abstract void update(GL2 gl);
}
