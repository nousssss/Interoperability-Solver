/**
 * 
 */
package org.processmining.plugins.connectionfactories.logpetrinet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;

/**
 * @author aadrians
 * 
 */
@ConnectionObjectFactory
@Plugin(name = "Event class of a Log/Petrinet connection factory", parameterLabels = { "Log", "Petrinet" }, returnLabels = "connection", returnTypes = EvClassLogPetrinetConnection.class, userAccessible = false)
public class EvClassLogPetrinetConnectionFactory {
	@PluginVariant(variantLabel = "Petrinet", requiredParameterLabels = { 0, 1 })
	public EvClassLogPetrinetConnection connect(UIPluginContext context, XLog log, PetrinetGraph net) {

		// list possible classifiers
		List<XEventClassifier> classList = new ArrayList<XEventClassifier>(log.getClassifiers());
		// add default classifiers
		if (!classList.contains(XLogInfoImpl.RESOURCE_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.RESOURCE_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.NAME_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.NAME_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.STANDARD_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.STANDARD_CLASSIFIER);
		}

		Object[] availableEventClass = classList.toArray(new Object[classList.size()]);
		EvClassLogPetrinetConnection con = null;
		int selection = 0;
		// build and show the UI to make the mapping
		EvClassLogPetrinetConnectionFactoryUI ui = new EvClassLogPetrinetConnectionFactoryUI(log, net,
				availableEventClass);
		do {

			InteractionResult result = context.showWizard("Mapping Petrinet - Event Class of Log", true, true, ui);

			// create the connection or not according to the button pressed in the UI
			if (result == InteractionResult.FINISHED) {
				// check if all event classes are mapped
				XLogInfo summary = XLogInfoFactory.createLogInfo(log, ui.getSelectedClassifier());
				XEventClasses eventClasses = summary.getEventClasses();
				Collection<XEventClass> colEventClasses = new HashSet<XEventClass>(eventClasses.getClasses());
				colEventClasses.removeAll(ui.getMap().values());

				if (colEventClasses.size() > 0) {
					// create new JList
					JList list = new JList(colEventClasses.toArray());
					JPanel panel = new JPanel();
					BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
					panel.setLayout(layout);
					panel.add(new JLabel("The following event classes are not mapped to any transitions in the model:"));

					JScrollPane sp = new JScrollPane(list);
					panel.add(sp);
					panel.add(new JLabel("Do you want to return to mapping menu and map them?"));

					Object[] options = { "No, I've mapped all necessary event classes", "Yes, go back to mapping" };

					selection = JOptionPane.showOptionDialog(null, panel, "Mapping", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (selection == 0) {

						// mapping is finished, create connection
						con = new EvClassLogPetrinetConnection("Connection between " + net.getLabel() + " and "
								+ XConceptExtension.instance().extractName(log), net, log, ui.getSelectedClassifier(),
								ui.getMap());
						
					}
					;
				} else {
					selection = 0;
					con = new EvClassLogPetrinetConnection("Connection between " + net.getLabel() + " and "
							+ XConceptExtension.instance().extractName(log), net, log, ui.getSelectedClassifier(),
							ui.getMap());
				}
			} else {
				return null;
			}
		} while (selection != 0);

		// return the connection (or null if the connection hasn't been created)
		return con;

	}
}
