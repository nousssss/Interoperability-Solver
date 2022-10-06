package org.processmining.plugins.inductiveminer2.attributes;

import java.util.Collection;

import org.deckfour.xes.model.XAttributable;

public abstract class AttributeVirtualTraceTimeAbstract extends AttributeVirtual {

	protected long min = Long.MAX_VALUE;
	protected long max = Long.MIN_VALUE;

	@Override
	public final void add(XAttributable x) {
		long value = getTime(x);
		if (value != Long.MIN_VALUE) {
			min = Math.min(value, min);
			max = Math.max(value, max);
		}
	}

	@Override
	public final boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public final boolean isNumeric() {
		return false;
	}

	@Override
	public final boolean isTime() {
		return true;
	}

	@Override
	public final boolean isDuration() {
		return false;
	}

	@Override
	public final Collection<String> getStringValues() {
		assert false;
		return null;
	}

	@Override
	public final boolean getBooleanHasTrue() {
		assert false;
		return false;
	}

	@Override
	public final boolean getBooleanHasFalse() {
		assert false;
		return false;
	}

	@Override
	public final double getNumericMin() {
		assert false;
		return -Double.MAX_VALUE;
	}

	@Override
	public final double getNumericMax() {
		assert false;
		return -Double.MAX_VALUE;
	}

	@Override
	public final long getTimeMin() {
		return min;
	}

	@Override
	public final long getTimeMax() {
		return max;
	}

	@Override
	public final long getDurationMin() {
		assert false;
		return Long.MIN_VALUE;
	}

	@Override
	public final long getDurationMax() {
		assert false;
		return Long.MIN_VALUE;
	}

	@Override
	public final String getLiteral(XAttributable trace) {
		assert false;
		return null;
	}

	@Override
	public final Boolean getBoolean(XAttributable x) {
		assert false;
		return null;
	}

	@Override
	public final double getNumeric(XAttributable trace) {
		assert false;
		return -Double.MAX_VALUE;
	}

	@Override
	public final long getDuration(XAttributable trace) {
		assert false;
		return Long.MIN_VALUE;
	}
}