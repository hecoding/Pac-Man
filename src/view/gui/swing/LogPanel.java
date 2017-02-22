package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

public class LogPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Logger logger;
	JTextArea logText;
	
	public LogPanel(GeneralController gCtrl) {
		logger = gCtrl.getLogger();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}
	
	private void initGUI() {
		logText = new JTextArea();
		logText.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
		logText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
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
