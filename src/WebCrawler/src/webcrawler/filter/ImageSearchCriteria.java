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

public class ImageSearchCriteria extends SearchCriteria {
    public static final LinkFilter ImageSearchFilter = new LinkFilter() {
         private Pattern pattern; 
            {
                pattern = Pattern.compile("https:\\/\\/[^\"]+?&");
            }
            @Override
            public String operate(String raw) {
                Matcher matcher = pattern.matcher(raw);
                if(matcher.find()) 
                {
                    String res = matcher.group();
                    return res.substring(0, res.length()-1);
                }
                return null;
            }
    };
    
    
    private String fileType;
    public ImageSearchCriteria(String context, String filetype) {
        super(context);

        this.fileType = filetype;
        this.setLinkFilter(ImageSearchCriteria.ImageSearchFilter);
    }

    public String getFileType() { return fileType;}

    @Override
    public String getSearchString() {
        return "/search?q=" + getContext() + "&tbm=isch&ift=" + fileType;
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
    
