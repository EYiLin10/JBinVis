package jbinvis.frontend.hexTable;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MaskFormatter;

import jbinvis.frontend.HexTableFrame;
import jbinvis.frontend.MainFrame;
import jbinvis.main.JBinVis;
import jbinvis.visualisations.Bytemap;
import jbinvis.visualisations.Digraph;
import jbinvis.visualisations.FrequencyHistogram;
import jbinvis.visualisations.Sphere;
import jbinvis.visualisations.Trigraph;

/**
 * @author YiLin
 * Updates the Hex Editor whenever there are changes
 */
public class GuiTransformer  {
	
	// SINGLETON //
	private JBinVis _singleton = null;
	private MainFrame _mainframe = null;
	HexTableFrame gui = null;
      
    public GuiTransformer() {
    	
    	if ( _singleton == null )
    		_singleton = JBinVis.getInstance();
		if (_mainframe == null)
			_mainframe = MainFrame.getInstance();
		if (gui == null)
			gui = HexTableFrame.getInstance();
		
    }
    
    public void clearTable() {
		gui.editorTable.setModel(new HexEditorTableModel());
		//int width = (int) (gui.scrollPane.getWidth() * 0.48)/gui.panelCols;
		int width = 20; 	
		gui.editorTable.setSize(new Dimension(width*gui.panelCols, width*gui.panelRows));
		gui.editorTable.setRowHeight(width);								// set row height
		for (int i = 0; i < gui.panelCols; i++) {
			gui.editorTable.getColumnModel().getColumn(i).setWidth(width);	// set column width			
		}
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		gui.editorTable.setDefaultRenderer(Object.class, centerRenderer);	// set cell alignment to center
		gui.editorTable.setTableHeader(null);	// remove column headers as we don't need them
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter("HH");	// only allow hexadecimal
		} catch (Exception e) {
			// TODO
		}
		JFormattedTextField tf = new JFormattedTextField(formatter);
		new EventListeners(tf);
		for (int i = 0; i < gui.panelCols; i++) {
			gui.editorTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(tf));
		}
	}
    
    private String[] getBinariesArray(String binariesRead) {
		String[] binariesArr = binariesRead.split("(?<=\\G.{2})");
		return binariesArr;
	}
    
    public void updateEditor(String lines) {
		int rows = (int) Math.ceil(lines.length()/2.0/16.0);
		int cols = gui.panelCols;
		int i = 0;
		String[] binariesArr = getBinariesArray(lines);

		HexEditorTableModel model = (HexEditorTableModel) gui.editorTable.getModel();
		//DefaultTableModel model = (DefaultTableModel) gui.editorTable.getModel();
		for (int r = 0; r < rows; r++) {
			Vector<String> rowData = new Vector<String>();
			for (int c = 0; c < cols; c++) {
				try {
					rowData.add(binariesArr[i++]);	// dynamically populate and add rows
				} catch (Exception e) {
					rowData.add("  ");	// sometimes we don't have a full row
				}
			}
			model.addRow(rowData);
		}
	}
    
    public Vector<String> updateEditorRowByRow(String line) {
		int cols = gui.panelCols;
		int i = 0;
		String[] binariesArr = getBinariesArray(line);
		Vector<String> rowData = new Vector<String>();
		for (int c = 0; c < cols; c++) {
			try {
				rowData.add(binariesArr[i++]);	// dynamically populate and add rows
				//System.out.print(rowData.get(c) + " ");				
			} catch (Exception e) {
				rowData.add("  ");	// sometimes we don't have a full row
			}
		}
		return rowData;
	}
    
    //implement save edits on the hex table
    public void saveEditor() {
		SaveBackgroundWorker sbw = new SaveBackgroundWorker(_mainframe.fileName);
		sbw.execute();
	}
    
    //update display after changes have been done to the hex table
    public void updateDisplay(String hexStr, String type) {
		
		switch(type) {
			case "B":
				new Bytemap();
				break;
			case "D":
				new Digraph();
				break;
			case "F":
				new FrequencyHistogram();
				break;
			case "T":
				new Trigraph();
				break;
			case "S":
				//System.out.println("x");				
				break;
			default:
				System.out.println("Invalid visualization type!");
		}
	}
    
    public byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
    
}
