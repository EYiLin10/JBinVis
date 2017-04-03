/*
 *  
 */
package webcrawler;

import java.io.Serializable;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Billy
 */
public abstract class SearchCriteria implements Serializable{
    private static final long SerialVersionUID = 1L;
    
    private String context;
    private LinkFilter filter;
    
    public String getContext() {
        return context;
    }
    
    public LinkFilter getLinkFilter() {
        return filter;
    }
    
    public void setLinkFilter(LinkFilter filter) {
        this.filter = filter;
    }
    
    private SearchCriteria(String context) {
        this.context = context;
        this.filter = null;
    }
    
    public String getSearchString() { return ""; }
    public String getBaseUrl() { return ""; }

    public String getNext(Document doc) {
        return null;
    }

    void process(Document doc, ArrayList<String> searchLinks) {
    }
    
    public static class ImageSearchCriteria extends SearchCriteria {
        public ImageSearchCriteria(String context) {
            super(context);
        }
        
        @Override
        public String getSearchString() {
            return "/search?q=" + getContext() + "&tbm=isch";
        }
        
        @Override
        public String getBaseUrl() {
            return "https://www.google.com.sg";
        }
        
        @Override
        public String getNext(Document doc) {
           Elements elem = doc.select("a:has(span):contains(Next)");
            if (elem.size() > 0) {
                return elem.get(0).attr("href");
            }
            return null;
        }
        
    }
    
    public static class DocumentSearchCriteria extends SearchCriteria {
        private String fileType;
        
        public DocumentSearchCriteria(String context, String fileType) {
            super(context);
            this.fileType = fileType;
            
            this.setLinkFilter(new LinkFilter.LinkFilterDefaultImpl("(pdf|ppt|doc|xls|jpg|jpeg|gif|bmp|png|mp3|mp4|dll)"));
        }
        
        public String getFileType() {
            return fileType;
        }
        
        @Override
        public String getNext(Document doc) {
            Elements elem = doc.select("a:has(span):contains(Next)");
            if (elem.size() > 0) {
                return elem.get(0).attr("href");
            }
            return null;
        }
        
        @Override
        public String getSearchString() {
            return "/search?q=" + getContext() + " filetype:" + 
                    getFileType() + "&num=100";
        }
        
        @Override
        public String getBaseUrl() {
            return  "https://www.google.com";
        }
        
        public void process(Document doc, ArrayList<String> searchLinks) {
            LinkFilter filter = getLinkFilter();
            Elements elems = doc.select("a[href]");
            for (Element link : elems) {
                String temp = link.attr("href");
                String filtered = temp;

                if(filter!=null)
                    filtered = filter.operate(temp);

                if(temp.startsWith("/url?") && filtered!=null)
                    searchLinks.add(filtered);
            }
        }
    }
}
