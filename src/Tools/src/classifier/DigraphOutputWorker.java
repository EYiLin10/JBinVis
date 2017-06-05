package classifier;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DigraphOutputWorker implements Runnable {
    public static interface DigraphOutputWorkerHandler {
        void onFinished();
    }
    
	private String outputDir;
	private File[] files;
	private int count = 0;
	 
    private static Random random = new Random();
    
    private DigraphOutputWorkerHandler handler = null;
    
    public static final int TARGET_SAMPLE_COUNT = 100;
    
	public DigraphOutputWorker(File dir) {
		files = dir.listFiles();
		outputDir = "output/" + dir.getName() + "/";
		File file = new File(outputDir);
		file.mkdirs();
		
		
	}
	
	public void setHandler(DigraphOutputWorkerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
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
                    outputImage(file.getDigraph());
                    count++;
                }
            }
            
            file.close();
            
            System.out.println(Thread.currentThread().getName() + ": " + count + "/" + TARGET_SAMPLE_COUNT);
        }
        
        if(handler!=null) handler.onFinished();
	}

	private ToolImage image = null;
	
	private void outputImage(byte[] digraph) {
		if(image == null) {
			image = new ToolImage(256,256);
		}
		
		image.put(digraph);
		image.save(outputDir + count + ".png");
	}
}