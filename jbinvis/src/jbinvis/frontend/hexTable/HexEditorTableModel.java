package jbinvis.frontend.hexTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import jbinvis.frontend.HexTableFrame;
import jbinvis.frontend.MainFrame;
import jbinvis.main.JBinVis;

/**
 * @author YiLin
 * Custom table model for hex editor
 */
@SuppressWarnings("serial")
public class HexEditorTableModel extends AbstractTableModel {
	
	private JBinVis _singleton = null;
	private MainFrame _mainframe = null;
	HexTableFrame gui = null;
	
	private String[] headers = null;
	private String[] rowData = null;
	private List<Object> dataList = new ArrayList<Object>();
	private Object[][] data = null;
	
	public HexEditorTableModel() {
		
		if ( _singleton == null )
    		_singleton = JBinVis.getInstance();
		if (_mainframe == null)
			_mainframe = MainFrame.getInstance();
		if (gui == null)
			gui = HexTableFrame.getInstance();
		
		headers = new String[gui.panelCols];
		rowData = new String[gui.panelCols];
		
		Arrays.fill(headers, "ColumnHeader");
		Arrays.fill(rowData, "");
		for (int i = 0; i < gui.panelRows; i++)
			dataList.add(rowData);
		data = dataList.toArray(new Object[dataList.size()][]);
	}

	@Override
	public int getRowCount() {
		return data.length;
	}
	
	public void setRowCount(int rowCount) {
		if (rowCount > getRowCount()) {
			for (int i = getRowCount(); i < rowCount; i++)
				dataList.add(rowData);
			data = dataList.toArray(new Object[dataList.size()][]);
		}
		else if (rowCount < getRowCount()) {
			for (int i = getRowCount()-1; i >= rowCount; i--)
				dataList.remove(i);
			data = dataList.toArray(new Object[dataList.size()][]);
		}
        fireTableDataChanged();
        gui.editorTable.repaint();
	}
	
	public void addRow(Vector<String> rowData) {
		String[] tmpData = new String[rowData.size()];
		for (int i = 0; i < tmpData.length; i++)
			tmpData[i] = rowData.elementAt(i);
		dataList.add(tmpData);
		data = dataList.toArray(new Object[dataList.size()][]);
		fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
	
	public void setValueAt(Object value, int row, int col	 ) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public boolean isCellEditable(int row, int col) {
		return true;
	}	
}
