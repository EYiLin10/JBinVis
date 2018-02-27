/**
 * 
 */
package jbinvis.frontend.hexTable;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingWorker;

import jbinvis.frontend.HexTableFrame;
import jbinvis.frontend.MainFrame;
import jbinvis.main.JBinVis;

/**
 * @author YiLin
 *
 */
public class ReadBackgroundWorker extends SwingWorker<List<Vector<String>>, Vector<String>> {
	
	private JBinVis _singleton = null;
	private MainFrame _mainframe = null;
	HexTableFrame gui = null;
	GuiTransformer gTransformer = null;
	
	private String fileName = null;
	private HexEditorTableModel model = null;
	private List<Byte> byteList = new ArrayList<Byte>();

	public ReadBackgroundWorker(String fileName, HexEditorTableModel model) {
		
		this._singleton = JBinVis.getInstance();
		this._mainframe = MainFrame.getInstance();
		this.gui = HexTableFrame.getInstance();
		this.gTransformer = gui.gTransformer;
		
		this.fileName = fileName;
		this.model = model;
		
	}

	@Override
	protected List<Vector<String>> doInBackground() throws Exception {
		// TODO Auto-generated method stub
		List<Vector<String>> listOfVectors = new ArrayList<Vector<String>>();
		try {
			FileInputStream fis = new FileInputStream(this.fileName);
			int lineLen = gui.panelCols * 2;
			byte[] bytes = new byte[16];
			int len;
			gui.hexRead = "";
			do {
				len = fis.read(bytes);
				for (int i = 0; i < len; i++) {
					byteList.add(bytes[i]);							// store bytes
					String tmp = String.format("%02X", bytes[i]);	// read file into hex
					gui.hexRead += tmp;
					if (gui.hexRead.length() % lineLen == 0) {
						int start = gui.hexRead.length() - lineLen;
						int end = gui.hexRead.length();
						Vector<String> res = gTransformer.updateEditorRowByRow(gui.hexRead.substring(start, end));
						listOfVectors.add(res);
						publish(res);
					}
					
				}
			} while (len != -1);
			// the last row of data that may not fill up entire row
			if (gui.hexRead.length() % lineLen > 0) {
				int start = gui.hexRead.length() - (gui.hexRead.length() % lineLen);
				int end = gui.hexRead.length();
				Vector<String> res = gTransformer.updateEditorRowByRow(gui.hexRead.substring(start, end));
				listOfVectors.add(res);
				publish(res);
			}
			fis.close();
		} catch (Exception e) {
			System.out.print("Reading of file went wrong!");
			//e.printStackTrace();
		}
		if (listOfVectors.isEmpty())
			System.out.print("File length cannot be 0!");
		return listOfVectors;
	}
	
	protected void process(List<Vector<String>> chunks) {
		int n = model.getRowCount();
		for (Vector<String> v : chunks)
			model.addRow(v);
		model.fireTableRowsInserted(n, n + chunks.size());
	}

}
