/*
 *  
 */
package webcrawler.filter;

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
public class SecondaryImageCrawl extends SecondaryCrawl {
    public static final LinkFilter SecondaryImageFilter = new LinkFilter() {
        private Pattern pattern; 
        private final String ext = "(png|jpg|jpeg|gif|tiff|bmp)";
        {
            pattern = Pattern.compile("https?:\\/\\/.+?\\."+ext);
        }
        @Override
        public String operate(String raw) {
            Matcher matcher = pattern.matcher(raw);
            if(matcher.find()) 
                return matcher.group();
            return null;
        }

    };
    
    public SecondaryImageCrawl(int targetCount) {
        super(targetCount);
        this.setLinkFilter(SecondaryImageCrawl.SecondaryImageFilter);
    }
    
    @Override
    protected void process(Document doc, ArrayList<String> results) {
        Elements elems = doc.select("img[src]");
        for(Element link : elems) {
            String temp = link.attr("src");
            String filtered = filter.operate(temp);
            if(filtered != null) {
                results.add(filtered);
            }
        }
    }
}
