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
		this.fileName = fileName;
		
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (fileName != null) {
			try {
				FileOutputStream fos = new FileOutputStream(_mainframe.fileName, false);
				byte[] toWrite = gTransformer.hexStringToByteArray(gui.hexRead);
				fos.write(toWrite);
				fos.close();
				//CommonOutputs.debug(gui.fileName + " saved.");
			} catch (Exception e) {
				//CommonOutputs.error("Saving of file went wrong!");
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
