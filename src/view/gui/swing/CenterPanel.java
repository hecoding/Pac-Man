package view.gui.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
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
 	
 	ArrayList<XYPlot> subplots;
 	ArrayList<ArrayList<XYSeries>> series;
	ArrayList<ArrayList<XYSeriesCollection>> datasets;
 	JTextArea programText;
 	JPanel runButtonPanel;
 	
 	long beforeTime = 0;
 	
	public CenterPanel(GeneralController gCtrl) {
		this.gCtrl = gCtrl;
		this.gCtrl.addObserver(this);
		this.progressBar = this.gCtrl.getProgressBar();
		
		this.subplots = new ArrayList<>(this.gCtrl.getNumOfObjectives());
		this.datasets = new ArrayList<>(this.gCtrl.getNumOfObjectives());
		this.series = new ArrayList<>(this.gCtrl.getNumOfObjectives());
		
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
		graphPanel.add(this.createGeneralChartPanel(), BorderLayout.CENTER);
		tabs.add("Progress", graphPanel);
		
		// Program tab
		programText = new JTextArea();
		programText.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
		programText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		programText.setEditable(false);
		programText.setLineWrap(true);
		programText.setWrapStyleWord(true);
		programPanel.setLayout(new BorderLayout());
		programPanel.add(new JScrollPane(programText), BorderLayout.CENTER);
		tabs.add("Derivation", programPanel);
		
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
	
	public ChartPanel createGeneralChartPanel() {
		CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Generations"));
		plot.setGap(10.0);
		
		for (int i = 0; i < this.gCtrl.getNumOfObjectives(); i++) {
			XYPlot subp = this.createSubplot(i);
			this.subplots.add(subp);
			plot.add(subp);
		}
        plot.setOrientation(PlotOrientation.VERTICAL);
		
		return new ChartPanel(new JFreeChart("", plot));
	}
	
	public XYPlot createSubplot(int numOfPlot) {
		ArrayList<XYSeries> serie = new ArrayList<>(4);
		serie.add(new XYSeries("Worst of generation"));
		serie.add(new XYSeries("Best of generation"));
		serie.add(new XYSeries("Generation average"));
		serie.add(new XYSeries("Absolute best"));
		this.series.add(serie);
		
		XYSeriesCollection dataset1 = new XYSeriesCollection();
		XYSeriesCollection dataset2 = new XYSeriesCollection();
		ArrayList<XYSeriesCollection> datasetCol = new ArrayList<>(2);
		datasetCol.add(dataset1);
		datasetCol.add(dataset2);
		this.datasets.add(datasetCol);
		
		Color transparent = new Color(0,0,0,0);
		Color lighterGray = new Color(200, 200, 200);
		Color blue = new Color(175, 224, 229);
		Color blue1 = new Color(119, 141, 178);
		Color blue2 = new Color(46, 77, 127);
		
		XYPlot plot = new XYPlot();
		plot.setDataset(0, dataset2);
		XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);
		lineRenderer.setSeriesPaint(0, blue1);
		lineRenderer.setSeriesStroke(0, new BasicStroke(0.8f));
		lineRenderer.setSeriesPaint(1, blue2);
		lineRenderer.setSeriesStroke(1, new BasicStroke(2.5f));
		if(numOfPlot == 0) {
			lineRenderer.setSeriesVisibleInLegend(0, true);
			lineRenderer.setSeriesVisibleInLegend(1, true);
		}
		else {
			lineRenderer.setSeriesVisibleInLegend(0, false);
			lineRenderer.setSeriesVisibleInLegend(1, false);
		}
		plot.setRenderer(0, lineRenderer);
		
		plot.setDataset(1, dataset1);
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
		
		NumberAxis domainAxis = new NumberAxis("");
		plot.setDomainAxis(domainAxis);
		domainAxis.setTickMarkPaint(Color.black);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis rangeAxis = new NumberAxis(this.gCtrl.getFitnessName(numOfPlot));
		plot.setRangeAxis(rangeAxis);
		rangeAxis.setTickMarkPaint(Color.black);
		rangeAxis.setLowerMargin(0.01);
		rangeAxis.setUpperMargin(0.01);
		rangeAxis.setAutoRangeIncludesZero(false);
		
		return plot;
	}

	@Override
	public void onStart() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setVisible(true);
				cancelButton.setVisible(true);
				for (XYPlot plot : subplots) {
					plot.getDomainAxis().setRange(0, gCtrl.getGenerations());
				}
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
		for (int i = 0; i < this.gCtrl.getNumOfObjectives(); i++) {
			ArrayList<XYSeriesCollection> dataset = this.datasets.get(i);
			for (XYSeriesCollection collec : dataset) {
				collec.removeAllSeries();
			}
			
			XYSeries worstSeries = this.series.get(i).get(0);
			XYSeries bestSeries = this.series.get(i).get(1);
			XYSeries avgSeries = this.series.get(i).get(2);
			XYSeries absoluteSeries = this.series.get(i).get(3);
			
			worstSeries.clear();
			bestSeries.clear();
			avgSeries.clear();
			absoluteSeries.clear();
			
			for(int j = 0; j < this.gCtrl.getWorstObjectives().size(); j++) {
				worstSeries.add(j, this.gCtrl.getWorstObjectives().get(j).get(i));
			}
			
			for(int j = 0; j < this.gCtrl.getBestObjectives().size(); j++) {
				bestSeries.add(j, this.gCtrl.getBestObjectives().get(j).get(i));
			}
			
			for(int j = 0; j < this.gCtrl.getAverageObjectives().size(); j++) {
				avgSeries.add(j, this.gCtrl.getAverageObjectives().get(j).get(i));
			}
			
			for(int j = 0; j < this.gCtrl.getAbsoluteBestObjectives().size(); j++) {
				absoluteSeries.add(j, this.gCtrl.getAbsoluteBestObjectives().get(j).get(i));
			}
			
			dataset.get(0).addSeries(worstSeries);
			dataset.get(0).addSeries(bestSeries);
			
			dataset.get(1).addSeries(avgSeries);
			dataset.get(1).addSeries(absoluteSeries);
			
			// plot.setVisible(true);
		}
	}

}
