/*
 *  
 */
package webcrawler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Billy
 */
public class WebCrawler {

    public WebCrawler() {
        
    }
    
    /**
     * Scrapes search results for links to a particular file type
     * @param fileType 
     */
    public void collectFileType(String fileType) {
                
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       CollectionBatcher batcher = new CollectionBatcher(1000);
       batcher.addQuery(new SearchCriteria.ImageSearchCriteria("dogs or cats or cows or chickens"));
       batcher.addQuery(new SearchCriteria.DocumentSearchCriteria("dogs or cats or cows or chickens", "ppt"));
       
       batcher.execute();
       
       ArrayList<String> links = batcher.getLinks();
       System.out.println(links.size());
    }
    
}
