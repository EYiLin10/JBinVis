/*
 *  
 */
package webcrawler;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Collects links that satisfy a particular search
 * @author Billy
 */
public class LinksCollector {

    private static class SearchPage {
        public SearchPage() {
            searchLinks = new ArrayList<String>();
            nextPage = null;
        }
        public ArrayList<String> searchLinks;
        public String nextPage;
    }
    
    private final int targetSearchCount;
    private int currentCount;
    private int currentPage;
    private ArrayList<String> urls;
    private final SearchCriteria criteria;

    public LinksCollector(SearchCriteria search, int searchCount) {
        targetSearchCount = searchCount;
        currentCount = 0;
        currentPage = 1;
        criteria = search;
    }
    
    public void start() throws IOException {
        urls = new ArrayList<String>();
        int attempt = 0;
        String queryLink = criteria.getSearchString();
        
        while(currentCount<targetSearchCount) {
            System.out.println("Searching page " + currentPage);
            SearchPage page = null;

            page = fetchPageLinks(criteria.getBaseUrl() + queryLink);

            // save all the urls
            urls.addAll(page.searchLinks);
            currentCount += page.searchLinks.size();
            
            // move to next page
            queryLink = page.nextPage;
            if(queryLink == null)
                break;
            currentPage++;
        }
    }
    
    public void dumpToFile(String filename) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
        stream.writeObject(urls);
        stream.close();
        System.out.println("Dumped urls to " + filename);
    }
    
    /**
     * Returns all the links in the page
     * @return
     * @throws IOException 
     */
    private SearchPage fetchPageLinks(String url) throws IOException {
        LinkFilter filter = criteria.getLinkFilter();
        
        SearchPage page = new SearchPage();
        
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();
        
        FileWriter writer = new FileWriter("blah.html");
        writer.write(doc.outerHtml());
        writer.close();
   
        criteria.process(doc, page.searchLinks);
        page.nextPage = criteria.getNext(doc);
        return page;
    }
    
    
    public ArrayList<String> getLinks() {
        return urls;
    }
    
}
