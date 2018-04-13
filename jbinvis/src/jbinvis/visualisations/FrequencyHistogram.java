package jbinvis.visualisations;

import com.jogamp.opengl.GL2;
import jbinvis.backend.FileCache;
import jbinvis.main.FileUpdateListener;
import jbinvis.main.JBinVis;
import jbinvis.renderer.BinVisCanvas;
import jbinvis.renderer.CanvasShader;
import jbinvis.renderer.RenderLogic;
import jbinvis.renderer.camera.OrthographicCamera;

/**
 * Represents a byte frequency histogram
 * @author Billy
 */
public class FrequencyHistogram extends RenderLogic implements FileUpdateListener {

    private final JBinVis jbinvis;
    private OrthographicCamera camera = null;
    
    private int halfQuadSize = 256, centerX=256, centerY=256;
    private int[] histogram = null;
    
    private double max=1;
    private int windowSize = 4096;
    
    private CanvasShader shader = null;

    
    public FrequencyHistogram() {
        jbinvis = JBinVis.getInstance();
    }

    @Override
    public void init(GL2 gl) {
        camera = new OrthographicCamera(gl);
        shader = new CanvasShader(gl,"histogram");
        
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
    public void render(GL2 gl, double delta) {
        gl.glClearColor(0.5f,0.5f,0.5f,1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        camera.update(gl);
        
        double t, r, b, l, x,y1,y2,w,h,range;
        
        t=centerY - halfQuadSize;
        r=centerX + halfQuadSize;
        b=centerY + halfQuadSize;
        l=centerX - halfQuadSize;

        // render a quad for background
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        gl.glColor3d(0, 0, 0);
        gl.glVertex2d(r,t);
        gl.glVertex2d(r,b);
        gl.glVertex2d(l,t);
        gl.glVertex2d(l,b);
        gl.glEnd();
        
        
        t += 3;
        r -= 3;
        b -= 3;
        l += 3;
        
        // render histogram
        w = (r - l)/255.0; // width of a "segment"
        h = b - t; // full height of a "segment"
  
        shader.begin(gl);
        
        gl.glColor3d(0,1.0,0);
        gl.glBegin(GL2.GL_TRIANGLES);
        for(int i=0;i<255;i++) {
            x = l + i*w; // horizontal offset
            y1 = t + h - (histogram[i]) * h / max;
            y2 = t + h - (histogram[i+1]) * h / max;
            
            gl.glVertex2d(x,y1);
            gl.glVertex2d(x+w,y2);
            gl.glVertex2d(x+w,b);
            gl.glVertex2d(x,y1);
            gl.glVertex2d(x+w,b);
            gl.glVertex2d(x,b);
        }
        gl.glEnd();
        
        shader.end(gl);
    }

    @Override
    public String getName() {
        return "Byte Frequency Histogram";
    }

    @Override
    public void dispose(GL2 gl) {
        shader.dispose(gl);
    }
    
    private void updateHistogram() {
        if(!jbinvis.isLoaded())
            return;
        
        histogram = null;
        histogram = new int[256];
        max = 1;
        
        int value;
        long fileOffset = jbinvis.getFileOffset();
        FileCache cache = jbinvis.getFile();
        
        for(int i=0;i<windowSize;i++) {
            value = cache.read(i + fileOffset);
            if(value < 0) 
                break; // stop after EOF
            
            histogram[value]++;
            
            if(histogram[value]>max)
                max = histogram[value];
        }
    }
    
    @Override
    public void fileOffsetUpdated() {
        updateHistogram();
    }

    @Override
    public void fileClosed() {
    }

    @Override
    public void fileOpened() {
        updateHistogram();
    }

    @Override
    public void onAttachToCanvas(BinVisCanvas canvas) {
        jbinvis.addFileUpdateListener(this);
        histogram = new int[256];
        updateHistogram();
    }

    @Override
    public void onUnattachFromCanvas(BinVisCanvas canvas) {
        jbinvis.removeFileUpdateListener(this);
        histogram = null;
        System.gc();
    }
    
}
