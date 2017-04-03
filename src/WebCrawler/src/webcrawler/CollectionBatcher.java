/*
 *  
 */
package webcrawler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Automates the collection of links
 * @author Billy
 */
public class CollectionBatcher {
    
    private LinkedList<SearchCriteria> queuedQueries;
    private ArrayList<String> allLinks;
    private int targetSearchCount;
    
    private CollectionBatcherHandler handler = null;
    
    public void setHandler(CollectionBatcherHandler handler) {
        this.handler = handler;
    }
   
    public CollectionBatcher(int targetCount) {
        targetSearchCount = targetCount;
        queuedQueries = new LinkedList<SearchCriteria>();
    }
    
    /**
     * To support resuming from the last batch collection
     * @param state 
     */
    public CollectionBatcher(CollectionBatcherState state) {
        queuedQueries = state.queue;
        targetSearchCount = state.targetSearchCount;
    }
    
    public void addQuery(SearchCriteria query) {
        queuedQueries.add(query);
    }
    
    public void execute() {
        allLinks = new ArrayList<String>();
        LinksCollector collector;
        while(!queuedQueries.isEmpty()) {
            SearchCriteria curQuery = queuedQueries.element();
            
            System.out.println("Query: " + curQuery);
            
            collector = new LinksCollector(curQuery, targetSearchCount);            
            try {
                collector.start();
            }
            catch(IOException e) {
                if(handler!=null) {
                    // save the state
                    CollectionBatcherState state = new CollectionBatcherState();
                    state.queue = queuedQueries;
                    state.targetSearchCount = targetSearchCount;
                    state.allLinks = allLinks;
                    
                    handler.onCollectionError(state, e);
                }
                else
                    e.printStackTrace();
                break;
            }
            
            // successful crawl. save all the links
            allLinks.addAll(collector.getLinks());
            
            // remove this query
            queuedQueries.remove();
        }
    }
    
    public ArrayList<String> getLinks() {
        return allLinks;
    }
    
    public static class CollectionBatcherState implements Serializable {
        private static final long serialVersionUID = 1L;
 
        public LinkedList<SearchCriteria> queue;
        public ArrayList<String> allLinks;
        public int targetSearchCount;
        
    }
    
    public static interface CollectionBatcherHandler {
        void onCollectionError(CollectionBatcherState savedState, Throwable cause);
    }
}
