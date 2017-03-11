package jbinvis.visualisations;

import com.jogamp.opengl.GL2;
import java.util.HashMap;
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
    
    private CanvasTexture contrastTex = null;
    private CanvasTexture texture = null;
    private CanvasShader shader = null;
    private OrthographicCamera camera = null;
    
    private int uniformSamplerContrast;
    private int uniformSamplerTexture;
    
    
    private int[] histogram = null;
    private int[] grayCount = null;
    private float[] pdf = null;
    
    private int windowSize = 8192;
    
    private int halfQuadSize=256, centerX=256, centerY=256;

    
    public Digraph() {
        jbinvis = JBinVis.getInstance();
        pdf = new float[256];
    }

    @Override
    public void init(GL2 gl) {
        camera = new OrthographicCamera(gl);
        texture = CanvasTexture.create2D(gl, 256, 256);
        contrastTex = CanvasTexture.create1D(gl, 256, 1);
        shader = new CanvasShader(gl, "digraph");
        
        uniformSamplerTexture = shader.getUniformLocation(gl, "u_texture");
        uniformSamplerContrast = shader.getUniformLocation(gl, "u_contrast");
        contrastTex.setUniformSampler(gl, uniformSamplerContrast);
        
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
        contrastTex.dispose(gl);
        shader.dispose(gl);
    }

    @Override
    public void render(GL2 gl, double delta) {
        gl.glClearColor(0.5f,0.5f,0.5f,1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        
        camera.update(gl);
        texture.bind(gl);
        contrastTex.bind(gl);
        

        shader.begin(gl);
        texture.setUniformSampler(gl, uniformSamplerTexture);
        contrastTex.setUniformSampler(gl, uniformSamplerContrast);
        
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
        
        int histSize = 256*256-1;
        
        histogram = null;
        histogram = new int[histSize+1];
        
        grayCount = null;
        grayCount = new int[256];
        
        
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
            
            //texture.setPixel(value[1], value[0], 0xFF00);
        }
        
        computeContrast();
    }
    
    private void computeContrast() {
        int[] intensity = new int[windowSize];
        
        // intensity is a frequency histogram of digraph count
        for(int i=0;i<256*256;i++) {
            intensity[histogram[i]]++;
        }
        int[] H = new int[windowSize];
        float runningSum = 0;
        
        for(int i=0;i<windowSize;i++) {
            runningSum += intensity[i] / 256.0f / 256.0f;
            H[i] = (int)(runningSum * 255);
        }
        
        int range = 255 - H[0];
        if(range == 0) range = 1;
        int clr;
        for(int i=0;i<256*256;i++) {
            clr = histogram[i];
            texture.setPixel(i%256, i/256, ((H[clr] - H[0]) * 255 / range)<<8);
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
        grayCount = null;
        jbinvis.removeFileUpdateListener(this);
        System.gc();
    }
    
    
}
