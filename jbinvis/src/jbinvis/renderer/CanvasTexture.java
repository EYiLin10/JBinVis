package jbinvis.renderer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import java.nio.ByteBuffer;

/**
 * Encapsulates a texture that can be drawn using openGL functions, whose
 * internal pixels can be set as well.
 *
 * @author Billy
 */
public class CanvasTexture {

    /**
     * Each pixel takes 3 bytes, in order, RGB
     */
    private byte[] pixels;
    public final int pixelCount;

    // contains the generated texture number from glGenTextures
    private int[] texIndex;
    private int texTarget;

    // holds the buffer wrapping the internal array of pixels
    private ByteBuffer buffer;

    private boolean dirty = false;

    public final int width;
    public final int height;

    public final int dimensions;

    private int textureUnit;

    public int getTextureUnit() {
        return textureUnit;
    }
    
    public int getTextureIndex() {
        return texIndex[0];
    }

    public byte[] getPixels() {
        return pixels;
    }

    /**
     * Create a 1D texture on texture unit 0
     */
    public static CanvasTexture create1D(GL2 gl, int size) {
        CanvasTexture tex = new CanvasTexture(gl, size);
        tex.textureUnit= GL2.GL_TEXTURE0;
        tex.createByteBuffer();
        tex.createTexture(gl);
        
        return tex;
    }
    
    /**
     * Create a 1D texture on specified texture unit index (default is 0)
     */
     public static CanvasTexture create1D(GL2 gl, int size, int texUnitIndex) {
        CanvasTexture tex = new CanvasTexture(gl, size);
        tex.textureUnit= GL2.GL_TEXTURE0 + texUnitIndex;
        tex.createByteBuffer();
        tex.createTexture(gl);
        
        return tex;
    }
     
     /**
     * Create a 2D texture on specified texture unit 0
     */
    public static CanvasTexture create2D(GL2 gl, int width, int height) {
        CanvasTexture tex = new CanvasTexture(gl, width, height);
        tex.textureUnit= GL2.GL_TEXTURE0;
        tex.createByteBuffer();
        tex.createTexture(gl);
        
        return tex;
    }
    
    /**
     * Create a 2D texture on specified texture unit index (default is 0)
     */
    public static CanvasTexture create2D(GL2 gl, int width, int height, int texUnitIndex) {
        CanvasTexture tex = new CanvasTexture(gl, width, height);
        tex.textureUnit= GL2.GL_TEXTURE0 + texUnitIndex;
        tex.createByteBuffer();
        tex.createTexture(gl);
        
        return tex;
    }

    private CanvasTexture(GL2 gl, int size) {
        dimensions = 1;
        width = size;
        height = 1;
        pixelCount = size;
        texTarget = GL2.GL_TEXTURE_1D;
    }

    private CanvasTexture(GL2 gl, int width, int height) {
        this.width = width;
        this.height = height;
        dimensions = 2;
        pixelCount = width * height;
        texTarget = GL2.GL_TEXTURE_2D;
    }

    private void createByteBuffer() {
        pixels = new byte[width * height * 3];
        buffer = ByteBuffer.wrap(pixels);
    }

    private void createTexture(GL2 gl) {
        texIndex = new int[1];

        gl.glGenTextures(1, texIndex, 0);

        gl.glActiveTexture(textureUnit);
        gl.glBindTexture(texTarget, texIndex[0]);

        if (dimensions == 1) {
            gl.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGB, width,
                    0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, buffer);
        } else {
            gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, width, height,
                    0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, buffer);
        }

        gl.glTexParameteri(texTarget, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameteri(texTarget, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

        gl.glBindTexture(texTarget, 0);

        gl.glActiveTexture(GL2.GL_TEXTURE0);
    }

    /**
     * Sets the colour of a pixel given by the coordinates.
     *
     * @param y 0 for 1D textures
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("x and y values should be within dimensions");
        }

        int offset = 3 * (y * width + x);
        pixels[offset] = (byte) r;
        pixels[offset + 1] = (byte) g;
        pixels[offset + 2] = (byte) b;

        dirty = true;
    }

    public void setPixel(int x, int y, int clrRGB) {
        setPixel(x, y, (clrRGB & 0xFF0000) >> 16, (clrRGB & 0xFF00) >> 8, clrRGB & 0xFF);
    }

    public int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("x and y values should be within dimensions");
        }

        int offset = 3 * (y * width + x);
        return (pixels[offset] << 16) & 0xFF000 | (pixels[offset + 1] << 8) & 0xFF00 | pixels[offset + 2] & 0xFF;
    }

    /**
     * When the pixel buffer is modified, call this to update the buffer in GPU
     */
    public void update(GL2 gl) {
        gl.glActiveTexture(textureUnit);
        gl.glBindTexture(texTarget, texIndex[0]);
        if (dimensions == 1) {
            gl.glTexSubImage1D(texTarget, 0, 0, width, 
                    GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, buffer);
        } else {
            gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, 0, 0, this.width, this.height,
                    GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, buffer);
        }
    }

    public void clear() {
        for (int i = 0; i < pixels.length / 3; i++) {
            pixels[i * 3] = pixels[i * 3 + 1] = pixels[i * 3 + 2] = 0;
        }
        dirty = true;
    }

    /**
     * Binds the current texture. Compatible with fixed-function pipeline.
     *
     * @param gl
     */
    public void bind(GL2 gl) {
        gl.glActiveTexture(textureUnit);
        gl.glBindTexture(texTarget, texIndex[0]);
        if (dirty) {
            update(gl);
            dirty = false;
        }

        gl.glActiveTexture(GL2.GL_TEXTURE0);
    }

    public void setUniformSampler(GL2 gl, int location) {
        gl.glUniform1i(location, textureUnit - GL2.GL_TEXTURE0);
    }

    public void dispose(GL2 gl) {
        gl.glDeleteTextures(1, texIndex, 0);
    }
}
