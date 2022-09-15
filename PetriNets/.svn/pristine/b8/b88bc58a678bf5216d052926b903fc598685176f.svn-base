package org.processmining.plugins.pnml.elements.extensions.opennet;

import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.opennet.OpenNetLabel;
import org.processmining.models.graphbased.directed.opennet.OpenNetLabel.Type;
import org.processmining.models.graphbased.directed.opennet.OpenNetPort;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.plugins.pnml.elements.PnmlNode;

public abstract class PnmlLabel extends PnmlNode {

	protected PnmlLabel(String tag) {
		super(tag);
	}

	public void convertToOpenNet(OpenNetPort port) {
		OpenNetLabel sync = new OpenNetLabel((((name != null) && (name.text != null)) ? name.text.getText() : id), id,
				getType());
		port.add(sync);

	}

	protected abstract Type getType();

	public PnmlLabel convertFromOpenNet(OpenNetLabel sync, Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map) {
		name = factory.createPnmlName(sync.getLabel());
		id = sync.getId();
		return this;
	}

	public final static class Input extends PnmlLabel {
		public final static String TAG = "input";

		public Input() {
			super(TAG);
		}

		protected Type getType() {
			return Type.ASYNC_INPUT;
		}

	}

	public final static class Output extends PnmlLabel {
		public final static String TAG = "output";

		public Output() {
			super(TAG);
		}

		protected Type getType() {
			return Type.ASYNC_OUTPUT;
		}

	}

	public final static class Sync extends PnmlLabel {
		public final static String TAG = "synchronous";

		public Sync() {
			super(TAG);
		}

		protected Type getType() {
			return Type.SYNC;
		}

	}
}
