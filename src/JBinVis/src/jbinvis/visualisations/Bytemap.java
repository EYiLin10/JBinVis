/*
 *  
 */
package jbinvis.visualisations;

import com.jogamp.opengl.GL2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedOutputStream;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jbinvis.backend.FileCache;
import jbinvis.frontend.settingspanel.BytemapSettingsPanel;
import jbinvis.frontend.settingspanel.SettingsToLogicInterface;
import jbinvis.main.JBinVis;
import jbinvis.math.Hilbert;
import jbinvis.math.ShannonEntropy;
import jbinvis.renderer.BinVisCanvas;
import jbinvis.renderer.CanvasShader;
import jbinvis.renderer.CanvasTexture;
import jbinvis.renderer.RenderLogic;
import jbinvis.renderer.camera.OrthographicCamera;

/**
 * A simple bytemap visualisation
 *
 * @author Billy
 */
public class Bytemap extends RenderLogic implements jbinvis.main.FileUpdateListener,
        SettingsToLogicInterface<BytemapSettingsPanel>, ActionListener, ChangeListener, KeyListener {

    private CanvasTexture texture = null;
    private CanvasShader shader = null;
    private OrthographicCamera camera = null;
    private BytemapSettingsPanel settingsPanel = null;

    private int halfQuadSize = 256;
    private int centerX = 256, centerY = 256;
    private final JBinVis jbinvis;
    private final int PIXEL_COUNT = 512 * 512;

    private int fillerFunction = 0;
    private int pixelFormat = 0;
    private int scanWidth = 512;
    private int pixelSize = 1;
    private int colourScheme = 0;
    private int windowSize = 32;

    private int uniformPixelSize = -1;

    private final ShannonEntropy entropy;

    public Bytemap() {
        jbinvis = JBinVis.getInstance();
        entropy = new ShannonEntropy();
    }

    @Override
    public void init(GL2 gl) {
        camera = new OrthographicCamera(gl);
        texture = new CanvasTexture(gl, 512, 512);
        shader = new CanvasShader(gl, "texturePassThru");

        uniformPixelSize = shader.getUniformLocation(gl, "u_pixelSize");
    }

    @Override
    public void update(GL2 gl, double delta) {
        camera.update(gl);
    }

    @Override
    public void render(GL2 gl, double delta) {
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        texture.bind(gl);
        shader.begin(gl);
        // set uniform variable
        gl.glUniform1i(uniformPixelSize, pixelSize);

        // render a quad to display texture
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);

        gl.glColor3d(1, 1, 1);
        gl.glTexCoord2d(1, 0);
        gl.glVertex2d(centerX + halfQuadSize, centerY - halfQuadSize);
        gl.glTexCoord2d(1, 1);
        gl.glVertex2d(centerX + halfQuadSize, centerY + halfQuadSize);
        gl.glTexCoord2d(0, 0);
        gl.glVertex2d(centerX - halfQuadSize, centerY - halfQuadSize);
        gl.glTexCoord2d(0, 1);
        gl.glVertex2d(centerX - halfQuadSize, centerY + halfQuadSize);

        gl.glEnd();

        shader.end(gl);
    }

    @Override
    public void resize(int width, int height) {
        if (camera != null) {
            camera.setViewportDimensions(width, height);
        }

        halfQuadSize = Math.min(width, height) / 2;
        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    public void dispose(GL2 gl) {
        shader.dispose(gl);
        texture.dispose(gl);
        jbinvis.removeFileUpdateListener(this);
        detachPanel();
    }

    @Override
    public void fileOffsetUpdated() {
        colorTexture();
    }

    @Override
    public void fileClosed() {
        resetTexture();
    }

    @Override
    public void fileOpened() {
        fileOffsetUpdated();
    }

    /**
     * Reset all pixels to black
     */
    private void resetTexture() {
        for (int i = 0; i < PIXEL_COUNT; i++) {
            texture.setPixel(i & 0x1FF, i >> 9, 0);
        }
    }

    /**
     * Reads the loaded file and colors in the pixels
     */
    private void colorTexture() {
        if (!jbinvis.isLoaded()) {
            return;
        }

        if (fillerFunction == 1) {
            colorTextureHilbert();
        } else {
            colorTextureScanline();
        }
    }

    private void colorTextureHilbert() {
        long offset = jbinvis.getFileOffset();
        int[] value = new int[4];
        int transformed;
        boolean isEOF = false;

        FileCache file = jbinvis.getFile();

        switch (pixelFormat) {
            case 0: // 8bpp

                if (colourScheme != 2) {
                    for (int i = 0; i < PIXEL_COUNT; i++) {
                        transformed = Hilbert.xy(i);
                        value[0] = isEOF ? 0 : file.read(offset + i);

                        if (value[0] < 0) {
                            value[0] = 0;
                            isEOF = true;
                        }
                        texture.setPixel(transformed & 0x1FF, transformed >> 9, convertToColour(value[0]));
                    }
                } else {
                    // entropy
                    // clear the calculator
                    entropy.clear();
                    entropy.setWindowSize(windowSize);

                    double sum = 0;
                    int w2 = windowSize / 2;

                    // prepare window
                    for (int i = -w2 - 1; i < w2 - 1; i++) {
                        value[0] = file.read(offset + i);
                        if (value[0] >= 0) {
                            entropy.addItem(value[0]);
                        }

                    }

                    for (int i = 0; i < PIXEL_COUNT; i++) {
                        transformed = Hilbert.xy(i);

                        value[0] = file.read(offset + i + w2 - 1);
                        if (value[0] < 0) {
                            entropy.removeItem();
                        } else {
                            entropy.addItem(value[0]);
                        }

                        sum = entropy.calculateEntropy();
                        value[0] = (int) sum;
                        texture.setPixel(transformed & 0x1FF, transformed >> 9, 0, value[0], 0);
                    }
                }

                break;

            case 1: // 24 bpp RGB
                for (int i = 0; i < PIXEL_COUNT; i++) {
                    transformed = Hilbert.xy(i);
                    if (isEOF) {
                        value[0] = value[1] = value[2] = 0;
                    } else if (offset + i * 3 + 2 >= jbinvis.getFileSize()) {
                        value[0] = value[1] = value[2] = 0;
                        isEOF = true;
                    } else {
                        value[0] = file.read(offset + i * 3);
                        value[1] = file.read(offset + i * 3 + 1);
                        value[2] = file.read(offset + i * 3 + 2);
                    }
                    texture.setPixel(transformed & 0x1FF, transformed >> 9, value[0], value[1], value[2]);
                }
                break;

            case 2: // 32 bpp ARGB
            case 3: // 32 bpp BGRA
                for (int i = 0; i < PIXEL_COUNT; i++) {
                    transformed = Hilbert.xy(i);
                    if (isEOF) {
                        texture.setPixel(transformed & 0x1FF, transformed >> 9, 0);
                    } else if (offset + i * 4 + 3 >= jbinvis.getFileSize()) {
                        value[0] = value[1] = value[2] = 0;
                        isEOF = true;
                    } else {
                        value[0] = file.read(offset + i * 4);
                        value[1] = file.read(offset + i * 4 + 1);
                        value[2] = file.read(offset + i * 4 + 2);
                        value[3] = file.read(offset + i * 4 + 3);
                    }

                    if (pixelFormat == 2) {
                        // multiply value[0] across the rest
                        value[1] = value[0] * value[1] / 255;
                        value[2] = value[0] * value[2] / 255;
                        value[3] = value[0] * value[3] / 255;
                        texture.setPixel(transformed & 0x1FF, transformed >> 9, value[1], value[2], value[3]);
                    } else {
                        // multiply value[3] across the rest
                        value[0] = value[3] * value[0] / 255;
                        value[1] = value[3] * value[1] / 255;
                        value[2] = value[3] * value[2] / 255;
                        texture.setPixel(transformed & 0x1FF, transformed >> 9, value[2], value[1], value[0]);
                    }
                }
                break;
        }
    }

    private int convertToColour(int value) {
        if (colourScheme == 0) {
            return value << 8;
        } else if (value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z') {
            return 0x69FF65;
        } else if (value >= '0' && value <= '9') {
            return 0xFF0000;
        } else {
            value >>= 1;
            return value | (value << 8) | (value << 16);
        }
    }

    private void colorTextureScanline() {
        long offset = jbinvis.getFileOffset();
        int[] value = new int[4];
        int transformed;
        boolean isEOF = false;

        FileCache file = jbinvis.getFile();

        int tx, ty;

        switch (pixelFormat) {
            case 0: // 8bpp
                if (colourScheme != 2) {
                    // greyscale and character class
                    for (int i = 0; i < PIXEL_COUNT; i++) {
                        tx = i & 0x1FF;
                        ty = i >> 9;
                        if (tx >= scanWidth) {
                            texture.setPixel(tx, ty, 0, 0, 0);
                        } else {
                            value[0] = isEOF ? 0 : file.read(offset++);

                            if (value[0] < 0) {
                                value[0] = 0;
                                isEOF = true;
                            }
                            texture.setPixel(tx, ty, convertToColour(value[0]));
                        }
                    }
                } else {
                    // entropy
                    // clear the calculator
                    entropy.clear();
                    entropy.setWindowSize(windowSize);

                    double sum = 0;
                    int w2 = windowSize / 2;

                    // prepare window
                    for (int i = -w2 - 1; i < w2 - 1; i++) {
                        value[0] = file.read(offset + i);
                        if (value[0] >= 0) {
                            entropy.addItem(value[0]);
                        }

                    }

                    for (int i = 0; i < PIXEL_COUNT; i++) {
                        tx = i & 0x1FF;
                        ty = i >> 9;

                        if (i > 94800) {
                            sum = 0;
                        }

                        if (tx >= scanWidth) {
                            texture.setPixel(tx, ty, 0, 0, 0);
                        } else {
                            value[0] = file.read(offset + w2 - 1);
                            if (value[0] < 0) {
                                entropy.removeItem();
                            } else {
                                entropy.addItem(value[0]);
                            }

                            sum = entropy.calculateEntropy();
                            value[0] = (int) sum;
                            texture.setPixel(tx, ty, 0, value[0], 0);

                            offset++;
                        }
                    }
                }

                break;

            case 1: // 24 bpp RGB
                for (int i = 0; i < PIXEL_COUNT; i++) {
                    tx = i & 0x1FF;
                    ty = i >> 9;
                    if (tx >= scanWidth) {
                        texture.setPixel(tx, ty, 0, 0, 0);
                    } else {
                        if (isEOF) {
                            value[0] = value[1] = value[2] = 0;
                        } else if (offset + 2 >= jbinvis.getFileSize()) {
                            value[0] = value[1] = value[2] = 0;
                            isEOF = true;
                        } else {
                            value[0] = file.read(offset);
                            value[1] = file.read(offset + 1);
                            value[2] = file.read(offset + 2);
                            offset += 3;
                        }
                        texture.setPixel(tx, ty, value[0], value[1], value[2]);
                    }
                }
                break;

            case 2: // 32 bpp ARGB
            case 3: // 32 bpp BGRA
                for (int i = 0; i < PIXEL_COUNT; i++) {
                    tx = i & 0x1FF;
                    ty = i >> 9;
                    if (tx >= scanWidth) {
                        texture.setPixel(tx, ty, 0, 0, 0);
                    } else {
                        if (isEOF) {
                            texture.setPixel(tx, ty, 0);
                        } else if (offset + 4 >= jbinvis.getFileSize()) {
                            value[0] = value[1] = value[2] = 0;
                            isEOF = true;
                        } else {
                            value[0] = file.read(offset);
                            value[1] = file.read(offset + 1);
                            value[2] = file.read(offset + 2);
                            value[3] = file.read(offset + 3);
                            offset += 4;
                        }

                        if (pixelFormat == 2) {
                            // multiply value[0] across the rest
                            value[1] = value[0] * value[1] / 255;
                            value[2] = value[0] * value[2] / 255;
                            value[3] = value[0] * value[3] / 255;
                            texture.setPixel(tx, ty, value[1], value[2], value[3]);
                        } else {
                            // multiply value[3] across the rest
                            value[0] = value[3] * value[0] / 255;
                            value[1] = value[3] * value[1] / 255;
                            value[2] = value[3] * value[2] / 255;
                            texture.setPixel(tx, ty, value[2], value[1], value[0]);
                        }
                    }

                }
                break;
        }
    }

    @Override
    public String getName() {
        return "Bytemap";
    }

    @Override
    public void attachPanel(BytemapSettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;
        settingsPanel.getComboFunction().addActionListener(this);
        settingsPanel.getComboPixelFormat().addActionListener(this);
        settingsPanel.getComboColourScheme().addActionListener(this);
        settingsPanel.getSpinnerPixelSize().addChangeListener(this);
        settingsPanel.getSpinnerScanwidth().addChangeListener(this);

        settingsPanel.getSpinnerPixelSize().addKeyListener(this);
        settingsPanel.getSpinnerScanwidth().addKeyListener(this);

        // make sure values are correct
        settingsPanel.getComboColourScheme().setSelectedIndex(colourScheme);
        settingsPanel.getComboFunction().setSelectedIndex(fillerFunction);
        settingsPanel.getComboPixelFormat().setSelectedIndex(pixelFormat);
        settingsPanel.getSpinnerPixelSize().setValue(pixelSize);
        settingsPanel.getSpinnerScanwidth().setValue(scanWidth);
    }

    @Override
    public void detachPanel() {
        settingsPanel.getComboFunction().removeActionListener(this);
        settingsPanel.getComboPixelFormat().removeActionListener(this);
        settingsPanel.getComboColourScheme().removeActionListener(this);
        settingsPanel.getSpinnerPixelSize().removeChangeListener(this);
        settingsPanel.getSpinnerScanwidth().removeChangeListener(this);
        settingsPanel.getSpinnerPixelSize().removeKeyListener(this);
        settingsPanel.getSpinnerScanwidth().removeKeyListener(this);

        this.settingsPanel = null;
    }

    // EVENT HANDLER FROM SETTINGS PANEL
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> obj;
        int temp;
        if (e.getSource() == settingsPanel.getComboFunction()) {
            // space filling function changed
            fillerFunction = settingsPanel.getComboFunction().getSelectedIndex();
        } else if (e.getSource() == settingsPanel.getComboPixelFormat()) {
            // pixel format changed
            pixelFormat = settingsPanel.getComboPixelFormat().getSelectedIndex();
        } else if (e.getSource() == settingsPanel.getComboColourScheme()) {
            // colour scheme changed
            colourScheme = settingsPanel.getComboColourScheme().getSelectedIndex();
        }

        validateStates();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object temp = null;
        if (e.getSource() == settingsPanel.getSpinnerPixelSize()) {
            temp = settingsPanel.getSpinnerPixelSize().getValue();
        } else if (e.getSource() == settingsPanel.getSpinnerScanwidth()) {
            temp = settingsPanel.getSpinnerScanwidth().getValue();
        }

        if (temp instanceof Integer) {
            int value;
            if (e.getSource() == settingsPanel.getSpinnerPixelSize()) {
                value = (int) temp;

                // ensure pixel size is bounded and is a power of two
                if (value > 32) {
                    pixelSize = 32;
                } else if (value < 0) {
                    pixelSize = 0;
                } else if (isPowerOfTwo(value)) {
                    pixelSize = value;
                }

                settingsPanel.getSpinnerPixelSize().setValue(pixelSize);
            } else if (e.getSource() == settingsPanel.getSpinnerScanwidth()) {
                value = (int) temp;

                // ensure scanwidth is bounded
                if (value < 1) {
                    scanWidth = 1;
                }
                if (value > 512) {
                    scanWidth = 512;
                } else {
                    scanWidth = value;
                }
                settingsPanel.getSpinnerScanwidth().setValue(scanWidth);
            }
        }
        validateStates();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == settingsPanel.getSpinnerPixelSize()
                || e.getSource() == settingsPanel.getSpinnerScanwidth()) {

            ChangeEvent ev = new ChangeEvent(e.getSource());
            stateChanged(ev);
        }
        else
        {
            if(e.getKeyChar() == 'd') {
            // DEBUG
            System.out.println("Dumping texture values");
            try {
                java.io.FileWriter writer = new java.io.FileWriter("dump.txt");
                int x;
                for (int i = 0; i < 512 * 512; i++) {
                    x = texture.getPixel(i % 512, i / 512);
                    writer.write((x >> 8) + "\n");
                }
                writer.close();
            } catch (Exception exp) {
                }
            }
        }

    }

    private void validateStates() {
        // scanwidth is only available if in scanline mode
        // and pixel size is 1
        settingsPanel.getSpinnerScanwidth().setEnabled(
                settingsPanel.getComboFunction().getSelectedIndex() == 0
                && settingsPanel.getSpinnerPixelSize().getValue().equals(1)
        );

        // colour scheme is only available if pixel format is 8bpp
        settingsPanel.getComboColourScheme().setEnabled(
                settingsPanel.getComboPixelFormat().getSelectedIndex() == 0
        );

        colorTexture();
    }

    private boolean isPowerOfTwo(int x) {
        while ((x & 1) == 0 && x > 1) {
            x >>= 1;
        }
        return (x == 1);
    }

    @Override
    public void onAttachToCanvas(BinVisCanvas canvas) {
        jbinvis.addFileUpdateListener(this);
        colorTexture();
    }

    @Override
    public void onUnattachFromCanvas(BinVisCanvas canvas) {
        jbinvis.removeFileUpdateListener(this);
    }

}
