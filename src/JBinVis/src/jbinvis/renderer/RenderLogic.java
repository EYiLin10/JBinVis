/*
 *  
 */
package jbinvis.renderer;

import com.jogamp.opengl.GL2;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Every visualisation will extend this class. This interface represents
 * the different rendering logic that can be plugged into the BinVisCanvas.
 * @author Billy
 */
public abstract class RenderLogic implements MouseWheelListener, KeyListener {
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
    public void update(GL2 gl,double delta) { }
    
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
    public void dispose(GL2 gl) { }
    
    /**
     * Called when this logic is initially attached to the 
     * canvas. Perform initialisations here.
     */
    public void init(GL2 gl) { }
    
    /**
     * Gets the name of this visualisation
     */
    public String getName() { return ""; }
    
    ////// USED INTERNALLY BY BINVIS CANVAS /////
    void _dispose(GL2 gl) { 
        if(!disposed) {
            disposed = true;
            dispose(gl);
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
    
    /////////////////////////////////////////////
    // Event handlers
    /////////////////////////////////////////////

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


}
