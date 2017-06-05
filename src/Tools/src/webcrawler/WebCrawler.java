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
         CollectionBatcher batcher = new CollectionBatcher(200);
         SecondaryImageCrawl seccrawl = new SecondaryImageCrawl(1000);
         
        batcher.addQuery(new ImageSearchCriteria("dogs", "png").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("dogs", "gif").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("dogs", "bmp").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("dogs", "jpg").setSecondaryCrawl(seccrawl));
        
        batcher.addQuery(new ImageSearchCriteria("cats", "png").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("cats", "gif").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("cats", "bmp").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("cats", "jpg").setSecondaryCrawl(seccrawl));
        
        batcher.addQuery(new ImageSearchCriteria("cows", "png").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("cows", "gif").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("cows", "bmp").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("cows", "jpg").setSecondaryCrawl(seccrawl));
        
        batcher.addQuery(new ImageSearchCriteria("chickens", "png").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("chickens", "gif").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("chickens", "bmp").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("chickens", "jpg").setSecondaryCrawl(seccrawl));
        
        batcher.addQuery(new ImageSearchCriteria("john cena", "png").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("john cena", "gif").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("john cena", "bmp").setSecondaryCrawl(seccrawl));
        batcher.addQuery(new ImageSearchCriteria("john cena", "jpg").setSecondaryCrawl(seccrawl));
   
        batcher.addQuery(new DocumentSearchCriteria("dogs", "ppt"));
        batcher.addQuery(new DocumentSearchCriteria("dogs", "pdf"));
        batcher.addQuery(new DocumentSearchCriteria("dogs", "doc"));
        batcher.addQuery(new DocumentSearchCriteria("dogs", "xls"));
 
         batcher.addQuery(new DocumentSearchCriteria("cats", "ppt"));
        batcher.addQuery(new DocumentSearchCriteria("cats", "pdf"));
        batcher.addQuery(new DocumentSearchCriteria("cats", "doc"));
        batcher.addQuery(new DocumentSearchCriteria("cats", "xls"));
        
         batcher.addQuery(new DocumentSearchCriteria("dogs", "ppt"));
        batcher.addQuery(new DocumentSearchCriteria("dogs", "pdf"));
        batcher.addQuery(new DocumentSearchCriteria("dogs", "doc"));
        batcher.addQuery(new DocumentSearchCriteria("dogs", "xls"));
        
         batcher.addQuery(new DocumentSearchCriteria("chickens", "ppt"));
        batcher.addQuery(new DocumentSearchCriteria("chickens", "pdf"));
        batcher.addQuery(new DocumentSearchCriteria("chickens", "doc"));
        batcher.addQuery(new DocumentSearchCriteria("chickens", "xls"));
        
         batcher.addQuery(new DocumentSearchCriteria("john cena", "ppt"));
        batcher.addQuery(new DocumentSearchCriteria("john cena", "pdf"));
        batcher.addQuery(new DocumentSearchCriteria("john cena", "doc"));
        batcher.addQuery(new DocumentSearchCriteria("john cena", "xls"));
        
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
