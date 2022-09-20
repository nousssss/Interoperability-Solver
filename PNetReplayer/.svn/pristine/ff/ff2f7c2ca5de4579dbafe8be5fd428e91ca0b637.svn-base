/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;

import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * @author aadrians Feb 25, 2012
 * 
 */
public class CreatePatternPanel extends ProMSplitPane {
	private static final long serialVersionUID = 8759205978114685628L;

	// reference
	private XLog log;

	// GUI
	private JPanel patternMainPanel = new JPanel();
	private JPanel antiPatternMainPanel = new JPanel();
	private JPanel bottomMainPanel = new JPanel();

	private ProMList evClassList;
	private DefaultListModel evClassListMdl;

	private ProMList commitedPatterns;
	private DefaultListModel commitedPatternsMdl;

	private ProMList candidatePatterns;
	private DefaultListModel candidatePatternsMdl;

	private JButton addEvClassBtn;
	private JButton remEvClassBtn;
	private JButton commitEvClassBtn;
	private JButton removeEvClassBtn;
	private JButton addOmegaPatternBtn;

	final JComboBox classifiersCbBox;

	public CreatePatternPanel(XLog log, XEventClassifier[] availableClassifiers) {
		super(ProMSplitPane.VERTICAL_SPLIT);
		// reference
		this.log = log;

		// shared colors
		Color greyColor = new Color(150, 150, 150);

		// factory 
		SlickerFactory factory = SlickerFactory.instance();

		// initialize event class selection
		JPanel classifierSelectionPanel = new JPanel();
		classifierSelectionPanel.setBackground(greyColor);
		JLabel selectClassifierLbl = new JLabel("Choose Event Classifier");
		selectClassifierLbl.setBackground(greyColor);
		classifiersCbBox = factory.createComboBox(availableClassifiers);
		classifiersCbBox.setPreferredSize(new Dimension(250, 25));
		classifierSelectionPanel.add(selectClassifierLbl);
		classifierSelectionPanel.add(classifiersCbBox);
		classifiersCbBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				initialize((XEventClassifier) classifiersCbBox.getSelectedItem());
			}
		});

		// initialize event classes model
		evClassListMdl = new DefaultListModel();
		evClassList = new ProMList("List of Event Class", evClassListMdl);
		evClassList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add event class
		candidatePatternsMdl = new DefaultListModel();
		candidatePatterns = new ProMList("Event Class Sequence Pattern", candidatePatternsMdl);

		addEvClassBtn = factory.createButton("  Add >>  ");
		addEvClassBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				candidatePatternsMdl.addElement(evClassList.getSelectedValues()[0]);
			}
		});

		remEvClassBtn = factory.createButton("<< Remove");
		remEvClassBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					candidatePatternsMdl.removeElement(candidatePatterns.getSelectedValues()[0]);
				} catch (Exception exc) {
					// nothing 
				}
			}
		});

		// commit pattern
		commitedPatternsMdl = new DefaultListModel();
		commitedPatterns = new ProMList("Committed Patterns", commitedPatternsMdl);
		
		commitEvClassBtn = factory.createButton("Commit as New Pattern");
		commitEvClassBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!candidatePatternsMdl.isEmpty()) {
					EvClassPattern pattern = new EvClassPattern(candidatePatternsMdl.size());
					Enumeration<?> elements = candidatePatternsMdl.elements();
					while (elements.hasMoreElements()) {
						pattern.add((XEventClass) elements.nextElement());
					}

					// add the pattern to committed patterns
					commitedPatternsMdl.add(0, pattern);
				}
				// reset all candidate
				candidatePatternsMdl.removeAllElements();
			}
		});

		removeEvClassBtn = factory.createButton("Remove selected pattern(s)");
		removeEvClassBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Object obj : commitedPatterns.getSelectedValues()) {
					commitedPatternsMdl.removeElement(obj);
				}
				;
			}
		});

		// construct pattern creation main panel
		double[][] size = new double[][] { { 300, 3, 120, 3, 300 }, { 35, 10, TableLayout.FILL, 10, 25 } };
		TableLayout layout = new TableLayout(size);
		patternMainPanel.setLayout(layout);
		patternMainPanel.add(classifierSelectionPanel, "0,0,4,0");

		patternMainPanel.add(evClassList, "0,1,0,3");
		JPanel middlePanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(middlePanel, BoxLayout.Y_AXIS);
		middlePanel.setLayout(boxLayout);
		middlePanel.add(addEvClassBtn);
		middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		middlePanel.add(remEvClassBtn);
		middlePanel.setBackground(greyColor);
		addEvClassBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		remEvClassBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		patternMainPanel.add(middlePanel, "2,2");
		patternMainPanel.add(candidatePatterns, "4,1,4,3");
		patternMainPanel.add(commitEvClassBtn, "4,4");
		patternMainPanel.setBackground(greyColor);
		JScrollPane hscrollPane = new JScrollPane(patternMainPanel);
		hscrollPane.setOpaque(true);
		hscrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		hscrollPane.getViewport().setOpaque(true);
		hscrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		hscrollPane.setBorder(BorderFactory.createEmptyBorder());
		hscrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		hscrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollBar hBar = hscrollPane.getHorizontalScrollBar();
		hBar.setUI(new SlickerScrollBarUI(hBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		hBar.setOpaque(true);
		hBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);

		JScrollBar vBar = hscrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);

		patternMainPanel.setPreferredSize(new Dimension(200, 200));
		hscrollPane.setPreferredSize(new Dimension(200, 400));
		hscrollPane.setMinimumSize(new Dimension(200, 100));

		// construct anti pattern creation panel
		antiPatternMainPanel = new JPanel();
		antiPatternMainPanel.setBackground(greyColor);
		antiPatternMainPanel.setLayout(new BoxLayout(antiPatternMainPanel, BoxLayout.Y_AXIS));
		final ProMList evClassListAntiPattern = new ProMList("List of Event Class", evClassListMdl);
		evClassListAntiPattern.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		antiPatternMainPanel.add(evClassListAntiPattern);
		addOmegaPatternBtn = factory.createButton("Commit omega-pattern of the selected event class");
		addOmegaPatternBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// only continue if there is a value selected
				Object[] selectedVal = evClassListAntiPattern.getSelectedValues();
				if (selectedVal != null) {
					// check if the same anti pattern has been added
					boolean isExist = false;
					Enumeration<?> elmts = commitedPatternsMdl.elements();
					while (elmts.hasMoreElements()) {
						Object nextElmt = elmts.nextElement();
						if (nextElmt instanceof OmegaPattern) {
							// this is not a pattern, but omega-pattern
							if (((OmegaPattern) nextElmt).getAntiPatternEvClass().equals(selectedVal[0])) {
								isExist = true;
								break;
							}
						}
					}

					// up to this point, only add one if this is not exist 
					if (!isExist) {
						commitedPatternsMdl.add(0, new OmegaPattern((XEventClass) selectedVal[0]));
					}
				}
			}
		});
		antiPatternMainPanel.add(addOmegaPatternBtn);

		// construct panel that merge pattern and anti pattern
		SlickerTabbedPane tabPane = factory.createTabbedPane("Create New Pattern/Omega-Pattern", new Color(150,150,150),
				Color.BLACK, Color.BLACK);
		tabPane.addTab("Pattern", this.patternMainPanel);
		tabPane.addTab("Omega-Pattern", this.antiPatternMainPanel);

		// construct bottom main panel
		bottomMainPanel.setLayout(new BoxLayout(bottomMainPanel, BoxLayout.Y_AXIS));
		bottomMainPanel.setBackground(new Color(190,190,190));
		bottomMainPanel.add(commitedPatterns);
		bottomMainPanel.add(removeEvClassBtn);
		commitedPatterns.setAlignmentX(Component.CENTER_ALIGNMENT);
		bottomMainPanel.setPreferredSize(new Dimension(200, 100));
		bottomMainPanel.setMinimumSize(new Dimension(200, 100));

		// combine all
		setLeftComponent(tabPane);
		setRightComponent(bottomMainPanel);

		// set size
		setPreferredSize(new Dimension(600, 500));
//		setMinimumSize(new Dimension(600, 500));
//		setMaximumSize(new Dimension(700, 500));

		// initialize values of event class
		initialize((XEventClassifier) classifiersCbBox.getSelectedItem());
	}

	public void initialize(XEventClassifier classifier) {
		// remove all saved patterns
		commitedPatternsMdl.removeAllElements();

		// remove all candidate patterns
		candidatePatternsMdl.removeAllElements();

		// update content of event class
		evClassListMdl.removeAllElements();
		XLogInfo info = XLogInfoFactory.createLogInfo(log, classifier);
		List<XEventClass> ecWithStartEventType = new ArrayList<XEventClass>(10);
		List<XEventClass> ecWithSuspendEventtype = new ArrayList<XEventClass>(3);
		List<XEventClass> ecWithScheduleEventtype = new ArrayList<XEventClass>(3);
		Collection<XEventClass> eventClasses = info.getEventClasses().getClasses();
		List<XEventClass> list = new ArrayList<XEventClass>(eventClasses);
		Collections.sort(list, new Comparator<XEventClass>(){
			public int compare(XEventClass arg0, XEventClass arg1) {
				return arg0.getId().compareTo(arg1.getId());
			}			
		});
		for (XEventClass ec : list) {
			evClassListMdl.addElement(ec);

			// by default, create pattern of 1 element
			EvClassPattern newPattern = new EvClassPattern(1);
			newPattern.add(ec);
			commitedPatternsMdl.addElement(newPattern);

			// try to identify pattern with start suspend
			if (ec.toString().endsWith("start")) {
				ecWithStartEventType.add(ec);
			} else if (ec.toString().endsWith("suspend")) {
				ecWithSuspendEventtype.add(ec);
			} else if (ec.toString().endsWith("schedule")) {
				ecWithScheduleEventtype.add(ec);
			}
		}

		// start complete / schedule start complete
		ecWithStartEventType.removeAll(ecWithSuspendEventtype);
		startIteration: for (XEventClass ecStart : ecWithStartEventType) {
			String ecName = ecStart.getId();
			ecName = ecName.substring(0, ecName.lastIndexOf("start")).toLowerCase();
			for (XEventClass ec : eventClasses) {
				String ecNameComplete = ec.getId();
				int lastIdxComplete = ecNameComplete.lastIndexOf("complete");
				if (lastIdxComplete > 0) {
					if (ecName.equals(ecNameComplete.substring(0, lastIdxComplete).toLowerCase())) {
						// add start-complete pattern
						EvClassPattern newPattern = new EvClassPattern(2);
						newPattern.add(ecStart);
						newPattern.add(ec);
						commitedPatternsMdl.add(0,newPattern);
						
						// try to find schedule-start-complete
						for (XEventClass schedEc : ecWithScheduleEventtype){
							int lastIdxSched = schedEc.getId().lastIndexOf("schedule");
							if (ecName.equals(schedEc.getId().substring(0,lastIdxSched).toLowerCase())){
								// add schedule start complete pattern
								EvClassPattern schedPattern = new EvClassPattern(3);
								schedPattern.add(schedEc);
								schedPattern.add(ecStart);
								schedPattern.add(ec);
								commitedPatternsMdl.add(0,schedPattern);
							}
						}
						continue startIteration;
					}
				}
			}
		}
		
		// start suspend resume complete
		for (XEventClass ecSuspend : ecWithSuspendEventtype) {
			String ecName = ecSuspend.getId();
			ecName = ecName.substring(0, ecName.lastIndexOf("suspend")).toLowerCase();
			XEventClass[] candidate = new XEventClass[4];
			Arrays.fill(candidate, null);

			for (XEventClass ec : eventClasses) {
				String ecNameComplete = ec.getId();
				if ((ecNameComplete.toLowerCase()).startsWith(ecName)) {
					// refer to the same activity
					if (ecNameComplete.endsWith("start")) {
						candidate[0] = ec;
					} else if (ecNameComplete.endsWith("suspend")) {
						candidate[1] = ec;
					} else if (ecNameComplete.endsWith("resume")) {
						candidate[2] = ec;
					} else if (ecNameComplete.endsWith("complete")) {
						candidate[3] = ec;
					}
				}
			}

			if ((candidate[0] != null) && (candidate[3] != null)) {
				// add start-complete pattern
				EvClassPattern startComPattern = new EvClassPattern(2);
				startComPattern.add(candidate[0]);
				startComPattern.add(candidate[3]);
				commitedPatternsMdl.add(0,startComPattern);

				if ((candidate[1] != null) && (candidate[2] != null)) {
					// add start-suspend-resume-complete pattern
					EvClassPattern susResPattern = new EvClassPattern(4);
					susResPattern.add(candidate[0]);
					susResPattern.add(candidate[1]);
					susResPattern.add(candidate[2]);
					susResPattern.add(candidate[3]);
					commitedPatternsMdl.addElement(susResPattern);
				}
			}
		}
	}

	public Object[] getEvClassPatternArr() {
//		Object[] res = new Object[commitedPatternsMdl.size()];
//		int i = 0;
		Enumeration<?> enumer = commitedPatternsMdl.elements();
		List<EvClassPattern> l = new ArrayList<EvClassPattern>(commitedPatternsMdl.size());
		
		while (enumer.hasMoreElements()) {
//			res[i] = enumer.nextElement();
//			i++;
			l.add((EvClassPattern) enumer.nextElement());
		}
		Collections.sort(l, new Comparator<EvClassPattern>(){
			public int compare(EvClassPattern arg0, EvClassPattern arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		});
		return l.toArray();
//		return res;
	}

	public Collection<XEventClass> getEvClasses() {
		Set<XEventClass> set = new TreeSet<XEventClass>(new Comparator<XEventClass>() {
			private AlphanumComparator compStr = new AlphanumComparator();

			public int compare(XEventClass o1, XEventClass o2) {
				return compStr.compare(o1.getId(), o2.getId());
			}
		});

		Enumeration<?> elements = evClassListMdl.elements();
		while (elements.hasMoreElements()) {
			set.add((XEventClass) elements.nextElement());
		}
		return set;
	}

	public XEventClassifier getSelectedEvClassifier() {
		return (XEventClassifier) classifiersCbBox.getSelectedItem();
	}
}

class OmegaPattern {
	private XEventClass evClass;

	public OmegaPattern(XEventClass evClass) {
		this.evClass = evClass;
	}

	public XEventClass getAntiPatternEvClass() {
		return evClass;
	}

	public String toString() {
		return "OMEGA [" + evClass.getId() + "]";
	}
}
