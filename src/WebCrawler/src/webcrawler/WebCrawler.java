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
import webcrawler.CollectionBatcher;
import webcrawler.filter.ImageSearchCriteria;
import webcrawler.filter.SearchCriteria;
import webcrawler.filter.SecondaryImageCrawl;

/**
 *
 * @author Billy
 */
public class WebCrawler {

    public WebCrawler() {
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       CollectionBatcher batcher = new CollectionBatcher(100);
       batcher.addQuery(new ImageSearchCriteria("dogs or cats or cows or chickens", "png")
               .setSecondaryCrawl(new SecondaryImageCrawl(500)));
       batcher.addQuery(new ImageSearchCriteria("dogs or cats or cows or chickens", "jpg")
               .setSecondaryCrawl(new SecondaryImageCrawl(500)));
       batcher.addQuery(new ImageSearchCriteria("dogs or cats or cows or chickens", "bmp")
               .setSecondaryCrawl(new SecondaryImageCrawl(500)));
      // batcher.addQuery(new SearchCriteria.DocumentSearchCriteria("dogs or cats or cows or chickens", "ppt"));
       
       batcher.execute();
       
       ArrayList<String> links = batcher.getLinks();
       System.out.println(links.size());
    }
    
}
