package view.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jeco.core.util.observer.AlgObserver;

public class SettingsPanel extends JPanel implements AlgObserver {
	private static final long serialVersionUID = 1L;
	private final GeneralController gCtrl;
 	private final GUIController guiCtrl;
 	private JPanel settings;
 	private JPanel buttonPanel;
 	private JButton showAndPlayButton;
 	private JButton trainButton;
 	private JButton resetButton;
 	
 	private JTextField populationText;
 	private JTextField generationText;
 	private JTextField iterPerIndText;
 	private JTextField chromosomeLengthText;
 	private JTextField codonUpperBoundText;
 	private JTextField maxCntWrappingsText;
 	private JTextField numOfObjectivesText;
	private JComboBox<String> selectionOperatorBox;
 	private JTextField crossoverText;
 	private JSlider crossoverSlider;
	private JComboBox<String> crossoverOperatorBox;
 	private JTextField mutationText;
 	private JSlider mutationSlider;
	private JComboBox<String> mutationOperatorBox;
 	private JTextField elitismText;
 	private JSlider elitismSlider;
 	private JButton btnSelectObjetives;
	private ObjetiveSelectorPanel objectiveSelector;
 	private JPanel grammar;
 	private JComboBox<String> grammarBox;
	private JComboBox<String> ghostControllerBox;
	private JCheckBox neutralMutationCheck;
 	
 	private String populationTextDefault;
	private String generationTextDefault;
	private int crossoverSliderDefault;
	private int mutationSliderDefault;
	private int elitismSliderDefault;
	private Object grammarBoxDefault;
	private boolean neutralMutationCheckDefault;
	private String iterPerIndTextDefault;
	private String chromosomeLengthTextDefault;
	private String codonUpperBoundTextDefault;
	private String maxCntWrappingsTextDefault;
	private int[] objectiveSelectedIndicesDefault;
	private Object selectedGhostControllerDefault;
	private Object selectedSelectionOperatorDefault;
	private Object selectedCrossoverOperatorDefault;
	private Object selectedMutationOperatorDefault;

	private Border defaultborder;

	public SettingsPanel(GUIController ctrl, GeneralController gCtrl) {
		this.gCtrl = gCtrl;
		this.guiCtrl = ctrl;
		this.gCtrl.addObserver(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.setBorder(new TitledBorder("Settings"));
		
		initSettings();
		//this.add(settings, BorderLayout.CENTER); TODO
		JScrollPane scrollpane = new JScrollPane(settings);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollpane, BorderLayout.CENTER);

		buttonPanel = new JPanel(new BorderLayout());
		
		showAndPlayButton = new JButton("Show & play best");
		showAndPlayButton.setMnemonic(KeyEvent.VK_S);
		showAndPlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiCtrl.changeFocusToGame();
				guiCtrl.showAndRun(gCtrl.getBestProgram());
			}
		});
		showAndPlayButton.setVisible(false);
		buttonPanel.add(showAndPlayButton, BorderLayout.PAGE_START);
		
		trainButton = new JButton("Train");
		trainButton.setMnemonic(KeyEvent.VK_T);
		trainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					guiCtrl.changeFocusToFitness();

					gCtrl.setPopulationSize(Integer.parseInt(populationText.getText()));
					gCtrl.setGenerations(Integer.parseInt(generationText.getText()));
					gCtrl.setItersPerIndividual(Integer.parseInt(iterPerIndText.getText()));
					gCtrl.setChromosomeLength(Integer.parseInt(chromosomeLengthText.getText()));
					gCtrl.setCodonUpperBound(Integer.parseInt(codonUpperBoundText.getText()));
					gCtrl.setMaxCntWrappings(Integer.parseInt(maxCntWrappingsText.getText()));
					//ctrl.setHeight(Integer.parseInt(heightText.getText()));
					gCtrl.setSelectedSelectionOperator((String) selectionOperatorBox.getSelectedItem());
					gCtrl.setCrossProb(crossoverSlider.getValue() / 100.0); // .0 is important
					gCtrl.setSelectedCrossoverOperator((String) crossoverOperatorBox.getSelectedItem());
					gCtrl.setMutationProb(mutationSlider.getValue() / 100.0); // .0 is important
					gCtrl.setSelectedMutationOperator((String) mutationOperatorBox.getSelectedItem());
					gCtrl.setNeutralMutation(neutralMutationCheck.isSelected());
					gCtrl.setGrammar((String) grammarBox.getSelectedItem());
					gCtrl.setSelectedObjectives(objectiveSelector.getSelectedValues());
					gCtrl.setSelectedGhostController((String) ghostControllerBox.getSelectedItem());

					gCtrl.execute();
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(null,
							ex.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonPanel.add(trainButton, BorderLayout.CENTER);
		resetButton = new JButton("Reset");
		resetButton.setToolTipText("Set initial values");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restoreDefaults();
			}
		});
		buttonPanel.add(resetButton, BorderLayout.PAGE_END);
		this.add(buttonPanel, BorderLayout.PAGE_END);
		// maybe wrap around a JScrollPane and/or JSplitPane
		
		fillFields();
	}
	
	private void initSettings() {
		settings = new JPanel();
		settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
		
		//---------------------------------------------
		
		JPanel population = new JPanel();
		JLabel populationLabel = new JLabel("Population Size");
		population.add(populationLabel);
		populationText = new JTextField(4);
		defaultborder = populationText.getBorder();
		populationText.setInputVerifier(new PositiveIntegerVerifier());
		population.add(populationText);
		population.setMaximumSize(population.getPreferredSize());
		population.setMinimumSize(population.getPreferredSize());
		population.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(population);
		
		//---------------------------------------------
		
		JPanel generations = new JPanel();
		JLabel generationsLabel = new JLabel("Generations");
		generations.add(generationsLabel);
		generationText = new JTextField(4);
		generationText.setInputVerifier(new PositiveIntegerVerifier());
		generations.add(generationText);
		generations.setMaximumSize(generations.getPreferredSize());
		generations.setMinimumSize(generations.getPreferredSize());
		generations.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(generations);
		
		JPanel iterPerInd = new JPanel();
		JLabel iterPerIndLabel = new JLabel("Iter per ind");
		iterPerInd.add(iterPerIndLabel);
		iterPerIndText = new JTextField(4);
		iterPerIndText.setInputVerifier(new PositiveIntegerVerifier());
		iterPerInd.add(iterPerIndText);
		iterPerInd.setMaximumSize(iterPerInd.getPreferredSize());
		iterPerInd.setMinimumSize(iterPerInd.getPreferredSize());
		iterPerInd.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(iterPerInd);
		
		JPanel chromosomeLength = new JPanel();
		JLabel chromosomeLengthLabel = new JLabel("Chromosome length");
		chromosomeLength.add(chromosomeLengthLabel);
		chromosomeLengthText = new JTextField(4);
		chromosomeLengthText.setInputVerifier(new PositiveIntegerVerifier());
		chromosomeLength.add(chromosomeLengthText);
		chromosomeLength.setMaximumSize(chromosomeLength.getPreferredSize());
		chromosomeLength.setMinimumSize(chromosomeLength.getPreferredSize());
		chromosomeLength.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(chromosomeLength);
		
		JPanel codonUpperBound = new JPanel();
		JLabel codonUpperBoundLabel = new JLabel("Codon upper bound");
		codonUpperBound.add(codonUpperBoundLabel);
		codonUpperBoundText = new JTextField(4);
		codonUpperBoundText.setInputVerifier(new PositiveIntegerVerifier());
		codonUpperBound.add(codonUpperBoundText);
		codonUpperBound.setMaximumSize(codonUpperBound.getPreferredSize());
		codonUpperBound.setMinimumSize(codonUpperBound.getPreferredSize());
		codonUpperBound.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(codonUpperBound);
		
		JPanel maxCntWrappings = new JPanel();
		JLabel maxCntWrappingsLabel = new JLabel("Max cnt wrappings");
		maxCntWrappings.add(maxCntWrappingsLabel);
		maxCntWrappingsText = new JTextField(4);
		maxCntWrappingsText.setInputVerifier(new PositiveIntegerAndZeroVerifier());
		maxCntWrappings.add(maxCntWrappingsText);
		maxCntWrappings.setMaximumSize(maxCntWrappings.getPreferredSize());
		maxCntWrappings.setMinimumSize(maxCntWrappings.getPreferredSize());
		maxCntWrappings.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(maxCntWrappings);
		
		//---------------------------------------------
		JSeparator a = new JSeparator();
		a.setMaximumSize(new Dimension(420, 1));
		settings.add(a);
		//---------------------------------------------
		
		JPanel objectivesPanel = new JPanel();
		objectivesPanel.setLayout(new BoxLayout(objectivesPanel, BoxLayout.Y_AXIS));
		
		JPanel objectivescacaPanel = new JPanel();
		JPanel objectivesTitle = new JPanel();
		JLabel numOfObjectivesLabel = new JLabel("Objectives #");
		objectivesTitle.add(numOfObjectivesLabel);
		numOfObjectivesText = new JTextField(4);
		numOfObjectivesText.setInputVerifier(new PositiveIntegerVerifier());
		numOfObjectivesText.setEditable(false);
		objectivesTitle.add(numOfObjectivesText);
		objectivescacaPanel.add(objectivesTitle);
		
		objectivescacaPanel.setMaximumSize(objectivescacaPanel.getPreferredSize());
		objectivescacaPanel.setMinimumSize(objectivescacaPanel.getPreferredSize());
		objectivescacaPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		objectivesPanel.add(objectivescacaPanel);
		
		btnSelectObjetives = new JButton("Select objectives");
		objectiveSelector = new ObjetiveSelectorPanel(new String[]{""}, new int[]{0});
		btnSelectObjetives.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, objectiveSelector, "Multi-objective selector", JOptionPane.PLAIN_MESSAGE);
				numOfObjectivesText.setText(String.valueOf(objectiveSelector.getSelectedIndices().length));
			}
		});
		btnSelectObjetives.setMaximumSize(btnSelectObjetives.getPreferredSize());
		btnSelectObjetives.setMinimumSize(btnSelectObjetives.getPreferredSize());
		btnSelectObjetives.setAlignmentX(Component.RIGHT_ALIGNMENT);
		objectivesPanel.add(btnSelectObjetives);
		
		objectivesPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(objectivesPanel);

		//---------------------------------------------
		JSeparator g = new JSeparator();
		g.setMaximumSize(new Dimension(420, 1));
		settings.add(g);
		//---------------------------------------------

		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));

			JPanel selectionOperatorPanel = new JPanel();
			selectionOperatorPanel.setLayout(new BoxLayout(selectionOperatorPanel, BoxLayout.Y_AXIS));

			JLabel selectionOperatorLabel = new JLabel("Selection operator");
			JPanel justforpadding5 = new JPanel();
			justforpadding5.add(selectionOperatorLabel);
			justforpadding5.setAlignmentX(Component.CENTER_ALIGNMENT);
			selectionOperatorPanel.add(justforpadding5);

			selectionOperatorBox = new JComboBox<>();
			selectionOperatorBox.setPreferredSize(new Dimension(200, selectionOperatorBox.getPreferredSize().height));
			selectionOperatorPanel.add(selectionOperatorBox);
			selectionOperatorPanel.setMaximumSize(selectionOperatorPanel.getPreferredSize());
			selectionOperatorPanel.setMinimumSize(selectionOperatorPanel.getPreferredSize());
			selectionOperatorPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			selectionPanel.add(selectionOperatorPanel);

		selectionPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(selectionPanel);

		//---------------------------------------------
		JSeparator b = new JSeparator();
		b.setMaximumSize(new Dimension(420, 1));
		settings.add(b);
		//---------------------------------------------
		
		JPanel crossoverPanel = new JPanel();
		crossoverPanel.setLayout(new BoxLayout(crossoverPanel, BoxLayout.Y_AXIS));

		JPanel crossoverMethodPanel = new JPanel();
		crossoverMethodPanel.setLayout(new BoxLayout(crossoverMethodPanel, BoxLayout.Y_AXIS));
		JLabel crossoverLabel = new JLabel("Crossover");
		JPanel crossSel = new JPanel();
		crossSel.add(crossoverLabel);
		crossoverText = new JTextField(4);
		crossoverText.setInputVerifier(new DoubleLessThanZeroVerifier());
		crossoverSlider = new JSlider(); // init here so that sliderupdater doesn't nullpointer
		crossoverText.getDocument().addDocumentListener(new SliderUpdater(crossoverSlider, crossoverText));
		crossSel.add(crossoverText);
		//crossoverBox = new JComboBox<String>();
		//crossSel.add(crossoverBox);
		crossoverMethodPanel.add(crossSel);
		crossoverMethodPanel.setMaximumSize(crossoverMethodPanel.getPreferredSize());
		crossoverMethodPanel.setMinimumSize(crossoverMethodPanel.getPreferredSize());
		crossoverMethodPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		crossoverPanel.add(crossoverMethodPanel);
		
		crossoverSlider.setMinimum(0);
		crossoverSlider.setMaximum(100);
		crossoverSlider.setMajorTickSpacing(30);
		crossoverSlider.setMinorTickSpacing(5);
		crossoverSlider.setPaintTicks(true);
		crossoverSlider.setPaintLabels(false);
		//crossoverSlider.setToolTipText(crossoverSlider.getValue() + " %");
		crossoverSlider.addChangeListener(new SliderListenerAndUpdater(crossoverText));
		crossoverSlider.setMaximumSize(crossoverSlider.getPreferredSize());
		crossoverSlider.setMinimumSize(crossoverSlider.getPreferredSize());
		crossoverSlider.setAlignmentX(Component.RIGHT_ALIGNMENT);
		crossoverPanel.add(crossoverSlider);

		JPanel crossoverOperatorPanel = new JPanel();
			crossoverOperatorPanel.setLayout(new BoxLayout(crossoverOperatorPanel, BoxLayout.Y_AXIS));

			JLabel crossoverOperatorLabel = new JLabel("Crossover operator");
			JPanel justforpadding3 = new JPanel();
			justforpadding3.add(crossoverOperatorLabel);
			justforpadding3.setAlignmentX(Component.CENTER_ALIGNMENT);
			crossoverOperatorPanel.add(justforpadding3);

			crossoverOperatorBox = new JComboBox<>();
			crossoverOperatorBox.setPreferredSize(new Dimension(200, crossoverOperatorBox.getPreferredSize().height));
			crossoverOperatorPanel.add(crossoverOperatorBox);
			crossoverOperatorPanel.setMaximumSize(crossoverOperatorPanel.getPreferredSize());
			crossoverOperatorPanel.setMinimumSize(crossoverOperatorPanel.getPreferredSize());
			crossoverOperatorPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			crossoverPanel.add(crossoverOperatorPanel);
		
		crossoverPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(crossoverPanel);
		

		//---------------------------------------------
		JSeparator c = new JSeparator();
		c.setMaximumSize(new Dimension(420, 1));
		settings.add(c);
		//---------------------------------------------
		
		JPanel mutationPanel = new JPanel();
		mutationPanel.setLayout(new BoxLayout(mutationPanel, BoxLayout.Y_AXIS));

		JPanel mutationMethodPanel = new JPanel();
		JPanel mutationTitle = new JPanel();
		JLabel mutationLabel = new JLabel("Mutation");
		mutationTitle.add(mutationLabel);
		mutationText = new JTextField(4);
		mutationText.setInputVerifier(new DoubleLessThanZeroVerifier());
		mutationSlider = new JSlider(); // init here so that sliderupdater doesn't nullpointer
		mutationText.getDocument().addDocumentListener(new SliderUpdater(mutationSlider, mutationText));
		mutationTitle.add(mutationText);
		mutationMethodPanel.add(mutationTitle);
		//mutationBox = new JComboBox<String>();
		//mutationMethodPanel.add(mutationBox);
		mutationMethodPanel.setMaximumSize(mutationMethodPanel.getPreferredSize());
		mutationMethodPanel.setMinimumSize(mutationMethodPanel.getPreferredSize());
		mutationMethodPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		mutationPanel.add(mutationMethodPanel);

		mutationSlider.setMinimum(0);
		mutationSlider.setMaximum(100);
		mutationSlider.setMajorTickSpacing(30);
		mutationSlider.setMinorTickSpacing(5);
		mutationSlider.setPaintTicks(true);
		mutationSlider.setPaintLabels(false);
		//mutationSlider.setToolTipText(mutationSlider.getValue() + " %");
		mutationSlider.addChangeListener(new SliderListenerAndUpdater(mutationText));
		mutationSlider.addChangeListener(new SliderListener());
		mutationSlider.setMaximumSize(mutationSlider.getPreferredSize());
		mutationSlider.setMinimumSize(mutationSlider.getPreferredSize());
		mutationSlider.setAlignmentX(Component.RIGHT_ALIGNMENT);
		mutationPanel.add(mutationSlider);

		JPanel mutationOperatorPanel = new JPanel();
			mutationOperatorPanel.setLayout(new BoxLayout(mutationOperatorPanel, BoxLayout.Y_AXIS));

			JLabel mutationOperatorLabel = new JLabel("Mutation operator");
			JPanel justforpadding4 = new JPanel();
			justforpadding4.add(mutationOperatorLabel);
			justforpadding4.setAlignmentX(Component.CENTER_ALIGNMENT);
			mutationOperatorPanel.add(justforpadding4);

			mutationOperatorBox = new JComboBox<>();
			mutationOperatorBox.setPreferredSize(new Dimension(200, mutationOperatorBox.getPreferredSize().height));
			mutationOperatorPanel.add(mutationOperatorBox);
			mutationOperatorPanel.setMaximumSize(mutationOperatorPanel.getPreferredSize());
			mutationOperatorPanel.setMinimumSize(mutationOperatorPanel.getPreferredSize());
			mutationOperatorPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			mutationPanel.add(mutationOperatorPanel);
		
		mutationPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(mutationPanel);

		//---------------------------------------------

		JPanel neutralMutation = new JPanel();
		JLabel cbterm = new JLabel("+ Neutral mutation");
		neutralMutation.add(cbterm);
		neutralMutationCheck = new JCheckBox();
		neutralMutation.add(neutralMutationCheck);
		neutralMutation.setMaximumSize(neutralMutation.getPreferredSize());
		neutralMutation.setMinimumSize(neutralMutation.getPreferredSize());
		neutralMutation.setAlignmentX(Component.RIGHT_ALIGNMENT);
		neutralMutation.setToolTipText("Whereby a codon changes value but still encodes for the same derivation.");
		settings.add(neutralMutation);
		
		//---------------------------------------------
		JSeparator e = new JSeparator();
		e.setMaximumSize(new Dimension(420, 1));
		settings.add(e);
		//---------------------------------------------
		
		
		JPanel elitism = new JPanel();
		elitism.setLayout(new BoxLayout(elitism, BoxLayout.Y_AXIS));

		JPanel elitismMethodPanel = new JPanel();
		JPanel elitismTitle = new JPanel();
		JLabel elitismLabel = new JLabel("Elitism");
		elitismTitle.add(elitismLabel);
		elitismText = new JTextField(4);
		elitismText.setInputVerifier(new DoubleLessThanZeroVerifier());
		elitismSlider = new JSlider(); // init here so that sliderupdater doesn't nullpointer
		elitismText.getDocument().addDocumentListener(new SliderUpdater(elitismSlider, elitismText));
		elitismTitle.add(elitismText);
		elitismMethodPanel.add(elitismTitle);
		//mutationBox = new JComboBox<String>();
		//mutationMethodPanel.add(mutationBox);
		elitismMethodPanel.setMaximumSize(elitismMethodPanel.getPreferredSize());
		elitismMethodPanel.setMinimumSize(elitismMethodPanel.getPreferredSize());
		elitismMethodPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		elitism.add(elitismMethodPanel);

		elitismSlider.setMinimum(0);
		elitismSlider.setMaximum(100);
		elitismSlider.setMajorTickSpacing(30);
		elitismSlider.setMinorTickSpacing(5);
		elitismSlider.setPaintTicks(true);
		elitismSlider.setPaintLabels(false);
		//elitismSlider.setToolTipText(elitismSlider.getValue() + " %");
		elitismSlider.addChangeListener(new SliderListenerAndUpdater(elitismText));
		elitismSlider.addChangeListener(new SliderListener());
		elitismSlider.setMaximumSize(elitismSlider.getPreferredSize());
		elitismSlider.setMinimumSize(elitismSlider.getPreferredSize());
		elitismSlider.setAlignmentX(Component.RIGHT_ALIGNMENT);
		elitism.add(elitismSlider);
		
		elitism.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(elitism);
		
		//---------------------------------------------
		JSeparator d = new JSeparator();
		d.setMaximumSize(new Dimension(420, 1));
		settings.add(d);
		//---------------------------------------------
				
		grammar = new JPanel();
		grammar.setLayout(new BoxLayout(grammar, BoxLayout.Y_AXIS));
		
		JLabel grammarLabel = new JLabel("Grammar");
		JPanel justforpadding = new JPanel();
		justforpadding.add(grammarLabel);
		justforpadding.setAlignmentX(Component.CENTER_ALIGNMENT);
		grammar.add(justforpadding);
		
		grammarBox = new JComboBox<>();
		grammarBox.setPreferredSize(new Dimension(200, grammarBox.getPreferredSize().height));
		grammar.add(grammarBox);
		grammar.setMaximumSize(grammar.getPreferredSize());
		grammar.setMinimumSize(grammar.getPreferredSize());
		grammar.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(grammar);
		
		//---------------------------------------------
		JSeparator f = new JSeparator();
		f.setMaximumSize(new Dimension(420, 1));
		settings.add(f);
		//---------------------------------------------

		JPanel ghostControllerPanel = new JPanel();
		ghostControllerPanel.setLayout(new BoxLayout(ghostControllerPanel, BoxLayout.Y_AXIS));
		
		JLabel ghostControllerLabel = new JLabel("Ghost controller");
		JPanel justforpadding2 = new JPanel();
		justforpadding2.add(ghostControllerLabel);
		justforpadding2.setAlignmentX(Component.CENTER_ALIGNMENT);
		ghostControllerPanel.add(justforpadding2);
		
		ghostControllerBox = new JComboBox<>();
		ghostControllerBox.setPreferredSize(new Dimension(200, ghostControllerBox.getPreferredSize().height));
		ghostControllerPanel.add(ghostControllerBox);
		ghostControllerPanel.setMaximumSize(ghostControllerPanel.getPreferredSize());
		ghostControllerPanel.setMinimumSize(ghostControllerPanel.getPreferredSize());
		ghostControllerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		settings.add(ghostControllerPanel);
	}
	
	private void fillFields() {
		this.populationText.setText(String.valueOf(this.gCtrl.getPopulationSize()));
		this.generationText.setText(String.valueOf(this.gCtrl.getGenerations()));
		this.crossoverText.setText(String.valueOf(this.gCtrl.getCrossProb()));
		this.crossoverSlider.setValue((int) (this.gCtrl.getCrossProb() * 100));
		this.mutationText.setText(String.valueOf(this.gCtrl.getMutationProb()));
		this.mutationSlider.setValue((int) (this.gCtrl.getMutationProb() * 100));
		this.elitismText.setText(String.valueOf(this.gCtrl.getElitismPercentage()));
		this.elitismSlider.setValue((int) (this.gCtrl.getElitismPercentage() * 100));
		this.neutralMutationCheck.setSelected(this.gCtrl.isNeutralMutationEnabled());
		for(String item : this.gCtrl.getCleanGrammarNames()) {
			this.grammarBox.addItem(item);
		}
		this.grammarBox.setSelectedItem(this.gCtrl.getCleanGrammar());
		grammar.setMaximumSize(grammar.getPreferredSize());
		grammar.setMinimumSize(grammar.getPreferredSize());
		for(String item : this.gCtrl.getGhostControllerNames()) {
			this.ghostControllerBox.addItem(item);
		}
		this.ghostControllerBox.setSelectedItem(this.gCtrl.getGhostControllerName());
		ghostControllerBox.setMaximumSize(ghostControllerBox.getPreferredSize());
		ghostControllerBox.setMinimumSize(ghostControllerBox.getPreferredSize());

		for(String item : this.gCtrl.getSelectionOperatorNames()) {
			this.selectionOperatorBox.addItem(item);
		}
		this.selectionOperatorBox.setSelectedItem(this.gCtrl.getSelectedSelectionOperator());
		selectionOperatorBox.setMaximumSize(selectionOperatorBox.getPreferredSize());
		selectionOperatorBox.setMinimumSize(selectionOperatorBox.getPreferredSize());

		for(String item : this.gCtrl.getCrossoverOperatorNames()) {
			this.crossoverOperatorBox.addItem(item);
		}
		this.crossoverOperatorBox.setSelectedItem(this.gCtrl.getSelectedCrossoverOperator());
		crossoverOperatorBox.setMaximumSize(crossoverOperatorBox.getPreferredSize());
		crossoverOperatorBox.setMinimumSize(crossoverOperatorBox.getPreferredSize());

		for(String item : this.gCtrl.getMutationOperatorNames()) {
			this.mutationOperatorBox.addItem(item);
		}
		this.mutationOperatorBox.setSelectedItem(this.gCtrl.getSelectedMutationOperator());
		mutationOperatorBox.setMaximumSize(mutationOperatorBox.getPreferredSize());
		mutationOperatorBox.setMinimumSize(mutationOperatorBox.getPreferredSize());

		this.iterPerIndText.setText(String.valueOf(this.gCtrl.getItersPerIndividual()));
		this.chromosomeLengthText.setText(String.valueOf(this.gCtrl.getChromosomeLength()));
		this.codonUpperBoundText.setText(String.valueOf(this.gCtrl.getCodonUpperBound()));
		this.maxCntWrappingsText.setText(String.valueOf(this.gCtrl.getMaxCntWrappings()));
		this.numOfObjectivesText.setText(String.valueOf(this.gCtrl.getNumOfSelectedObjectives()));
		this.objectiveSelector.setOptions(this.gCtrl.getObjectivesNames());
		this.objectiveSelector.setSelectedValues(this.gCtrl.getSelectedObjectivesNames());
		
		saveDefaults();
	}
	
	private void saveDefaults() {
		populationTextDefault = populationText.getText();
		generationTextDefault = generationText.getText();
		iterPerIndTextDefault = iterPerIndText.getText();
		chromosomeLengthTextDefault = chromosomeLengthText.getText();
		codonUpperBoundTextDefault = codonUpperBoundText.getText();
		maxCntWrappingsTextDefault = maxCntWrappingsText.getText();

		crossoverSliderDefault = crossoverSlider.getValue();
		mutationSliderDefault = mutationSlider.getValue();
		elitismSliderDefault = elitismSlider.getValue();
		neutralMutationCheckDefault = neutralMutationCheck.isSelected();
		grammarBoxDefault = grammarBox.getSelectedItem();
		objectiveSelectedIndicesDefault = objectiveSelector.getSelectedIndices();
		selectedGhostControllerDefault = ghostControllerBox.getSelectedItem();
		selectedSelectionOperatorDefault = selectionOperatorBox.getSelectedItem();
		selectedCrossoverOperatorDefault = crossoverOperatorBox.getSelectedItem();
		selectedMutationOperatorDefault = mutationOperatorBox.getSelectedItem();
	}
	
	private void restoreDefaults() {
		populationText.setText(populationTextDefault);
		generationText.setText(generationTextDefault);
		iterPerIndText.setText(iterPerIndTextDefault);
		chromosomeLengthText.setText(chromosomeLengthTextDefault);
		codonUpperBoundText.setText(codonUpperBoundTextDefault);
		maxCntWrappingsText.setText(maxCntWrappingsTextDefault);
		numOfObjectivesText.setText(String.valueOf(this.gCtrl.getNumOfSelectedObjectives()));

		crossoverSlider.setValue(crossoverSliderDefault);
		mutationSlider.setValue(mutationSliderDefault);
		elitismSlider.setValue(elitismSliderDefault);
		neutralMutationCheck.setSelected(neutralMutationCheckDefault);
		grammarBox.setSelectedItem(grammarBoxDefault);
		objectiveSelector.setSelectedIndices(objectiveSelectedIndicesDefault);
		ghostControllerBox.setSelectedItem(selectedGhostControllerDefault);
		selectionOperatorBox.setSelectedItem(selectedSelectionOperatorDefault);
		crossoverOperatorBox.setSelectedItem(selectedCrossoverOperatorDefault);
		mutationOperatorBox.setSelectedItem(selectedMutationOperatorDefault);
	}

	@Override
	public void onStart() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				trainButton.setEnabled(false);
				resetButton.setEnabled(false);
				
				populationText.setEnabled(false);
			 	generationText.setEnabled(false);
			 	iterPerIndText.setEnabled(false);
			 	chromosomeLengthText.setEnabled(false);
			 	codonUpperBoundText.setEnabled(false);
			 	maxCntWrappingsText.setEnabled(false);
			 	numOfObjectivesText.setEnabled(false);
			 	crossoverText.setEnabled(false);
			 	crossoverSlider.setEnabled(false);
			 	mutationText.setEnabled(false);
			 	mutationSlider.setEnabled(false);
			 	elitismText.setEnabled(false);
			 	elitismSlider.setEnabled(false);
			 	neutralMutationCheck.setEnabled(false);
			 	grammarBox.setEnabled(false);
			 	btnSelectObjetives.setEnabled(false);
			 	ghostControllerBox.setEnabled(false);
			 	selectionOperatorBox.setEnabled(false);
			 	crossoverOperatorBox.setEnabled(false);
			 	mutationOperatorBox.setEnabled(false);
			}
		});
	}

	@Override
	public void onEnd() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showAndPlayButton.setVisible(true);
				for (Component cmp : buttonPanel.getComponents()) {
					cmp.setEnabled(true);
				}
				
				populationText.setEnabled(true);
			 	generationText.setEnabled(true);
			 	iterPerIndText.setEnabled(true);
			 	chromosomeLengthText.setEnabled(true);
			 	codonUpperBoundText.setEnabled(true);
			 	maxCntWrappingsText.setEnabled(true);
			 	numOfObjectivesText.setEnabled(false);
			 	crossoverText.setEnabled(true);
			 	crossoverSlider.setEnabled(true);
			 	mutationText.setEnabled(true);
			 	mutationSlider.setEnabled(true);
			 	elitismText.setEnabled(true);
			 	elitismSlider.setEnabled(true);
			 	neutralMutationCheck.setEnabled(true);
			 	grammarBox.setEnabled(true);
			 	btnSelectObjetives.setEnabled(true);
			 	ghostControllerBox.setEnabled(true);
			 	selectionOperatorBox.setEnabled(true);
			 	crossoverOperatorBox.setEnabled(true);
			 	mutationOperatorBox.setEnabled(true);
			}
		});
	}
	
	@Override
	public void onIncrement(int n) {
		
	}

	class SliderListenerAndUpdater implements ChangeListener {
		final JTextField text;
		
		public SliderListenerAndUpdater(JTextField text) {
			this.text = text;
		}
		
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	        	if((Double.parseDouble(this.text.getText()) * 100) != source.getValue()) {
	        		try {
	        			this.text.setText(String.valueOf(source.getValue() / 100.0));
	        		} catch(IllegalStateException ex) {
	        			// too fast clicking
	        		}
	        	}
	        }
		}
	}
	
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            int num = source.getValue();
	            source.setToolTipText(String.valueOf(num) + " %");
	        }
		}
	}
	
	class PositiveIntegerAndZeroVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			JTextField field = (JTextField) input;
			try {
				int a = Integer.parseInt(((JTextField) input).getText());
				if (a >= 0) {
					field.setBorder(defaultborder);
					guiCtrl.setErrors(false);
					return true;
				}
				else {
					field.setBorder(BorderFactory.createLineBorder(Color.red));
					guiCtrl.setErrors(true);
					return false;
				}
			} catch (NumberFormatException e) {
				field.setBorder(BorderFactory.createLineBorder(Color.red));
				guiCtrl.setErrors(true);
				return false;
			}
		}
		
	}
	
	class PositiveIntegerVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			JTextField field = (JTextField) input;
			try {
				int a = Integer.parseInt(((JTextField) input).getText());
				if (a >= 1) {
					field.setBorder(defaultborder);
					guiCtrl.setErrors(false);
					return true;
				}
				else {
					field.setBorder(BorderFactory.createLineBorder(Color.red));
					guiCtrl.setErrors(true);
					return false;
				}
			} catch (NumberFormatException e) {
				field.setBorder(BorderFactory.createLineBorder(Color.red));
				guiCtrl.setErrors(true);
				return false;
			}
		}
		
	}
	
	class DoubleLessThanZeroVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			JTextField field = (JTextField) input;
			try {
				double a = Double.parseDouble(((JTextField) input).getText());
				if (a >= 0 && a <= 1) {
					field.setBorder(defaultborder);
					guiCtrl.setErrors(false);
					return true;
				}
				else {
					field.setBorder(BorderFactory.createLineBorder(Color.red));
					guiCtrl.setErrors(true);
					return false;
				}
			} catch (NumberFormatException e) {
				field.setBorder(BorderFactory.createLineBorder(Color.red));
				guiCtrl.setErrors(true);
				return false;
			}
		}
		
	}

	class SliderUpdater implements DocumentListener {
		final JSlider slider;
		final JTextField text;

		public SliderUpdater(JSlider slider, JTextField text) {
			this.slider = slider;
			this.text = text;
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			try {
				this.slider.setValue( (int) (Double.parseDouble(this.text.getText()) * 100) );
				this.text.setBorder(defaultborder);
			} catch (NumberFormatException ex) {
				this.text.setBorder(BorderFactory.createLineBorder(Color.red));
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			try {
				this.slider.setValue( (int) (Double.parseDouble(this.text.getText()) * 100) );
				this.text.setBorder(defaultborder);
			} catch (NumberFormatException ex) {
				this.text.setBorder(BorderFactory.createLineBorder(Color.red));
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	}

}
