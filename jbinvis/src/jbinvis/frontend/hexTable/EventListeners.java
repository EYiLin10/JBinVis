package jbinvis.frontend.hexTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;

import jbinvis.frontend.HexTableFrame;
import jbinvis.frontend.MainFrame;
import jbinvis.main.JBinVis;

/**
 * @author YiLin
 * Listen for changes in HexFrameTable
 */
public class EventListeners {

	// SINGLETON //
	private JBinVis _singleton = null;
	private MainFrame _mainframe = null;
	HexTableFrame gui = null;
	GuiTransformer gTransformer = null;
	
	private String oldStr = null, newStr = null;

	public EventListeners(String whoCalled) {
		
		if ( _singleton == null )
    		_singleton = JBinVis.getInstance();
		if (_mainframe == null)
			_mainframe = MainFrame.getInstance();
		if (gui == null)
			gui = HexTableFrame.getInstance();
		if (gTransformer == null)
			gTransformer = HexTableFrame.gTransformer;
	
		switch (whoCalled) {
//			case "update":
//				createUpdateListener();
//				break;
			case "save":
				createSaveListener();
				break;
			case "read":
				//System.out.println("read detected");
				createReadListener();
				break;
			default:
				//System.out.println("Invalid listener!");
				//System.out.println(whoCalled);
		}
	}
	
	public EventListeners(JFormattedTextField tf) {
		if ( _singleton == null )
    		JBinVis.getInstance(); 
		if (gTransformer == null)
			gTransformer = gui.gTransformer;
		createTextFieldListener(tf);	
	}
	
	private void createTextFieldListener(JFormattedTextField tf) {
		tf.addFocusListener(new FocusAdapter() {
			@Override
            public void focusGained(FocusEvent e) {
                tf.selectAll();
                oldStr = tf.getText();
            }
			@Override
			public void focusLost(FocusEvent e) {
				newStr = tf.getText();
				if (!oldStr.equals(newStr)) {
					//CommonOutputs.debug("Typed: " + tf.getText());
					gui.hexChanged++;
					updateHexRead();
					updateBytesRead();
				}
				clearOldAndNewStr();
				
			}
		});
	}
	
//	private void createUpdateListener() {
//		gui.updateButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if (_mainframe.fileName == null || gui.hexRead == null)
//				//if (SphereSettingsPanel.hexRead == null)
//					System.out.printf("There is nothing to visualize!");
//				else {
//					if (gui.hexChanged > 0) {
//						updateHexRead();
//						updateBytesRead();
//					}
//					gTransformer.updateDisplay(gui.hexRead, "S");
//				}
//			}
//		});
//		
//	}
	
	private void createSaveListener() {
		gui.saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				if (_mainframe.fileName == null)
//					System.out.printf("A file must be read before it can be saved!");
//				if (SphereSettingsPanel.overwrite) gTransformer.saveEditor();
//				else {
				
				String curName = _mainframe.fileName;
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				String[] curNameArr = curName.split(Pattern.quote("."));		// split into oldName + {extension}
				String suggestedName = curNameArr[0] + "_new." + curNameArr[1];
				fileChooser.setSelectedFile(new File(suggestedName));			// old name + _new + {extension}
				
				int rVal = fileChooser.showSaveDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					_mainframe.fileName = fileChooser.getSelectedFile().toString();
					gTransformer.saveEditor();
					_mainframe.reloadWhenUpdate(fileChooser.getSelectedFile().toString());
					}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					_mainframe.fileName = curName;
				}
//				}
			}
		});
	}
	
	private void clearOldAndNewStr() {
		oldStr = null;
		newStr = null;
	}
	
	private void updateHexRead() {
		JTable model = HexTableFrame.gui.editorTable;
		HexTableFrame.gui.hexRead = "";
		for (int r = 0; r < model.getRowCount(); r++) {
			for (int c = 0; c < model.getColumnCount(); c++) {
				HexTableFrame.gui.hexRead += model.getValueAt(r, c);
			}
		}
		HexTableFrame.gui.hexRead = HexTableFrame.gui.hexRead.trim();
		
		System.out.println("Updated Hex String: " + HexTableFrame.gui.hexRead );
	}
	
	private void updateBytesRead() {
		HexTableFrame.gui.bytesRead = gTransformer.hexStringToByteArray(HexTableFrame.gui.hexRead);
	}
	
	public void createReadListener() {
		
		//_mainframe.menuSphere.addActionListener(new java.awt.event.ActionListener() {
		//gui.readButton.addActionListener(new ActionListener() {
			//@Override
			//public void actionPerformed(ActionEvent e) {
//				JFileChooser fileChooser = new JFileChooser();
//				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//				fileChooser.setAcceptAllFileFilterUsed(false);
//				int rVal = fileChooser.showOpenDialog(null);
//				if (rVal == JFileChooser.APPROVE_OPTION) {
//					gTransformer.clearTable();
//					gui.hexRead = "";
//					gui.hexChanged = 0;
//					gui.fileName = fileChooser.getSelectedFile().toString();
			
		gui.hexChanged = 0;
		
		HexEditorTableModel model = (HexEditorTableModel) HexTableFrame.gui.editorTable.getModel();
		model.setRowCount(0);	// delete all
		ReadBackgroundWorker bw = new ReadBackgroundWorker(_mainframe.fileName, model);
		bw.execute();
					/*
					 * read and display all at once rather than row by row
					try {
						FileInputStream fis = new FileInputStream(gui.fileName);
						byte[] bytes = new byte[16];
						int len;
						MyTableModel model = (MyTableModel) gui.editorTable.getModel();
						model.setRowCount(0);	// delete all
						do {
							len = fis.read(bytes);
							for (int i = 0; i < len; i++) {
								String tmp = String.format("%02X", bytes[i]);	// read file into hex
								gui.hexRead += tmp;
							}
						} while (len != -1);
						fis.close();
						gTransformer.updateEditor(gui.hexRead);
						//cube = getPolarCoordinates(toDecimalFromHex(binariesRead));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					*/
					//updateDisplay();
					//TODO
				//}
//				if (rVal == JFileChooser.CANCEL_OPTION) {
//					_mainframe.fileName = null;
//				}
			//}
		//});
	}
	
}
