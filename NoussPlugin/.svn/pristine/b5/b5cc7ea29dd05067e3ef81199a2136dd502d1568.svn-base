package org.processmining.models.workshop.fbetancor.visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.BorderPanel;
import org.processmining.framework.util.ui.widgets.ProMScrollContainer;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.models.workshop.fbetancor.constructors.Output;
import org.processmining.models.workshop.fbetancor.constructors.OutputDefinition;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Class that implements the GUI for the data quality plug-in.
 * 
 * @author R. Verhulst
 *
 */

public class GraphicalUserInterface {
	/**
	 * Main Panel
	 */
	private static JPanel mainPanel;

	/**
	 * Split panel to be placed on the mainpanel.
	 */
	private static JSplitPane sp;

	/**
	 * List of all the output.
	 */
	private static List<Output> outputListGetter;

	/**
	 * Scroll Container for the right Panel.
	 */
	private static ProMScrollContainer rightPromScrollContainer;

	/**
	 * Scroll Container for the left Panel.
	 */
	private static ProMScrollContainer leftPromScrollContainer;

	/**
	 * Textbox for the right Panel output.
	 */
	private static JTextPane textBox;

	/**
	 * Overall Advice String.
	 */
	private static String adviceTotal;

	@Plugin(name = "Data Quality Plugin (123)", parameterLabels = { "Log" }, returnLabels = {
			"Data Quality Score Card" }, returnTypes = { JComponent.class }, userAccessible = true)

	@Visualizer
	public JComponent visualize(PluginContext context, OutputDefinition output) {
		return getMainPanel(output);
	}

	/**
	 * Get the main panel.
	 * 
	 * @param output
	 * @return mainPanel
	 */
	public JPanel getMainPanel(OutputDefinition output) {
		outputListGetter = output.getOutputList();
		// .addChild(ProMScrollContainerChilds)
		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		if (mainPanel == null) {
			adviceTotal = "";
			adviceTotal += "" + "<html> <b> Overall Advice: </b> <br><br>";
			mainPanel = SlickerFactory.instance().createRoundedPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			setLeftPanel();
			setRightPanel();
			setSplitPanel();
			setTitle(output.getEventlogName());
			setScore(output.getOutputScore());
			setDimensions(output.getOutputList());
			adviceTotal += "</html>";
			setAdviceButton();
		}
		return mainPanel;
	}

	/**
	 * Set the button for the overall advice. When clicked on, the right panels
	 * shows the information.
	 */
	private void setAdviceButton() {
		JButton adviceButton = new JButton("Overall Advice");
		adviceButton.setBounds(685, 23, 230, 60);
		adviceButton.setFocusPainted(false);
		adviceButton.setBackground(new Color(0, 51, 51));
		adviceButton.setForeground(Color.white);
		adviceButton.setFont(new Font("Tahoma", Font.BOLD, 12));

		leftPromScrollContainer.add(adviceButton);

		adviceButton.addActionListener(new ActionListener() {
			/**
			 * Sets the advice information on the right panel when clicked on
			 * the button.
			 */
			public void actionPerformed(ActionEvent e) {
				rightPromScrollContainer.removeAll();
				textBox.removeAll();
				sp.remove(rightPromScrollContainer);
				setRightPanel();

				JLabel label1 = new JLabel();
				label1 = new JLabel("" + adviceTotal + "");

				label1.setFont(new Font("Arial", Font.ITALIC, 15));
				label1.setForeground(Color.black);
				label1.setVerticalAlignment(SwingConstants.TOP);
				Dimension dimSize = new Dimension(label1.getPreferredSize());
				label1.setPreferredSize(dimSize);

				ProMTextArea textAreaProM = new ProMTextArea();
				textAreaProM.setFont(new Font("Arial", Font.ITALIC, 15));
				textAreaProM.setForeground(Color.black);
				textAreaProM.setBackground(Color.gray);
				textAreaProM.add(label1);
				textAreaProM.setPreferredSize(dimSize);

				JScrollPane scrollPane = new JScrollPane(textAreaProM, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setFont(new Font("Arial", Font.ITALIC, 15));
				scrollPane.setForeground(Color.black);
				scrollPane.setBackground(Color.white);
				scrollPane.setBounds(10, 10, 920, 880);

				rightPromScrollContainer.add(scrollPane);
				sp.add(rightPromScrollContainer);
				mainPanel.isVisible();
			}
		});

	}

	/**
	 * Sets the split panel for the GUI.
	 */
	public void setSplitPanel() {
		sp.setResizeWeight(0.5);
		sp.setEnabled(false);
		sp.setDividerSize(0);
		sp.add(leftPromScrollContainer);
		sp.add(rightPromScrollContainer);
		mainPanel.add(sp, BorderLayout.CENTER);
		mainPanel.revalidate();
	}

	/**
	 * Sets the left panel of the split panel.
	 */
	public void setLeftPanel() {
		leftPromScrollContainer = new ProMScrollContainer();
		leftPromScrollContainer.setBackground(Color.gray);
		leftPromScrollContainer.setLayout(null);
	}

	/**
	 * Sets the right panel of the split panel.
	 */
	public void setRightPanel() {
		textBox = new JTextPane();
		textBox.setLayout(null);
		rightPromScrollContainer = new ProMScrollContainer(1, 1);
		rightPromScrollContainer.setLayout(null);
	}

	/**
	 * Sets the title of the report.
	 */
	public void setTitle(String eventlogName) {
		JLabel lblDataQualityReport = new JLabel("<html>Event Log Name: " + eventlogName + "</html>");
		lblDataQualityReport.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblDataQualityReport.setBounds(29, 13, 635, 32);
		lblDataQualityReport.setForeground(Color.black);
		leftPromScrollContainer.add(lblDataQualityReport);
	}

	/**
	 * Sets the logName on the report.
	 * 
	 * @param eventlogName
	 */
	public void setScore(String outputScore) {
		JLabel lblScore = new JLabel("   Overall Score: " + outputScore);
		lblScore.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblScore.setBounds(29, 58, 683, 16);
		lblScore.setForeground(Color.black);
		leftPromScrollContainer.add(lblScore);
	}

	/**
	 * Sets all the different quality checks on the left-panel.
	 * 
	 * @param outputList
	 */
	public void setDimensions(List<Output> outputList) {
		int modulo = 0;
		int rowN = 0;
		int counting = 0;

		final Map<JButton, Integer> buttonMap = new HashMap<JButton, Integer>();

		for (Output element : outputList) {
			BorderPanel panel = new BorderPanel(1, 3);
			panel.setBorder(BorderFactory.createLineBorder(Color.gray));

			/*
			 * Set the panel in the right position.
			 */
			if (modulo % 2 == 0) {
				panel.setBounds(30, (100 + 90 * rowN), 260, 80);
				modulo++;
			} else if (modulo % 2 == 1) {
				panel.setBounds(342, (100 + 90 * rowN), 260, 80);
				modulo = 0;
				rowN++;
//			} else if (modulo % 3 == 2) {
//				panel.setBounds(655, (100 + 90 * rowN), 260, 80);
//				modulo = 0;
//				rowN++;
			}

			leftPromScrollContainer.add(panel);
			panel.setLayout(null);

			JLabel lblNewLabel = new JLabel("" + element.getName());
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
			lblNewLabel.setBounds(12, 5, 240, 25);

			double scoreValue = Double.parseDouble(element.getScore());
			JPanel colorPanel = new JPanel();

			if (scoreValue < 6.0 && scoreValue != 0.0) {
				colorPanel.setBackground(Color.red);
				adviceTotal += "" + "<b>- " + element.getName() + ":</b> <br> " + element.getAdvice() + "<br><br>";
			} else if (scoreValue < 8.0 && scoreValue != 0.0) {
				colorPanel.setBackground(Color.orange);
				adviceTotal += "" + "<b>- " + element.getName() + ":</b> <br> " + element.getAdvice() + "<br><br>";
			} else if (scoreValue != 0.0) {
				colorPanel.setBackground(Color.green);
			} else {
				colorPanel.setBackground(Color.gray);
			}

			colorPanel.setBorder(new LineBorder(Color.black, 1));
			colorPanel.setBounds(12, 55, (int) (scoreValue * 10), 10);
			panel.add(colorPanel);

			panel.add(lblNewLabel);

			String score = "";
			if (element.getScore().equals("0.0")) {
				score += "" + "-";
			} else {
				score += "" + element.getScore();
			}

			JLabel lblScore = new JLabel("Score: " + score);
			lblScore.setBounds(12, 31, 105, 16);
			panel.add(lblScore);

			JButton btnNewButton = new JButton("Details");
			btnNewButton.setBounds(147, 33, 97, 30);
			btnNewButton.setFocusPainted(false);
			btnNewButton.setBackground(new Color(0, 51, 51));
			btnNewButton.setForeground(Color.white);
			btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));

			panel.add(btnNewButton);

			buttonMap.put(btnNewButton, counting);
			btnNewButton.addActionListener(new ActionListener() {
				/**
				 * Sets the information on the right panel when clicked on the
				 * button.
				 */
				public void actionPerformed(ActionEvent e) {
					rightPromScrollContainer.removeAll();
					textBox.removeAll();
					sp.remove(rightPromScrollContainer);
					setRightPanel();
					Integer index = buttonMap.get(e.getSource());
					Output element = getElement(index);

					String score = "";
					if (element.getScore().equals("0.0")) {
						score += "" + "-";
					} else {
						score += "" + element.getScore();
					}

					JLabel label1 = new JLabel();
					
					String tempFaults = "";
					if (element.getFaults().equals("") || element.getFaults().isEmpty()) {
						tempFaults += ""+"-";
					} else {
						tempFaults += ""+element.getFaults();
					}

					label1 = new JLabel(
							"<html><b>Data Quality Aspect:</b> <br>" + element.getName() + "<br><br><b>Score:</b> <br>"
									+ score + "<br><br><b>Explanation:</b> <br>" + element.getExplanation() + ""
									+ "<br><br> <b>Advice:</b> <br>" + element.getAdvice()
									+ "<br><br> <b> Details: </b><br> " + tempFaults + "</html>",
							SwingConstants.LEFT);
					
					label1.setFont(new Font("Arial", Font.ITALIC, 15));
					label1.setForeground(Color.black);
					label1.setVerticalAlignment(SwingConstants.TOP);
					Dimension dimSize = new Dimension(label1.getPreferredSize());
					label1.setPreferredSize(dimSize);

					ProMTextArea textAreaProM = new ProMTextArea();
					textAreaProM.setFont(new Font("Arial", Font.ITALIC, 15));
					textAreaProM.setForeground(Color.black);
					textAreaProM.setBackground(Color.gray);
					textAreaProM.add(label1);
					textAreaProM.setPreferredSize(dimSize);

					JScrollPane scrollPane = new JScrollPane(textAreaProM, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					scrollPane.setFont(new Font("Arial", Font.ITALIC, 15));
					scrollPane.setForeground(Color.black);
					scrollPane.setBackground(Color.white);
					scrollPane.setBounds(10, 10, 920, 880);

					rightPromScrollContainer.add(scrollPane);
					sp.add(rightPromScrollContainer);
					mainPanel.isVisible();
				}
			});
			counting++;
		}
	}

	/**
	 * Get the correct element from the list of outputs.
	 * 
	 * @param count
	 * @return single output element.
	 */
	private static Output getElement(int count) {
		return outputListGetter.get(count);
	}

}