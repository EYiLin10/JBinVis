
import java.io.BufferedReader;
import java.io.FileReader;

/*
 *  
 */

/**
 *
 * @author Billy
 */
public class Similarity {
    public static void main(String[] args) {
        try {
            FileReader f1 = new FileReader("naive_method.txt");
            FileReader f2 = new FileReader("kahan_method.txt");
            
            BufferedReader reader1 = new BufferedReader(f1);
            BufferedReader reader2 = new BufferedReader(f2);
            
            String l1, l2;
            int ln = 1;
            
            do {
                l1 = reader1.readLine();
                l2 = reader2.readLine();
                
                if(l1 == null || l2 == null) 
                    break;
                
                if(!l1.equals(l2))
                    System.out.println("Line " + ln + ": " + l1 + " " + l2 );
                
                ln++;
            }
            while(l1 != null && l2 != null);
            
            f1.close();
            f2.close();
            
        }
        catch(Exception e) {
            
        }
    }
}
