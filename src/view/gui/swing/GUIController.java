package view.gui.swing;

public class GUIController {	
	private CenterPanel centerPanel;
	//private SettingsPanel settingsPanel;
	private StatusBarPanel status;
	
	public GUIController() {
		
	}
	
	public void changeFocusToFitness() {
		this.centerPanel.tabs.setSelectedIndex(0);
	}
	
	public void changeFocusToGame() {
		this.centerPanel.tabs.setSelectedIndex(3);
	}
	
	public void showAndRun(String s) {
		this.centerPanel.gp.copyAndRun(s);
	}
	
	public void setErrors(Boolean b) {
		this.status.setErrors(b);
	}
	
	public void setCenterPanel(CenterPanel c) {
		this.centerPanel = c;
	}
	
	public void setSettingsPanel(SettingsPanel s) {
		//this.settingsPanel = s;
	}
	
	public void setStatusBarPanel(StatusBarPanel s) {
		this.status = s;
	}
}
