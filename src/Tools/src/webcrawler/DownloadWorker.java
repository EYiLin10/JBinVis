/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;

import java.util.ArrayList;

/**
 *
 * @author ONGB0024
 */
public class DownloadWorker implements Runnable {
    private ArrayList<String> links;
  
    private int offset, increment;
    public DownloadWorker(ArrayList<String> links, int offset, int increment) {
        this.offset = offset;
        this.increment = increment;
        this.links = links;
    }
    @Override
    public void run() {
        Downloader download = new Downloader("downloads");
        while(offset < links.size())
        {
            System.out.println("Downloading " + (offset+1) + "/" + links.size());
            download.download(links.get(offset));
            offset += increment;
        }
    }
    
}
