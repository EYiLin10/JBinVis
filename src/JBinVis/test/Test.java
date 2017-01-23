
import com.jogamp.opengl.GL2;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jbinvis.renderer.BinVisCanvas;
import jbinvis.renderer.CanvasTexture;
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

        private CanvasTexture tex = null;
        boolean flag = true;
        double countdown = 1;

        @Override
        public void render(GL2 gl, double delta) {
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glDisable(GL2.GL_LIGHTING);
            tex.bind(gl);

            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-0.5, 0.5, 0);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0.5, 0.5, 0);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(0.5, -0.5, 0);

            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-0.5, 0.5, 0);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(0.5, -0.5, 0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.5, -0.5, 0);
            gl.glEnd();
        }

        @Override
        public void update(GL2 gl, double delta) {
            countdown -= delta;
            if (countdown <= 0) {
                countdown = 1;

                flag = !flag;
                byte a = (byte)0xFF ,b=(byte)0;
                int c,d;
                if (flag) {
                    for(int i=0;i<16;i++) {
                        c = (i >> 2) & 1;
                        d = i & 1;
                        if( (c^d) == 0) 
                            tex.setPixel(i%4, i/4, a, a, a);
                        else
                            tex.setPixel(i%4, i/4, b,b, b);
                    }
                } else {
                    for(int i=0;i<16;i++) {
                        c = (i >> 2) & 1;
                        d = i & 1;
                        if( (c^d) == 1) 
                            tex.setPixel(i%4, i/4, a, a, a);
                        else
                            tex.setPixel(i%4, i/4, b,b, b);
                    }
                }

            }
        }

        @Override
        public void dispose(GL2 gl) {
            tex.dispose(gl);
            System.out.println("Disposed!");
        }

        @Override
        public void init(GL2 gl) {
            tex = CanvasTexture.create2D(gl, 4, 4);
            byte a = (byte) 0xff;

            tex.setPixel(0, 0, a, a, a);
            tex.setPixel(2, 0, a, a, a);
            tex.setPixel(0, 2, a, a, a);
            tex.setPixel(2, 2, a, a, a);

            tex.setPixel(1, 1, a, a, a);
            tex.setPixel(3, 1, a, a, a);
            tex.setPixel(1, 3, a, a, a);
            tex.setPixel(3, 3, a, a, a);
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
