package org.processmining.plugins.annotatedgraph;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.annotatedgraph.AnnotatedEdge;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.jgraph.ProMJAnnotatedGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;

@Plugin(name = "Show annotated graph", returnLabels = { "Visualization for an annotated graph" }, returnTypes = { JComponent.class }, parameterLabels = { "AnnotatedGraph" }, userAccessible = false)
@Visualizer
public class AnnotatedGraphVisualizer {

	/* Field declarations */
	protected String[] aggregateAttributeNamesArray, primitivesSettings = new String[13];

	protected PluginContext child;

	protected boolean containsAggregateAttributes = false;
	protected CustomMouseListener customMouseListener = new CustomMouseListener();
	protected GraphLayoutConnection glConnection = null;
	protected ProMJGraphPanel graphPanel;
	protected HashMap<String, HashMap<String, Object>> localLayoutMap = new HashMap<String, HashMap<String, Object>>();
	protected ArrayList<String> modelNames = new ArrayList<String>();
	protected AnnotatedGraph originalGraph;
	protected Set<AnnotatedVertex> previousVertexSet = new HashSet<AnnotatedVertex>();
	@SuppressWarnings("rawtypes")
	protected JComboBox[] primitivesBoxes = new JComboBox[12];
	protected JPanel rootPanel = new JPanel(), eastPanel = new JPanel(), controlsPanel = new JPanel(), primitivesPanel = new JPanel();
	protected JTable selectedElementDataTable;
	protected JLabel selectedElementLabel, vSliderValueLabel = new JLabel("0 %"),
			eSliderValueLabel = new JLabel("0 %"), visibilitySliderValueLabel = new JLabel("0 %"), descriptionLabel = new JLabel("");
	protected NiceIntegerSlider visibilitySlider, vSlider, eSlider;
	protected double vTreshold, eTreshold, edgeVisibility = 0;

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Renders the table cells for table in controlpanel */
	private final class CustomDefaultTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 3387053920301412585L;

		@Override
		public Component getTableCellRendererComponent(JTable aTable, Object aNumberValue, boolean aIsSelected,
				boolean aHasFocus, int aRow, int aColumn) {
			Component renderer = super.getTableCellRendererComponent(aTable, aNumberValue, aIsSelected, aHasFocus,
					aRow, aColumn);
			int privateColumn = aColumn - 1;
			if (privateColumn >= 0) {
				renderer.setForeground(modelColorMap.get(aNumberValue.toString()));
				Font font = new Font("Verdana", Font.BOLD, renderer.getFont().getSize());
				renderer.setFont(font);
			}
			return this;
		}
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Listener that reacts when mouse is pressed in graphpanel */
	private class CustomMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			Object element = graphPanel.getElementForLocation(e.getX(), e.getY());

			String[] columnNames = {};
			Object[][] data = {};

			if (element != null) {
				DefaultTableModel model = (DefaultTableModel) selectedElementDataTable.getModel();
				if (element instanceof AnnotatedVertex) {
					selectedElementLabel.setText("Graph element:    " + ((AnnotatedVertex) element).getValue());
					HashMap<String, HashMap<String, ArrayList<?>>> elementAttributes = ((AnnotatedVertex) element)
							.getAttributes();

					if (!(elementAttributes == null)) {
						String[] attributeArray = elementAttributes.keySet().toArray(
								new String[elementAttributes.keySet().size()]);
						Arrays.sort(attributeArray);

						columnNames = new String[modelNames.size() + 1];
						data = new Object[attributeArray.length][modelNames.size() + 1];

						int i = 0;
						columnNames[0] = "";
						int k = 1;
						for (String modelName : modelNames) {
							columnNames[k] = modelName;
							k++;
						}
						for (String elementAttributeName : attributeArray) {
							int j = 1;
							data[i][0] = elementAttributeName;
							for (String modelName : modelNames) {
								data[i][j] = ((AnnotatedVertex) element).attributesToString(elementAttributeName,
										modelName);
								j++;
							}
							i++;
						}
					}
				} else if (element instanceof AnnotatedEdge) {
					selectedElementLabel.setText("Graph element:    "
							+ ((AnnotatedEdgeIMP) element).getSource().getValue() + "_"
							+ ((AnnotatedEdgeIMP) element).getTarget().getValue() + "_"
							+ ((AnnotatedEdgeIMP) element).getModel());
					HashMap<String, ArrayList<?>> elementAttributes = ((AnnotatedEdgeIMP) element).getAttributes();

					if (!(elementAttributes == null)) {
						String[] attributeArray = elementAttributes.keySet().toArray(
								new String[elementAttributes.keySet().size()]);
						Arrays.sort(attributeArray);

						columnNames = new String[2];
						data = new Object[attributeArray.length][2];

						int i = 0;
						columnNames[0] = "";
						columnNames[1] = ((AnnotatedEdgeIMP) element).getModel();
						for (String elementAttributeName : attributeArray) {
							data[i][0] = elementAttributeName;
							data[i][1] = ((AnnotatedEdgeIMP) element).attributesToString(elementAttributeName);
							i++;
						}
					}
				}
				model.setDataVector(data, columnNames);
			} else {
				selectedElementLabel.setText("Graph element:    (Click on a graph element to see attributes)");
				DefaultTableModel model = (DefaultTableModel) selectedElementDataTable.getModel();
				model.setDataVector(data, columnNames);
			}
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Static field declarations */
	protected static Color[] contrastColors = { new Color(182, 52, 187), new Color(237, 41, 57),
			new Color(173, 136, 0), new Color(240, 171, 0), new Color(114, 199, 231), new Color(2, 71, 49),
			new Color(83, 104, 43), new Color(122, 184, 0), new Color(76, 51, 39), new Color(246, 146, 64) };
	protected static HashMap<String, Color> modelColorMap = new HashMap<String, Color>();

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	protected void defineInteractionPanels() {
		SlickerFactory slickerFac = SlickerFactory.instance();
		String[] colorSchemeArray = { "GYR", "WB", "CONTRAST", "MODEL COLOR" };
		String[] functionArray = AnnotatedGraphMerger.FUNCTIONTAGS;

		/* Controls Panel */
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
		controlsPanel.setBackground(new Color(100, 100, 100));
		controlsPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 30, 30, new Color(100, 100, 100)));
		controlsPanel.setPreferredSize(new Dimension(500, 1000));
		controlsPanel.setMaximumSize(new Dimension(500, 1000));

		/* Static label */
		JLabel vertexSliderNameLabel = new JLabel("Vertex treshold (%)");
		setAppearance(vertexSliderNameLabel);
		/* Variable slider value label */
		setAppearance(vSliderValueLabel);

		vSlider = slickerFac.createNiceIntegerSlider("Vertex treshold (%)", 0, 100, 0,
				NiceSlider.Orientation.HORIZONTAL);
		vSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				vSliderValueLabel.setText((Integer.toString(vSlider.getValue())) + "%");
				if (vSlider.getSlider().getValueIsAdjusting() == false) {
					vTreshold = vSlider.getValue();
					updateGraphPanel();
				}
			}
		});
		vSlider.setOpaque(false);
		vSlider.setToolTipText("<html>Slider to adjust the relative vertex treshold (in %).</html>");
		vSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		vSlider.setPreferredSize(new Dimension(500, 50));
		vSlider.setMaximumSize(new Dimension(500, 50));

		/* Static label */
		JLabel edgeSliderNameLabel = new JLabel("Edge treshold (%)");
		setAppearance(edgeSliderNameLabel);
		/* Variable slider value label */
		setAppearance(eSliderValueLabel);

		eSlider = slickerFac.createNiceIntegerSlider("Edge treshold (%)", 0, 100, 0, NiceSlider.Orientation.HORIZONTAL);
		eSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				eSliderValueLabel.setText(Integer.toString(eSlider.getValue()) + "%");
				if (eSlider.getSlider().getValueIsAdjusting() == false) {
					eTreshold = eSlider.getValue();
					updateGraphPanel();
				}
			}
		});
		eSlider.setOpaque(false);
		eSlider.setToolTipText("<html>Slider to adjust the relative edge treshold (in %).</html>");
		eSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		eSlider.setPreferredSize(new Dimension(500, 50));
		eSlider.setMaximumSize(new Dimension(500, 50));

		String[] columnNames = {};

		Object[][] data = {};

		/* Define table that shows attribute data for the selected graph element */
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		selectedElementDataTable = new JTable(model) {
			private static final long serialVersionUID = 8231333263796464054L;

			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);

				try {
					tip = getValueAt(rowIndex, colIndex).toString();
					tip = tip.replace(") ", "),");
					String[] tipArray = tip.split(",");
					tip = "";
					int lineLength = 0;
					for (String tipPiece : tipArray) {
						tip += " " + tipPiece;
						lineLength += tipPiece.length();
						if (lineLength >= 100) {
							tip += "<br/>";
							lineLength = 0;
						}
					}
					tip = "<html>" + tip + "</html>";
				} catch (RuntimeException e1) {
				}

				return tip;
			}
		};
		selectedElementDataTable.setFillsViewportHeight(true);
		selectedElementDataTable.getTableHeader().setAlignmentX(Component.LEFT_ALIGNMENT);
		selectedElementDataTable.setMinimumSize(new Dimension(312, 300));
		selectedElementDataTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		selectedElementDataTable.getTableHeader().setDefaultRenderer(new CustomDefaultTableCellRenderer());
		selectedElementDataTable.setMaximumSize(new Dimension(1000, 1000));

		/* Labels for table, dropdown boxes */
		selectedElementLabel = new JLabel("Graph element:    (Click on a graph element to see attributes)");
		setAppearance(selectedElementLabel);
		setAppearance(visibilitySliderValueLabel);
		visibilitySlider = slickerFac.createNiceIntegerSlider("Edge visibility (%)", 0, 100, 0,
				NiceSlider.Orientation.HORIZONTAL);
		visibilitySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				visibilitySliderValueLabel.setText((Integer.toString(visibilitySlider.getValue())) + "%");
				if (visibilitySlider.getSlider().getValueIsAdjusting() == false) {
					edgeVisibility = visibilitySlider.getValue();
					updateGraphPanel();
				}
			}
		});
		visibilitySlider.setOpaque(false);
		visibilitySlider.setToolTipText("<html>Slider to adjust the edge visibility (in %).</html>");
		visibilitySlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		visibilitySlider.setPreferredSize(new Dimension(500, 50));
		visibilitySlider.setMaximumSize(new Dimension(500, 50));

		JButton switchToPrimitivesButton = slickerFac.createButton("Switch to Primitives panel");
		switchToPrimitivesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rootPanel.remove(eastPanel);
				rootPanel.validate();
				eastPanel.remove(controlsPanel);
				eastPanel.validate();				
				eastPanel.add(primitivesPanel, BorderLayout.CENTER);
				eastPanel.validate();
				rootPanel.add(eastPanel, BorderLayout.EAST);
				rootPanel.validate();

			}
		});
		controlsPanel.add(switchToPrimitivesButton);
		
		controlsPanel.add(new JLabel(" "));
		JLabel modelsHeader = new JLabel("Models:");
		setAppearance(modelsHeader);
		controlsPanel.add(modelsHeader);
		for (String modelName : modelNames) {
			JLabel modelLabel = new JLabel(modelName);
			setAppearance(modelLabel);
			modelLabel.setForeground(modelColorMap.get(modelName));
			modelLabel.setOpaque(true);
			modelLabel.setBackground(new Color(255, 255, 255));
			controlsPanel.add(modelLabel);
		}
		controlsPanel.add(new JLabel(" "));
		controlsPanel.add(selectedElementLabel);
		controlsPanel.add(new JLabel(" "));
		controlsPanel.add(selectedElementDataTable.getTableHeader());
		controlsPanel.add(selectedElementDataTable);

		controlsPanel.add(vSlider);
		controlsPanel.add(vSliderValueLabel);
		controlsPanel.add(eSlider);
		controlsPanel.add(eSliderValueLabel);
		controlsPanel.add(new JLabel(" "));
		controlsPanel.add(visibilitySlider);
		controlsPanel.add(visibilitySliderValueLabel);

		/* Primitives Panel */
		double size[][] = { { 0.40, 0.25, TableLayout.FILL },
				{ 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 } };

		primitivesPanel.setLayout(new TableLayout(size));
		primitivesPanel.setBackground(new Color(100, 100, 100));
		primitivesPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 30, 30, new Color(100, 100, 100)));
		primitivesPanel.setPreferredSize(new Dimension(500, 1000));
		primitivesPanel.setMaximumSize(new Dimension(500, 1000));

		if (aggregateAttributeNamesArray.length > 0){
			
			// Header
			JLabel globalPrimitivesLabel = new JLabel("GLOBAL PRIMITIVES");
			setAppearance(globalPrimitivesLabel);
			globalPrimitivesLabel.setForeground(new Color(255, 216, 0));
			primitivesPanel.add(globalPrimitivesLabel, "0, 1");
			
			// Attribute selection
			definePrimitivesBox(0, "Attribute", "0, 2", null, null, aggregateAttributeNamesArray, "2, 2");
			primitivesSettings[0] = aggregateAttributeNamesArray[0];
	
			// Vertex height
			definePrimitivesBox(1, "Vertex height", "0, 3", "Function", "1, 3", functionArray, "2, 3");
	
			// Vertex width
			definePrimitivesBox(2, "Vertex width", "0, 4", "Function", "1, 4", functionArray, "2, 4");
	
			// Vertex color function
			definePrimitivesBox(3, "Vertex color", "0, 5", "Function", "1, 5", functionArray, "2, 5");
	
			// Vertex color scheme
			definePrimitivesBox(4, null, null, "Color scheme", "1, 6", colorSchemeArray, "2, 6");
	
			// Vertex border thickness function
			definePrimitivesBox(5, "Vertex border thickness", "0, 7", "Function", "1, 7", functionArray, "2, 7");
	
			// Vertex border color function
			definePrimitivesBox(6, "Vertex border color", "0, 8", "Function", "1, 8", functionArray, "2, 8");
	
			// Vertex border color scheme
			definePrimitivesBox(7, null, null, "Color scheme", "1, 9", colorSchemeArray, "2, 9");
	
			// Edge thickness function
			definePrimitivesBox(8, "Edge thickness", "0, 11", "Function", "1, 11", functionArray, "2, 11");
	
			// Local primitives
			JLabel localPrimitivesLabel = new JLabel("LOCAL PRIMITIVES");
			setAppearance(localPrimitivesLabel);
			localPrimitivesLabel.setForeground(new Color(255, 216, 0));
			primitivesPanel.add(localPrimitivesLabel, "0, 15");
	
			// Vertex lane height function
			definePrimitivesBox(9, "Vertex lane height", "0, 16", "Function", "1, 16", functionArray, "2, 16");
	
			// Vertex lane color function
			definePrimitivesBox(10, "Vertex lane color", "0, 17", "Function", "1, 17", functionArray, "2, 17");
	
			// Vertex lane color scheme
			definePrimitivesBox(11, null, null, "Color scheme", "1, 18", colorSchemeArray, "2, 18");
	
			JButton savePrimitivesButton = slickerFac.createButton("Apply changes");
			savePrimitivesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateGraphPanel();
				}
			});
			primitivesPanel.add(savePrimitivesButton, "1, 20");
	
			JButton clearPrimitivesButton = slickerFac.createButton("Clear all");
			clearPrimitivesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < primitivesSettings.length; i++) {
						primitivesBoxes[i].setSelectedIndex(0);
						primitivesSettings[i] = "";
					}
					updateGraphPanel();
				}
			});
			primitivesPanel.add(clearPrimitivesButton, "2, 20");
		}

		JButton switchToControlsButton = slickerFac.createButton("Switch to Controls panel");
		switchToControlsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rootPanel.remove(eastPanel);
				rootPanel.validate();
				eastPanel.remove(primitivesPanel);
				eastPanel.validate();
				eastPanel.add(controlsPanel, BorderLayout.CENTER);
				eastPanel.validate();
				rootPanel.add(eastPanel, BorderLayout.EAST);
				rootPanel.validate();

			}
		});
		primitivesPanel.add(switchToControlsButton, "0, 0");
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Define ComboBox for Primitives Panel */
	@SuppressWarnings("unchecked")
	private void definePrimitivesBox(final int index, String label1, String position1, String label2, String position2,
			String[] comboBoxArray, String position3) {
		SlickerFactory slickerFac = SlickerFactory.instance();

		if (!((label1 == null) || (position1 == null))) {
			JLabel primitiveVertexHeightLabel = new JLabel(label1);
			setAppearance(primitiveVertexHeightLabel);
			primitivesPanel.add(primitiveVertexHeightLabel, position1);
		}
		if (!((label2 == null) || (position2 == null))) {
			JLabel primitiveVertexHeightFunctionLabel = new JLabel(label2);
			setAppearance(primitiveVertexHeightFunctionLabel);
			primitivesPanel.add(primitiveVertexHeightFunctionLabel, position2);
		}
		primitivesBoxes[index] = slickerFac.createComboBox(comboBoxArray);
		primitivesBoxes[index].insertItemAt("", 0);
		primitivesSettings[index] = "";
		if (comboBoxArray.length > 0) {
			primitivesBoxes[index].setSelectedIndex(0);
			if (index == 0){
				primitivesBoxes[index].setSelectedIndex(1);
			}
		}
		primitivesBoxes[index].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JComboBox<String> cb = (JComboBox<String>) event.getSource();
				primitivesSettings[index] = (String) cb.getSelectedItem();
			}
		});
		primitivesPanel.add(primitivesBoxes[index], position3);
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	private AnnotatedGraph determineElementVisualizations(AnnotatedGraph graph) {
		HashMap<AnnotatedVertex, Boolean> vertexVisuals = new HashMap<AnnotatedVertex, Boolean>();
		Color[] whiteBlueColors = { new Color(227, 238, 255), new Color(227, 238, 255), new Color(198, 221, 255),
				new Color(170, 204, 255), new Color(142, 187, 255), new Color(113, 170, 255), new Color(85, 153, 255),
				new Color(57, 136, 255), new Color(28, 119, 255), new Color(0, 102, 255) }, greenYellowRedColors = {
				new Color(122, 184, 0), new Color(152, 181, 0), new Color(181, 178, 0), new Color(211, 175, 0),
				new Color(240, 171, 0), new Color(239, 145, 11), new Color(239, 119, 23), new Color(238, 93, 34),
				new Color(238, 67, 46), new Color(237, 41, 57) };

		// Determine visuals
		if (containsAggregateAttributes) {

//			ProMShapeRenderer.modelNames = modelNames;
//			ProMShapeRenderer.modelColorMap = modelColorMap;
			
			descriptionLabel.setText("");
			String tempDescription = "";

			// Create description
			if (!((primitivesSettings[0] == null))){
				tempDescription += "The visualization entirely depends on the data attribute <i>" + primitivesSettings[0].toLowerCase() + "</i>.";
			}
			
			if (!((primitivesSettings[0] == null) || (primitivesSettings[8] == null) || (primitivesSettings[8] == ""))) {
				tempDescription += " The <i>edge thickness</i> is computed as the <i>" + primitivesSettings[8].toLowerCase() + "</i> over the data for attribute <i>" + primitivesSettings[0].toLowerCase() + "</i>. The larger the computed value is compared to other edges, the thicker the edge.";
			}
			
			ArrayList<String> globalVertexDescriptions = new ArrayList<String>();
			//v height
			if (!((primitivesSettings[0] == null) || (primitivesSettings[1] == null) || (primitivesSettings[1] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The <i>vertex height</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex height</i>");
				}
			}
			
			//v width
			if (!((primitivesSettings[0] == null) || (primitivesSettings[2] == null) || (primitivesSettings[2] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The <i>vertex width</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex width</i>");
				}
			}
			
			//v color
			if (!((primitivesSettings[0] == null) || (primitivesSettings[3] == null)
					|| (primitivesSettings[4] == null) || (primitivesSettings[3] == "") || (primitivesSettings[4] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The <i>vertex color</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex color</i>");
				}
			}
			
			//v border thickness
			if (!((primitivesSettings[0] == null) || (primitivesSettings[5] == null) || (primitivesSettings[5] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The <i>vertex border thickness</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex border thickness</i>");
				}
			}
			
			//v border color
			if (!((primitivesSettings[0] == null) || (primitivesSettings[6] == null)
					|| (primitivesSettings[7] == null) || (primitivesSettings[6] == "") || (primitivesSettings[7] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The </i>vertex border color</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex border color</i>");
				}
			}
			
			if (!globalVertexDescriptions.isEmpty()){
				if (globalVertexDescriptions.size() > 1){
					tempDescription += " " + globalVertexDescriptions.get(0);
					for(int i = 1; i < (globalVertexDescriptions.size() - 1); i++){
						tempDescription += ", " + globalVertexDescriptions.get(i);
					}
					tempDescription += " and " + globalVertexDescriptions.get(globalVertexDescriptions.size() - 1) + " will depend on the vertex data for <i>" + primitivesSettings[0].toLowerCase() + "</i>.";
				} else {
					tempDescription += " The " + globalVertexDescriptions.get(0) + " will depend on the vertex data for <i>" + primitivesSettings[0].toLowerCase() + "</i>.";
				}
				tempDescription += " The larger the computed function value is compared to other vertices, the larger the node, thicker the border will be or, in case of the color, a color to the end of the scheme is chosen.";
			}
			
			globalVertexDescriptions.clear();
			
			//v lane height
			if (!((primitivesSettings[0] == null) || (primitivesSettings[9] == null) || (primitivesSettings[9] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The <i>vertex lane height</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex lane height</i>");
				}
			}
			
			// Vertex lane color
			if (!((primitivesSettings[0] == null) || (primitivesSettings[10] == null)
					|| (primitivesSettings[11] == null) || (primitivesSettings[10] == "") || (primitivesSettings[11] == ""))) {
				if (globalVertexDescriptions.isEmpty()){
					globalVertexDescriptions.add("The <i>vertex lane color</i>");
				} else {
					globalVertexDescriptions.add("<i>vertex lane color</i>");
				}
			}
			
			if (!globalVertexDescriptions.isEmpty()){
				if (globalVertexDescriptions.size() > 1){
					tempDescription += " " + globalVertexDescriptions.get(0);
					for(int i = 1; i < (globalVertexDescriptions.size() - 1); i++){
						tempDescription += ", " + globalVertexDescriptions.get(i);
					}
					tempDescription += " and " + globalVertexDescriptions.get(globalVertexDescriptions.size() - 1) + " will depend on the vertex data for <i>" + primitivesSettings[0].toLowerCase() + "</i>.";
				} else {
					tempDescription += " The " + globalVertexDescriptions.get(0) + " will depend on the vertex data for <i>" + primitivesSettings[0].toLowerCase() + "</i>.";
				}
				tempDescription += " The larger the computed value is compared to other models that contain the edge, the large the lane height or, in case of the color, a color to the end of the scheme is chosen.";
			}
			
			//Visuals
			
			// Edges
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graph.getEdges()) {
				HashMap<String, HashMap<String, Double>> eAggregates = e.getGlobalAggregates();

				// Edge thickness
				if (!((primitivesSettings[0] == null) || (primitivesSettings[8] == null) || (primitivesSettings[8] == ""))) {
					if (eAggregates.containsKey(primitivesSettings[0])
							&& eAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[8])) {
						double eAggregateValue = eAggregates.get(primitivesSettings[0]).get(primitivesSettings[8]);

						int newEdgeThickness = ((int) Math.round(17 * eAggregateValue)) + 3;

						e.getAttributeMap().put(AttributeMap.LINEWIDTH, new Float(newEdgeThickness));
					}
				}
			}

			// Vertices
			for (AnnotatedVertex v : graph.getVertices()) {
				HashMap<String, HashMap<String, Double>> vGlobalAggregates = v.getGlobalAggregates();
				HashMap<String, HashMap<String, HashMap<String, Double>>> vLocalAggregates = v.getLocalAggregates();

				// Vertex height
				if (!((primitivesSettings[0] == null) || (primitivesSettings[1] == null) || (primitivesSettings[1] == ""))) {
					if (vGlobalAggregates.containsKey(primitivesSettings[0])
							&& vGlobalAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[1])) {
						double vAggregateValue = vGlobalAggregates.get(primitivesSettings[0])
								.get(primitivesSettings[1]);

						int newVertexHeight = ((int) Math.round(3 * AnnotatedVertex.STDHEIGHT * vAggregateValue))
								+ AnnotatedVertex.STDHEIGHT;

						Dimension oldDimension = (Dimension) v.getAttributeMap().get(AttributeMap.SIZE);
						v.getAttributeMap().put(AttributeMap.SIZE, new Dimension(oldDimension.width, newVertexHeight));
						vertexVisuals.put(v, true);
						
						//primitivesDefined = true;
					}
				}

				// Vertex width
				if (!((primitivesSettings[0] == null) || (primitivesSettings[2] == null) || (primitivesSettings[2] == ""))) {
					if (vGlobalAggregates.containsKey(primitivesSettings[0])
							&& vGlobalAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[2])) {
						double vAggregateValue = vGlobalAggregates.get(primitivesSettings[0])
								.get(primitivesSettings[2]);

						int newVertexWidth = ((int) Math.round(3 * AnnotatedVertex.STDWIDTH * vAggregateValue))
								+ AnnotatedVertex.STDWIDTH;

						Dimension oldDimension = (Dimension) v.getAttributeMap().get(AttributeMap.SIZE);
						v.getAttributeMap().put(AttributeMap.SIZE, new Dimension(newVertexWidth, oldDimension.height));
						vertexVisuals.put(v, true);
						
						//primitivesDefined = true;
					}
				}

				// Vertex color
				if (!((primitivesSettings[0] == null) || (primitivesSettings[3] == null)
						|| (primitivesSettings[4] == null) || (primitivesSettings[3] == "") || (primitivesSettings[4] == ""))) {
					if (vGlobalAggregates.containsKey(primitivesSettings[0])
							&& vGlobalAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[3])) {
						double vAggregateValue = vGlobalAggregates.get(primitivesSettings[0])
								.get(primitivesSettings[3]);
						int i = (int) Math.round(vAggregateValue * 10);
						if (primitivesSettings[4].equals("GYR")) {
							v.getAttributeMap().put(AttributeMap.FILLCOLOR, greenYellowRedColors[Math.min(i, 9)]);
						}
						if (primitivesSettings[4].equals("WB")) {
							v.getAttributeMap().put(AttributeMap.FILLCOLOR, whiteBlueColors[Math.min(i, 9)]);
						}
						if (primitivesSettings[4].equals("CONTRAST")) {
							v.getAttributeMap().put(AttributeMap.FILLCOLOR, contrastColors[Math.min(i, 9)]);
						}
						vertexVisuals.put(v, true);
						
						//primitivesDefined = true;
					}
				}

				// Vertex border thickness
				if (!((primitivesSettings[0] == null) || (primitivesSettings[5] == null) || (primitivesSettings[5] == ""))) {
					if (vGlobalAggregates.containsKey(primitivesSettings[0])
							&& vGlobalAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[5])) {
						double vAggregateValue = vGlobalAggregates.get(primitivesSettings[0])
								.get(primitivesSettings[5]);

						int newVertexBorderWidth = ((int) Math.round(9 * vAggregateValue)) + 2;

						v.getAttributeMap().put(AttributeMap.BORDERWIDTH, newVertexBorderWidth);
						vertexVisuals.put(v, true);
						
						//primitivesDefined = true;
					}
				}

				// Vertex border color
				if (!((primitivesSettings[0] == null) || (primitivesSettings[6] == null)
						|| (primitivesSettings[7] == null) || (primitivesSettings[6] == "") || (primitivesSettings[7] == ""))) {
					if (vGlobalAggregates.containsKey(primitivesSettings[0])
							&& vGlobalAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[6])) {
						double vAggregateValue = vGlobalAggregates.get(primitivesSettings[0])
								.get(primitivesSettings[6]);
						int i = (int) Math.round(vAggregateValue * 10);
						if (primitivesSettings[7].equals("GYR")) {
							v.getAttributeMap().put(AttributeMap.STROKECOLOR, greenYellowRedColors[Math.min(i, 9)]);
						}
						if (primitivesSettings[7].equals("WB")) {
							v.getAttributeMap().put(AttributeMap.STROKECOLOR, whiteBlueColors[Math.min(i, 9)]);
						}
						if (primitivesSettings[7].equals("CONTRAST")) {
							v.getAttributeMap().put(AttributeMap.STROKECOLOR, contrastColors[Math.min(i, 9)]);
						}
						vertexVisuals.put(v, true);
						
						//primitivesDefined = true;
					}
				}

				// Vertex lane height
				if (!((primitivesSettings[0] == null) || (primitivesSettings[9] == null) || (primitivesSettings[9] == ""))) {
					HashMap<String, Double> tempLocalAggregates = new HashMap<String, Double>();
					ArrayList<String> localModelNames = new ArrayList<String>();
					double totalValue = 0;
					int[] laneHeights = new int[modelNames.size()];

					for (String modelName : modelNames) {
						if (vLocalAggregates.containsKey(primitivesSettings[0])
								&& vLocalAggregates.get(primitivesSettings[0]).containsKey(modelName)
								&& vLocalAggregates.get(primitivesSettings[0]).get(modelName)
										.containsKey(primitivesSettings[9])) {
							localModelNames.add(modelName);
						}
					}

					for (String modelName : localModelNames) {
						tempLocalAggregates.put(modelName, vLocalAggregates.get(primitivesSettings[0]).get(modelName)
								.get(primitivesSettings[9]));
						totalValue += tempLocalAggregates.get(modelName);
					}
					int i = 0;
					Dimension vDimension = (Dimension) v.getAttributeMap().get(AttributeMap.SIZE);
					for (String modelName : localModelNames) {
						laneHeights[i] = (int) Math.round((tempLocalAggregates.get(modelName) / totalValue)
								* vDimension.height);
						i++;
					}
					v.getAttributeMap().put("vertexLaneHeights", laneHeights);
					vertexVisuals.put(v, true);
					
					//primitivesDefined = true;
				}

				// Vertex lane color
				if (!((primitivesSettings[0] == null) || (primitivesSettings[10] == null)
						|| (primitivesSettings[11] == null) || (primitivesSettings[10] == "") || (primitivesSettings[11] == ""))) {
					HashMap<String, Double> tempLocalAggregates = new HashMap<String, Double>();
					ArrayList<String> localModelNames = new ArrayList<String>();
					double maxValue = 0;
					Color[] laneColors = new Color[modelNames.size()];

					for (String modelName : modelNames) {
						if (vLocalAggregates.containsKey(primitivesSettings[0])
								&& vLocalAggregates.get(primitivesSettings[0]).containsKey(modelName)
								&& vLocalAggregates.get(primitivesSettings[0]).get(modelName)
										.containsKey(primitivesSettings[11])) {
							localModelNames.add(modelName);
						}
					}

					for (String modelName : localModelNames) {
						tempLocalAggregates.put(modelName, vLocalAggregates.get(primitivesSettings[0]).get(modelName)
								.get(primitivesSettings[10]));
						maxValue = Math.max(maxValue, tempLocalAggregates.get(modelName));
					}
					int i = 0, j = 0;
					for (String modelName : localModelNames) {
						i = (int) Math.round((tempLocalAggregates.get(modelName) / maxValue) * 10);
						if (primitivesSettings[11].equals("GYR")) {
							laneColors[j] = greenYellowRedColors[Math.min(i, 9)];
						}
						if (primitivesSettings[11].equals("WB")) {
							laneColors[j] = whiteBlueColors[Math.min(i, 9)];
						}
						if (primitivesSettings[11].equals("CONTRAST")) {
							laneColors[j] = contrastColors[Math.min(i, 9)];
						}
						j++;
					}
					v.getAttributeMap().put("vertexLaneColors", laneColors);
					vertexVisuals.put(v, true);
					
					//primitivesDefined = true;
				}
			}
			
			if (tempDescription.equals("")){
				tempDescription += "There are no primitives set. This results in a graph showing gray vertices and colored sets of edges for each of the input models.";
			}
			
			// Finalize description: create multiline text
			String[] parts = tempDescription.split(" ");
			
			descriptionLabel.setText("<html>");
			int linesCompleted = 0;
			for(int i = 0; i < parts.length; i++){
				descriptionLabel.setText(descriptionLabel.getText() + parts[i] + " ");
				int test = descriptionLabel.getText().replaceAll("\\<.*?>","").length();
				if (descriptionLabel.getText().length() - (linesCompleted * 80) > 80){
					descriptionLabel.setText(descriptionLabel.getText() + "<br>");
					linesCompleted++;
				}
/*				if (((i % 10) == 0) && (i > 0)){
					newTempDescription += "<br>";
				}*/
			}
			descriptionLabel.setText(descriptionLabel.getText() + "</html>");
			eastPanel.remove(descriptionLabel);
			eastPanel.validate();
			eastPanel.add(descriptionLabel, BorderLayout.NORTH);
			eastPanel.validate();
			rootPanel.validate();
		}

		/*
		 * Fade out all graph elements that didn't receive a visualization yet
		 * Also take care of additional edge fading
		 */
		HashMap<AnnotatedVertex, HashMap<AnnotatedVertex, Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>>> elements = graph
				.getElements();
		for (AnnotatedVertex source : elements.keySet()) {
			if ((!(vertexVisuals.containsKey(source))) && containsAggregateAttributes) {
				source.getAttributeMap().put(AttributeMap.FILLCOLOR, AnnotatedVertex.FADEFILLCOLOR);
				source.getAttributeMap().put(AttributeMap.STROKECOLOR, AnnotatedVertex.FADESTROKECOLOR);
			}
			for (AnnotatedVertex target : elements.get(source).keySet()) {
				double maxValue = Integer.MIN_VALUE;
				HashMap<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>, Double> edgeMap = new HashMap<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>, Double>();

				Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> edges = elements.get(source)
						.get(target);
				for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : edges) {
					e.getAttributeMap().put(AttributeMap.EDGECOLOR, modelColorMap.get(e.getModel()));
					if (!((primitivesSettings[0] == null) || (primitivesSettings[8] == null) || (primitivesSettings[8] == ""))) {
						HashMap<String, HashMap<String, Double>> eAggregates = e.getGlobalAggregates();
						if (eAggregates.containsKey(primitivesSettings[0])
								&& eAggregates.get(primitivesSettings[0]).containsKey(primitivesSettings[8])) {
							edgeMap.put(e, eAggregates.get(primitivesSettings[0]).get(primitivesSettings[8]));
							maxValue = Math.max(maxValue, edgeMap.get(e));
						}
					}
				}
				for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : edgeMap.keySet()) {
					Color edgeColor = modelColorMap.get(e.getModel());
					double fadeValue = 1.0 - edgeVisibility / 100.0;
					if (maxValue > edgeMap.get(e)) {
						e.getAttributeMap().put(
								AttributeMap.EDGECOLOR,
								new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), (int) Math
										.round(fadeValue * 255)));
					}
				}
			}
		}

		return graph;
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Filter Graph to reduce vertices and edges; Also determine visuals */
	@SuppressWarnings("unchecked")
	public AnnotatedGraph filterGraph(AnnotatedGraph graph) {
		boolean respectsTreshold = false;
		boolean hasEdges = false;
		boolean needsVertexCheck = true;
		String filterAttribute = "frequency";

		// Retrieve maximum value for filterAttribute; No such value --> return
		Double edgeMax = graph.getMaxAttribute(filterAttribute, AnnotatedGraph.ElementType.AnnotatedEdge);
		if (!(edgeMax == null)) {
			double localEdgeTreshold = (eTreshold / 100.00) * edgeMax;
			eSliderValueLabel.setText(eTreshold + "% (" + localEdgeTreshold + ")");

			// Filter edges
			HashSet<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> graphEdges = new HashSet<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>(
					graph.getEdges());
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graphEdges) {
				respectsTreshold = false;
				if (e.getAttributes().containsKey(filterAttribute)) {
					double eAttributeValue = Double.parseDouble((((ArrayList<Object>) e.getAttributes().get(
							filterAttribute)).get(0)).toString());

					respectsTreshold |= (eAttributeValue >= localEdgeTreshold);
				}
				if (!(respectsTreshold) && (e.getAttributes().containsKey(filterAttribute))) {
					graph.removeEdge(e);
				}
			}
		}

		// Retrieve maximum value for filterAttribute; No such value --> return
		Double vertexMax = graph.getMaxAttribute(filterAttribute, AnnotatedGraph.ElementType.AnnotatedVertex);
		if (!(vertexMax == null)) {
			double localVertexTreshold = (vTreshold / 100.00) * vertexMax;
			vSliderValueLabel.setText(vTreshold + "% (" + localVertexTreshold + ")");

			// Filter vertices
			// This needs a while-loop because:
			// .. We don't allow for vertices to be unconnected
			// .. Once one vertex is to be removed (because of treshold or unconnected), all connected edges are to be removed and neighbourly vertices may become unconnected and are to be removed as well
			while (needsVertexCheck) {
				needsVertexCheck = false;

				ArrayList<AnnotatedVertex> graphVertices = new ArrayList<AnnotatedVertex>(graph.getVertices());
				for (AnnotatedVertex v : graphVertices) {
					respectsTreshold = false;
					hasEdges = false;
					if (v.getAttributes().containsKey(filterAttribute)) {
						for (String modelName : v.getAttributes().get(filterAttribute).keySet()) {
							double vAttributeValue = Double.parseDouble((((ArrayList<Object>) v.getAttributes()
									.get(filterAttribute).get(modelName)).get(0)).toString());

							respectsTreshold |= (vAttributeValue >= localVertexTreshold);
							if (respectsTreshold) {
								break;
							}
						}
					}
					for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graph.getEdges()) {
						hasEdges |= (e.getSource().equals(v) || e.getTarget().equals(v));
						if (hasEdges) {
							break;
						}
					}
					if ((!(respectsTreshold && hasEdges)) && (v.getAttributes().containsKey(filterAttribute))) {
						graph.removeVertex(v);
						needsVertexCheck = true;
					}
				}
			}
		}

		Set<AnnotatedVertex> graphVertices = graph.getVertices();

		if (previousVertexSet.size() == graphVertices.size()) {
			boolean equal = true;
			for (AnnotatedVertex v1 : graphVertices) {
				boolean found = false;
				for (AnnotatedVertex v2 : previousVertexSet) {
					if (v1.equals(v2)) {
						found = true;
						break;
					}
				}
				if (!found) {
					equal = false;
					break;
				}
			}
			if (!equal) {
				glConnection = null;
				previousVertexSet = graphVertices;
			}
		} else {
			glConnection = null;
			previousVertexSet = graphVertices;
		}

		return graph;
	}

	private void setAppearance(JLabel plainLabel) {
		plainLabel.setOpaque(false);
		plainLabel.setForeground(Color.WHITE);
		plainLabel.setHorizontalAlignment(JLabel.LEFT);
		plainLabel.setHorizontalTextPosition(JLabel.LEFT);
		plainLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	/* User changed one of the control values; update graph */
	private void updateGraphPanel() {
		vSlider.setEnabled(false);
		eSlider.setEnabled(false);
		visibilitySlider.setEnabled(false);

		if (graphPanel != null) {
			rootPanel.remove(graphPanel);
			graphPanel = null;
		}
		if (graphPanel == null) {
			AnnotatedGraph g = originalGraph.getClone();
			g = filterGraph(g);
			g = determineElementVisualizations(g);
			setGraphLayoutCallProMJGraphVisualizer(g);
			graphPanel.getComponent().addMouseListener(customMouseListener);
			g = null;
			System.gc();
			rootPanel.add(graphPanel, BorderLayout.CENTER);
		}

		visibilitySlider.setEnabled(true);
		eSlider.setEnabled(true);
		vSlider.setEnabled(true);
		rootPanel.revalidate();
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent initializeVisualization(final PluginContext context, final AnnotatedGraph graph) {
		originalGraph = graph;
		HashSet<String> aggregateAttributeNames = new HashSet<String>();

		// [BEGIN] Gather attributes that can be used to define vertex and edge visualizations
		//       & Gather attributes that can be used to filter vertices and edges
		for (AnnotatedVertex v : originalGraph.getVertices()) {
			aggregateAttributeNames.addAll(v.getGlobalAggregates().keySet());

			HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = v.getAttributes();
			for (String attributeName : vAttributes.keySet()) {
				//TODO AANPASSEN: OPHALEN UIT GRAAF
				for (String modelName : vAttributes.get(attributeName).keySet()) {
					if (!(modelNames.contains(modelName))) {
						modelNames.add(modelName);
					}
				}
			}
		}
		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : originalGraph.getEdges()) {
			aggregateAttributeNames.addAll(e.getGlobalAggregates().keySet());

			if (!(modelNames.contains(e.getModel()))) {
				modelNames.add(e.getModel());
			}
		}

		// [END] Gather attributes that can be used to define vertex and edge visualizations
		//     & Gather attributes that can be used to filter vertices and edges

		// Sort modelNames and provide each model with a color identifier
		String[] modelNamesArray = modelNames.toArray(new String[modelNames.size()]);
		Arrays.sort(modelNamesArray);
		for (int i = 0; i < modelNamesArray.length; i++) {
			modelNames.set(i, modelNamesArray[i]);
			modelColorMap.put(modelNamesArray[i], contrastColors[i]);
		}

		// Set aggregateAttribute and define vertex and edge tooltips
		aggregateAttributeNamesArray = aggregateAttributeNames.toArray(new String[aggregateAttributeNames.size()]);
		if (!(aggregateAttributeNames.isEmpty())) {
			// Because there exists aggregate function calculations, we now know the graph is based on multiple sources and therefore is a real "Merged" graph
			// Showing a data visualization now makes sense
			containsAggregateAttributes = true;
			Arrays.sort(aggregateAttributeNamesArray);
			// Set tooltips for startup
			for (AnnotatedVertex v : originalGraph.getVertices()) {
				String toolTip = "<html>" + v.getValue() + "<br />";
				for (String modelName : modelNames) {
					toolTip += "<br />" + modelName + ": " + v.attributesToString(primitivesSettings[0], modelName);
				}
				toolTip += "</html>";
				v.getAttributeMap().put(AttributeMap.TOOLTIP, toolTip);
			}
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : originalGraph.getEdges()) {
				String toolTip = "<html>" + e.getModel() + "<br />";
				for (String modelName : modelNames) {
					toolTip += "<br />" + modelName + ": " + e.attributesToString(primitivesSettings[0]);
				}
				toolTip += "</html>";
				e.getAttributeMap().put(AttributeMap.TOOLTIP, toolTip);
			}
		}

		// Define panels
		// - 'root' is the parent JPanel containing 'panel' and 'right'
		// - 'panel' is the JPanel containing the visualized graph
		// - 'right' is the JPanel containing the controls
		child = context.createChildContext("");
		child.getConnectionManager().setEnabled(false);
		
		// Set up interaction panels: Controls and Primitives
		defineInteractionPanels();

		descriptionLabel.setText("");
		descriptionLabel.setForeground(new Color(255, 216, 0));
		
		updateGraphPanel();//setGraphLayoutCallProMJGraphVisualizer(filterGraph(originalGraph));

		graphPanel.getComponent().addMouseListener(customMouseListener);

		rootPanel.setBorder(BorderFactory.createEmptyBorder());
		rootPanel.setBackground(new Color(100, 100, 100));
		rootPanel.setLayout(new BorderLayout());
		eastPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 0, 0, new Color(100, 100, 100)));
		eastPanel.setBackground(new Color(100, 100, 100));
		eastPanel.setLayout(new BorderLayout());		
		
		rootPanel.add(graphPanel, BorderLayout.CENTER);
		
		eastPanel.add(descriptionLabel, BorderLayout.NORTH);
		eastPanel.add(controlsPanel, BorderLayout.CENTER);		
		rootPanel.add(eastPanel, BorderLayout.EAST);
		return rootPanel;
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Determine graph layout and call the ProM visualizer */
	@SuppressWarnings("unchecked")
	private void setGraphLayoutCallProMJGraphVisualizer(AnnotatedGraph graph) {
		// Visualize the graph using the default ProMJGraphVisualizer
		if (!(glConnection == null)) {
			for (AnnotatedVertex v : graph.getVertices()) {
				glConnection.setEdgePoints(v, (List<Point2D>) localLayoutMap.get(v.getValue()).get("edgepoints"));
				glConnection.setSize(v, (Dimension2D) localLayoutMap.get(v.getValue()).get("size"));
				glConnection.setPosition(v, (Point2D) localLayoutMap.get(v.getValue()).get("position"));
				if ((Boolean) localLayoutMap.get(v.getValue()).get("collapsed")) {
					glConnection.collapse(v);
				} else {
					glConnection.expand(v);
				}
				glConnection.setPortOffset(v, (Point2D) localLayoutMap.get(v.getValue()).get("portOffset"));
			}
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graph.getEdges()) {
				String ID = e.getSource().getValue() + e.getTarget().getValue() + e.getModel();
				glConnection.setEdgePoints(e, (List<Point2D>) localLayoutMap.get(ID).get("edgepoints"));
				glConnection.setSize(e, (Dimension2D) localLayoutMap.get(ID).get("size"));
				glConnection.setPosition(e, (Point2D) localLayoutMap.get(ID).get("position"));
				if ((Boolean) localLayoutMap.get(ID).get("collapsed")) {
					glConnection.collapse(e);
				} else {
					glConnection.expand(e);
				}
				glConnection.setPortOffset(e, (Point2D) localLayoutMap.get(ID).get("portOffset"));
			}
			graphPanel = ProMJAnnotatedGraphVisualizer.instance().visualizeGraph(glConnection, child, graph,
					new ViewSpecificAttributeMap());
		} else {
			graphPanel = ProMJAnnotatedGraphVisualizer.instance().visualizeGraph(child, graph);
		}
		if (glConnection == null) {
			glConnection = ProMJAnnotatedGraphVisualizer.instance().getGlConnection();
			for (AnnotatedVertex v : graph.getVertices()) {
				if (!(localLayoutMap.containsKey(v.getValue()))) {
					localLayoutMap.put(v.getValue(), new HashMap<String, Object>());
				}
				localLayoutMap.get(v.getValue()).put("edgepoints", glConnection.getEdgePoints(v));
				localLayoutMap.get(v.getValue()).put("size", glConnection.getSize(v));
				localLayoutMap.get(v.getValue()).put("position", glConnection.getPosition(v));
				localLayoutMap.get(v.getValue()).put("collapsed", glConnection.isCollapsed(v));
				localLayoutMap.get(v.getValue()).put("portOffset", glConnection.getPortOffset(v));
			}
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graph.getEdges()) {
				String ID = e.getSource().getValue() + e.getTarget().getValue() + e.getModel();
				if (!(localLayoutMap.containsKey(ID))) {
					localLayoutMap.put(ID, new HashMap<String, Object>());
				}
				localLayoutMap.get(ID).put("edgepoints", glConnection.getEdgePoints(e));
				localLayoutMap.get(ID).put("size", glConnection.getSize(e));
				localLayoutMap.get(ID).put("position", glConnection.getPosition(e));
				localLayoutMap.get(ID).put("collapsed", glConnection.isCollapsed(e));
				localLayoutMap.get(ID).put("portOffset", glConnection.getPortOffset(e));
			}
		}
	}
	
	public static String insert(String bag, String marble, int index) {
	    String bagBegin = bag.substring(0,index);
	    String bagEnd = bag.substring(index);
	    return bagBegin + marble + bagEnd;
	}
}
