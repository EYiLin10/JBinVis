/*
 *  
 */
package jbinvis.main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import jbinvis.backend.FileCache;
import jbinvis.frontend.MainFrame;
import jbinvis.math.Hilbert;

/**
 *
 * @author Billy
 */
public class JBinVis {

    // SINGLETON //
    private static JBinVis _singleton = null;
    
    public static JBinVis getInstance() {
        return _singleton;
    }

    //////////////
    
    private boolean isFileLoaded = false;
    private FileCache fileCache = null;
    private long fileLength = 0;
    
    // observers
    private ArrayList<FileUpdateListener> fileUpdateListeners;
    
    private long fileOffset = 0;  
 
    private JBinVis() {   
        fileUpdateListeners = new ArrayList();
    }
    
    public void closeFile() {
        if(fileCache == null)
            return;
        
        fileCache.close();
        fileLength = fileOffset = 0;
        isFileLoaded = false;
        
        for(FileUpdateListener l : fileUpdateListeners)
            l.fileClosed();
    }
    
    /**
     * Loads a new file to be analysed.
     * @param filename
     * @return true if successful
     */
    public boolean loadFile(String filename) {
        closeFile();
        
        try {
            fileCache = new FileCache(new RandomAccessFile(filename, "r"));
        }
        catch(IOException e) {
            e.printStackTrace();
            fileCache = null;
            return false;
        }
        
        fileLength = fileCache.size();
        
        isFileLoaded = true;
        for(FileUpdateListener l : fileUpdateListeners)
            l.fileOpened();
        return true;
    }
    
    /**
     * Returns length of opened file.
     */
    public long getFileSize() {
        return fileLength;
    }
    
    /**
     * Returns the current offset into the file
     * @return 
     */
    public long getFileOffset() {
        return fileOffset;
    }
    
    public FileCache getFile() {
        return fileCache;
    }
    
    public boolean isLoaded() {
        return isFileLoaded;
    }
    
    /**
     * Sets the file offset, clamped to the length of the file. This will 
     * send an update message to all observers
     */
    public void setFileOffset(long offset) {
        fileOffset = offset;
        if(fileOffset >= fileLength)
            fileOffset = fileLength - 1;
        else if(fileOffset < 0) 
            fileOffset = 0;
        
        for(FileUpdateListener l : fileUpdateListeners)
            l.fileOffsetUpdated();
    }
    
    /**
     * Registers an update listener
     */
    public void addFileUpdateListener(FileUpdateListener o) {
        fileUpdateListeners.add(o);
    }
    
    /**
     * Unregisters an update listener
     */
    public void removeFileUpdateListener(FileUpdateListener o) {
        fileUpdateListeners.remove(o);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {   

        // create singleton instance
        _singleton = new JBinVis();
        
        // initialise Hilbert curve
        Hilbert.getInstance();
        
        //MainFrame frame = new MainFrame();
        
        MainFrame frame = new MainFrame();
        frame.setTitle("JBinVis");
        frame.setVisible(true); 

    }
    
}
