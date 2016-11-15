/*
 *  
 */
package jbinvis.renderer;

import com.jogamp.opengl.GL2;

/**
 * Every visualisation will extend this class. This interface represents
 * the different rendering logic that can be plugged into the BinVisCanvas.
 * @author Billy
 */
public abstract class RenderLogic {
    private boolean disposed = false;
    private int renderLogicId = -1;
    
    /**
     * The main rendering function, called every frame.
     * @param gl The GL2 instance giving access to OpenGL functions
     * @param delta Elapsed time between calls
     */
    public void render(GL2 gl, double delta) { }
    
    /**
     * Function called before a render
     * @param delta 
     */
    public void update(double delta) { }
    
    /**
     * Called whenever the parent canvas is resized. glViewport has been called 
     * before this function is called.
     * @param width
     * @param height 
     */
    public void resize(int width, int height) {}
    
    /**
     * Clean up
     */
    public void dispose() { }
    
    ////// USED INTERNALLY BY BINVIS CANVAS /////
    void _dispose() { 
        if(!disposed) {
            disposed = true;
            dispose();
        }
    }
    
    void _setId(int id) {
        this.renderLogicId = id;
    }
    
    int _getId() {
        return this.renderLogicId;
    }
    
    /////////////////////////////////////////////

    /**
     * Gets if this instance have been disposed, so the canvas will not attempt to 
     * use this instance.
     * @return 
     */
    public final boolean isDisposed() {
        return disposed;
    }
}
