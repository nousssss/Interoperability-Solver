/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.conversion;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * Create a PNRepResult object of only reliable results from manifest
 * 
 * @author aadrians Jun 1, 2012
 * 
 */
public class Manifest2PNRepResult {
	public static PNRepResult convert(Manifest manifest) {
		// temporary variable
		XLog log = manifest.getLog();
		XLogInfo logInfo = XLogInfoImpl.create(log, manifest.getEvClassifier());
		XEventClasses ec = logInfo.getEventClasses();
		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		final NumberFormat nfi = NumberFormat.getInstance();
		nfi.setMaximumFractionDigits(0);
		nfi.setMinimumFractionDigits(0);

		Map<Integer, SyncReplayResult> mappedManifests = new HashMap<Integer, SyncReplayResult>();

		int[] casePointer = manifest.getCasePointers();
		for (int i = 0; i < casePointer.length; i++) {
			SyncReplayResult res = mappedManifests.get(casePointer[i]);
			if (res != null) {
				// just add the new trace id
				res.addNewCase(i);
			} else {
				// construct alignment from manifest
				int[] caseEncoded = manifest.getManifestForCase(i);

				List<StepTypes> stepTypesLst = new ArrayList<StepTypes>(caseEncoded.length / 2);
				List<Object> eventClassLst = new ArrayList<Object>(caseEncoded.length / 2);
				int pointer = 0;
				Iterator<XEvent> it = log.get(i).iterator();
				TIntSet countedManifest = new TIntHashSet(caseEncoded.length / 2);

				while (pointer < caseEncoded.length) {
					if (caseEncoded[pointer] == Manifest.MOVELOG) {
						stepTypesLst.add(StepTypes.L);
						eventClassLst.add(ec.getClassOf(it.next()));
						pointer++;
					} else if (caseEncoded[pointer] == Manifest.MOVEMODEL) {
						Transition t = manifest.getTransitionOf(caseEncoded[pointer + 1]);
						//eventClassLst.add(manifest.getTransClassOf(t).getId());
						eventClassLst.add(t);
						if (t.isInvisible()) {
							stepTypesLst.add(StepTypes.MINVI);
						} else {
							stepTypesLst.add(StepTypes.MREAL);
						}
						pointer += 2;
					} else if (caseEncoded[pointer] == Manifest.MOVESYNC) {
						it.next(); // event class name is not used

						// only if it has been printed before, add it
						if (!countedManifest.contains(caseEncoded[pointer + 1])) {
							stepTypesLst.add(StepTypes.LMGOOD);
							eventClassLst.add(manifest.getTransitionOf(manifest.getEncTransOfManifest(caseEncoded[pointer + 1])));
							countedManifest.add(caseEncoded[pointer + 1]);
						}
						pointer += 2;
					}
				}
				res = new SyncReplayResult(eventClassLst, stepTypesLst, i);

				// add stats
				res.setReliable(true);
				res.addInfo(PNRepResult.TRACEFITNESS, manifest.getTraceFitness(i));
				res.addInfo(PNRepResult.RAWFITNESSCOST, manifest.getRawCostFitness(i));
				res.addInfo(PNRepResult.MOVELOGFITNESS, manifest.getMoveLogFitness(i));
				res.addInfo(PNRepResult.MOVEMODELFITNESS, manifest.getMoveModelFitness(i));
				res.addInfo(PNRepResult.NUMSTATEGENERATED, manifest.getNumStates(i));
				res.addInfo(PNRepResult.TIME, manifest.getComputationTime(i));
				res.setReliable(manifest.isCaseReliable(i));
				
				mappedManifests.put(casePointer[i], res);
			}
		}
		return new PNRepResultImpl(mappedManifests.values());
	}
}
