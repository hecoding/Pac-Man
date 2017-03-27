package view.gui.swing;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;

public class ObjetiveSelectorPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JList<String> listCurrentObjs;
	
	//To show this Panel as a popup, use:
	//JOptionPane.showMessageDialog(null, new ObjetiveSelectorPanel(options, indices));
	
	public ObjetiveSelectorPanel(String[] options, int[] indices) {

		listCurrentObjs = new JList<String>(options);
		listCurrentObjs.setVisibleRowCount(4);
		listCurrentObjs.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;

			@Override
		    public void setSelectionInterval(int index0, int index1) {
		        if(super.isSelectedIndex(index0)) {
		            super.removeSelectionInterval(index0, index1);
		        }
		        else {
		            super.addSelectionInterval(index0, index1);
		        }
		    }
		});
		for (int i = 0; i < options.length; i++) {
			
		}
		listCurrentObjs.setSelectedIndices(indices);
		
		JScrollPane scrollPane = new JScrollPane(listCurrentObjs);
		add(scrollPane);
	}
	
	public int[] selectedObjs() {
		return listCurrentObjs.getSelectedIndices();
	}

}
