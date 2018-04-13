
package jbinvis.renderer.camera;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Billy
 */
public class OrthographicCamera extends Camera{

    /**
     * The x-offset of the camera
     */
    public double x=0;
    /**
     * The y-offset of the camera
     */
    public double y=0;
    
    /**
     * Creates a new orthographic camera
     * @param gl 
     */
    public OrthographicCamera(GL2 gl) {
        super(gl);
    }
    
    @Override
    public void update(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        gl.glOrtho(x, x+viewportWidth, y+viewportHeight, y, 1, 100);
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
    }
    
}
