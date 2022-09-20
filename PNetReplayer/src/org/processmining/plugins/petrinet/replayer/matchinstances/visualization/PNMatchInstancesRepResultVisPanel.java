/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.visualization;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.replayresult.visualization.ProMPropertiesPanelWithComp;
import org.processmining.plugins.petrinet.replayresult.visualization.ProMTableWithoutHeader;
import org.processmining.plugins.petrinet.replayresult.visualization.ProcessInstanceConformanceView;
import org.processmining.plugins.petrinet.visualization.AlignmentConstants;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * @author aadrians Feb 27, 2013
 * 
 */
public class PNMatchInstancesRepResultVisPanel extends JPanel {
	private static final long serialVersionUID = 1567293647506986556L;

	/**
	 * Pointers to property variable
	 */
	protected static int RELIABLEMIN = 0;
	protected static int RELIABLEMAX = 1;
	protected static int MIN = 2;
	protected static int MAX = 3;

	// standard deviation is calculated based on http://mathcentral.uregina.ca/QQ/database/QQ.09.02/carlos1.html
	protected static int SVAL = 4;
	protected static int MVAL = 6;
	protected static int SVALRELIABLE = 5;
	protected static int MVALRELIABLE = 7;
	protected static int PERFECTCASERELIABLECOUNTER = 8;
	
	// this value has to be stored because it is used by actionListener
	protected int numReliableCaseInvolved = 0;
	protected long totalTime = 0L;
	
	// total calculated values
	protected Map<String, Double[]> calculations = new HashMap<String, Double[]>();
	
	protected final DefaultTableModel reliableCasesTModel = new DefaultTableModel() {
		private static final long serialVersionUID = -4303950078200984098L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public PNMatchInstancesRepResultVisPanel(PetrinetGraph net, XLog log, PNMatchInstancesRepResult logReplayResult,
			Progress progress) {
		TableLayout mainLayout = new TableLayout(new double[][] { { TableLayout.FILL }, { TableLayout.FILL } });
		setLayout(mainLayout);
		setBorder(BorderFactory.createEmptyBorder());
		if (progress != null) {
			progress.setMaximum(logReplayResult.size());
		}
		add(createBottomPanel(net, log, logReplayResult, progress), "0,0");
	}

	private Component createBottomPanel(PetrinetGraph net, final XLog log, final PNMatchInstancesRepResult allLogReplayResult,
			Progress progress) {
		// coloring scheme
		Color bgColor = new Color(30, 30, 30);

		// collected stats
		int numCaseInvolved = 0;
		
		// add util
		SlickerFactory factory = SlickerFactory.instance();

		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		final NumberFormat nfi = NumberFormat.getInstance();
		nfi.setMaximumFractionDigits(0);
		nfi.setMinimumFractionDigits(0);

		final ProMPropertiesPanelWithComp logAlignmentPanel = new ProMPropertiesPanelWithComp("Log-model Alignments");
				
		// loop of one trace and a petri net
		for (AllSyncReplayResult logReplayResult : allLogReplayResult){
			if (progress != null) {
				progress.inc();
			}

			// create combobox that shows cases with the same all alignments
			SortedSet<String> caseIDSets = new TreeSet<String>(new AlphanumComparator());
			XConceptExtension ce = XConceptExtension.instance();
			for (int index : logReplayResult.getTraceIndex()) {
				String name = ce.extractName(log.get(index));
				if (name == null) {
					name = String.valueOf(index);
				}
				caseIDSets.add(name);
			}
			int caseIDSize = caseIDSets.size();
			
			// panel that shows alignment
			JPanel allAlignmentPanel = new JPanel();
			allAlignmentPanel.setLayout(new BoxLayout(allAlignmentPanel, BoxLayout.Y_AXIS));
			
			List<List<Object>> niList = logReplayResult.getNodeInstanceLst();
			List<List<StepTypes>> stepTypeList = logReplayResult.getStepTypesLst();
			
			Iterator<List<Object>> niListIt = niList.iterator();
			Iterator<List<StepTypes>> stepTypeListIt = stepTypeList.iterator();
			Iterator<Map<String, Double>> infoIt = logReplayResult.getSingleInfo().iterator();

			// loop of an alignment from many alignments
			// create process instance conformance view
			while (niListIt.hasNext()){
				List<Object> result = new ArrayList<Object>(niList.size());
				for (Object obj : niListIt.next()) {
					if (obj instanceof Transition) {
						result.add(((Transition) obj).getLabel());
					} else if (obj instanceof String) {
						result.add(obj);
					} else {
						result.add(obj.toString());
					}
				}
				
				String conformanceViewTitle = "Alignment";
				
				if (infoIt.hasNext()) {
					Map<String, Double> currentSingleInfo = infoIt.next();
					if (currentSingleInfo.containsKey(PNRepResult.TRACEFITNESS)) {
						conformanceViewTitle = String.format("Alignment %.02f", currentSingleInfo.get(PNRepResult.TRACEFITNESS));	
					}					
				}				

				ProcessInstanceConformanceView conformanceView = new ProcessInstanceConformanceView(conformanceViewTitle, result,
						stepTypeListIt.next());
				allAlignmentPanel.add(new ScrollBar(conformanceView));
			}

			// collect stats
			// create label for combobox
			JLabel lbl1 = factory.createLabel(caseIDSize + " case(s) :");
			lbl1.setForeground(Color.WHITE);

			JComboBox combo = factory.createComboBox(caseIDSets.toArray());
			combo.setPreferredSize(new Dimension(200, combo.getPreferredSize().height));
			combo.setMinimumSize(new Dimension(200, combo.getPreferredSize().height));
			combo.setMaximumSize(new Dimension(200, combo.getPreferredSize().height));
			
			totalTime += logReplayResult.getInfo().get(PNMatchInstancesRepResult.TIME);

			// create table for map info
			Object[][] infoSingleTrace;
			Map<String, Double> allInfo = logReplayResult.getInfo();
			// check if there is object info
			Map<InfoObjectConst, Object> allInfoObject = logReplayResult.getInfoObject();
			if (allInfoObject == null){
				infoSingleTrace = new Object[allInfo.size() + 2][2];
			} else {
				infoSingleTrace = new Object[allInfo.size() + 2 + (2 * allInfoObject.size())][2];
			}

			int propCounter = 0;
			infoSingleTrace[propCounter++] = new Object[] { "#Cases", nfi.format(caseIDSize) };
			infoSingleTrace[propCounter++] = new Object[] { "Is Alignment Reliable?", logReplayResult.isReliable() ? "Yes" : "No" };

			for (Entry<String, Double> e : allInfo.entrySet()){
				
				if (Math.floor(e.getValue()) == Math.ceil(e.getValue())) {
					infoSingleTrace[propCounter++] = new Object[] { e.getKey(), nfi.format(e.getValue()) };
				} else {
					infoSingleTrace[propCounter++] = new Object[] { e.getKey(), nf.format(e.getValue()) };
				}
				
				// use it to calculate property
				Double[] oldValues = calculations.get(e.getKey());
				if (oldValues == null) {
					oldValues = new Double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE,
							0.00000, 0.00000, 0.00000, 0.00000, 0.00000 };
					calculations.put(e.getKey(), oldValues);
				}
				if (Double.compare(oldValues[MIN], e.getValue()) > 0) {
					oldValues[MIN] = e.getValue();
				}
				if (Double.compare(oldValues[MAX], e.getValue()) < 0) {
					oldValues[MAX] = e.getValue();
				}
				
				int counterCaseIDSize = 0;
				if (numCaseInvolved == 0) {
					oldValues[MVAL] = e.getValue();
					oldValues[SVAL] = 0.0000;
					counterCaseIDSize++;
				}
				for (int i = counterCaseIDSize; i < caseIDSize; i++) {
					double oldMVal = oldValues[MVAL];
					oldValues[MVAL] += ((e.getValue() - oldValues[MVAL]) / (i + numCaseInvolved + 1));
					oldValues[SVAL] += ((e.getValue() - oldMVal) * (e.getValue() - oldValues[MVAL]));
				}

				if (logReplayResult.isReliable()) {
					if (Double.compare(oldValues[RELIABLEMIN], e.getValue()) > 0) {
						oldValues[RELIABLEMIN] = e.getValue();
					}
					if (Double.compare(oldValues[RELIABLEMAX], e.getValue()) < 0) {
						oldValues[RELIABLEMAX] = e.getValue();
					}

					counterCaseIDSize = 0;
					if (numReliableCaseInvolved == 0) {
						oldValues[MVALRELIABLE] = e.getValue();
						oldValues[SVALRELIABLE] = 0.0000;
						counterCaseIDSize++;
					}
					for (int i = counterCaseIDSize; i < caseIDSize; i++) {
						double oldMVal = oldValues[MVALRELIABLE];
						oldValues[MVALRELIABLE] += ((e.getValue() - oldValues[MVALRELIABLE]) / (i + numReliableCaseInvolved + 1));
						oldValues[SVALRELIABLE] += ((e.getValue() - oldMVal) * (e.getValue() - oldValues[MVALRELIABLE]));
					}

					if (Double.compare(e.getValue(), 1.0000000) == 0) {
						oldValues[PERFECTCASERELIABLECOUNTER] += caseIDSize;
					}
				}
			}
			
			if (allInfoObject != null){
				for (Entry<InfoObjectConst, Object> entry : allInfoObject.entrySet()){
					switch (entry.getKey()){
						case NUMREPRESENTEDALIGNMENT:
							@SuppressWarnings("unchecked")
							List<Integer> intList = (List<Integer>) entry.getValue();
							double sizeList = intList.size();
							infoSingleTrace[propCounter++] = new Object[] { "#Represented", intList.toString() };
							infoSingleTrace[propCounter++] = new Object[] { "#Representatives", nfi.format(sizeList) }; // number of representatives
							
							// use it to calculate property
							Double[] oldValues = calculations.get("#Representatives per trace");
							if (oldValues == null) {
								oldValues = new Double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE,
										0.00000, 0.00000, 0.00000, 0.00000, 0.00000 };
								calculations.put("#Representatives per trace", oldValues);
							}
							if (Double.compare(oldValues[MIN], sizeList) > 0) {
								oldValues[MIN] = sizeList;
							}
							if (Double.compare(oldValues[MAX], sizeList) < 0) {
								oldValues[MAX] = sizeList;
							}
							
							int counterCaseIDSize = 0;
							if (numCaseInvolved == 0) {
								oldValues[MVAL] = sizeList;
								oldValues[SVAL] = 0.0000;
								counterCaseIDSize++;
							}
							for (int i = counterCaseIDSize; i < caseIDSize; i++) {
								double oldMVal = oldValues[MVAL];
								oldValues[MVAL] += ((sizeList - oldValues[MVAL]) / (i + numCaseInvolved + 1));
								oldValues[SVAL] += ((sizeList - oldMVal) * (sizeList - oldValues[MVAL]));
							}

							if (logReplayResult.isReliable()) {
								if (Double.compare(oldValues[RELIABLEMIN], sizeList) > 0) {
									oldValues[RELIABLEMIN] = sizeList;
								}
								if (Double.compare(oldValues[RELIABLEMAX], sizeList) < 0) {
									oldValues[RELIABLEMAX] = sizeList;
								}

								counterCaseIDSize = 0;
								if (numReliableCaseInvolved == 0) {
									oldValues[MVALRELIABLE] = sizeList;
									oldValues[SVALRELIABLE] = 0.0000;
									counterCaseIDSize++;
								}
								for (int i = counterCaseIDSize; i < caseIDSize; i++) {
									double oldMVal = oldValues[MVALRELIABLE];
									oldValues[MVALRELIABLE] += ((sizeList - oldValues[MVALRELIABLE]) / (i + numReliableCaseInvolved + 1));
									oldValues[SVALRELIABLE] += ((sizeList - oldMVal) * (sizeList - oldValues[MVALRELIABLE]));
								}

								if (Double.compare(sizeList, 1.0000000) == 0) {
									oldValues[PERFECTCASERELIABLECOUNTER] += caseIDSize;
								}
							}
							break;
					}
				}
			}
			
			DefaultTableModel tableModel = new DefaultTableModel(infoSingleTrace, new Object[] { "Property", "Value" }) {
				private static final long serialVersionUID = -4303950078200984098L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			ProMTableWithoutHeader promTable = new ProMTableWithoutHeader(tableModel);
			promTable.setPreferredWidth(0, 180);
			promTable.setPreferredWidth(1, 100);
			RoundedPanel alignmentStatsPanel = new RoundedPanel(10);
			TableLayout leftPanelLayout = new TableLayout(new double[][] { { TableLayout.PREFERRED, TableLayout.FILL },
					{ 25, TableLayout.FILL } });
			alignmentStatsPanel.setLayout(leftPanelLayout);
			JLabel lblCaseId = new JLabel("Case id(s):");
			lblCaseId.setForeground(Color.white);
			alignmentStatsPanel.add(lblCaseId, "0,0");
			alignmentStatsPanel.add(combo, "1,0");
			alignmentStatsPanel.add(promTable, "0,1,1,1");
			alignmentStatsPanel.setPreferredSize(new Dimension(400, 100));
			alignmentStatsPanel.setMaximumSize(new Dimension(400, 400));
			if (logReplayResult.isReliable()) {
				alignmentStatsPanel.setBackground(new Color(70, 70, 70, 210));
			} else {
				alignmentStatsPanel.setBackground(Color.RED);
			}
			
			// add it to current panel
			logAlignmentPanel.addProperty(alignmentStatsPanel, new ScrollBar(allAlignmentPanel));

			// calculate stats
			numCaseInvolved += caseIDSize;
			if (logReplayResult.isReliable()){
				numReliableCaseInvolved += caseIDSize;
			}
			
		}
		
		// reliable replay result
		ProMTableWithoutHeader tableReliableResult = createTable(numReliableCaseInvolved, totalTime, allLogReplayResult.getInfo(), nfi);
		
		// STATS PANEL
		int lineNumberRight = 0;
		JPanel statisticPanel = new JPanel();
		statisticPanel.setBackground(bgColor);
		double[][] rightMainPanelSize = new double[][] {
				{ TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, 30, TableLayout.PREFERRED, 30, TableLayout.PREFERRED, TableLayout.PREFERRED,
						60, TableLayout.PREFERRED } };

		statisticPanel.setLayout(new TableLayout(rightMainPanelSize));

		// add LEGEND
		statisticPanel.add(createLegendPanel(), "0, " + lineNumberRight++ + ", c, t");

		// add STATS FROM RELIABLE ALIGNMENTS
		JLabel lblReliable = factory.createLabel("STATS FROM RELIABLE ALIGNMENTS");
		statisticPanel.add(lblReliable, "0, " + lineNumberRight++ + ", c, b");

		statisticPanel.add(tableReliableResult, "0, " + lineNumberRight++ + ", c, t");
		
		// add ALIGNMENT STATISTICS
		Map<InfoObjectConst, Object> infoObj = null;
		// check additional info
		if ((infoObj = allLogReplayResult.iterator().next().getInfoObject()) != null){
			// add information about the minimum, max, and average value of the info object
			for (Entry<InfoObjectConst, Object> entry : infoObj.entrySet()){
				switch (entry.getKey()){
					case NUMREPRESENTEDALIGNMENT:
						// iterate through all num represented alignment
						int min = -1;
						int max = -1;
						
						Iterator<AllSyncReplayResult> it = allLogReplayResult.iterator();
						while (it.hasNext()){
							@SuppressWarnings("unchecked")
							List<Integer> lst = (List<Integer>) it.next().getInfoObject().get(InfoObjectConst.NUMREPRESENTEDALIGNMENT);
							
							for (int e : lst){
								if (min < 0){
									min = e;
								} else {
									min = Math.min(e, min);
								}
								
								if (max < 0){
									max = e;
								} else {
									max = Math.max(e,max);
								}
							}
						}
						
						Double[] val = new Double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE,
								0.00000, 0.00000, 0.00000, 0.00000, 0.00000};
						val[MIN] = (double) min;
						val[MAX] = (double) max;
						val[RELIABLEMAX] = val[MAX];
						val[RELIABLEMIN] = val[MIN];
						
						calculations.put("#Represented by a Representative (max/min)", val);
						break;
				};
			}
		} 
		
		Set<String> allProp = calculations.keySet();		
		final JComboBox comboAllCases = factory.createComboBox(allProp.toArray(new Object[allProp.size()]));
		comboAllCases.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				populateValue(comboAllCases.getSelectedItem().toString(), nf, nfi, numReliableCaseInvolved);
			}
		});
		comboAllCases.setPreferredSize(new Dimension(300, comboAllCases.getPreferredSize().height));
		comboAllCases.setMinimumSize(new Dimension(200, comboAllCases.getPreferredSize().height));

		JLabel lblStats = factory.createLabel("ALIGNMENT STATISTICS");
		statisticPanel.add(lblStats, "0, " + lineNumberRight++ + ", c, b");

		statisticPanel.add(comboAllCases, "0, " + lineNumberRight++ + ", c, t");

		// create statistics table
		populateValue(comboAllCases.getSelectedItem().toString(), nf, nfi, numReliableCaseInvolved);
		ProMTableWithoutHeader reliableCasesStatistics = new ProMTableWithoutHeader(this.reliableCasesTModel);
		reliableCasesStatistics.setPreferredSize(new Dimension(350, 100));
		reliableCasesStatistics.setPreferredWidth(0, 230);
		reliableCasesStatistics.setPreferredWidth(1, 120);

		statisticPanel.add(reliableCasesStatistics, "0, " + lineNumberRight++ + ", c, t");

		// set the action for searching
//		JButton searchButton = factory.createButton("Goto Case ID");
//		final ProMTextField searchTerm = new ProMTextField("<type case id here>");
//
//		searchButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if (!searchTerm.getText().equals("")) {
//					int counter = 1;
//					int candidate = 0;
//					boolean lookingBetterCandidate = true;
//					XConceptExtension ci = XConceptExtension.instance();
//					for (AllSyncReplayResult res : allLogReplayResult) {
//						if ((lookingBetterCandidate)
//								&& (ci.extractName(log.get(res.getTraceIndex().first())).startsWith(searchTerm
//										.getText()))) {
//							candidate = counter;
//							lookingBetterCandidate = false;
//						}
//
//						for (int index : res.getTraceIndex()) {
//							String caseID = ci.extractName(log.get(index));
//							if (caseID.equals(searchTerm.getText())) {
//								logAlignmentPanel.setPosition((counter - 1) / (allLogReplayResult.size() + 0.333));
//								return;
//							} else if (lookingBetterCandidate && (caseID.startsWith(searchTerm.getText()))) {
//								candidate = counter;
//							}
//						}
//						counter++;
//					}
//					logAlignmentPanel.setPosition((candidate - 1) / (allLogReplayResult.size() + 0.333));
//				}
//				;
//			}
//		});

		
		// MAIN PANEL
		ProMSplitPane splitPanel = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setBorder(BorderFactory.createEmptyBorder());
		splitPanel.setOneTouchExpandable(true);
		
		JPanel alignmentPanel = new JPanel();
		alignmentPanel.setBackground(new Color(192,192,192));
		alignmentPanel.setLayout(new TableLayout(new double[][]{{.8, .2},{TableLayout.FILL}}));
//		alignmentPanel.setLayout(new TableLayout(new double[][]{{.8, .2},{30, TableLayout.FILL}}));
//		alignmentPanel.add(searchTerm, "0,0");
//		alignmentPanel.add(searchButton, "1,0");
//		alignmentPanel.add(logAlignmentPanel, "0,1,1,1");
		alignmentPanel.add(logAlignmentPanel, "0,0,1,0");
		
		splitPanel.setLeftComponent(alignmentPanel);
		splitPanel.setRightComponent(statisticPanel);
		statisticPanel.setBackground(new Color(192,192,192));
		splitPanel.setResizeWeight(1.0);
		return splitPanel;		
	}
	
	private ProMTableWithoutHeader createTable(int numReliableCaseInvolved, long totalTime, Map<String, String> map, NumberFormat nfi) {
		int idx = 0;
		Object[][] infoTable;

		if (map != null) {
			infoTable = new Object[2 + map.size()][2];
		} else {
			infoTable = new Object[2][2];
		}
		infoTable[idx++] = new Object[] { "#Cases replayed", numReliableCaseInvolved };
		infoTable[idx++] = new Object[] { "Total time (ms)", nfi.format(totalTime) };
		
		if (map != null) {
			for (String key : map.keySet()) {
				infoTable[idx++] = new Object[] { key, map.get(key) };
			}
		}

		DefaultTableModel tableModel = new DefaultTableModel(infoTable, new Object[] { "Property", "Value" }) {
			private static final long serialVersionUID = -4303950078200984098L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		ProMTableWithoutHeader promTable = new ProMTableWithoutHeader(tableModel);
		
		promTable.setPreferredSize(new Dimension(350, 100));
		promTable.setPreferredWidth(0, 230);
		promTable.setPreferredWidth(1, 120);
		return promTable;
	}

	protected Component createLegendPanel() {
		SlickerFactory factory = SlickerFactory.instance();

		JPanel legendPanel = new JPanel();
		legendPanel.setBorder(BorderFactory.createEmptyBorder());
		legendPanel.setBackground(new Color(192, 192, 192));
		TableLayout layout = new TableLayout(new double[][] { { 0.10, TableLayout.FILL }, {} });
		legendPanel.setLayout(layout);

		layout.insertRow(0, 0.2);

		int row = 1;

		layout.insertRow(row, TableLayout.PREFERRED);
		JLabel legend = factory.createLabel("LEGEND");
		legendPanel.add(legend, "0,1,1,1,c, c");
		row++;

		layout.insertRow(row, 0.2);

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel greenPanel = new JPanel();
		greenPanel.setBackground(AlignmentConstants.MOVESYNCCOLOR);
		legendPanel.add(greenPanel, "0," + row + ",r, c");
		JLabel syncLbl = factory.createLabel("-Synchronous move (move log+model)");
		legendPanel.add(syncLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel greyPanel = new JPanel();
		greyPanel.setBackground(AlignmentConstants.MOVEMODELINVICOLOR);
		legendPanel.add(greyPanel, "0," + row + ",r, c");
		JLabel moveInviLbl = factory.createLabel("-Unobservable move (move model only)");
		legendPanel.add(moveInviLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel purplePanel = new JPanel();
		purplePanel.setBackground(AlignmentConstants.MOVEMODELREALCOLOR);
		legendPanel.add(purplePanel, "0," + row + ",r, c");
		JLabel moveRealLbl = factory.createLabel("-Skipped event class (move model only)");
		legendPanel.add(moveRealLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel yellowPanel = new JPanel();
		yellowPanel.setBackground(AlignmentConstants.MOVELOGCOLOR);
		legendPanel.add(yellowPanel, "0," + row + ",r, c");
		JLabel moveLogLbl = factory.createLabel("-Inserted event class (move log only)");
		legendPanel.add(moveLogLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel orangePanel = new JPanel();
		orangePanel.setBackground(AlignmentConstants.MOVEREPLACEDCOLOR);
		legendPanel.add(orangePanel, "0," + row + ",r, c");
		JLabel moveReplaceLbl = factory.createLabel("-Replaced violation (move log+model)");
		legendPanel.add(moveReplaceLbl, "1," + row++ + ",l, c");
		
		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel redPanel = new JPanel();
		redPanel.setBackground(AlignmentConstants.MOVESWAPPEDCOLOR);
		legendPanel.add(redPanel, "0," + row + ",r, c");
		JLabel moveSwappedLbl = factory.createLabel("-Swapped violation (move log+model)");
		legendPanel.add(moveSwappedLbl, "1," + row++ + ",l, c");
		
		return legendPanel;
	}
	
	protected void populateValue(String key, NumberFormat nf, NumberFormat nfi, int numReliableCaseInvolved) {
		Object[][] data = new Object[5][2];

		Double[] values = calculations.get(key);
		data[0] = new Object[] { "Average/case",
				numReliableCaseInvolved == 0 ? "<NaN>" : nf.format(values[MVALRELIABLE]) };
		data[1] = new Object[] {
				"Max.",
				numReliableCaseInvolved == 0 ? "<NaN>" : Math.floor(values[RELIABLEMAX]) == Math
						.ceil(values[RELIABLEMAX]) ? nfi.format(values[RELIABLEMAX].intValue()) : nf
						.format(values[RELIABLEMAX]) };
		data[2] = new Object[] {
				"Min.",
				numReliableCaseInvolved == 0 ? "<NaN>" : Math.floor(values[RELIABLEMIN]) == Math
						.ceil(values[RELIABLEMIN]) ? nfi.format(values[RELIABLEMIN].intValue()) : nf
						.format(values[RELIABLEMIN]) };
		data[3] = new Object[] {
				"Std. Deviation",
				numReliableCaseInvolved == 0 ? "0" : nf.format(Math.sqrt(values[SVALRELIABLE]
						/ (numReliableCaseInvolved - 1))) };

		data[4] = new Object[] { "#Cases with value 1.00", nfi.format(values[PERFECTCASERELIABLECOUNTER].intValue()) };

		reliableCasesTModel.setDataVector(data, new Object[] { "Property", "Value" });

	}
}

class ScrollBar extends JScrollPane {
	private static final long serialVersionUID = 1L;

	public ScrollBar(JComponent component){
		super(component);
		setOpaque(true);
		setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		getViewport().setOpaque(true);
		getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder());
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollBar hBar = getHorizontalScrollBar();
		hBar.setUI(new SlickerScrollBarUI(hBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		hBar.setOpaque(true);
		hBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);

		
		JScrollBar vBar = getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
	}
}