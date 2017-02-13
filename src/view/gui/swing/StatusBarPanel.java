package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jeco.core.util.observer.AlgObserver;

public class StatusBarPanel extends JPanel implements AlgObserver {
	private static final long serialVersionUID = 1L;
	GeneralController gCtrl;
	private JTextArea outputTextArea;
	private Color defaultColor = new Color(245,245,245);
	
	public StatusBarPanel(GeneralController gCtrl) {
		this.gCtrl = gCtrl;
		this.gCtrl.addObserver(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	private void initGUI() {
		outputTextArea = new JTextArea();
		outputTextArea.setEditable(false);
		outputTextArea.setBackground(defaultColor);
		outputTextArea.setLineWrap(true);
		outputTextArea.setWrapStyleWord(true);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);
		this.setVisible(false);
	}
	
	public void setErrors(Boolean b) {
		if(b) {
			this.outputTextArea.setForeground(Color.red);
			this.outputTextArea.setText("There are errors");
			this.setVisible(true);
		}
		else {
			this.outputTextArea.setForeground(defaultColor);
			this.outputTextArea.setText("");
			this.setVisible(false);
		}
	}
	
	public void setStatus(String s) {
		this.outputTextArea.setForeground(Color.black);
		this.outputTextArea.setText(s);
		if(s.equals(""))
			this.setVisible(false);
		else
			this.setVisible(true);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onEnd() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {/*
				if(ctrl.isFinished()) {
					String s = new String("");
					if(ctrl.isRangeParameters()) {
						double[] x = ctrl.getRangeList();
						ArrayList<Double> y = ctrl.getRangeResults();
						Double best;
						if(ctrl.isMinimization()) best = Collections.min(y);
						else best = Collections.max(y);
						int idx = y.indexOf(best);
						
						s += "The best result is " + best.intValue() + " with the parameter " + String.format("%.2f", x[idx]);
					}
					setStatus(s);
				}*/
			}
		});
	}
	
	@Override
	public void onIncrement(int n) {
		
	}
}
