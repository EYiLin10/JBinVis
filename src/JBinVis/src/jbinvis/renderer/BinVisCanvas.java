/*
 *  
 */
package jbinvis.renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import java.awt.Container;
import java.awt.Dimension;
import java.util.HashSet;

/**
 * Encapsulates the canvas onto which all visualisations will be drawn. 
 * This exposes the underlying OpenGL functions needed for rendering purposes.
 * @author Billy
 */
public class BinVisCanvas extends GLCanvas implements GLEventListener {
    private static final Dimension minimumSize = new Dimension(2,2);
    
    // keep a history of render logic so they can all be disposed
    private HashSet<RenderLogic> renderLogics = new HashSet();
    private int nextId = 1000;
    
    private RenderLogic renderLogic = null;
    private long lastCallTime = 0;
    
    private BinVisCanvas(Container parent, GLCapabilities caps) {
        super(caps);
        parent.add(this);
        
        // add event listeners
        this.addGLEventListener(this);
        
        lastCallTime = System.currentTimeMillis();
        this.setSize(300,300);
    }
    
    /**
     * Creates an instance of the canvas and add to the specified container
     * @param parent The container to add this canvas into
     * @return The instantiated canvas
     */
    public static BinVisCanvas create(Container parent) {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcaps = new GLCapabilities(glprofile);
        BinVisCanvas canvas = new BinVisCanvas(parent, glcaps);
        return canvas;
    }
    
    public void setRenderLogic(RenderLogic render) {
        if(render._getId() < 0) {
            // this render logic is being set here the first time
            render._setId(nextId++);
            renderLogics.add(render);
        }
        this.renderLogic = render;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // call dispose on all render logics that were used here
        for(RenderLogic logic: renderLogics) {
            if(!logic.isDisposed()) {
                logic.dispose();
            }
        }
        renderLogic = null;
        renderLogics.clear();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        double delta = 0;
        final GL2 gl = drawable.getGL().getGL2();
        
        // calculate delta
        long curTime = System.currentTimeMillis();
        delta = (curTime - lastCallTime) / 1000.0;
        lastCallTime = curTime;
        
        // update and rendering
        if(renderLogic!=null && !renderLogic.isDisposed()) {
            renderLogic.update(delta);
            renderLogic.render(gl, delta);
        }
        else {
            gl.glClearColor(0,0,0,0);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        }
        
        gl.glFlush();
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(x,y,width,height);
        
        this.setMinimumSize(minimumSize);
        
        if(renderLogic != null)
            renderLogic.resize(width, height);
    }
}
