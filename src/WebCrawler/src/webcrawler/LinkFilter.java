/*
 *  
 */
package webcrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Billy
 */
public abstract class LinkFilter {
    /**
     * Operates on the given string to extract the appropriate link. If 
     * unsuccessful, a null string is returned.
     * @param raw
     * @return 
     */
    public abstract String operate(String raw);
    
    public static class LinkFilterDefaultImpl extends LinkFilter {
        private Pattern pattern;
        
        public LinkFilterDefaultImpl(String ext) 
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
    }
}
