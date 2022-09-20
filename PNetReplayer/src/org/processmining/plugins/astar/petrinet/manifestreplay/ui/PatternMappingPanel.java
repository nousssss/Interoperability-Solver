/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import gnu.trove.set.hash.TIntHashSet;
import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.DefTransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.IDBasedTransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.ITransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians Feb 25, 2012
 * 
 */
public class PatternMappingPanel extends JPanel {
	private static final long serialVersionUID = 196090779952147775L;
	public static final String NOPATTERN = "[NONE]";
	protected static final Dimension CBBOXSIZE = new Dimension(100, 25);

	public static final Color GREY = new Color(150, 150, 150);
	public static final Color DARKGREY = new Color(50, 50, 50);

	// reference to original petri net
	private PetrinetGraph net;

	// required later on
	private TransClasses tc;

	// GUI to map transition class to activities
	private JComboBox classifierSelectionCbBox;
	private Map<TransClass, GroupCbBox> trans2cbBoxLst = new HashMap<TransClass, GroupCbBox>();

	// to generate GUI
	private SlickerFactory factory;
	private Object[] patternsWithNone;
	private TableLayout bottomLayout;
	private JPanel bottomPanel;

	public PatternMappingPanel() {
		// temporary classes
		int rowCounter = 0;
		factory = SlickerFactory.instance();

		// initiate top labels
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// add selection of transition classifier
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		final JLabel hLabel = new JLabel("Transition Classifier");
		hLabel.setOpaque(false);
		hLabel.setForeground(WidgetColors.HEADER_COLOR);
		hLabel.setFont(hLabel.getFont().deriveFont(15f));
		topPanel.add(hLabel, "0, " + rowCounter + ", l, c");

		topPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		ITransClassifier[] transClassifier = new ITransClassifier[2];
		transClassifier[0] = new DefTransClassifier();
		transClassifier[1] = new IDBasedTransClassifier();

		classifierSelectionCbBox = factory.createComboBox(transClassifier);
		classifierSelectionCbBox.setPreferredSize(new Dimension(400, 35));
		classifierSelectionCbBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTransClass(net, (ITransClassifier) classifierSelectionCbBox.getSelectedItem(), patternsWithNone,
						true);
			}
		});

		topPanel.add(classifierSelectionCbBox, "1, " + rowCounter + ", l, c");
		topPanel.setBackground(GREY);
		rowCounter++;

		// initiate bottom panel
		bottomPanel = new JPanel();
		double[][] bottomSize = new double[][] { { .35, .65 }, { TableLayout.FILL } };
		bottomLayout = new TableLayout(bottomSize);
		bottomPanel.setLayout(bottomLayout);
		bottomPanel.setBackground(GREY);

		// add top and bottom
		add(topPanel);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(bottomPanel);
	}

	public ITransClassifier getSelectedClassifier() {
		return (ITransClassifier) classifierSelectionCbBox.getSelectedItem();
	}

	public void initiateTransClass(PetrinetGraph net, ITransClassifier transClassifier, Object[] patterns) {
		// copy reference to Petrinet
		this.net = net;

		// util variables
		patternsWithNone = new Object[patterns.length + 1];
		patternsWithNone[0] = NOPATTERN;
		System.arraycopy(patterns, 0, patternsWithNone, 1, patterns.length);

		updateTransClass(net, transClassifier, patternsWithNone, true);
	}

	public void updateTransClass(PetrinetGraph net, ITransClassifier transClassifier, Object[] patternsWithNone,
			boolean showOneMatchDialog) {
		boolean isIgnoreBracket = false;
		if (showOneMatchDialog) {
			if (JOptionPane.YES_OPTION == JOptionPane
					.showConfirmDialog(
							new JPanel(),
							"Consider only activity name (strings before the first bracket) when premapping transition classes to patterns?",
							"Mapping option", JOptionPane.YES_NO_OPTION)) {
				isIgnoreBracket = true;
			}
			;
		}

		bottomPanel.removeAll();
		bottomPanel.invalidate();

		// clear all info
		trans2cbBoxLst.clear();

		// remove all GUI
		for (int i = 0; i < bottomLayout.getNumRow(); i++) {
			bottomLayout.deleteRow(0);
		}

		// identify all classifier
		tc = new TransClasses(net, transClassifier);

		// add mapping between transitions and selected event class
		int i = 0;
		for (TransClass tClasses : tc.getTransClasses()) {
			GroupCbBox groupCbBox = new GroupCbBox(this, patternsWithNone, factory, preSelectOption(tClasses.getId(),
					patternsWithNone, isIgnoreBracket));
			trans2cbBoxLst.put(tClasses, groupCbBox);
			bottomLayout.insertRow(i, TableLayout.MINIMUM);

			JPanel panel = new JPanel();
			JLabel label = new JLabel(tClasses.getId());
			panel.add(label);

			bottomPanel.add(panel, "0," + i);
			bottomPanel.add(groupCbBox, "1, " + i);

			if (i % 2 == 0) {
				label.setForeground(Color.WHITE);
				panel.setBackground(PatternMappingPanel.DARKGREY);
				groupCbBox.setColor(PatternMappingPanel.DARKGREY);
			} else {
				label.setBackground(Color.BLACK);
				label.getParent().setBackground(PatternMappingPanel.GREY);
				groupCbBox.setColor(PatternMappingPanel.GREY);
			}
			i++;
		}
		bottomPanel.revalidate();
	}

	/**
	 * Returns the Event Option Box index of the most similar event for the
	 * transition.
	 * 
	 * @param transition
	 *            Name of the transitions
	 * @param events
	 *            Array with the options for this transition
	 * @return Index of option more similar to the transition
	 */
	public int preSelectOption(String transition, Object[] patterns, boolean isIgnoreBracket) {
		
		// HV: Forget about the fancy stuff when there is a perfect match.
		for (int i = 1; i < patterns.length; i++) {
			// Remove everything from the last "(" onwards.
			String pattern = patterns[i].toString();
			int index = pattern.lastIndexOf("(");
			String prefix = (index == -1 ? pattern : pattern.substring(0, index));
			// Check whether perfect match.
			if (transition.equals(prefix)) {
				// Found a perfect match. Return it.
				return i;
			}
		}
		
		// check if the transition is invisible from its label
		String invisibleTransitionRegEx = "(silent)|(tau)|(skip)|(invisible)";
		Pattern pattern = Pattern.compile(invisibleTransitionRegEx);
		Matcher matcher = pattern.matcher(transition.toLowerCase());
		if (matcher.find() && matcher.start() == 0) {
			return 0;
		}

		// check for codes
		if (transition.length() < 5) {
			invisibleTransitionRegEx = "[a-z]+[0-9]+";
			pattern = Pattern.compile(invisibleTransitionRegEx);
			matcher = pattern.matcher(transition.toLowerCase());
			if (matcher.find() && matcher.start() == 0) {
				return 0;
			}
		}
		//The metric to get the similarity between strings
		AbstractStringMetric metric = new Levenshtein();

		float simOld = Float.MIN_VALUE;
		TIntHashSet candidates = new TIntHashSet(1);

		for (int i = 1; i < patterns.length; i++) {
			String patternStr = patterns[i].toString();
			int lastIndexBracket = patternStr.lastIndexOf("(");
			float sim;
			if (lastIndexBracket > 0) {
				sim = metric.getSimilarity(transition, patternStr.substring(0, lastIndexBracket));
			} else {
				sim = metric.getSimilarity(transition, patternStr);
			}

			if (simOld < sim) {
				simOld = sim;
				candidates.clear();
				candidates.add(i);
			} else if (simOld == sim) {
				if (!isIgnoreBracket) {
					// compare with strings
					if (metric.getSimilarity(transition, patterns[candidates.iterator().next()].toString()) < metric
							.getSimilarity(transition, patterns[i].toString())) {
						candidates.clear();
						candidates.add(i);
					}
				} else {
					candidates.add(i);
				}
			}
		}

		// choose the one with longest pattern
		int index = 0;
		if (candidates.size() > 0) {
			int patternNumber = 0;

			for (int i : candidates.toArray()) {
				if (((EvClassPattern) patterns[i]).size() > patternNumber) {
					index = i;
					patternNumber = ((EvClassPattern) patterns[i]).size();
				}
			}
		}

		// if this does not work, try to remove space

		return index;
	}

	public Map<TransClass, Set<EvClassPattern>> getMapPattern() {
		Map<TransClass, Set<EvClassPattern>> res = new HashMap<TransClass, Set<EvClassPattern>>();
		for (Entry<TransClass, GroupCbBox> entry : trans2cbBoxLst.entrySet()) {
			Collection<EvClassPattern> patterns = entry.getValue().getPatterns();
			if (patterns.size() > 0) {
				res.put(entry.getKey(), (Set<EvClassPattern>) patterns);
			}
		}
		return res;
	}

	public TransClasses getTransClasses() {
		return tc;
	}

	public Collection<TransClass> getTransClassCollection() {
		return tc.getTransClasses();
	}

}

// taken from : http://stackoverflow.com/questions/2423128/java-swing-background-of-a-jpanel
class AlternatingColorTable extends JTable {
	private static final long serialVersionUID = -1289495648901351209L;

	public AlternatingColorTable() {
		super();
	}

	public AlternatingColorTable(TableModel tableModel) {
		super(tableModel);
	}

	/** Extends the renderer to alternate row colors */
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		Component returnComp = super.prepareRenderer(renderer, row, col);

		if (!returnComp.getBackground().equals(getSelectionBackground())) {
			Color background = (row % 2 == 0 ? PatternMappingPanel.GREY : PatternMappingPanel.DARKGREY);
			returnComp.setBackground(background);
			background = null;
		}
		return returnComp;
	}
}

class GroupCbBox extends JPanel {
	private static final long serialVersionUID = 2092283848637997399L;
	private int nextIndex = 0;
	private TableLayout lo;
	private List<JComboBox> lstComboBox;
	private JButton newPatternBtn;
	private JButton removePatternBtn;
	private Object[] patternsWithNone;

	public GroupCbBox(final PatternMappingPanel container, final Object[] patterns, final SlickerFactory factory,
			final int defaultSelection) {
		super();
		this.patternsWithNone = patterns;

		// set layout
		double[][] size = new double[][] { { .70, .10, .10 }, { TableLayout.FILL } };
		lo = new TableLayout(size);
		setLayout(lo);

		// add the first combobox
		JComboBox cbBox = factory.createComboBox(patterns);
		cbBox.setPreferredSize(PatternMappingPanel.CBBOXSIZE);
		cbBox.setMinimumSize(PatternMappingPanel.CBBOXSIZE);
		lstComboBox = new ArrayList<JComboBox>(2);
		lstComboBox.add(cbBox);
		cbBox.setSelectedIndex(defaultSelection);

		// add the combobox
		add(cbBox, "0,0");

		// add new pattern button
		newPatternBtn = factory.createButton("+");
		newPatternBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// add the first combobox
				JComboBox newCbBox = factory.createComboBox(patterns);
				newCbBox.setSelectedIndex(defaultSelection);

				lstComboBox.add(newCbBox);

				// add the combobox
				lo.insertRow(nextIndex, 25);
				add(newCbBox, "0," + nextIndex);

				if (nextIndex == 1) {
					add(removePatternBtn, "2, 0, l, b");
				}
				nextIndex++;

				revalidate();
				container.revalidate();
			}
		});
		add(newPatternBtn, "1,0,r,b");
		nextIndex++;

		// add remove pattern
		removePatternBtn = factory.createButton("-");

		// remove previous pattern button
		removePatternBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lo.deleteRow(nextIndex - 1);
				lstComboBox.remove(nextIndex - 1);

				if (nextIndex == 2) {
					lo.removeLayoutComponent(removePatternBtn);
				}
				nextIndex--;
				revalidate();
				container.revalidate();
			}
		});
	}

	public void setColor(Color color) {
		setBackground(color);
	}

	protected Collection<EvClassPattern> getPatterns() {
		Set<EvClassPattern> set = new HashSet<EvClassPattern>(lstComboBox.size());
		for (JComboBox cbBox : lstComboBox) {
			if (cbBox.getSelectedItem() instanceof EvClassPattern) {
				set.add((EvClassPattern) cbBox.getSelectedItem());
			} else if (cbBox.getSelectedItem() instanceof OmegaPattern) {
				OmegaPattern antiPattern = (OmegaPattern) cbBox.getSelectedItem();
				// add all patterns except of that one
				for (int i = 1; i < patternsWithNone.length; i++) {
					if (patternsWithNone[i] instanceof EvClassPattern) {
						EvClassPattern ecPattern = (EvClassPattern) patternsWithNone[i];
						if ((ecPattern.size() == 1) && (!ecPattern.get(0).equals(antiPattern.getAntiPatternEvClass()))) {
							set.add(ecPattern);
						}
					}
				}
			}
		}
		return set;
	}

}