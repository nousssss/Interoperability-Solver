package org.processmining.plugins.multietc.plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.multietc.automaton.Automaton;
import org.processmining.plugins.multietc.automaton.AutomatonFactory;
import org.processmining.plugins.multietc.automaton.AutomatonNode;
import org.processmining.plugins.multietc.reflected.ReflectedLog;
import org.processmining.plugins.multietc.reflected.ReflectedTrace;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.multietc.sett.MultiETCSettings;
import org.processmining.plugins.multietc.sett.MultiETCSettingsUI;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.IPNMatchInstancesLogReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.AllOptAlignmentsGraphAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.AllOptAlignmentsGraphSamplingAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.ui.PNMatchInstancesReplayerUI;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * 
 * Plugins to check conformance (precision) based on Align Precision techniques.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(
		name = "ETCConformance", 
		parameterLabels = {"Log", "Petri net"}, 
		returnLabels = { "Conformance Summary", "Precision Automaton", "Backwards Precision Automaton" }, 
		returnTypes = { MultiETCResult.class, Automaton.class, Automaton.class })
public class MultiETCPlugin {
	
	/**
	 * Check Conformance (precision) based on ETC Precision and Align Precision techniques.
	 * @param context Context of ProM.
	 * @param log Log.
	 * @param net Petri net.
	 * @return Return the summary with conformance information, and the automatons (forward and backwards) resulting of checking precision.
	 * @throws ConnectionCannotBeObtained 
	 */
	@UITopiaVariant(
			uiLabel = "Check Precision based on Align-ETConformance", 
			affiliation = "Universitat Politecnica de Catalunya", 
			author = " J.Munoz-Gama", 
			email = "jmunoz"+ (char) 0x40 + "lsi.upc.edu", 
			website = "http://www.lsi.upc.edu/~jmunoz", 
			pack = "ETConformance")
	@PluginVariant(
			variantLabel = "Version to display the options and the algortihms", 
			requiredParameterLabels = {0,1})
	public Object[] checkMultiETC(UIPluginContext context, XLog log, Petrinet net) throws ConnectionCannotBeObtained{
		//Get the Settings for the MultiETC Precision Checking
		MultiETCSettingsUI ui = new MultiETCSettingsUI();
		MultiETCSettings sett = ui.getSettings(context);
		
		if(sett == null){
			context.log("Not Settings speficied or canceled");
			return null;
		}
		else{
			String logName = log.getAttributes().get("concept:name").toString();
			String modelName = net.getLabel();
			context.getFutureResult(0).setLabel("Conformance of " + modelName + "/" + logName);
			context.getFutureResult(1).setLabel("Precicion Automaton of " + modelName + "/" + logName);
			context.getFutureResult(2).setLabel("Backwards Precicion Automaton of " + modelName + "/" + logName);
			
			return checkMultiETC(context,log,net,sett);
		}
	}
	
	
	
	
	public Object[] checkMultiETC(UIPluginContext context, XLog log, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained {
		// Choose according to the algorithm
		if(sett.getAlgorithm() == MultiETCSettings.Algorithm.ALIGN_1) return checkMultiETCAlign1(context,log,net,sett);
		else if(sett.getAlgorithm() == MultiETCSettings.Algorithm.ALIGN_REPRE) return checkMultiETCAlignSample(context,log,net,sett);
		else if(sett.getAlgorithm() == MultiETCSettings.Algorithm.ALIGN_ALL) return checkMultiETCAlignAll(context,log,net,sett);
		else if(sett.getAlgorithm() == MultiETCSettings.Algorithm.ETC) return checkMultiETCEtc(context,log,net,sett);
		return null;
	}




	public Object[] checkMultiETCAlign1(UIPluginContext context, XLog log, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained {
		// Compute the 1-Alignments
		//Get Params
		PNReplayerUI pnReplayerUI = new PNReplayerUI();
		Object[] resultConfiguration = pnReplayerUI.getConfiguration(context, net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
		
		//Compute the alignments
		IPNReplayAlgorithm selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
		PNRepResult alignments = null;
		try {
			alignments = selectedAlg.replayLog(context, net, log, (TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING], 
					(IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS]);
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return checkMultiETCAlign1(context,log, net, sett, alignments);
	}
	
	public Object[] checkMultiETCAlignSample(UIPluginContext context, XLog log, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained{
		//Compute the Sample Alignments
		//Get Params
		PNMatchInstancesReplayerUI pnReplayerUI = new PNMatchInstancesReplayerUI(context, new AllOptAlignmentsGraphAlg());
		Object[] resultConfiguration = pnReplayerUI.getConfiguration(net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
		
		// check connection between petri net and marking
		Marking initMarking = null;
		try {
			initMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (Exception exc) {
			initMarking = new Marking();
		}

		Marking finalMarking = null;
		try {
			finalMarking = context.getConnectionManager()
					.getFirstConnection(FinalMarkingConnection.class, context, net)
					.getObjectWithRole(FinalMarkingConnection.MARKING);
		} catch (Exception exc) {
			finalMarking = new Marking();
		}
		
		//Compute the alignments
		IPNMatchInstancesLogReplayAlgorithm selectedAlg = new AllOptAlignmentsGraphSamplingAlg();
		PNMatchInstancesRepResult alignments = null;
		try {
			alignments = selectedAlg.replayLog(context, net, initMarking, finalMarking, log,
					(TransEvClassMapping) resultConfiguration[PNMatchInstancesReplayerUI.MAPPING], (Object[]) resultConfiguration[PNMatchInstancesReplayerUI.PARAMETERS]);
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return checkMultiETCAlignAll(context, log, net, sett, alignments);
	}

	public Object[] checkMultiETCAlignAll(UIPluginContext context, XLog log, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained{
		//Compute the All Alignments
		//Get Params
		PNMatchInstancesReplayerUI pnReplayerUI = new PNMatchInstancesReplayerUI(context, new AllOptAlignmentsGraphAlg());
		Object[] resultConfiguration = pnReplayerUI.getConfiguration(net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
		
		// check connection between petri net and marking
		Marking initMarking = null;
		try {
			initMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (Exception exc) {
			initMarking = new Marking();
		}

		Marking finalMarking = null;
		try {
			finalMarking = context.getConnectionManager()
					.getFirstConnection(FinalMarkingConnection.class, context, net)
					.getObjectWithRole(FinalMarkingConnection.MARKING);
		} catch (Exception exc) {
			finalMarking = new Marking();
		}
		
		//Compute the alignments
		IPNMatchInstancesLogReplayAlgorithm selectedAlg = new AllOptAlignmentsGraphAlg();
		PNMatchInstancesRepResult alignments = null;
		try {
			alignments = selectedAlg.replayLog(context, net, initMarking, finalMarking, log,
					(TransEvClassMapping) resultConfiguration[PNMatchInstancesReplayerUI.MAPPING], (Object[]) resultConfiguration[PNMatchInstancesReplayerUI.PARAMETERS]);
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return checkMultiETCAlignAll(context, log, net, sett, alignments);
	}

	
	
	public Object[] checkMultiETCAlign1(PluginContext context, XLog log, Petrinet net, MultiETCSettings sett,
			PNRepResult repResult) throws ConnectionCannotBeObtained {
		//Create a Reflected Log from a 1-Alignment 
		ReflectedLog refLog = new ReflectedLog();
		
		for (SyncReplayResult rep : repResult) {
			ReflectedTrace t = new ReflectedTrace();
			
			//Check the Alignments that are not Movements on the Log only
			Iterator<Object> itTask = rep.getNodeInstance().iterator();
			Iterator<StepTypes> itType = rep.getStepTypes().iterator();
			while(itTask.hasNext()){
				
				StepTypes type = itType.next();
				
				//If it is a log move, just skip
				if(type == StepTypes.L){
					itTask.next();//Skip the task
				}
				
				else{ //It is a PetriNet Transition
					Transition trans = ((Transition) itTask.next());
					t.add(trans);
				}
			}
			
			//Avoid adding empty traces
			if(!t.isEmpty()){
				//Compute Weight: num of cases represented by the alignment
				int cases = rep.getTraceIndex().size();
				t.putWeight(cases);
				//Add trace
				refLog.add(t);
			}
		}
		
		return checkMultiETC(context,refLog,net,sett);
	}
	
	@SuppressWarnings("unchecked")
	public Object[] checkMultiETCAlignAll(PluginContext context, XLog log, Petrinet net, MultiETCSettings sett,
			PNMatchInstancesRepResult allAlignments) throws ConnectionCannotBeObtained {
		ReflectedLog refLog = new ReflectedLog();
		
		for(AllSyncReplayResult caseAlignments : allAlignments){
			
			//Check if Sample or Not
			List<Integer> sampleReps = null;
			if(caseAlignments.getInfoObject() != null){
				if (caseAlignments.getInfoObject().get(InfoObjectConst. NUMREPRESENTEDALIGNMENT) != null){
					sampleReps = (List<Integer>) caseAlignments.getInfoObject().get(InfoObjectConst. NUMREPRESENTEDALIGNMENT);
				}
			}
			
			//Compute the number of alignments (if sampling, the ones represented by the samples)
			int nAlign = 0;
			for(int i = 0; i<caseAlignments.getNodeInstanceLst().size(); i++){
				//Sample
				if(sampleReps != null){
					nAlign +=  sampleReps.get(i);
				}
				//Not Sample
				else{
					nAlign ++;
				}
			}
			
			//Compute the increment of weight assigned to each alignment
			int nCases = caseAlignments.getTraceIndex().size();
			double weightPerAlign = (double) nCases / (double) nAlign;
			
			//For each alignment in this set
			for(int i = 0; i<caseAlignments.getNodeInstanceLst().size(); i++){
				ReflectedTrace t = new ReflectedTrace();
				
				//Check the Alignments that are not Movements on the Log only
				Iterator<Object> itTask = caseAlignments.getNodeInstanceLst().get(i).iterator();
				Iterator<StepTypes> itType = caseAlignments.getStepTypesLst().get(i).iterator();
				while(itTask.hasNext()){
					StepTypes type = itType.next();
					
					//If it is a log move, just skip
					if(type == StepTypes.L){
						itTask.next();//Skip the task
					}
					
					else{ //It is a PetriNet Transition
						Transition trans = ((Transition) itTask.next());
						t.add(trans);
					}
				}
				
				//Avoid adding empty traces
				if(!t.isEmpty()){
					//SetWeight: num of Cases / num Alignments found for those cases
					int represented  = (sampleReps != null) ? sampleReps.get(i) : 1;
					t.putWeight(represented * weightPerAlign);
					//Add trace
					refLog.add(t);
				}	
			}
		}
		return checkMultiETC(context,refLog,net,sett);
	}
	
	
	
	
	
	
	
	public Object[] checkMultiETCEtc(UIPluginContext context, XLog log, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained{
		//Compute the Mapping
		TransEvClassMapping mapping = null;
		try {
			EvClassLogPetrinetConnection conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,
					log);
			mapping = (TransEvClassMapping) conn
					.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JPanel(), "No mapping can be constructed between the net and the log");
			return null;
		}
		
		//Check the Transition has not invisible/duplicated transitions (not allowed for ETC)
		Set<Transition> unmappedTrans = new HashSet<Transition>();
		Set<XEventClass> events = new HashSet<XEventClass>();
		Set<XEventClass> duplicatedEvents = new HashSet<XEventClass>();
		for (Entry<Transition, XEventClass> entry : mapping.entrySet()) {
			if(events.contains(entry.getValue())){
				duplicatedEvents.add(entry.getValue());
			}
			else{
				events.add(entry.getValue());
			}
			
			if (entry.getValue().equals(mapping.getDummyEventClass())) {
				unmappedTrans.add(entry.getKey());
			}
		}
		if(!unmappedTrans.isEmpty() || !duplicatedEvents.isEmpty()){
			JPanel panel = new JPanel();
			BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
			panel.setLayout(layout);
			if(!unmappedTrans.isEmpty()){
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JList listUnmapped = new JList(unmappedTrans.toArray());
				panel.add(new JLabel("The following transitions are not mapped to any event class:"));
				JScrollPane unmappedsp = new JScrollPane(listUnmapped);
				panel.add(unmappedsp);
			}
			if (!duplicatedEvents.isEmpty()){
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JList listDuplicates = new JList(duplicatedEvents.toArray());
				panel.add(new JLabel("The following events has multiple transitions associated:"));
				JScrollPane duplicatedsp = new JScrollPane(listDuplicates);
				panel.add(duplicatedsp);
				
			}
			panel.add(new JLabel("We recommend to use Align Precision, change the mapping or use ETConformance specific plugin"));
			JOptionPane.showMessageDialog(null, panel, "No Invisible/Duplictes for ETC-Precision", JOptionPane.INFORMATION_MESSAGE);
			return checkMultiETC(context,log,net);
		}

		return checkMultiETCEtc(context, log, net, sett, mapping);
		
		
	}

	
	public Object[] checkMultiETCEtc(UIPluginContext context, XLog log, Petrinet net, MultiETCSettings sett,
			TransEvClassMapping mapping) throws ConnectionCannotBeObtained {
		//Create a even-transition mapping ASSUMING THE FIRST TRANSITION ASSOCIATED TO THE EVENT
		Map<XEventClass, Transition> event2trans = new HashMap<XEventClass, Transition>();
		
		for (Entry<Transition, XEventClass> entry : mapping.entrySet()) {
			if(!event2trans.containsKey(entry.getValue())){
				event2trans.put(entry.getValue(), entry.getKey());
			}
		}
		
		return checkMultiETCEtc(context,log,net,sett,event2trans, mapping.getEventClassifier());
	}


	public Object[] checkMultiETCEtc(PluginContext context, XLog log, Petrinet net, MultiETCSettings sett,
			Map<XEventClass, Transition> mapping, XEventClassifier classifier) throws ConnectionCannotBeObtained {
		// Create the Reflected log from the Event Log (ASSUMING NO INVISIBLE OR DUPLICATES)
		ReflectedLog refLog = new ReflectedLog();
		
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
		
		for(XTrace trace: log){
			//Weight of the trace
			ReflectedTrace refTrace = new ReflectedTrace();
			refTrace.putWeight(1);
			refLog.add(refTrace);
			
			for(XEvent event: trace){
				XEventClass label = logInfo.getEventClasses().getClassOf(event);
				Transition trans = mapping.get(label);
				refTrace.add(trans);
			}
		}
		
		return checkMultiETC(context,refLog,net,sett);
	}



	public Object[] checkMultiETC(PluginContext context, ReflectedLog refLog, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained {
		Object[] forwards = checkMultiETCForwards(context, refLog, net, sett);
		MultiETCResult resFor = (MultiETCResult) forwards[0];
		Automaton autoFor = (Automaton) forwards[1];
		
		Object[] backwards = checkMultiETCBackwards(context, refLog, net, sett);
		MultiETCResult resBack = (MultiETCResult) backwards[0];
		Automaton autoBack = (Automaton) backwards[1];
		
		//Merge the results of the backwards conformance checking with the forwards ones
		mergeForwardsBackwardsResults(resFor, resBack);
		
		return new Object[] {resFor, autoFor, autoBack};
	}


	private void mergeForwardsBackwardsResults(MultiETCResult resFor, MultiETCResult resBack) {
		
		resFor.putAttribute(MultiETCResult.AUTO_STATES_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES));
		resFor.putAttribute(MultiETCResult.AUTO_STATES_IN_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES_IN));
		resFor.putAttribute(MultiETCResult.AUTO_STATES_OUT_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES_OUT));
		
		resFor.putAttribute(MultiETCResult.BACK_PRECISION, resBack.getAttribute(MultiETCResult.PRECISION));
		
		double balanced = ( (Double) resFor.getAttribute(MultiETCResult.PRECISION) + (Double) resBack.getAttribute(MultiETCResult.PRECISION)) / 2;
		resFor.putAttribute(MultiETCResult.BALANCED_PRECISION, balanced);		
	}




	public Object[] checkMultiETCForwards(PluginContext context, ReflectedLog refLog, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained {
		
		Marking iniM = null;
		Marking endM = null;
		
		//Force Forward automaton
		sett.setWindow(MultiETCSettings.Window.BACKWARDS);
		
		//If Past you need the initial Marking of the net as initial state
		if(sett.getWindow() == MultiETCSettings.Window.BACKWARDS){
			InitialMarkingConnection initCon = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			iniM = (Marking) initCon.getObjectWithRole(InitialMarkingConnection.MARKING);
		}
		
		return checkMultiETC(context,refLog,net,iniM, endM,sett);
		
	}
	
	public Object[] checkMultiETCBackwards(PluginContext context, ReflectedLog refLog, Petrinet net, MultiETCSettings sett) throws ConnectionCannotBeObtained {
		
		Marking iniM = null;
		Marking endM = null;
		
		//Force Backward automaton
		sett.setWindow(MultiETCSettings.Window.FORWARDS);

		//If Future you need the final Marking of the net as final marking
		if(sett.getWindow() == MultiETCSettings.Window.FORWARDS){
			FinalMarkingConnection finalCon = context.getConnectionManager().getFirstConnection(
					FinalMarkingConnection.class, context, net);
			endM = (Marking) finalCon.getObjectWithRole(FinalMarkingConnection.MARKING);
		}
		
		return checkMultiETC(context,refLog,net,iniM, endM,sett);
		
	}
		

	public Object[] checkMultiETC (PluginContext context, ReflectedLog log, Petrinet net, Marking iniM, Marking endM, MultiETCSettings etcSett){
		
		AutomatonFactory factory = new AutomatonFactory(etcSett);
		Automaton a = factory.createAutomaton();
		MultiETCResult res = new MultiETCResult();
		a.checkConformance(log, net, iniM, endM, res, etcSett);
		
		setSettingsInfoInResult(etcSett,res);
		setAutomatonInfoInResult(a,res);
		
		return new Object[] {res, a};
	}



	

	private void setAutomatonInfoInResult(Automaton a, MultiETCResult res) {
		int states = 0;
		int in = 0;
		int out = 0;
		for(AutomatonNode n: a.getJUNG().getVertices()){
			states++;
			if (n.getMarking() == null) out++;
			else in++;
		}
		res.putAttribute(MultiETCResult.AUTO_STATES, states);
		res.putAttribute(MultiETCResult.AUTO_STATES_IN, in);
		res.putAttribute(MultiETCResult.AUTO_STATES_OUT, out);
		
	}


	private void setSettingsInfoInResult(MultiETCSettings etcSett, MultiETCResult res) {
		res.putAttribute(MultiETCResult.ETC_SETT, etcSett);	
	}
	


}
