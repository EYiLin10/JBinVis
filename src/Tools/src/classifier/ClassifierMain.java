/*
 *  
 */
package classifier;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.ml.CvANN_MLP;
import org.opencv.ml.CvANN_MLP_TrainParams;

/**
 *
 * @author Billy
 */
public class ClassifierMain {
    
    CvANN_MLP mlp;
    CvANN_MLP_TrainParams params;
    
    public ClassifierMain() {
        mlp = new CvANN_MLP();
        TermCriteria criteria = new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS, 1000, 0.00001);

        params = new CvANN_MLP_TrainParams();
        params.set_train_method(CvANN_MLP_TrainParams.BACKPROP);
        params.set_bp_dw_scale(0.1);
        params.set_bp_moment_scale(0.1);
        params.set_term_crit(criteria);
        
        Mat layers = new Mat(4,1,CvType.CV_32SC1);
        layers.row(0).setTo(new Scalar(256*256)); // input neuron
        layers.row(1).setTo(new Scalar(256));
        layers.row(2).setTo(new Scalar(64));
        layers.row(3).setTo(new Scalar(4)); // output neuron
        mlp.create(layers);
    }
    
    public void run() {
        final int colCount = 256*256;
        
        File outputDir = new File("output");
        if(!outputDir.exists())
            return;
        
        File[] dat = outputDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return(pathname.getAbsolutePath().endsWith(".dat"));
            }
        });
        
        // play training
        Mat tempMat = Mat.zeros(1,colCount, CvType.CV_32FC1);
        Mat tempOutMat = Mat.zeros(1, dat.length, CvType.CV_32FC1);
        Mat inputs = new Mat();
        Mat outputs = new Mat();
        
        float[] inputRaw = new float[256*256];
        float[] outputRaw = new float[dat.length];
        
        for(int fi=0;fi<dat.length;fi++) {
            File aFile = dat[fi];
            ClassifierInputFile cif = new ClassifierInputFile(aFile);
           
            for(int oi=0;oi<dat.length;oi++) 
                outputRaw[oi] = (oi == fi) ? 1 : -1;
            tempOutMat.put(0,0,outputRaw);
            
            for(int i=0;i<2;i++) {
                cif.putSample(i<<16, inputRaw);
                
                tempMat.put(0, 0, inputRaw);
                inputs.push_back(tempMat);
                outputs.push_back(tempOutMat);
            }
            
            cif.close();
        }
        
        Mat empty = new Mat();

        mlp.train(inputs, outputs, empty,empty, params, 0);
        
        for(int fi=0;fi<dat.length;fi++) {
            File aFile = dat[fi];
            ClassifierInputFile cif = new ClassifierInputFile(aFile);
            
            cif.putSample(21<<16, inputRaw);
            tempMat.put(0, 0, inputRaw);
            
            mlp.predict(tempMat, tempOutMat);
            
            tempOutMat.get(0,0, outputRaw);
            for(int i=0;i<outputRaw.length;i++) {
                System.out.print(outputRaw[i] + " ");
            }
            
            System.out.println("");
            
            cif.close();
        }
    }
    
    public static void main(String[] args) throws IOException {
        System.setProperty("java.library.path", "x64");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        ClassifierMain m = new ClassifierMain();
        m.run();
    }

}
