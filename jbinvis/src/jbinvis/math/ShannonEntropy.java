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
    private double currentComp = 0;
    private int recountTimer = 1000;

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
        for (int i = 0; i < 64; i++) {
            t = i * 4;
            count[t] = count[t + 1] = count[t + 2] = count[t + 3] = 0;
        }
        totalCount = 0;
        currentSum = currentComp = 0;
        queue.clear();
    }

    private double kahan_y, kahan_t;

    /**
     * Adds a new occurrence of specified item. If window size is exceeded, the
     * lower edge is removed
     */
    public void addItem(int val) {

        if (count[val] > 0) {
            kahan_y = -shannon(count[val]) - currentComp;
            kahan_t = currentSum + kahan_y;
            currentComp = (kahan_t - currentSum) - kahan_y;
            currentSum = kahan_t;
        }

        count[val]++;
        kahan_y = shannon(count[val]) - currentComp;
        kahan_t = currentSum + kahan_y;
        currentComp = (kahan_t - currentSum) - kahan_y;
        currentSum = kahan_t;

        totalCount++;
        queue.addLast(val);

        if (queue.size() > windowSize) {
            removeItem();
        }
    }

    /**
     * Removes the item that was first added (lower edge of window)
     */
    public void removeItem() {
        if (queue.size() > 0) {
            Integer x = queue.removeFirst();

            kahan_y = -shannon(count[x]) - currentComp;
            kahan_t = currentSum + kahan_y;
            currentComp = (kahan_t - currentSum) - kahan_y;
            currentSum = kahan_t;

            count[x]--;
            kahan_y = shannon(count[x]) - currentComp;
            kahan_t = currentSum + kahan_y;
            currentComp = (kahan_t - currentSum) - kahan_y;
            currentSum = kahan_t;

            totalCount--;

            if (totalCount == 0) {
                currentSum = 0;
            }
        }
    }

    /**
     * Calculate the shannon entropy of the current state of the instance
     * between 0 and 256
     */
    public double calculateEntropy() {
        if (totalCount == 0) {
            return 0;
        }

        return (Math.log(totalCount) / convToBase2 - currentSum / totalCount) / windowSizeBits * 255;
    }

    public double naiveCalculateEntropy() {
         if (totalCount == 0) {
            return 0;
        }

        double x;
        double tc = totalCount;
        currentSum = 0;
        for(int i=0;i<256;i++) {
            x = count[i] / tc;
            currentSum += shannon(x);
        }
        return -currentSum / windowSizeBits * 255;
  
    }
    
    private final double convToBase2 = Math.log(2);

    private double shannon(double p) {
        if (p == 0) {
            return 0;
        } else {
            return Math.log(p) * p / convToBase2;
        }
    }

}
