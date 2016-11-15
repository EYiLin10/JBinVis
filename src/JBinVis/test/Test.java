
import com.jogamp.opengl.GL2;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import jbinvis.renderer.BinVisCanvas;
import jbinvis.renderer.RenderLogic;

/*
 *  
 */

/**
 *
 * @author Billy
 */
public class Test {

    public static class RenderTriangle extends RenderLogic {
        private boolean disposed = false;
        
        @Override
        public void render(GL2 gl, double delta) {
            gl.glClearColor(0,0,0,1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
            gl.glBegin(GL2.GL_TRIANGLES);
                gl.glColor3d(1,0,0);
                gl.glVertex3d(0, 0.5, 0);
                gl.glColor3d(0,1,0);
                gl.glVertex3d(0.5, -0.5, 0);
                gl.glColor3d(0,0,1);
                gl.glVertex3d(-0.5,-0.5, 0);
            gl.glEnd();
        }
        
        @Override
        public void dispose() {
            System.out.println("Disposed!");
        }

    }
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        BinVisCanvas canvas = BinVisCanvas.create(frame);
        RenderTriangle a = new RenderTriangle();
        canvas.setRenderLogic(a);
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
