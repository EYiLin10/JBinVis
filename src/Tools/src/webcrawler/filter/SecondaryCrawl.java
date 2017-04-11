/*
 *  
 */
package webcrawler.filter;

import java.util.ArrayList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Second level crawling after initial crawl
 * @author Billy
 */
public abstract class SecondaryCrawl {
    protected ArrayList<String> links;
    protected LinkFilter filter;
    protected int targetCount;
    
    public void setLinkFilter(LinkFilter filter) { this.filter = filter;}
    
    public ArrayList<String> getLinks() { return links;}
    public void setLinks(ArrayList<String> links) { this.links = links;}
    
    SecondaryCrawl(int targetCount) {
        this.links = null;
        this.filter = null;
        this.targetCount = targetCount;
    }
    
    /**
     * Runs the secondary crawler on the given links 
     * @return The list of all links crawled
     */
    public ArrayList<String> execute() {
        System.out.println("Secondary crawl started");
        ArrayList<String> results = new ArrayList<String>();
        for(int i=0;i<links.size();i++) {
            String url = links.get(i);
            System.out.println(results.size() + "/" + targetCount + " Crawling into " + url);
            Connection conn = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0")
                            .timeout(5000);
            
            Document doc = null;
            try {
                doc = conn.get();
            }
            catch(Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Skipping");
                continue;
            }
            process(doc, results);
            
            if(results.size()>= targetCount) break;
        }
        
        return results;
    }
    
    protected void process(Document doc, ArrayList<String> results) {
    }
}
