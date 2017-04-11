/*
 *  
 */
package webcrawler.filter;

import java.io.Serializable;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
/**
 *
 * @author Billy
 */
public abstract class SearchCriteria implements Serializable{
    private static final long SerialVersionUID = 1L;
    
    private String context;
    private LinkFilter filter;
    
    private SecondaryCrawl secondaryCrawl = null;
    
    public SearchCriteria setSecondaryCrawl(SecondaryCrawl crawl) { secondaryCrawl = crawl; return this;}
    public SecondaryCrawl getSecondaryCrawl() { return secondaryCrawl; }
    
    public String getContext() {
        return context;
    }
    
    public LinkFilter getLinkFilter() {
        return filter;
    }
    
    public void setLinkFilter(LinkFilter filter) {
        this.filter = filter;
    }
    
    SearchCriteria(String context) {
        this.context = context;
        this.filter = null;
    }
    
    public String getSearchString() { return ""; }
    public String getBaseUrl() { return ""; }

    public String getNext(Document doc) {
        return null;
    }

    public void process(Document doc, ArrayList<String> searchLinks) {
    }

    @Override
    public String toString() {
        return "[" + context + "]";
    }

    
}
