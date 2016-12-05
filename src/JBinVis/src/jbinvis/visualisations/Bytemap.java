/*
 *  
 */
package jbinvis.visualisations;

import com.jogamp.opengl.GL2;
import jbinvis.backend.FileCache;
import jbinvis.main.JBinVis;
import jbinvis.renderer.CanvasShader;
import jbinvis.renderer.CanvasTexture;
import jbinvis.renderer.RenderLogic;
import jbinvis.renderer.camera.OrthographicCamera;

/**
 * A simple bytemap visualisation
 * @author Billy
 */
public class Bytemap extends RenderLogic implements jbinvis.main.FileUpdateListener {
    private CanvasTexture texture = null;
    private CanvasShader shader = null;
    private OrthographicCamera camera = null;
    
    private int halfQuadSize = 256;
    private int centerX = 256, centerY=256;
    private final JBinVis jbinvis;
    private final int PIXEL_COUNT = 512*512;
    
    public Bytemap() {
        jbinvis = JBinVis.getInstance();
        jbinvis.addFileUpdateListener(this);
    }

    @Override
    public void init(GL2 gl) {
        camera = new OrthographicCamera(gl);
        texture = new CanvasTexture(gl, 512, 512);
        shader = new CanvasShader(gl, "texturePassThru");
    }

    @Override
    public void update(GL2 gl, double delta) {
        camera.update(gl);
    }
    
    @Override
    public void render(GL2 gl, double delta) {
        gl.glClearColor(0.5f,0.5f,0.5f,1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        texture.bind(gl);
        shader.begin(gl);
        
        // render a quad to display texture
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        
        gl.glColor3d(1,1,1);
        gl.glTexCoord2d(1, 0);
        gl.glVertex2d(centerX + halfQuadSize, centerY - halfQuadSize);
        gl.glTexCoord2d(1, 1);
        gl.glVertex2d(centerX + halfQuadSize, centerY + halfQuadSize);
        gl.glTexCoord2d(0, 0);
        gl.glVertex2d(centerX - halfQuadSize, centerY - halfQuadSize);
        gl.glTexCoord2d(0, 1);
        gl.glVertex2d(centerX - halfQuadSize, centerY + halfQuadSize);
        
        gl.glEnd();
        
        shader.end(gl);
    }

    @Override
    public void resize(int width, int height) {
        if(camera!=null)
            camera.setViewportDimensions(width, height);
       
        halfQuadSize = Math.min(width, height)/2;
        centerX = width / 2;
        centerY = height / 2;
    }
    
    @Override
    public void dispose(GL2 gl) {
        shader.dispose(gl);
        texture.dispose(gl);
        jbinvis.removeFileUpdateListener(this);
    }

    @Override
    public void fileOffsetUpdated() {
        colorTexture();
    }
    
    @Override
    public void fileClosed() {
        resetTexture();
    }
    
    @Override
    public void fileOpened() {
        fileOffsetUpdated();
    }
    
    /**
     * Reset all pixels to black
     */
    private void resetTexture() {
        for(int i=0;i<PIXEL_COUNT;i++) {
            texture.setPixel(i & 0x1FF, i >> 9, 0);
        }
    }
    
    /**
     * Reads the loaded file and colors in the pixels
     */
    private void colorTexture() {
        if(!jbinvis.isLoaded())
            return;
        
        long offset = jbinvis.getFileOffset();
        int value;
        FileCache file = jbinvis.getFile();
        
        for(int i=0;i<PIXEL_COUNT;i++) {
            value = file.read(offset + i);
            
            if(value < 0)
                value = 0;
            
            texture.setPixel(i & 0x1FF, i >> 9, 0, value, 0);
        }
    }
}
