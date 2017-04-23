/*
 *  
 */
package jbinvis.visualisations;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import jbinvis.renderer.camera.PerspectiveCamera;
import jbinvis.main.FileUpdateListener;
import jbinvis.main.JBinVis;
import jbinvis.renderer.CanvasShader;
import jbinvis.renderer.CanvasTexture;
import jbinvis.renderer.FrameBuffer;
import jbinvis.renderer.RenderLogic;
import jbinvis.renderer.camera.OrthographicCamera;

/**
 *
 * @author Billy
 */
public class Trigraph extends RenderLogic implements FileUpdateListener{
    private JBinVis jbinvis;
    private PerspectiveCamera camera;
    private OrthographicCamera orthoCam;
    
    private CanvasShader shaderPass1;
    private CanvasShader shaderPass2;
    
    private CanvasTexture tex2;
    
    private int u_sampler;
    
    private int uniformCamera;
    private int uniformA, uniformB;
    
    private float distNear = 1;
    private float distFar = 3;
    
    private final int sideCount = 64;
    private final int pointCount = sideCount*sideCount*sideCount;
    
    double angle = 0;
    
    private GLU glu;
    
    private int halfQuadSize=256, centerX=256, centerY=256;
    
    private FrameBuffer fbo;
    
    public Trigraph() {
        jbinvis = JBinVis.getInstance();
       glu = new GLU();
    }

    @Override
    public void init(GL2 gl) {
        camera = new PerspectiveCamera(gl);
        orthoCam = new OrthographicCamera(gl);
        
        shaderPass1 = new CanvasShader(gl, "trigraph_pass1");
        shaderPass2 = new CanvasShader(gl, "trigraph_pass2");
        fbo = new FrameBuffer(gl, 512, 512);
        u_sampler = shaderPass2.getUniformLocation(gl, "u_texture");

        
    }
   
    @Override
    public void update(GL2 gl, double delta) {
        if(camera != null) {
            camera.eyeX = Math.cos(angle) * 2;
            camera.eyeZ = Math.sin(angle) * 2;
            camera.update(gl);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (camera != null) {
            camera.setViewportDimensions(width, height);
            orthoCam.setViewportDimensions(width, height);
        }
        
        halfQuadSize = Math.min(width, height) / 2;
        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    public void render(GL2 gl, double delta) {

        // render onto fbo
        fbo.begin(gl);
        
        gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
        gl.glViewport(0, 0, fbo.getWidth(), fbo.getWidth());
        gl.glClearColor(1, 0.5f, 0.5f, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, 1, 1, 0, 0.1, 100);
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluLookAt(0, 0, 1, 0, 0,0, 0, 1,0);

        shaderPass1.begin(gl);
        
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glColor3d(1,0,0);
        gl.glVertex3d(0.5, 0, 0);
        gl.glVertex3d(1, 1, 0);
        gl.glVertex3d(0, 1, 0);
        gl.glEnd();
        
        fbo.end(gl);
        
        shaderPass1.end(gl);
        
        gl.glPopAttrib();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
        
        
        // render onto plane
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
       
        // use ortho cam
        orthoCam.update(gl);
        shaderPass2.begin(gl);
        fbo.getTexture().setUniformSampler(gl, u_sampler);
        fbo.getTexture().bind(gl);
        
        // why did this turn the triangle 90 degrees c-clockwise?
        
        
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3d(1,1,1);
        gl.glVertex3d(centerX-halfQuadSize, centerY-halfQuadSize, 0); gl.glTexCoord2d(0,0);
        gl.glVertex3d(centerX+halfQuadSize, centerY-halfQuadSize, 0); gl.glTexCoord2d(1,0);
        gl.glVertex3d(centerX+halfQuadSize, centerY+halfQuadSize, 0); gl.glTexCoord2d(1,1);
        gl.glVertex3d(centerX-halfQuadSize, centerY+halfQuadSize, 0); gl.glTexCoord2d(0,1);
        gl.glEnd();
        
        shaderPass2.end(gl);
        

        angle += delta * Math.PI / 3;
    }

    @Override
    public void dispose(GL2 gl) {
        fbo.dispose(gl);
        shaderPass1.dispose(gl);
        shaderPass2.dispose(gl);
    }
    
    
    
    
    @Override
    public void fileOffsetUpdated() {
    }

    @Override
    public void fileClosed() {
    }

    @Override
    public void fileOpened() {
    }
    
}
