/*
 *  
 */
package classifier;

import java.io.File;

/**
 *
 * @author Billy
 */
public class ClassifierInputFile {
    private static byte[] _buffer = new byte[256*256];
    
    private File inputFile;
    private BufferedFile bufferedFile;
    
    public int getSampleCount() {
        return (int)(bufferedFile.getLength() >> 16);
    }

    public ClassifierInputFile(File input) {
        inputFile = input;
        bufferedFile = BufferedFile.open(inputFile.getAbsolutePath());
    }

    public void putSample(int index, float[] buffer) {
        bufferedFile.getValue(index, _buffer);
        int temp;
        for(int i=0;i<_buffer.length;i++) {
            temp = _buffer[i];
            if(temp<0) temp+= 128;
            buffer[i] = temp/ 255.0f;
        }
    }
    
    public void close() {
        if(bufferedFile!=null) {
            bufferedFile.close();
            bufferedFile = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
