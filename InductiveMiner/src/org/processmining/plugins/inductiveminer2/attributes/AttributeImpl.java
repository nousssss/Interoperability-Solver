package org.processmining.plugins.inductiveminer2.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;

import gnu.trove.set.hash.THashSet;

public class AttributeImpl extends AttributeAbstract {

	public enum Type {
		undecided, literal, numeric, time, bool;
	}

	private String name;
	private Type type = Type.undecided;
	private Collection<String> valuesLiteral;
	private double valuesNumericMin;
	private double valuesNumericMax;
	private long valuesTimeMin;
	private long valuesTimeMax;
	private boolean hasFalse;
	private boolean hasTrue;

	public AttributeImpl(String name) {
		this.name = name;
		this.valuesLiteral = new THashSet<>();
		this.valuesNumericMin = Double.MAX_VALUE;
		this.valuesNumericMax = -Double.MAX_VALUE;
		this.valuesTimeMin = Long.MAX_VALUE;
		this.valuesTimeMax = Long.MIN_VALUE;
		this.hasFalse = false;
		this.hasTrue = false;
	}

	public AttributeImpl(String name, Type type) {
		this.name = name;
		this.type = type;
		switch (type) {
			case literal :
				valuesLiteral = new THashSet<>();
				break;
			case numeric :
				valuesNumericMin = Double.MAX_VALUE;
				valuesNumericMax = -Double.MAX_VALUE;
				break;
			case time :
				valuesTimeMin = Long.MAX_VALUE;
				valuesTimeMax = Long.MIN_VALUE;
				break;
			case bool :
				hasFalse = false;
				hasTrue = false;
				break;
			case undecided :
				break;
			default :
				break;
		}
	}

	public void addValue(XAttribute attribute) {
		valuesLiteral.add(attribute.toString());
		if (type == type.undecided) {
			Boolean bool = AttributeUtils.parseBooleanFast(attribute);
			if (bool != null) {
				type = type.bool;
			} else {
				double numeric = AttributeUtils.parseDoubleFast(attribute);
				if (numeric != -Double.MAX_VALUE) {
					type = type.numeric;
				} else {
					long time = AttributeUtils.parseTimeFast(attribute);
					if (time != Long.MIN_VALUE) {
						type = type.time;
					} else {
						type = type.literal;
					}
				}
			}
		}
		//process boolean
		if (type == type.bool) {
			Boolean bool = AttributeUtils.parseBooleanFast(attribute);
			if (bool != null) {
				//this is a boolean
				if (bool) {
					hasTrue = true;
				} else {
					hasFalse = true;
				}
			} else {
				//this is a string, remove the boolean storage
				type = Type.literal;
			}
		}
		//process numeric
		if (type == type.numeric) {
			double numeric = AttributeUtils.parseDoubleFast(attribute);
			if (numeric != -Double.MAX_VALUE) {
				//this is a number
				valuesNumericMin = Math.min(valuesNumericMin, numeric);
				valuesNumericMax = Math.max(valuesNumericMax, numeric);
			} else {
				//this is a string, remove the number storage
				type = Type.literal;
			}
		}
		//process time
		if (type == type.time) {
			long time = AttributeUtils.parseTimeFast(attribute);
			if (time != Long.MIN_VALUE) {
				//this is a time
				valuesTimeMin = Math.min(valuesTimeMin, time);
				valuesTimeMax = Math.max(valuesTimeMax, time);
			} else {
				//this is a string, remove the number storage
				type = Type.literal;
			}
		}
	}

	public void addNumber(double number) {
		assert type == Type.numeric;
		valuesNumericMin = Math.min(valuesNumericMin, number);
		valuesNumericMax = Math.max(valuesNumericMax, number);
	}

	public void addTime(long time) {
		assert type == Type.time;
		valuesTimeMin = Math.min(valuesTimeMin, time);
		valuesTimeMax = Math.max(valuesTimeMax, time);
	}

	public void addBoolean(boolean bool) {
		assert type == Type.bool;
		if (bool) {
			hasTrue = true;
		} else {
			hasFalse = true;
		}
	}

	public void finalise() {
		switch (type) {
			case literal :
				// sort the values
				valuesLiteral = new ArrayList<String>(valuesLiteral);
				Collections.sort((ArrayList<String>) valuesLiteral);
				break;
			default :
				valuesLiteral = null;
				break;
		}
	}

	;

	@Override
	public boolean isLiteral() {
		return type == Type.literal;
	}

	@Override
	public boolean isNumeric() {
		return type == Type.numeric;
	}

	@Override
	public boolean isTime() {
		return type == Type.time;
	}

	@Override
	public boolean isBoolean() {
		return type == Type.bool;
	}

	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public boolean isDuration() {
		return false;
	}

	@Override
	public long getDurationMin() {
		assert false;
		return Long.MIN_VALUE;
	}

	@Override
	public long getDurationMax() {
		assert false;
		return Long.MIN_VALUE;
	}

	@Override
	public Collection<String> getStringValues() {
		return valuesLiteral;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public double getNumericMin() {
		assert type == Type.numeric;
		return valuesNumericMin;
	}

	@Override
	public double getNumericMax() {
		assert type == Type.numeric;
		return valuesNumericMax;
	}

	@Override
	public long getTimeMin() {
		assert type == Type.time;
		return valuesTimeMin;
	}

	@Override
	public long getTimeMax() {
		assert type == Type.time;
		return valuesTimeMax;
	}

	@Override
	public boolean getBooleanHasTrue() {
		return hasTrue;
	}

	@Override
	public boolean getBooleanHasFalse() {
		return hasFalse;
	}

	@Override
	public String getLiteral(XAttributable x) {
		assert type == Type.literal;
		if (x.hasAttributes() && x.getAttributes().containsKey(name)) {
			return x.getAttributes().get(name).toString();
		}
		return null;
	}

	@Override
	public double getNumeric(XAttributable x) {
		assert type == Type.numeric;
		if (x.hasAttributes() && x.getAttributes().containsKey(name)) {
			return AttributeUtils.parseDoubleFast(x.getAttributes().get(name));
		}
		return -Double.MAX_VALUE;
	}

	@Override
	public long getTime(XAttributable x) {
		assert type == Type.time;
		if (x.hasAttributes() && x.getAttributes().containsKey(name)) {
			return AttributeUtils.parseTimeFast(x.getAttributes().get(name));
		}
		return Long.MIN_VALUE;
	}

	@Override
	public Boolean getBoolean(XAttributable x) {
		assert type == Type.bool;
		if (x.hasAttributes() && x.getAttributes().containsKey(name)) {
			return AttributeUtils.parseBooleanFast(x.getAttributes().get(name));
		}
		return null;
	}

	public long getDuration(XAttributable x) {
		assert false;
		return Long.MIN_VALUE;
	}
}
