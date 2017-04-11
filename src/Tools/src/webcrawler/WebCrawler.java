/*
 *  
 */
package webcrawler;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import webcrawler.filter.DocumentSearchCriteria;
import webcrawler.filter.ImageSearchCriteria;
import webcrawler.filter.SearchCriteria;
import webcrawler.filter.SecondaryImageCrawl;

/**
 *
 * @author Billy
 */
public class WebCrawler {

    private ArrayList<String> links;
    
    public WebCrawler() {
         CollectionBatcher batcher = new CollectionBatcher(100);
       batcher.addQuery(new ImageSearchCriteria("dogs or cats or cows or chickens", "png")
               .setSecondaryCrawl(new SecondaryImageCrawl(500)));
       batcher.addQuery(new ImageSearchCriteria("dogs or cats or cows or chickens", "jpg")
               .setSecondaryCrawl(new SecondaryImageCrawl(500)));
       batcher.addQuery(new ImageSearchCriteria("dogs or cats or cows or chickens", "bmp")
               .setSecondaryCrawl(new SecondaryImageCrawl(500)));
      batcher.addQuery(new DocumentSearchCriteria("dogs or cats or cows or chickens", "ppt"));
       batcher.addQuery(new DocumentSearchCriteria("dogs or cats or cows or chickens", "pdf"));
       batcher.addQuery(new DocumentSearchCriteria("dogs or cats or cows or chickens", "doc"));
       batcher.addQuery(new DocumentSearchCriteria("dogs or cats or cows or chickens", "xls"));
       
       batcher.execute();
       
       links = batcher.getLinks();
       
       try {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("links.dat", false));
        stream.writeObject(links);
        stream.close();
       }catch(IOException e) {
           e.printStackTrace();
       }
        
      
    }
    
    public void download() {
        Thread[] threads = new Thread[4];
        for(int i=0;i<threads.length;i++) {
            threads[i] = new Thread(new DownloadWorker(links,i, threads.length));
            threads[i].start();
        }
        
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
      WebCrawler crawler = new WebCrawler();
      crawler.download();
      // System.out.println(links.size());
    }
    
}
