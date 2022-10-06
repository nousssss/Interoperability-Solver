package org.processmining.plugins.inductiveminer2.attributes;

import java.util.Collection;

import org.deckfour.xes.model.XAttributable;

public abstract class AttributeVirtualTraceBooleanAbstract extends AttributeVirtual {

	protected boolean hasTrue = false;
	protected boolean hasFalse = false;

	@Override
	public final void add(XAttributable x) {
		Boolean value = getBoolean(x);
		if (value != null) {
			if (value) {
				hasTrue = true;
			} else {
				hasFalse = true;
			}
		}
	}

	@Override
	public final boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return true;
	}

	@Override
	public final boolean isNumeric() {
		return false;
	}

	@Override
	public final boolean isTime() {
		return false;
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
	public final boolean getBooleanHasTrue() {
		return hasTrue;
	}

	@Override
	public final boolean getBooleanHasFalse() {
		return hasFalse;
	}

	@Override
	public final long getTimeMin() {
		assert false;
		return Long.MIN_VALUE;
	}

	@Override
	public final long getTimeMax() {
		assert false;
		return Long.MIN_VALUE;
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
	public final double getNumeric(XAttributable x) {
		assert false;
		return -Double.MAX_VALUE;
	}

	@Override
	public final String getLiteral(XAttributable x) {
		assert false;
		return null;
	}

	@Override
	public final long getTime(XAttributable x) {
		assert false;
		return Long.MIN_VALUE;
	}

	@Override
	public final long getDuration(XAttributable x) {
		assert false;
		return Long.MIN_VALUE;
	}

}
