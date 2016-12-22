package jbinvis.visualisations;

import com.jogamp.opengl.GL2;
import jbinvis.backend.FileCache;
import jbinvis.main.FileUpdateListener;
import jbinvis.main.JBinVis;
import jbinvis.renderer.BinVisCanvas;
import jbinvis.renderer.CanvasShader;
import jbinvis.renderer.CanvasTexture;
import jbinvis.renderer.RenderLogic;
import jbinvis.renderer.camera.OrthographicCamera;

/**
 * The digraph plot
 * @author Billy
 */
public class Digraph extends RenderLogic implements FileUpdateListener {
    private JBinVis jbinvis;
    
    private CanvasTexture texture = null;
    private CanvasShader shader = null;
    private OrthographicCamera camera = null;
    
    private int uniformMax;
    private int maxFreq = 1;
    
    private int[] histogram = null;
    private int windowSize = 8192;
    
    private int halfQuadSize=256, centerX=256, centerY=256;
    
    public Digraph() {
        jbinvis = JBinVis.getInstance();
    }

    @Override
    public void init(GL2 gl) {
        camera = new OrthographicCamera(gl);
        texture = new CanvasTexture(gl, 256,256);
        shader = new CanvasShader(gl, "digraph");
        
        uniformMax = shader.getUniformLocation(gl, "u_max");
        
        colorTexture();
    }

    @Override
    public void resize(int width, int height) {
        if(camera != null)
            camera.setViewportDimensions(width, height);
        
        
        halfQuadSize = Math.min(width, height) / 2;
        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    public void dispose(GL2 gl) {
        jbinvis.removeFileUpdateListener(this);
        texture.dispose(gl);
        shader.dispose(gl);
    }

    @Override
    public void render(GL2 gl, double delta) {
        gl.glClearColor(0.5f,0.5f,0.5f,1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        
        camera.update(gl);
        texture.bind(gl);
        shader.begin(gl);
        
        // provide the maximum frequency
        gl.glUniform1f(uniformMax, (float)maxFreq);
        
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        
        gl.glColor3d(1, 1, 1);
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

    /**
     * Updates the internal texture
     */
    private void colorTexture() {
        if(!jbinvis.isLoaded())
            return;
        if(texture == null)
            return;
        
        histogram = null;
        histogram = new int[256*256];
        maxFreq = 1;
        
        int hisIndex = 0;
        int[] value = {0,0};
        long fileOffset = jbinvis.getFileOffset();
        FileCache cache = jbinvis.getFile();
        
        texture.clear();
        
        for(int i=0;i<windowSize;i++) {
            value[0] = cache.read(fileOffset + i);
            value[1] = cache.read(fileOffset + i + 1);
            
            // we can stop if we are at EOF
            if(value[1] < 0)
                break;
            
            hisIndex = value[0]*256 + value[1];
            histogram[hisIndex]++;
            if(histogram[hisIndex] > maxFreq)
                maxFreq = histogram[hisIndex];
            
            texture.setPixel(value[0], value[1], histogram[hisIndex]);
        }
    }

    @Override
    public String getName() {
        return "Digraph";
    }
    
    @Override
    public void fileOffsetUpdated() {
        colorTexture();
    }

    @Override
    public void fileClosed() {
    }

    @Override
    public void fileOpened() {
        colorTexture();
    }

    @Override
    public void onAttachToCanvas(BinVisCanvas canvas) {
        // initialize histrogram
        histogram = new int[256*256];
        jbinvis.addFileUpdateListener(this);
        colorTexture();
    }

    @Override
    public void onUnattachFromCanvas(BinVisCanvas canvas) {
        histogram = null;
        jbinvis.removeFileUpdateListener(this);
        System.gc();
    }
    
    
}
