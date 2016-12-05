/*
 *  
 */
package jbinvis.backend;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Interface between logic and file reading, to seamlessly read and cache a
 * large part of a file. 
 * @author Billy
 */
public class FileCache {
        
    public static final int BANK_COUNT_BITS = 4;
    public static final int BANK_OFFSET_BITS = 16;
    public static final int BANK_COUNT = 1<<BANK_COUNT_BITS; 
    public static final int BANK_SIZE = 1<<BANK_OFFSET_BITS;
    
    private class CacheBank {
        public byte[] data = new byte[BANK_SIZE];
        public long tag = -1;
    }

    private CacheBank[] banks;
    private RandomAccessFile file;
    private long filesize;
    
    /**
     * Instantiates a new file cache using the input file.
     * @param file 
     */
    public FileCache(RandomAccessFile file) {
        banks = new CacheBank[BANK_COUNT];
        for(int i=0;i<BANK_COUNT;i++)
            banks[i] = new CacheBank();
        this.file = file;
        try {
            filesize = file.length();  
        }
        catch(IOException e) {
            System.out.println("FileCache: Cannot obtain file size!");
            filesize = 0;
        }
    }
    
    /**
     * Closes the file associated with this cache. 
     * Clears all buffer.
     */
    public void close() {
        try {
            file.close();
        }
        catch(IOException e) { }
        
        banks = null;
        System.gc();
    }  
    
    /**
     * Fetches a BANK_SIZE worth of data from given offset into file
     */
    private void fetch(long offset, CacheBank bank) {
        try {
            file.seek(offset);
        } catch (IOException ex) {
            System.out.println("FileCache: Could not seek to offset " + offset);
            for(int i=0;i<BANK_SIZE; i++) 
                bank.data[i] = 0;
            return;
        }
        for(int i=0;i<BANK_SIZE; i++) {
            try {
                bank.data[i] = file.readByte();
            }
            catch(EOFException e) {
                bank.data[i] = 0;
            }
            catch(IOException e) {
                bank.data[i] = 0;
            }
        }
    }
    
    /**
     * Gets the byte value at the given offset into the file.
     * @param offset
     * @return -1 if offset is larger than file size
     */
    public int read(long offset) {
        if(offset >= this.filesize) 
            return -1;
        
        int bankOffset = (int)(offset % BANK_SIZE); // offset into a bank
        int bankIndex = (int)((offset >> BANK_OFFSET_BITS) % BANK_COUNT); // which bank
        long bankTag = offset >> (BANK_OFFSET_BITS + BANK_COUNT_BITS); // bank tag
        
        CacheBank cache = banks[bankIndex];
        
        if(cache.tag != bankTag) {
            // different tag, gotta fetch in the bank
            long alignedOffset = (offset >> BANK_OFFSET_BITS) << BANK_OFFSET_BITS;
            fetch(alignedOffset, cache);
            
            // set the tag
            cache.tag = bankTag;
        }
        return (cache.data[bankOffset] & 0xFF);
    }
    
    /**
     * Gets the size of the file
     * @return 
     */
    public long size() {
        return filesize;
    }
    
    /**
     * This is to test the reading of a file with and without FileCache
     * @param args 
     */
    public static void main(String[] args) {
        final int attemptCount = 500;
        RandomAccessFile file;
        try {
            file = new RandomAccessFile("lib//gluegen-rt.jar", "r");
        } catch(IOException e) {
            return;
        }
        FileCache cache = new FileCache(file);
        
        // read
        long tm;
        double avgWith=0;
        for(int a = 0;a<attemptCount;a++) {
            tm=System.currentTimeMillis();
            for(int i=0;i<65536;i++) {
                cache.read(i+125);
            }
            avgWith += (System.currentTimeMillis() - tm) / 1000.0;
        }
        avgWith /= attemptCount;
        
        System.out.println("Average Time with FileCache: " + avgWith);
        
        double avgWithout=0;
        for(int a = 0;a<attemptCount;a++) {
            tm=System.currentTimeMillis();
            try {
            file.seek(125);
                for(int i=0;i<65536;i++) 
                    file.readByte();
             } catch(IOException e) {}
            avgWithout += (System.currentTimeMillis() - tm) / 1000.0;
        }
        avgWithout /= attemptCount;
        System.out.println("Average Time without FileCache: " + avgWithout);
        
        System.out.format("Speedup: %.2fx\n", avgWithout / avgWith);
        
        cache.close();
    }
}
