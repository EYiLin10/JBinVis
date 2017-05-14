/*
 *  
 */
package classifier;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;



/**
 * Represents a loaded binary file, whose digraph can be readily queried
 * @author Billy
 */
public class BufferedFile {
    public static final int WINDOW_SIZE = 8192;
    
    private double percentageZero = 0;
    private int[] digraph =null;
    private byte[] normalizedDigraph = null;
    private int[] histogram = null;
    private int[] H = null;
    private int offset = 0;
    private long length;
    
    public long getLength() { return length;}
    
    public byte[] getDigraph() { return normalizedDigraph;}
    
    private java.io.RandomAccessFile file = null;
    
    public double getPercentageZero() {return percentageZero ;}
    
    /**
     * Opens the specified file for processing.
     * @param filename
     * @return null on failure
     */
    public static BufferedFile open(String filename) {
        try {
            return new BufferedFile(filename);
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private BufferedFile(String filename) throws IOException {
        file = new RandomAccessFile(new File(filename), "r");
        digraph = new int[256*256];
        histogram = new int[WINDOW_SIZE];
        H = new int[WINDOW_SIZE];
        normalizedDigraph = new byte[256*256];
        length = file.length();
    }
    
    public void close() {
        try {
            file.close();
            file = null;
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        histogram = null;
        digraph = null;
        H = null;
        System.gc();
    }
    
    /**
     * Sets the offset into the file, and computes the digraph
     * @param offset 
     * @return true on success
     */
    public boolean setOffset(int offset) {
        if(offset >= length) offset = (int)length-1;
        
        this.offset = offset;
        
        // zero out the digraph
        for(int i=0;i<digraph.length;i++) digraph[i]=0;
        
        try {
            // seek to offset
            file.seek(offset);
        } catch (IOException ex) {
            Logger.getLogger(BufferedFile.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // read into the digraph
        int prevValue = -1;
        try{
            prevValue = file.read();
            if(prevValue<0) return false;
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        for(int i=1;i<WINDOW_SIZE;i++) {
            int value = -1;
            try{
                value = file.read();
            }
            catch(IOException e) {
                e.printStackTrace();
                return false;
            }
            
            if(value < 0) break;
            
            digraph[prevValue*256+value]++;
            prevValue = value;
        }
        
        normalizeDigraph();
        
        return true;
    }
    
    public void getValue(int offset, byte[] buffer) {
        try {
            file.seek(offset);
            file.read(buffer);
        } catch (IOException ex) {
            Logger.getLogger(BufferedFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    protected void normalizeDigraph() {
        percentageZero =0;
        
        for(int i=0;i<histogram.length;i++) {
            histogram[i]=0;
        }
        
        // frequency histogram of digraph count
        for(int i=0;i<256*256;i++) {
            histogram[digraph[i]]++;
        }
        
        float runningSum = 0;
        
        for(int i=0;i<WINDOW_SIZE;i++) {
            runningSum += histogram[i] / 256.0f / 256.0f;
            H[i] = (int)(runningSum * 255);
        }
        
        int range = 255 - H[0];
        if(range == 0) range = 1;
        int clr;
        for(int i=0;i<256*256;i++) {
           
            clr = digraph[i];
            normalizedDigraph[i] = (byte)((H[clr] - H[0]) * 255 / range);
            
            if(normalizedDigraph[i] == 0) percentageZero += 1;
        }
        percentageZero /= 256*256;
    }
    
    public void dumpDigraphAsImage(String filename) throws IOException {
        BufferedImage image = new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
        for(int i=0;i<digraph.length;i++) {
            image.setRGB(i % 256, i/256, 0xFF000000 | normalizedDigraph[i]<<8);
        }
        ImageIO.write(image, "png", new File(filename));
    }
    
    
    
}
