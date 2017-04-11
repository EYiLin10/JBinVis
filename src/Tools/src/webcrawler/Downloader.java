/*
 *  
 */
package webcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Billy
 */
public class Downloader {
    private String folder;
    private MessageDigest digest;
    
    public String getDestinationFolder () { return folder;}
    
    public Downloader(String folder) {
        this.folder = folder;
        mkdir(folder);
        try {
        digest = MessageDigest.getInstance("MD5");
        }
        catch(Exception e) 
        {
            digest = null;
        }
    }
    
    private void mkdir(String path) {
        File f = new File(path);
        if(!f.exists())
            f.mkdirs();
        
    }
    
    public boolean download(String link){
        URL url = null;
        
        try {
            url = new URL(link);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // create output file
        String filepath = getFilePath(link);
        File f = new File(filepath);
        try {
            FileUtils.copyURLToFile(url, f, 5000,5000);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    private void close(java.io.Closeable c) {
        try {
            c.close();
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getFilePath(String link) {
        int index = link.lastIndexOf(".");
        String ext = link.substring(index+1, link.length());
        
        try {
            digest.update(link.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        byte[] rawhash = digest.digest();
        String hash = "";
        for(int i=0;i<rawhash.length;i++) {
            hash += String.format("%02x", rawhash[i]);
        }
        
        String folders = this.folder + "/" + ext + "/";
        mkdir(folders);
        
        return folders + hash + "." + ext;
    }
}
