
package jbinvis.renderer;

import com.jogamp.opengl.GL;
import java.nio.ByteBuffer;

/**
 * Encapsulates a texture that can be 
 * drawn using openGL functions, whose internal pixels 
 * can be set as well.
 * @author Billy
 */
public class CanvasTexture {
  
    /**
     * Each pixel takes 3 bytes, in order, RGB
     */
    public final byte[] pixels;
    
    // contains the generated texture number from glGenTextures
    private final int[] texIndex;
    
    // holds the buffer wrapping the internal array of pixels
    private final ByteBuffer buffer;
    
    private boolean dirty = false;
    
    public final int width;
    public final int height;
    
    /**
     * Instantiates this texture with given width and height
     */
    public CanvasTexture(GL gl, int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new byte[width*height*3];
        texIndex = new int[1];
        
        buffer = ByteBuffer.wrap(pixels);
        gl.glGenTextures(1, texIndex, 0);
        
        // set texture properties
        gl.glBindTexture(GL.GL_TEXTURE_2D, texIndex[0]);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, width, height,
                0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }
    
    public void setPixel(int x, int y, byte r, byte g, byte b) {
        if(x<0 || x>=width || y<0 || y>=height)
            throw new IndexOutOfBoundsException("x and y values should be within dimensions");
        
        int offset = 3*(y*width + x);
        pixels[offset] = r;
        pixels[offset+1] = g;
        pixels[offset+2] = b;
        
        dirty = true;
    }
    
    /**
     * When the pixel buffer is modified, call this to update the buffer in 
     * GPU
     */
    public void update(GL gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, texIndex[0]);
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0,0, this.width, this.height, 
                GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
    }
    
    /**
     * Binds the current texture
     * @param gl 
     */
    public void bind(GL gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, texIndex[0]);
        if(dirty) {
            update(gl);
            dirty = false;
        }
    }
    
    public void dispose(GL gl) {
        gl.glDeleteTextures(1, texIndex,0);
    }
    
    public static void unbind(GL gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }
   
}
