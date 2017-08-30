/*
 *  
 */
package jbinvis.renderer.camera;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Billy
 */
public class PerspectiveCamera extends Camera {

    public double fov = 90;
    public double znear = 1;
    public double zfar = 1000;
    
    public double eyeX = 0;
    public double eyeY = 0;
    public double eyeZ = 1;
    
    public double targetX = 0;
    public double targetY = 0;
    public double targetZ = 0;
    
    public double upX = 0;
    public double upY = 1;
    public double upZ = 0;
    
    public PerspectiveCamera(GL2 gl) {
        super(gl);
    }
    
    @Override
    public void update(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(fov, viewportWidth/(double)viewportHeight, znear, zfar);
    
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        glu.gluLookAt(eyeX, eyeY, eyeZ, targetX, targetY, targetZ, upX, upY, upZ);
    }
    
    
    
}
