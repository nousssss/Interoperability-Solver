/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * Given a log and a process model, this class stores all best matching sequence of
 * all traces in the log.
 *  
 * @author aadrians
 *
 */
public class PNMatchInstancesRepResult extends TreeSet<AllSyncReplayResult>{
	private static final long serialVersionUID = 2112188771367205716L;
	
	public static final String RAWFITNESSCOST = "Raw Fitness Cost";
	public static final String QUEUEDSTATE = "Queued States";
	public static final String NUMALIGNMENTS = "#Alignments"; // the number of _all_ optimal alignments
	public static final String REPRESENTATIVES = "#Representatives"; // the number of _all_ optimal alignments
	public static final String TIME = "Calculation Time (ms)";
	public static final String ORIGTRACELENGTH = "Trace Length";
	public static final String NUMSTATES = "Num. States";
	public static final String MINFITNESSCOST = "Min Fitness Cost";
	public static final String MAXFITNESSCOST = "Max Fitness Cost";	
	public static final String TRACEFITNESS = "Trace Fitness";

	
	private Map<String, String> info = new HashMap<String, String>(2);

	public PNMatchInstancesRepResult(Collection<AllSyncReplayResult> col) {
		super(new Comparator<AllSyncReplayResult>() {

			public int compare(AllSyncReplayResult o1, AllSyncReplayResult o2) {
				if (o1.isReliable() && !o2.isReliable()) {
					return -1;
				}
				if (!o1.isReliable() && o2.isReliable()) {
					return 1;
				}
				SortedSet<Integer> s1 = o1.getTraceIndex();
				SortedSet<Integer> s2 = o2.getTraceIndex();
				if (s1.size() != s2.size()) {
					return s2.size() - s1.size();
				}
				if (o1.equals(o2)) {
					return 0;
				}
				List<List<StepTypes>> l1 = o1.getStepTypesLst();
				List<List<StepTypes>> l2 = o2.getStepTypesLst();
				
				if (l1.size() != l2.size()) {
					return l2.size() - l1.size();
				}
				Iterator<Integer> it1 = s1.iterator();
				Iterator<Integer> it2 = s2.iterator();
				while (it1.hasNext()) {
					Integer ss1 = it1.next();
					Integer ss2 = it2.next();
					if (!ss1.equals(ss2)) {
						return ss1.compareTo(ss2);
					}
				}
				return 0;
			}

		});
		addAll(col);
	}
	
	/**
	 * @return the info
	 */
	public Map<String, String> getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	
	/**
	 * Add info (could be fitness value, etc.)
	 * @param property
	 * @param value
	 */
	public void addInfo(String property, String value){
		this.info.put(property, value);
	}
}
