/*
 *  
 */

/**
 *
 * @author Billy
 */
public class KahanSum {
    public static double sum(double[] values) {
        double res = 0;
        double com = 0;
        
        for(int i=0;i<values.length;i++) {
            double y = values[i] - com;
            double t = res + y;
            com = (t - res) - y;
            res = t;
        }
        return res;
    }
    
    public static void main(String[] args) {
        // create a list that "theoretically" adds to zero
        int count = 10000;
        double[] lst = new double[count];
        for(int i=0;i<count/2;i++) {
            lst[i] = Math.random() * 10000;
            lst[i+count/2] = -lst[i];
        }
        
        double s = 0;
        for(int i=0;i<count;i++) {
            s+= lst[i];
        }
        System.out.println("Without compensation: " + s);
        
        s = sum(lst);
        System.out.println("With compensation" + s);
       
    }
}
