package org.processmining.plugins.inductiveminer2.attributes;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Pair;

import gnu.trove.map.hash.THashMap;

public class AttributesInfoImpl implements AttributesInfo {

	private final THashMap<String, Attribute> traceAttributes;
	private final THashMap<String, Attribute> eventAttributes;

	public AttributesInfoImpl(Iterable<XTrace> log) {
		//real attributes
		Pair<THashMap<String, AttributeImpl>, THashMap<String, AttributeImpl>> p = getRealAttributes(log);
		THashMap<String, AttributeImpl> traceAttributesReal = p.getA();
		traceAttributes = new THashMap<>();
		traceAttributes.putAll(traceAttributesReal);
		THashMap<String, AttributeImpl> eventAttributesReal = p.getB();
		eventAttributes = new THashMap<>();
		eventAttributes.putAll(eventAttributesReal);
	}

	public AttributesInfoImpl(Iterable<XTrace> log, AttributeVirtualFactory factory) {
		Pair<THashMap<String, AttributeImpl>, THashMap<String, AttributeImpl>> p = getRealAttributes(log);
		THashMap<String, AttributeImpl> traceAttributesReal = p.getA();
		traceAttributes = new THashMap<>();
		traceAttributes.putAll(traceAttributesReal);
		THashMap<String, AttributeImpl> eventAttributesReal = p.getB();
		eventAttributes = new THashMap<>();
		eventAttributes.putAll(eventAttributesReal);

		//virtual attributes
		{
			THashMap<String, AttributeVirtual> traceAttributesVirtual = new THashMap<>();
			for (AttributeVirtual attribute : factory.createVirtualTraceAttributes(traceAttributesReal,
					eventAttributesReal)) {
				traceAttributesVirtual.put(attribute.getName(), attribute);
			}

			THashMap<String, AttributeVirtual> eventAttributesVirtual = new THashMap<>();
			for (AttributeVirtual attribute : factory.createVirtualEventAttributes(traceAttributesReal,
					eventAttributesReal)) {
				eventAttributesVirtual.put(attribute.getName(), attribute);
			}

			for (XTrace trace : log) {
				for (AttributeVirtual traceAttribute : traceAttributesVirtual.values()) {
					traceAttribute.add(trace);
				}

				for (XEvent event : trace) {
					for (AttributeVirtual eventAttribute : eventAttributesVirtual.values()) {
						eventAttribute.add(event);
					}
				}
			}

			traceAttributes.putAll(traceAttributesVirtual);
			eventAttributes.putAll(eventAttributesVirtual);
		}
	}

	public static Pair<THashMap<String, AttributeImpl>, THashMap<String, AttributeImpl>> getRealAttributes(
			Iterable<XTrace> log) {
		THashMap<String, AttributeImpl> traceAttributesReal = new THashMap<>();
		THashMap<String, AttributeImpl> eventAttributesReal = new THashMap<>();

		for (XTrace trace : log) {
			addReal(traceAttributesReal, trace.getAttributes());
			for (XEvent event : trace) {
				addReal(eventAttributesReal, event.getAttributes());
			}
		}

		//finalise
		for (AttributeImpl attribute : traceAttributesReal.values()) {
			attribute.finalise();
		}
		for (AttributeImpl attribute : eventAttributesReal.values()) {
			attribute.finalise();
		}
		return Pair.of(traceAttributesReal, eventAttributesReal);
	}

	private static void addReal(THashMap<String, AttributeImpl> attributes, XAttributeMap add) {
		for (Entry<String, XAttribute> e : add.entrySet()) {
			AttributeImpl old = attributes.get(e.getKey());
			if (old == null) {
				AttributeImpl empty = new AttributeImpl(e.getKey());
				empty.addValue(e.getValue());
				attributes.put(e.getKey(), empty);
			} else {
				old.addValue(e.getValue());
			}
		}
	}

	/**
	 * Convenience function for classifiers.
	 * 
	 * @return
	 */
	@Override
	public Collection<Attribute> getEventAttributes() {
		return eventAttributes.values();
	}

	@Override
	public Attribute getEventAttributeValues(String attribute) {
		return eventAttributes.get(attribute);
	}

	@Override
	public Collection<Attribute> getTraceAttributes() {
		return new TreeSet<>(traceAttributes.values());
	}

	@Override
	public Attribute getTraceAttributeValues(String attribute) {
		return traceAttributes.get(attribute);
	}

}
