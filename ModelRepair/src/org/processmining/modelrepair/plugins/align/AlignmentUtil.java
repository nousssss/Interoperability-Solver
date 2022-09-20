package org.processmining.modelrepair.plugins.align;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.info.impl.XLogInfoImpl;

/**
 * Utility class to manage event names, classifiers, etc. for headless
 * computation of alignments.
 * 
 * @author dfahland
 */
public class AlignmentUtil {

	/**
	 * Reformat given eventName into different representation.
	 * 
	 * If XLogInfoImpl.STANDARD_CLASSIFIER is used, the eventname will be
	 * formatted into 'name+lifecycle' where 'lifecycle' is set to
	 * {@link XLifecycleExtension.StandardModel.COMPLETE} if no lifecycle
	 * information can be parsed from the given eventname.
	 * 
	 * Otherwise, the eventName will be stripped of lifecycle information and
	 * only the eventName will be returned.
	 * 
	 * @param qualified_eventName
	 * @param ecl
	 * @return
	 */
	public static String reformatEventName_legacy(String qualified_eventName, XEventClassifier ecl) {
		// split name into event name and life-cycle transition

		String name;
		String life_cycle;
		int plus_pos = qualified_eventName.indexOf('+');
		if (plus_pos >= 0) {
			name = qualified_eventName.substring(0, plus_pos);
			life_cycle = qualified_eventName.substring(plus_pos + 1);
		} else {
			name = qualified_eventName;
			life_cycle = XLifecycleExtension.StandardModel.COMPLETE.toString();
		}

		String eventName;
		// add event to trace
		if (ecl == XLogInfoImpl.STANDARD_CLASSIFIER)
			eventName = name + "+" + life_cycle;
		else
			eventName = name;

		return eventName;
	}
}
