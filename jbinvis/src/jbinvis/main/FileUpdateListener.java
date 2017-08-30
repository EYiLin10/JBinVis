package jbinvis.main;

/**
 * To listen for updates to the file offset
 * @author Billy
 */
public interface FileUpdateListener {
    void fileOffsetUpdated();
    void fileClosed();
    void fileOpened();
}
