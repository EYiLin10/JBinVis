/*
 *  
 */
package jbinvis.frontend;

import jbinvis.frontend.settingspanel.BytemapSettingsPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jbinvis.main.FileUpdateListener;
import jbinvis.main.JBinVis;
import jbinvis.renderer.BinVisCanvas;
import jbinvis.visualisations.Bytemap;

/**
 *
 * @author Billy
 */
public class MainFrame extends javax.swing.JFrame implements FileUpdateListener,
        ChangeListener
    {
    
    private BinVisCanvas canvas = null;
    private final JBinVis jbinvis;
    private BytemapSettingsPanel bytemapConfigPanel;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        jbinvis = JBinVis.getInstance();
        jbinvis.addFileUpdateListener(this);
        
        canvas = BinVisCanvas.create(panelCanvas);

        menuCloseFile.setEnabled(false);

        textOffset.setEnabled(false);
        sliderOffset.setEnabled(false);
        
        sliderOffset.addChangeListener(this);
        
        // initialise config panels
        bytemapConfigPanel = new BytemapSettingsPanel();
        ((Bytemap)RenderLogicHolder.fromId(RenderLogicHolder.RL_BYTEMAP)).attachPanel(bytemapConfigPanel);
        
        // default to bytemap in the beginning
        switchVisualisation(RenderLogicHolder.RL_BYTEMAP);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCanvas = new javax.swing.JPanel();
        panelSidebar = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        labelVisualisationName = new javax.swing.JLabel();
        panelConfigPane = new javax.swing.JPanel();
        panelOffset = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        textOffset = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        labelFileSize = new javax.swing.JLabel();
        sliderOffset = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuOpenFile = new javax.swing.JMenuItem();
        menuCloseFile = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuClose = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuBytemap = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(640, 640));
        setPreferredSize(new java.awt.Dimension(640, 640));
        setSize(new java.awt.Dimension(640, 640));

        panelCanvas.setBackground(new java.awt.Color(153, 153, 153));
        panelCanvas.setLayout(new java.awt.BorderLayout());

        panelSidebar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelSidebar.setPreferredSize(new java.awt.Dimension(300, 460));
        panelSidebar.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(296, 32));
        jPanel1.setLayout(new java.awt.BorderLayout());

        labelVisualisationName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel1.add(labelVisualisationName, java.awt.BorderLayout.CENTER);

        panelSidebar.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        panelConfigPane.setLayout(new java.awt.BorderLayout());
        panelSidebar.add(panelConfigPane, java.awt.BorderLayout.CENTER);

        panelCanvas.add(panelSidebar, java.awt.BorderLayout.LINE_START);

        panelOffset.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelOffset.setAlignmentY(0.0F);
        panelOffset.setMinimumSize(new java.awt.Dimension(100, 40));
        panelOffset.setName(""); // NOI18N
        panelOffset.setPreferredSize(new java.awt.Dimension(296, 50));

        jLabel2.setText("Offset");
        jLabel2.setToolTipText("");

        textOffset.setText("0");
        textOffset.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textOffsetFocusLost(evt);
            }
        });
        textOffset.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textOffsetKeyPressed(evt);
            }
        });

        jLabel3.setText("/");

        labelFileSize.setText("  ");

        sliderOffset.setValue(0);

        javax.swing.GroupLayout panelOffsetLayout = new javax.swing.GroupLayout(panelOffset);
        panelOffset.setLayout(panelOffsetLayout);
        panelOffsetLayout.setHorizontalGroup(
            panelOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOffsetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textOffset, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFileSize, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderOffset, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelOffsetLayout.setVerticalGroup(
            panelOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOffsetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(textOffset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(labelFileSize))
                    .addComponent(sliderOffset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCanvas.add(panelOffset, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelCanvas, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        menuOpenFile.setText("Open File");
        menuOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenFileActionPerformed(evt);
            }
        });
        jMenu1.add(menuOpenFile);

        menuCloseFile.setText("Close File");
        menuCloseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCloseFileActionPerformed(evt);
            }
        });
        jMenu1.add(menuCloseFile);
        jMenu1.add(jSeparator1);

        menuClose.setText("Exit");
        menuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCloseActionPerformed(evt);
            }
        });
        jMenu1.add(menuClose);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("View");

        menuBytemap.setText("Bytemap");
        menuBytemap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBytemapActionPerformed(evt);
            }
        });
        jMenu2.add(menuBytemap);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_menuCloseActionPerformed

    private void menuOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenFileActionPerformed
        // prompt user for file
        final JFileChooser fc = new JFileChooser();
        int retval = fc.showOpenDialog(this);
        
        if(retval == JFileChooser.APPROVE_OPTION) {
            jbinvis.loadFile(fc.getSelectedFile().getAbsolutePath());
            menuCloseFile.setEnabled(true);
        } 
    }//GEN-LAST:event_menuOpenFileActionPerformed

    private void menuCloseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCloseFileActionPerformed
        jbinvis.closeFile();
        menuCloseFile.setEnabled(false);
    }//GEN-LAST:event_menuCloseFileActionPerformed

    private void menuBytemapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBytemapActionPerformed
        switchVisualisation(RenderLogicHolder.RL_BYTEMAP);
    }//GEN-LAST:event_menuBytemapActionPerformed

    private boolean updateOffsetFromText = false;
    private void textOffsetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textOffsetFocusLost
        if(updateOffsetFromText) {
            // validate the value of text box
            String value = textOffset.getText();
            
            long offset = 0;
            try {
                offset = Long.parseLong(value);
                jbinvis.setFileOffset(offset);
            }
            catch(NumberFormatException e) {
                MsgBox("Offset value is incorrectly formatted", "Error");
            }
            
            textOffset.setText(Long.toString(jbinvis.getFileOffset()));
            updateOffsetFromText = false;
        }
    }//GEN-LAST:event_textOffsetFocusLost

    private void textOffsetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textOffsetKeyPressed
        // blur this component upon pressing escape or enter
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) 
        {
            updateOffsetFromText = false;
            textOffset.setText(Long.toString(jbinvis.getFileOffset()));
            this.requestFocus();
        }
        else if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateOffsetFromText = true;
            this.requestFocus();
        }
    }//GEN-LAST:event_textOffsetKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel labelFileSize;
    private javax.swing.JLabel labelVisualisationName;
    private javax.swing.JMenuItem menuBytemap;
    private javax.swing.JMenuItem menuClose;
    private javax.swing.JMenuItem menuCloseFile;
    private javax.swing.JMenuItem menuOpenFile;
    private javax.swing.JPanel panelCanvas;
    private javax.swing.JPanel panelConfigPane;
    private javax.swing.JPanel panelOffset;
    private javax.swing.JPanel panelSidebar;
    private javax.swing.JSlider sliderOffset;
    private javax.swing.JTextField textOffset;
    // End of variables declaration//GEN-END:variables

    // to prevent mutual recursion
    private boolean ignoreStateChanged = false, ignoreOffsetUpdated = false;
    
    @Override
    public void stateChanged(ChangeEvent e) {
        
        if(e.getSource() == sliderOffset) {
            if(!jbinvis.isLoaded())
                return;
            
            if(ignoreStateChanged)
                ignoreStateChanged = false;
            else {
                double percentage = (double)sliderOffset.getValue() / sliderOffset.getMaximum();
                ignoreOffsetUpdated = true;
                jbinvis.setFileOffset((int)(percentage * jbinvis.getFileSize()));
            }
        }
    }

    
    @Override
    public void fileOffsetUpdated() {
        textOffset.setText(Long.toString(jbinvis.getFileOffset()));

        if(ignoreOffsetUpdated)
            ignoreOffsetUpdated = false;
        else {
            // update slider
            double percentage = (double)jbinvis.getFileOffset() / jbinvis.getFileSize();
            ignoreStateChanged = true;
            sliderOffset.setValue((int)(percentage * sliderOffset.getMaximum()));
        }
    }

    @Override
    public void fileClosed() {
        sliderOffset.setEnabled(false);
        textOffset.setEnabled(false);
        labelFileSize.setText("");
        
        setSettingsPanelEnabled(false);
    }

    @Override
    public void fileOpened() {
        sliderOffset.setEnabled(true);
        textOffset.setEnabled(true);
        
        textOffset.setText("0");
        labelFileSize.setText(Long.toString(jbinvis.getFileSize()));
        
        setSettingsPanelEnabled(true);
    }

    
    /**
     * Helper method to show dialog message box
     */
    private void MsgBox(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Helper method to switch visualisations
     * @param index Index of visualisation defined by the RL_* constants in RenderLogicHolder
     */
    private void switchVisualisation(int index) {
        panelConfigPane.removeAll();
        
        switch(index) {
            case RenderLogicHolder.RL_BYTEMAP:
                this.panelConfigPane.add(bytemapConfigPanel, BorderLayout.CENTER);       
                this.canvas.setRenderLogic(RenderLogicHolder.getInstance().get(RenderLogicHolder.RL_BYTEMAP));
                break;
        }
        
        panelConfigPane.validate();
        
        // settings panel should not be enabled if there is no file opened
        setSettingsPanelEnabled(jbinvis.isLoaded());
        
        // display the visualisation name
        if(canvas.getRenderLogic()!=null)
            labelVisualisationName.setText(canvas.getRenderLogic().getName());
        else
            labelVisualisationName.setText("");
    }
    
    /**
     * Helper function to enable or disable the settings pane
     */
    private void setSettingsPanelEnabled(boolean enabled) {
        Component obj = panelConfigPane.getComponent(0);
        if(obj instanceof QuickEnable) {
            if(enabled)
                ((QuickEnable)obj).enableAll();
            else
                ((QuickEnable)obj).disableAll();
        }
        else
            System.out.println("Warning: Settings panel " + obj.getClass().getName() + 
                    " does not implement QuickEnable.");
    }

 
}
