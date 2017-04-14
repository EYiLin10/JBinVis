/*
 *  
 */
package classifier;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Billy
 */
public class Digrapher implements DirectoryWorker.DirectoryWorkerHandler{
    private Stack<File> directories;
    private int runCount = 0;
    private static final int THREAD_COUNT = 4;
    private ReentrantLock lock;
    
    public Digrapher() {
        new File("output/").mkdirs();
        lock = new ReentrantLock();
        
        // get all subdirectories of download
        File file = new File("downloads");
        File[] subdirs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        
        directories = new Stack<>();
        for(File f : subdirs) directories.push(f);
    }
    
   
    
    @Override
    public void onFinished() {
        lock.lock();
        runCount --;
        lock.unlock();
    }
    
    public void run() {
        boolean dispatched = false;
        int retryTimeout = 1000;
        while(!directories.isEmpty()) {
             dispatched = false;
             
            lock.lock();
            if(runCount<THREAD_COUNT) {
                runCount++;
                File f = directories.pop();
                
                // dispatch a new thread
                DirectoryWorker w = new DirectoryWorker(f);
                w.setFinishedHandler(this);
                        
                Thread thr = new Thread(w);
                
                thr.start();
                dispatched=true;
                retryTimeout = 1000;
                
                System.out.println("Dispatched " + thr.getName() + " for " + f.getName());
            }
            lock.unlock();
            
            if(!dispatched) {
                // thread pool was full
                try {
                    Thread.sleep(retryTimeout);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Digrapher.class.getName()).log(Level.SEVERE, null, ex);
                }
                retryTimeout <<=1;
                if(retryTimeout>30000) retryTimeout = 30000;
            }
            
        }
    }
    
    public static void main(String[] args) throws IOException {
        System.setProperty("java.library.path", "x64");
        System.loadLibrary("opencv_java2413");
        
        Digrapher m = new Digrapher();
        m.run();
    }

}
