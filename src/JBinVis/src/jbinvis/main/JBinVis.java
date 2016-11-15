/*
 *  
 */
package jbinvis.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import jbinvis.renderer.BinVisCanvas;

/**
 *
 * @author Billy
 */
public class JBinVis {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        BinVisCanvas canvas = BinVisCanvas.create(frame);
        frame.setSize(canvas.getSize());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                frame.dispose();
            }
        });
        frame.setVisible(true);
    }
    
}
