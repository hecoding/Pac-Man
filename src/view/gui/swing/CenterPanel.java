package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.math.plot.Plot2DPanel;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.util.observer.AlgObserver;
import view.gui.swing.GUIView.Worker1;

public class CenterPanel extends JPanel implements AlgObserver {
	private static final long serialVersionUID = 1L;
 	//private Controller ctrl;
 	private JTabbedPane tabs;
 	private JPanel graphPanel;
 	private JPanel mapPanel;
 	private JPanel programPanel;
 	private LogPanel logPanel;
 	private GamePanel gp;
 	private StatusBarPanel status;
 	JPanel centerPanel;
 	JProgressBar progressBar;
 	GrammaticalEvolution algorithm;
 	
 	AntTrailPane map;
 	Plot2DPanel plot;
 	JTextArea programText;
 	JPanel runButtonPanel;
 	
	public CenterPanel(GrammaticalEvolution algorithm, StatusBarPanel status) {
		this.algorithm = algorithm;
		this.algorithm.addObserver(this);
		this.status = status;
		this.progressBar = Worker1.progressBar;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {
		mapPanel = new JPanel(new BorderLayout());
		graphPanel = new JPanel(new BorderLayout());
		programPanel = new JPanel(new BorderLayout());
		tabs = new JTabbedPane();
		this.setLayout(new BorderLayout());
		
		map = new AntTrailPane();
		mapPanel.add(map, BorderLayout.CENTER);
		tabs.add("Map", mapPanel);
		
		plot = new Plot2DPanel();
		plot.addLegend("SOUTH");
		graphPanel.add(plot, BorderLayout.CENTER);
		tabs.add("Fitness", graphPanel);
		
		programText = new JTextArea();
		programText.setEditable(false);
		programText.setLineWrap(true);
		programText.setWrapStyleWord(true);
		programPanel.setLayout(new BorderLayout());
		programPanel.add(new JScrollPane(programText), BorderLayout.CENTER);
		tabs.add("Program", programPanel);
		
		logPanel = new LogPanel();
		tabs.addTab("Log", logPanel);
		
		gp = new GamePanel();
		tabs.addTab("Game", gp);
		
		tabs.setMnemonicAt(0, KeyEvent.VK_1);
		tabs.setMnemonicAt(1, KeyEvent.VK_2);
		tabs.setMnemonicAt(2, KeyEvent.VK_3);
		
		this.add(tabs, BorderLayout.CENTER);
		JPanel lowBar = new JPanel(new BorderLayout());
		lowBar.add(this.status, BorderLayout.PAGE_START);
		this.progressBar.setVisible(false);
		lowBar.add(this.progressBar, BorderLayout.CENTER);
		this.add(lowBar, BorderLayout.PAGE_END);
	}

	@Override
	public void onStart() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setVisible(true);
				plot.setVisible(false);
			}
		});
	}

	@Override
	public void onEnd() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setVisible(false);
				
				updateGraphPanel();
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
		
	}
	
	private void updateGraphPanel() {
		plot.removeAllPlots();
		
		plot.addLinePlot("Absolute best", toPrimitiveArray(this.algorithm.absoluteBestObjetives));
		plot.addLinePlot("Best of generation", toPrimitiveArray(this.algorithm.bestObjetives));
		plot.addLinePlot("Generation average", toPrimitiveArray(this.algorithm.averageObjetives));
		plot.addLinePlot("Worst of generation", toPrimitiveArray(this.algorithm.worstObjetives));
		plot.setFixedBounds(0, 0, this.algorithm.bestObjetives.size());
		
		plot.setVisible(true);
	}
	
	private static double[] toPrimitiveArray(ArrayList<Double> a) {
		return a.stream().mapToDouble(d -> d).toArray();
	}
	
	/*
	private void updateMapPanel() {
		mapPanel.removeAll();
		mapPanel.add(new AntTrailPane(ctrl.getResultMap()), BorderLayout.CENTER);
	}
	
	private void updateGraphPanel() {
		plot.removeAllPlots();
		
		if(!ctrl.isRangeParameters()) {
			double[] avgApt = ctrl.getAverageAptitudeList();
			plot.addLinePlot("Absolute best", ctrl.getBestChromosomeList());
			plot.addLinePlot("Best of generation", ctrl.getBestAptitudeList());
			plot.addLinePlot("Generation average", ctrl.getAverageAptitudeList());
			plot.setFixedBounds(0, 0, avgApt.length);
		}
		else {
			double[] range = ctrl.getRangeList();
			plot.addLinePlot("Distance", Color.red, range, ctrl.getResultsList());
			plot.setFixedBounds(0, range[0], range[range.length - 1]);
		}
		
		plot.setVisible(true);
	}
	
	private void updateProgramPanel() {
		String s = new String("");
		s += "Result: " + new Double(ctrl.getFunctionResult()).intValue() + " bits" + System.lineSeparator();
		s += "Best:" + System.lineSeparator();
		s += ctrl.getResult();
		
		programText.setText(s);
		programText.setCaretPosition(0);
	}*/
	
	public class AntTrailPane extends JPanel {
		private static final long serialVersionUID = 1L;/*
		private int columnCount;
		private int rowCount;
		//private Map map;
		private Color white = new Color(250,250,250);
		private Color black = new Color(33,33,33);
		private Color green = new Color(0,230,118);
		private Color yellow = new Color(253,216,53);
		private Color orange = new Color(249,168,37);
		private Color gray = new Color(158,158,158);*/

		public AntTrailPane() {/*
			this.map = AntTrailGeneticAlgorithm.getMap();
			this.rowCount = this.map.getRows();
			this.columnCount = this.map.getColumns();
		}
		
		public AntTrailPane(Map map) {
			this.map = map;
			this.rowCount = this.map.getRows();
			this.columnCount = this.map.getColumns();*/
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 200);
		}

		@Override
		protected void paintComponent(Graphics g) {/*
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();

			int width = getWidth();
			int height = getHeight();

			int cellWidth = width / columnCount;
			int cellHeight = height / rowCount;

			int xOffset = (width - (columnCount * cellWidth)) / 2;
			int yOffset = (height - (rowCount * cellHeight)) / 2;

			for (int row = 0; row < rowCount; row++) {
				for (int col = 0; col < columnCount; col++) {
					
					switch(this.map.get(row, col)) {
					case nothing:
						g.setColor(white);
						break;
					case food:
						g.setColor(black);
						break;
					case trail:
						g.setColor(yellow);
						break;
					case eatenfood:
						g.setColor(orange);
						break;
					case beginning:
						g.setColor(green);
						break;
					default:
						break;
					}
					
					g.fillRect(
							xOffset + (col * cellWidth),
							yOffset + (row * cellHeight),
							cellWidth,
							cellHeight);
					
					g.setColor(gray);
					g.drawRect(
							xOffset + (col * cellWidth),
							yOffset + (row * cellHeight),
							cellWidth,
							cellHeight);
				}
			}

			g2d.dispose();*/
		}
	}

}
