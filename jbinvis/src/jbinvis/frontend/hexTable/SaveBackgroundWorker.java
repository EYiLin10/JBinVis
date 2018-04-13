/**
 * 
 */
package jbinvis.frontend.hexTable;

import java.io.FileOutputStream;
import javax.swing.SwingWorker;
import jbinvis.frontend.HexTableFrame;
import jbinvis.frontend.MainFrame;
import jbinvis.main.JBinVis;


/**
 * @author YiLin
 * Background thread for saving of files
 *
 */
public class SaveBackgroundWorker extends SwingWorker<Void, String> {
	
	JBinVis _singleton = null;
	MainFrame _mainframe = null;
	HexTableFrame gui = null;
	GuiTransformer gTransformer = null;
	
	private String fileName = null;
	
	public SaveBackgroundWorker(String fileName) {
		
		this._singleton = JBinVis.getInstance();
		this._mainframe = MainFrame.getInstance();
		this.gui = HexTableFrame.getInstance();
		this.gTransformer = gui.gTransformer;
		this.fileName = _mainframe.fileName;
		
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (fileName != null) {
			try {
				FileOutputStream fos = new FileOutputStream(fileName, false);
				System.out.println(fileName);
				byte[] toWrite = gTransformer.hexStringToByteArray(gui.hexRead);
				fos.write(toWrite);
				fos.close();
				System.out.println(fileName + " saved.");
			} catch (Exception e) {
				System.out.println("Saving of file went wrong!");
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	protected void done() {
		if (gui.hexChanged > 0) {
			gTransformer.updateDisplay(gui.hexRead, "S");
		}
	}

}
