package org.processmining.plugins.pnml.elements.extensions;

import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.elements.PnmlAnnotation;
import org.xmlpull.v1.XmlPullParser;

/**
 * Extension PNML arc type object.
 * 
 * @author hverbeek
 */
public class PnmlArcType extends PnmlAnnotation {

	/**
	 * (E)PNML arc type tag.
	 */
	public final static String TAG = "arctype"; // PNML core
	public final static String EPNML_TAG = "type"; //ePNMNL (Yasper)

	/**
	 * Creates a fresh arc type.
	 * 
	 * @param tag
	 */
	protected PnmlArcType(String tag) {
		super(tag);
	}

	/**
	 * Checks whether the current start tag is known. If known, it imports the
	 * corresponding child element and returns true. Otherwise, it returns
	 * false.
	 * 
	 * @return Whether the start tag was known.
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		/*
		 * Unknown start tag.
		 */
		return false;
	}

	/**
	 * Exports the arc type.
	 */
	protected String exportElements(Pnml pnml) {
		/*
		 * Export annotation elements.
		 */
		return super.exportElements(pnml);
	}

	/*
	 * Returns whether regular arc.
	 */
	public boolean isNormal() {
		return (text == null) || (text.getText() == null) || text.getText().equals("normal");
	}

	/*
	 * Returns whether reset arc.
	 */
	public boolean isReset() {
		return (text != null) && (text.getText() != null) && text.getText().equals("reset");
	}

	/*
	 * Returns whether inhibitor arc.
	 */
	public boolean isInhibitor() {
		return (text != null) && (text.getText() != null) && text.getText().equals("inhibitor");
	}

	/*
	 * Returns whether read arc.
	 */
	public boolean isRead() {
		return (text != null) && (text.getText() != null) && text.getText().equals("read");
	}

	public void setNormal() {
		text = factory.createPnmlText("normal");
	}

	public void setReset() {
		text = factory.createPnmlText("reset");
	}

	public void setInhibitor() {
		text = factory.createPnmlText("inhibitor");
	}

	public void setRead() {
		text = factory.createPnmlText("read");
	}

}
