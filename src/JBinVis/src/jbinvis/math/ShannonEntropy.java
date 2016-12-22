package jbinvis.math;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Helper class to calculate shannon entropy
 */
public class ShannonEntropy {
    private int[] count = new int[256];
    private LinkedList<Integer> queue = new LinkedList<Integer>();
    
    private int totalCount = 0;
    private int windowSize = 64;
    private double windowSizeBits = 6;
    
    private double currentSum = 0;

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
        this.windowSizeBits = Math.log(windowSize) / convToBase2;
    }

    public int getWindowSize() {
        return windowSize;
    }
    
    /**
     * Clears the counter array
     */
    public void clear() {
        int t;
        for(int i=0;i<64;i++) {
            t=i*4;
            count[t] = count[t+1] = count[t+2] = count[t+3] = 0;
        }
        totalCount = 0;
        currentSum = 0;
        queue.clear();
    }
    
    /**
     * Adds a new occurrence of specified item. If window size is exceeded, the 
     * lower edge is removed
     */
    public void addItem(int val) {
        currentSum -= shannon(count[val]++);
        currentSum += shannon(count[val]);
        totalCount++;
        queue.addLast(val);

        if(queue.size() > windowSize) {
            removeItem(); 
        }
    }
    
    /**
     * Removes the item that was first added (lower edge of window)
     */
    public void removeItem() {
        if(queue.size() > 0) {
            Integer x = queue.removeFirst();
            currentSum -= shannon(count[x]--);
            currentSum += shannon(count[x]);
            totalCount--;
            
            if(totalCount==0) currentSum = 0;
        }
    }
    
    /**
     * Calculate the shannon entropy of the current state of the instance between 0 and 256
     */
    public double calculateEntropy() {
        return (Math.log(totalCount) / convToBase2 - currentSum / totalCount) / windowSizeBits * 256 ;
    }
    
    private final double convToBase2 = Math.log(2);
    
    private double shannon(double p) {
        if (p==0) return 0;
        else return Math.log(p) * p /  convToBase2;
    }
    
}
