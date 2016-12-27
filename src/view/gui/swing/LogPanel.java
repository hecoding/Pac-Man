package view.gui.swing;

import java.awt.BorderLayout;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jeco.core.algorithm.moge.PacmanGrammaticalEvolution;

public class LogPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Logger logger;
	JTextArea logText;
	
	public LogPanel() {
		logger = PacmanGrammaticalEvolution.logger;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	private void initGUI() {
		logText = new JTextArea();
		logText.setEditable(false);
		logText.setLineWrap(true);
		logText.setWrapStyleWord(true);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(logText), BorderLayout.CENTER);
		
		logger.setUseParentHandlers(false);
		logger.addHandler(new LogHandler());
		// this doesn't work
		//and this doesn't either https://java-swing-tips.blogspot.com.es/2015/02/logging-into-jtextarea.html
	}
	
	public class LogHandler extends Handler {

		@Override
		public void close() throws SecurityException {
			
		}

		@Override
		public void flush() {
			
		}

		@Override
		public void publish(LogRecord arg0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// filter (if isLoggable())
					String fuuuu = arg0.getMessage();
					logText.append(fuuuu);
				}
			});
		}
		
	}
}
