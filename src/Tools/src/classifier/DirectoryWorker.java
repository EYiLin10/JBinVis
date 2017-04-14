/*
 *  
 */
package classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Billy
 */
public class DirectoryWorker implements Runnable{
    public static interface DirectoryWorkerHandler {
        void onFinished();
    }
    
    private static Random random = new Random();
    
    private File directory;
    private File[] files;
    private OutputStream output;
    private boolean error = false;
    
    public static final int TARGET_SAMPLE_COUNT = 100;
    
    private int count = 0;
    private DirectoryWorkerHandler handler = null;
    
    public DirectoryWorker(File dir) {
        directory = dir;
        files = dir.listFiles();
        
        output = null;
        try {
            output = new FileOutputStream("output/" + dir.getName() + ".dat");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryWorker.class.getName()).log(Level.SEVERE, null, ex);
            error = true;
            return;
        }
     
    }

    public void setFinishedHandler(DirectoryWorkerHandler handler) {
        this.handler = handler;
        
    }
    
    @Override
    public void run() {
        
        if(output == null) {
            if(handler!=null) handler.onFinished();
            return;
        }
        
        int i=-1;
        
        int maxAttempt;
        
        while(++i<files.length && count < TARGET_SAMPLE_COUNT) {
            File curFile = files[i];
            BufferedFile file = BufferedFile.open(curFile.getAbsolutePath());
            if(file == null) {
                System.out.println("Cannot open file " + curFile.getAbsolutePath());
                
                continue;
            }
            
            if(file.getLength()<8192) {
                System.out.println("File too small: " + curFile.getAbsolutePath());
                file.close();
                continue;
            }
            
            maxAttempt = 10; // can change this based on size of file
            
            for(int attempt = 0;attempt<maxAttempt;attempt++) {
                int x = (int)(random.nextDouble() * (file.getLength()-8192));
                file.setOffset(x);
                
                if(file.getPercentageZero() <0.995 && file.getPercentageZero()>0.005) {
                    try {
                        output.write(file.getDigraph());
                        count++;
                    } catch (IOException ex) {
                        Logger.getLogger(DirectoryWorker.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
            }
            
            file.close();
            
            System.out.println(Thread.currentThread().getName() + ": " + count + "/" + TARGET_SAMPLE_COUNT);
        }
        
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(DirectoryWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(handler!=null) handler.onFinished();
    }
    
    
}
