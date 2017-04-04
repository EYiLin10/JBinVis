/*
 *  
 */
package webcrawler.filter;

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
    
}
