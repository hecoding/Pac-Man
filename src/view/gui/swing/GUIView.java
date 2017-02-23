package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class GUIView extends JFrame {
	private static final long serialVersionUID = 1L;
	final static int ticks = 19;
	private static GeneralController gCtrl;
	private static GUIController guiCtrl;
	private CenterPanel centerPanel;
	private SettingsPanel settingsPanel;
	private StatusBarPanel status;
	
	public GUIView() {
		
		this.setTitle("Pac-Man");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {
		gCtrl = new GeneralController();
		guiCtrl = new GUIController();
		this.status = new StatusBarPanel(gCtrl);
		this.centerPanel = new CenterPanel(gCtrl);
		this.settingsPanel = new SettingsPanel(guiCtrl, gCtrl);
		
		guiCtrl.setStatusBarPanel(this.status);
		guiCtrl.setCenterPanel(this.centerPanel);
		guiCtrl.setSettingsPanel(this.settingsPanel);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(settingsPanel, BorderLayout.LINE_START);
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(1024,668));
		this.setMinimumSize(new Dimension(750, 550));
		this.setLocationRelativeTo(null); // center on the screen (doesn't show nice with multiple monitors)
		this.setVisible(true);
	}

}
