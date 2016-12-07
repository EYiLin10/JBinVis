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
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.PriorityQueue;
import jbinvis.main.JBinVis;

/**
 * Encapsulates the canvas onto which all visualisations will be drawn. 
 * This exposes the underlying OpenGL functions needed for rendering purposes.
 * @author Billy
 */
public class BinVisCanvas extends GLCanvas implements GLEventListener, MouseWheelListener {
    private static final Dimension minimumSize = new Dimension(2,2);
    
    // keep a history of render logic so they can all be disposed
    private HashSet<RenderLogic> renderLogics = new HashSet();
    private int nextId = 1000;
    private PriorityQueue<RenderLogic> initQueue = new PriorityQueue();
    
    private RenderLogic renderLogic = null;
    private long lastCallTime = 0;
    
    private final JBinVis jbinvis;
    
    private FPSAnimator animator;
    
    private BinVisCanvas(Container parent, GLCapabilities caps) {
        super(caps);
        parent.add(this);
        
        // add event listeners
        this.addGLEventListener(this);
        
        this.addMouseWheelListener(this);
        
        lastCallTime = System.currentTimeMillis();
        
        // this will call the display function at 30 fps
        this.animator = new FPSAnimator(this, 30, true);
        this.animator.start();
        
        jbinvis = JBinVis.getInstance();
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
            
            // queue for init
            initQueue.add(render);
        }
        this.renderLogic = render;
    }
    
    public RenderLogic getRenderLogic() {
        return this.renderLogic;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        if(this.animator.isStarted())
            animator.stop();
        
        // call dispose on all render logics that were used here
        for(RenderLogic logic: renderLogics) {
            if(!logic.isDisposed()) {
                logic.dispose(gl);
            }
        }
        renderLogic = null;
        renderLogics.clear();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        double delta = 0;
        final GL2 gl = drawable.getGL().getGL2();
        
        // check init queue
        if(!initQueue.isEmpty()) {
            RenderLogic q = initQueue.remove();
            q.init(gl);
        }
        
        // calculate delta
        long curTime = System.currentTimeMillis();
        delta = (curTime - lastCallTime) / 1000.0;
        lastCallTime = curTime;
        
        // update and rendering
        if(renderLogic!=null && !renderLogic.isDisposed()) {
            renderLogic.update(gl,delta);
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(renderLogic!=null)
            this.renderLogic.mouseWheelMoved(e);
        
        int multiplier;
        if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
            multiplier = e.getUnitsToScroll();
        else 
            multiplier = e.getWheelRotation();
        
        // move the offset
        if(jbinvis.isLoaded()) {
            long offset = jbinvis.getFileOffset();
            long increment = 4096 * multiplier;
            
            jbinvis.setFileOffset(offset + increment);
        }
    }
}
