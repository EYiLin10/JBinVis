/*
 *  
 */
package classifier;

import java.io.IOException;

/**
 *
 * @author Billy
 */
public class ClassiferMain {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.library.path", "x64");
        System.loadLibrary("opencv_java2413");
        
        BufferedFile file = BufferedFile.open("snd.wav");
        file.setOffset(1024);
        // TODO: file saved is transparent!!
        file.dumpDigraphAsImage("blah.png");
    }
}
