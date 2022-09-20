package org.processmining.plugins.log.filter;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.BorderPanel;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;

import com.fluxicon.slickerbox.components.SlickerButton;

public class LogEventUnifier_UI  extends BorderPanel {

	private static final long serialVersionUID = 1L;

	public final static String DIALOG_TITLE = "Unify Log Events";
	
	private LogEventUnifier unifier;
	
	private ProMList<String> eventClassList;
	private DefaultListModel<String> eventClassListModel;

	private ProMList<String> typeAssignedList;
	private DefaultListModel<String> typeAssignedListModel;
	private ProMList<String> typeUnassignedList;
	private DefaultListModel<String> typeUnassignedListModel;
	
	public LogEventUnifier_UI(LogEventUnifier unifier) {
		super(0, 0);
		
		this.unifier = unifier;
		
		setLayout(new BorderLayout());
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		ProMSplitPane splitPane = new ProMSplitPane();
		
		BorderPanel leftPanel = new BorderPanel(0, 0);
			leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
			leftPanel.setOpaque(false);
			
//			BorderPanel eventChoice = new BorderPanel(0, 0);
//				eventChoice.setLayout(new FlowLayout());
//				eventChoice.setOpaque(false);
//
//				JLabel eventChoiceLabel = new JLabel("add new event class");
//				eventChoiceLabel.setOpaque(false);
//				eventChoice.add(eventChoiceLabel);
//				
//			leftPanel.add(eventChoice);

			eventClassListModel = new DefaultListModel<String>();
			eventClassList = new ProMList<String>("event classes", eventClassListModel);
			eventClassList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			eventClassList.addListSelectionListener(new EventClassSelectionListener());
			leftPanel.add(eventClassList);
				
			typeAssignedListModel = new DefaultListModel<String>();
			typeAssignedList = new ProMList<String>("assigned events", typeAssignedListModel);
			leftPanel.add(typeAssignedList, BorderLayout.CENTER);
			
		splitPane.setLeftComponent(leftPanel);
		
		BorderPanel rightPanel = new BorderPanel(0, 0);
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setOpaque(false);
		
			BorderPanel buttons = new BorderPanel(0, 0);
				buttons.setLayout(new FlowLayout());
				buttons.setOpaque(false);
				
				SlickerButton buttonRemove = new SlickerButton("> remove");
				buttonRemove.addActionListener(new RemoveEventsFromAssignmentListener());
				SlickerButton buttonAdd = new SlickerButton("< add");
				buttonAdd.addActionListener(new AddEventsToAssignmentListener());
				
				buttons.add(buttonRemove);
				buttons.add(buttonAdd);
			rightPanel.add(buttons, BorderLayout.NORTH);
		
				typeUnassignedListModel = new DefaultListModel<String>();
				typeUnassignedList = new ProMList<String>("unassigned events", typeUnassignedListModel);
		
			rightPanel.add(typeUnassignedList, BorderLayout.CENTER);
		
		splitPane.setRightComponent(rightPanel);
		splitPane.setResizeWeight(.5);
		
		
		splitPane.setPreferredSize(new Dimension(800, 500));
		
		add(splitPane, BorderLayout.CENTER);
	
		setValues(unifier);
	}
	
	/**
	 * Set values of controls based on values in the filter.
	 * @param filter
	 */
	protected void setValues(LogEventUnifier unifier) {

		updateUnassignedList();
		
		TreeSet<String> eventClassNames = new TreeSet<String>(unifier.mapping.e2eMap.keySet());
		eventClassListModel.clear();
		typeAssignedListModel.clear();
		for (String eventClass : eventClassNames) eventClassListModel.addElement(eventClass);
		
		if (eventClassListModel.size() > 0) {
			eventClassList.setSelectedIndex(0);
		}
	}
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context) {
		return context.showConfiguration(DIALOG_TITLE, this);
	}
	
	/**
	 * Populate filter object from settings in the panel.
	 * @param filter
	 */
	protected void updateValues(LogEventUnifier unifier) {
		// nothing to do, object is kept up to date by handlers
	}
	
	/**
	 * Open UI dialogue to populate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 */
	public InteractionResult setParameters(UIPluginContext context, LogEventUnifier filter) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) updateValues(filter);
		return wish;
	}

	/**
	 * Clear contents of list of assigned events {@link #typeAssignedList} and
	 * fill it with the events assigned to the currently selected event class
	 * (selection in {@link #eventClassList})
	 */
	private void updateAssignedList() {
		typeAssignedListModel.clear();
		if (eventClassList.getSelectedValuesList().size() > 0) {
			String selected = eventClassList.getSelectedValuesList().get(0);
			if (selected != null && unifier.mapping.e2eMap.containsKey(selected)) {
				for (String events : unifier.mapping.e2eMap.get(selected))
					typeAssignedListModel.addElement(events);
			}
		}
	}
	
	/**
	 * Update model of {@link #typeUnassignedList} based on data in
	 * {@link LogEventUnifier#unassigned}
	 */
	private void updateUnassignedList() {
		typeUnassignedListModel.clear();
		for (String unassigned : unifier.mapping.unassigned) typeUnassignedListModel.addElement(unassigned);
	}
	
	/**
	 * Listener to watch {@link LogEventUnifier_UI#eventClassList} for changes
	 * in the selection. Will update the contents of {@link LogEventUnifier_UI#typeAssignedList}
	 * based on {@link LogEventUnifier#e2eMap}.
	 * 
	 * @author dfahland
	 *
	 */
	private class EventClassSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			updateAssignedList();
		}
	}

	/**
	 * Listener for "remove" button in the panel, moves events selected in the
	 * {@link LogEventUnifier_UI#typeAssignedList} to {@link LogEventUnifier#unassigned}
	 * and updates lists accordingly
	 * 
	 * @author dfahland
	 *
	 */
	private class RemoveEventsFromAssignmentListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (   eventClassList.getSelectedValuesList().size() > 0
				&& typeAssignedList.getSelectedValuesList().size() > 0)
			{
				String selected = eventClassList.getSelectedValuesList().get(0);
				for (Object selectedEvent : typeAssignedList.getSelectedValuesList()) {
					unifier.mapping.e2eMap.get(selected).remove(selectedEvent);
					unifier.mapping.unassigned.add((String)selectedEvent);
				}
				updateAssignedList();
				updateUnassignedList();
			}
		}
	}
	
	/**
	 * Listener for "add" button in the panel, moves events selected in the
	 * {@link LogEventUnifier_UI#typeUnassignedList} to {@link LogEventUnifier#e2eMap}
	 * based on the current selection of {@link LogEventUnifier_UI#eventClassList}
	 * and updates lists accordingly
	 * 
	 * @author dfahland
	 *
	 */
	private class AddEventsToAssignmentListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (   eventClassList.getSelectedValuesList().size() > 0
				&& typeUnassignedList.getSelectedValuesList().size() > 0)
			{
				String selected = eventClassList.getSelectedValuesList().get(0);
				for (Object selectedEvent : typeUnassignedList.getSelectedValuesList()) {
					unifier.mapping.e2eMap.get(selected).add((String)selectedEvent);
					unifier.mapping.unassigned.remove(selectedEvent);
				}
				updateAssignedList();
				updateUnassignedList();
			}
		}
	}

//	/**
//	 * Listener to watch {@link LogEventUnifier_UI#attribute_filter_filter_on} and store
//	 * attribute names in {@link LogEventUnifier_UI#attribute_filter_log_attributes} and updated
//	 * {@link LogEventUnifier_UI#attribute_filter_log_values} accordingly
//	 */
//	private class FilterOnListener implements ActionListener {
//
//		public void actionPerformed(ActionEvent e) {
//			if (e.getID() == ActionEvent.ACTION_PERFORMED && e.getSource()== attribute_filter_filter_on) {
//				
//				TreeSet<String> attributeNames = getAttributes((String)attribute_filter_filter_on.getSelectedItem());
//				attribute_filter_log_attributes.setModel(new DefaultComboBoxModel(attributeNames.toArray()));
//				
//				TreeSet<String> values = getValues(
//						(String)attribute_filter_filter_on.getSelectedItem(),
//						(String)attribute_filter_log_attributes.getSelectedItem());
//				attribute_filter_log_values.setModel(new DefaultComboBoxModel(values.toArray()));
//			}
//		}
//	}
//	
//	/**
//	 * Listener to watch {@link LogEventUnifier_UI#attribute_filter_log_attributes} and store
//	 * attribute names in {@link LogEventUnifier_UI#attribute_filter_log_values}.
//	 */
//	private class AttributeListener implements ActionListener {
//
//		public void actionPerformed(ActionEvent e) {
//			if (e.getID() == ActionEvent.ACTION_PERFORMED && e.getSource() == attribute_filter_log_attributes) {
//				
//				TreeSet<String> values = getValues(
//						(String)attribute_filter_filter_on.getSelectedItem(),
//						(String)attribute_filter_log_attributes.getSelectedItem());
//				attribute_filter_log_values.setModel(new DefaultComboBoxModel(values.toArray()));
//			}
//		}
//	}
	

}
