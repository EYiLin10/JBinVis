/*
 *  
 */
package webcrawler.filter;

import webcrawler.filter.LinkFilter;
import webcrawler.filter.SearchCriteria;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Billy
 */

public class DocumentSearchCriteria extends SearchCriteria {
    
    public static final LinkFilter DocumentSearchFilter = new LinkFilter() {
        private Pattern pattern;
        private final String ext = "(pdf|ppt|doc|xls|jpg|jpeg|gif|bmp|png|mp3|mp4|dll)";
        {
            pattern = Pattern.compile("https?:[^:]+?\\."+ext);
        }

        @Override
        public String operate(String raw) {
            Matcher m = pattern.matcher(raw);
            if (m.find()) {
                return m.group();
            } else {
                return null;
            }
        }
    };
    
    private String fileType;

    public DocumentSearchCriteria(String context, String fileType) {
        super(context);
        this.fileType = fileType;

        this.setLinkFilter(DocumentSearchCriteria.DocumentSearchFilter);
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

    @Override
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

    @Override
    public String toString() {
        return super.toString() + " [file:" + fileType + "]";
    }
    
    
}
