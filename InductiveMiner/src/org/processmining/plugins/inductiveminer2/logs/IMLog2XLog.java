package org.processmining.plugins.inductiveminer2.logs;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

public class IMLog2XLog {
	public static XLog toXLog(IMLog log) {
		XAttributeMap logMap = new XAttributeMapImpl();
		putLiteral(logMap, "concept:name", "generated log from process tree");
		XLog result = new XLogImpl(logMap);

		for (IMTraceIterator it = log.iterator(); it.hasNext();) {
			it.nextFast();

			List<XEvent> trace = new ArrayList<>();

			while (it.itEventHasNext()) {
				it.itEventNext();
				XAttributeMap eventMap = new XAttributeMapImpl();
				putLiteral(eventMap, "concept:name", log.getActivity(it.itEventGetActivityIndex()));
				putLiteral(eventMap, "lifecycle:transition", it.itEventGetLifeCycleTransition().toString());
				trace.add(new XEventImpl(eventMap));
			}

			XTrace trace2 = new XTraceImpl(new XAttributeMapImpl());
			trace2.addAll(trace);
			result.add(trace2);
		}

		return result;
	}

	public static void putLiteral(XAttributeMap attMap, String key, String value) {
		attMap.put(key, new XAttributeLiteralImpl(key, value));
	}
}
