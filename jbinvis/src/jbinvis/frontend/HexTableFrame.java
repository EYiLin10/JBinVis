package jbinvis.frontend;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jbinvis.frontend.hexTable.EventListeners;
import jbinvis.frontend.hexTable.GuiTransformer;
import jbinvis.frontend.hexTable.HexEditorTableModel;
import jbinvis.main.JBinVis;

/**
 * @author YiLin
 *
 */
public class HexTableFrame extends JFrame implements QuickEnable, ChangeListener {

	private JFrame frame;
	
	private JBinVis _singleton = null;
	private MainFrame _mainframe = null;
	public static HexTableFrame gui = null;
	public static GuiTransformer gTransformer = null;
	
	public JScrollPane scrollPane = null;
	public JTable editorTable = null;
	public JPanel buttonsPanel = new JPanel();
	
	public JButton saveButton;
	public JButton updateButton;
	
	public String hexRead = "";								// string of hex read from file
	public Integer hexChanged;								// whether current hex string has changed
	public byte[] bytesRead;								// stores the bytes of the file
	
	public final Integer panelRows = 20;					// initial number of rows
	public final Integer panelCols = 16;					// initial number of columns

	public String[] headers = new String[panelCols];
	public String[] rowData = new String[panelCols];

	/**
	 * Launch the application.
	 */
	public void newHexTable() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HexTableFrame window = new HexTableFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HexTableFrame() {
		
		//i need dthe result here
		if ( _singleton == null )
    		_singleton = JBinVis.getInstance();
		if (_mainframe == null)
			_mainframe = MainFrame.getInstance();
		if (gui == null)
			gui = this;
		gTransformer = new GuiTransformer();
		
		initialize();
		System.out.println("after initialize");
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = this;
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Hex Table");
		
		Container container = frame.getContentPane();
		
		// Hex Table, master == getContentPane()
		editorTable = new JTable();
		gTransformer.clearTable();
		scrollPane = new JScrollPane(editorTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		container.add(scrollPane, BorderLayout.CENTER);
		
		// Buttons panel, master == getContentPane()
		buttonsPanel.setLayout(new GridLayout(1, 1));	
 		saveButton = new JButton("Save");
 		updateButton = new JButton("Update display");
 		buttonsPanel.add(saveButton);
 		buttonsPanel.add(updateButton); 		
 		container.add(buttonsPanel, BorderLayout.PAGE_END);
 		
 		// add listeners
 		new EventListeners("save");
 		new EventListeners("update");
 		
 		// set all visible
		buttonsPanel.setVisible(true);
		scrollPane.setVisible(true);
		editorTable.setVisible(true);	

	}
	
	public static HexTableFrame getInstance() {
		return gui;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disableAll() {
		// TODO Auto-generated method stub
		
	}

}
