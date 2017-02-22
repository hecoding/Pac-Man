package view.gui.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import jeco.core.util.observer.AlgObserver;

public class CenterPanel extends JPanel implements AlgObserver {
	private static final long serialVersionUID = 1L;
 	JTabbedPane tabs;
 	public JPanel graphPanel;
 	public JPanel programPanel;
 	public LogPanel logPanel;
 	public GamePanel gp;
 	public StatusBarPanel status;
 	JPanel centerPanel;
 	JProgressBar progressBar;
 	JButton cancelButton;
 	GeneralController gCtrl;
 	
 	JFreeChart chart;
 	XYPlot plot;
 	XYSeries worstSeries;
	XYSeries bestSeries;
	XYSeries avgSeries;
	XYSeries absoluteSeries;
 	XYSeriesCollection dataset;
 	XYSeriesCollection dataset2;
 	JTextArea programText;
 	JPanel runButtonPanel;
 	
 	long beforeTime = 0;
 	
	public CenterPanel(GeneralController gCtrl) {
		this.gCtrl = gCtrl;
		this.gCtrl.addObserver(this);
		this.progressBar = this.gCtrl.getProgressBar();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {
		graphPanel = new JPanel(new BorderLayout());
		programPanel = new JPanel(new BorderLayout());
		tabs = new JTabbedPane();
		this.setLayout(new BorderLayout());
		
		// Fitness tab
		worstSeries = new XYSeries("Worst of generation");
		bestSeries = new XYSeries("Best of generation");
		avgSeries = new XYSeries("Generation average");
		absoluteSeries = new XYSeries("Absolute best");
		
		dataset = new XYSeriesCollection();
		dataset2 = new XYSeriesCollection();
		
		Color transparent = new Color(0,0,0,0);
		Color lighterGray = new Color(200, 200, 200);
		Color blue = new Color(175, 224, 229);
		Color blue1 = new Color(119, 141, 178);
		Color blue2 = new Color(46, 77, 127);
		
		chart = ChartFactory.createXYAreaChart("", "Generations", "Fitness", dataset);
		chart.setBackgroundPaint(Color.white);
		
		plot = chart.getXYPlot();
		plot.setDataset(0, dataset2);
		XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);
		lineRenderer.setSeriesPaint(0, blue1);
		lineRenderer.setSeriesStroke(0, new BasicStroke(0.8f));
		lineRenderer.setSeriesPaint(1, blue2);
		lineRenderer.setSeriesStroke(1, new BasicStroke(2.5f));
		plot.setRenderer(0, lineRenderer);
		
		plot.setDataset(1, this.dataset);
		XYDifferenceRenderer diffRenderer = new XYDifferenceRenderer(
				blue, blue, false
			);
		diffRenderer.setSeriesPaint(0, transparent);
		diffRenderer.setSeriesPaint(1, transparent);
		diffRenderer.setSeriesVisibleInLegend(0, false);
		diffRenderer.setSeriesVisibleInLegend(1, false);
		plot.setRenderer(1, diffRenderer);
		plot.setOutlinePaint(null);
		plot.setBackgroundPaint(Color.white);
		plot.setForegroundAlpha(1);
		plot.setDomainGridlinePaint(lighterGray);
		plot.setRangeGridlinePaint(lighterGray);
		
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setTickMarkPaint(Color.black);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickMarkPaint(Color.black);
		rangeAxis.setLowerMargin(0.01);
		rangeAxis.setUpperMargin(0.01);
		rangeAxis.setAutoRangeIncludesZero(false);
		
		graphPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
		tabs.add("Fitness", graphPanel);
		
		// Program tab
		programText = new JTextArea();
		programText.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
		programText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		programText.setEditable(false);
		programText.setLineWrap(true);
		programText.setWrapStyleWord(true);
		programPanel.setLayout(new BorderLayout());
		programPanel.add(new JScrollPane(programText), BorderLayout.CENTER);
		tabs.add("Program", programPanel);
		
		// Log tab
		logPanel = new LogPanel(this.gCtrl);
		tabs.addTab("Log", logPanel);
		
		// Game tab
		gp = new GamePanel(this.gCtrl);
		tabs.addTab("Game", gp);
		
		tabs.setMnemonicAt(0, KeyEvent.VK_1);
		tabs.setMnemonicAt(1, KeyEvent.VK_2);
		tabs.setMnemonicAt(2, KeyEvent.VK_3);
		
		this.add(tabs, BorderLayout.CENTER);
		JPanel lowBar = new JPanel(new BorderLayout());
		this.status = new StatusBarPanel(this.gCtrl);
		lowBar.add(this.status, BorderLayout.PAGE_START);
		this.progressBar.setVisible(false);
		lowBar.add(this.progressBar, BorderLayout.CENTER);
		cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Cancel execution");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gCtrl.programWorkerStop();
			}
		});
		//cancelButton.setPreferredSize(new Dimension(cancelButton.getPreferredSize().width, 14));
		cancelButton.setVisible(false);
		lowBar.add(cancelButton, BorderLayout.LINE_END);
		this.add(lowBar, BorderLayout.PAGE_END);
	}

	@Override
	public void onStart() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setVisible(true);
				cancelButton.setVisible(true);
				//plot.setVisible(false);
			}
		});
	}

	@Override
	public void onEnd() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setVisible(false);
				cancelButton.setVisible(false);
				
				updateGraphPanel();
				
				programText.setText(gCtrl.getBestProgramPretty());
				/*if(ctrl.isFinished()) {
					updateMapPanel();
					updateGraphPanel();
					updateProgramPanel();
				}*/
			}
		});
	}
	
	@Override
	public void onIncrement(int n) {
		if(this.aSecondHavePassed())
			updateGraphPanel();
	}
	
	public boolean aSecondHavePassed() {
		double secondsCup = 0.5;
		
		if(this.beforeTime == 0) {
			this.beforeTime = System.nanoTime();
			return true;
		}
		
		long current = System.nanoTime();
		double elapsedSeconds = ((current - this.beforeTime) / 1000000000.0);
		
		if(elapsedSeconds > secondsCup) {
			this.beforeTime = current;
			return true;
		}
		
		return false;
	}
	
	private void updateGraphPanel() {
		this.dataset.removeAllSeries();
		this.dataset2.removeAllSeries();
		
		worstSeries.clear();
		bestSeries.clear();
		avgSeries.clear();
		absoluteSeries.clear();
		
		for(int i = 0; i < this.gCtrl.getWorstObjectives().size(); i++) {
			worstSeries.add(i, this.gCtrl.getWorstObjectives().get(i));
		}
		
		for(int i = 0; i < this.gCtrl.getBestObjectives().size(); i++) {
			bestSeries.add(i, this.gCtrl.getBestObjectives().get(i));
		}
		
		for(int i = 0; i < this.gCtrl.getAverageObjetives().size(); i++) {
			avgSeries.add(i, this.gCtrl.getAverageObjetives().get(i));
		}
		
		for(int i = 0; i < this.gCtrl.getAbsoluteBestObjetives().size(); i++) {
			absoluteSeries.add(i, this.gCtrl.getAbsoluteBestObjetives().get(i));
		}
		
		this.dataset.addSeries(worstSeries);
		this.dataset.addSeries(bestSeries);
		
		this.dataset2.addSeries(avgSeries);
		this.dataset2.addSeries(absoluteSeries);
		
		// plot.setVisible(true);
	}

}
