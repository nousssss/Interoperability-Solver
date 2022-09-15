/**
 * 
 */
package org.processmining.plugins.alignetc;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import nl.tue.astar.AStarException;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.alignetc.connection.AlignETCResultConnection;
import org.processmining.plugins.alignetc.core.ReplayAutomaton;
import org.processmining.plugins.alignetc.result.AlignETCResult;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * Conformance Checking (precision) between a Log and a Petri Net based on the
 * best fitting alignment of the log in the Petri net.
 * 
 * The details of the approach can be seen in the papers:
 * 
 * 1)"A fresh look at Precision in Process Conformance" by Jorge Munoz-Gama and
 * Josep Carmona, in Proceedings of
 * "Business Processes Management 2010 (BPM 2010)".
 * 
 * 2)
 * "Enhancing Precision in Process Conformance: Stability, Confidence and Severity"
 * by by Jorge Munoz-Gama and Josep Carmona, in Proceedings of "IEEE Symposium
 * Series in Computational Intelligence 2011 (SSCI)".
 * 
 * ETConformance was developed by Jorge Munoz-Gama and Josep Carmona from
 * "BarcelonaTech - Universitat Politecnica de Catalunya (UPC)". The log
 * alignments depends on the Replayer plugin developed by Arya Adriansyah.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(name = "Align ETConformance", parameterLabels = { "Log", "PetriNet" }, returnLabels = { "AlignETCResult" }, returnTypes = { AlignETCResult.class })
public class AlignETCPlugin {

	/**
	 * Align ETConformance variant with only Log and PetriNet.
	 * 
	 * @throws ConnectionCannotBeObtained
	 * @throws IllegalTransitionException
	 */
//	@UITopiaVariant(uiLabel = "Check Precision with Best-Align ETConformance", affiliation = "Universitat Politecnica de Catalunya", author = " J.Munoz-Gama & J.Carmona", email = "jmunoz"
//			+ (char) 0x40 + "lsi.upc.edu", website = "http://www.lsi.upc.edu/~jmunoz", pack = "ETConformance")
	@PluginVariant(variantLabel = "Log and PetriNet", requiredParameterLabels = { 0, 1 })
	public AlignETCResult checkAlignETC(UIPluginContext context, XLog log, Petrinet net)
			throws ConnectionCannotBeObtained, IllegalTransitionException {
		return checkAlignETC(context,log,net, true);
	}
	
	public AlignETCResult checkAlignETC(UIPluginContext context, XLog log, Petrinet net, boolean askParams)
			throws ConnectionCannotBeObtained, IllegalTransitionException {
		//Get the alignments
		PNMatchInstancesRepResult alignments = context.tryToFindOrConstructFirstObject(PNMatchInstancesRepResult.class,
				PNMatchInstancesRepResultConnection.class, PNMatchInstancesRepResultConnection.PNREPRESULT, net, log);
		return checkGenericAlignETC(context, log, net, alignments, askParams);
	}

	/**
	 * Align ETConformance variant with only Log and PetriNet.
	 * 
	 * @throws ConnectionCannotBeObtained
	 * @throws IllegalTransitionException
	 */
//	@UITopiaVariant(uiLabel = "Check Precision with 1-Align ETConformance", affiliation = "Universitat Politecnica de Catalunya", author = " J.Munoz-Gama & J.Carmona", email = "jmunoz"
//			+ (char) 0x40 + "lsi.upc.edu", website = "http://www.lsi.upc.edu/~jmunoz", pack = "ETConformance")
	@PluginVariant(variantLabel = "Log and PetriNet", requiredParameterLabels = { 0, 1 })
	public AlignETCResult check1AlignETC(UIPluginContext context, XLog log, Petrinet net)
			throws ConnectionCannotBeObtained, IllegalTransitionException {
		return check1AlignETC(context, log, net, true);
	}
	
	public AlignETCResult check1AlignETC(UIPluginContext context, XLog log, Petrinet net, boolean askParams)
			throws ConnectionCannotBeObtained, IllegalTransitionException {

		context.log("Start convertion between 1-align to n-align formats");

		//Get the 1-alignment
		PNRepResult repResult = context.tryToFindOrConstructFirstObject(PNRepResult.class,
				PNRepResultAllRequiredParamConnection.class, PNRepResultAllRequiredParamConnection.PNREPRESULT, net,
				log);
		
		return check1AlignETC(context,log, net,repResult, askParams);
		
	}
	
	public AlignETCResult check1AlignETC(UIPluginContext context, XLog log, Petrinet net, PNRepResult repResult, boolean askParams)
			throws ConnectionCannotBeObtained, IllegalTransitionException {

		//Convert to n-alignments object
		Collection<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();
		for (SyncReplayResult rep : repResult) {

			//Get all the attributes of the 1-alignment result
			List<List<Object>> nodes = new ArrayList<List<Object>>();
			nodes.add(rep.getNodeInstance());

			List<List<StepTypes>> types = new ArrayList<List<StepTypes>>();
			types.add(rep.getStepTypes());

			SortedSet<Integer> traces = rep.getTraceIndex();
			boolean rel = rep.isReliable();

			//Create a n-alignment result with this attributes
			AllSyncReplayResult allRep = new AllSyncReplayResult(nodes, types, -1, rel);
			allRep.setTraceIndex(traces);//The creator not allow add the set directly
			col.add(allRep);
		}
		PNMatchInstancesRepResult alignments = new PNMatchInstancesRepResult(col);

		context.log("End convertion between 1-align to n-align formats");

		return checkGenericAlignETC(context, log, net, alignments, askParams);
	}

	public AlignETCResult checkGenericAlignETC(UIPluginContext context, XLog log, Petrinet net,
			PNMatchInstancesRepResult alignments, boolean askParams) throws ConnectionCannotBeObtained, IllegalTransitionException {

		//Get the initial marking
		Marking iniMark = context.getConnectionManager()
				.getFirstConnection(InitialMarkingConnection.class, context, net)
				.getObjectWithRole(InitialMarkingConnection.MARKING);

		//Create a result object
		AlignETCResult res = new AlignETCResult();

		//Show and set the Settings
		if(askParams){
			AlignETCSettings sett = new AlignETCSettings(res);
			InteractionResult result = context.showWizard("Align ETConformanc Settings", true, true, sett.initComponents());

			switch (result) {
				case CANCEL :
					return cancel(context, "The user has cancelled AlignETConformance!");
				case FINISHED :
					sett.setSettings();//Get and store the settings
					break;
				default :
					return cancel(context, "Problem with the Settings");
			}
		}

		// AA: add timing information
		long startTime = System.nanoTime();
		
		// 1)Build Replay Automaton
		ReplayAutomaton ra = new ReplayAutomaton(context, alignments, net);

		// 2) Cut (Prune) the Automaton according to a threshold
		ra.cut(res.escTh);//TODO Get the threshold from user settings

		// 3) Extend the automaton with not reflected model behavior
		ra.extend(net, iniMark);

		// 4) Compute the Conformance metrics
		ra.conformance(res);

		long period = System.nanoTime() - startTime;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		System.out.print("Automaton Calculation time : ");
		System.out.print(nf.format(period / 1000000));
		System.out.println(" ms");
		System.out.print("AP value: ");
		System.out.println(res.ap);
		
		//TODO Add according to users settings
		res.alignments = alignments;

		//Connections
		// add connection
		AlignETCResultConnection con = context.addConnection(new AlignETCResultConnection(
				"Align ETConformance Result of " + XConceptExtension.instance().extractName(log) + " on "
						+ net.getLabel(), net, iniMark, log, alignments, res));
		con.setLabel("Connection between " + net.getLabel() + ", " + XConceptExtension.instance().extractName(log)
				+ ", and Align ETConformance Resul");

		return res;
	}

	// AA:add method without settings provided
	public AlignETCResult checkGenericAlignETC(PluginContext context, XLog log, Petrinet net, Marking iniMark,
			PNMatchInstancesRepResult alignments, AlignETCResult res, AlignETCSettings sett)
			throws ConnectionCannotBeObtained, IllegalTransitionException {

		// 1)Build Replay Automaton
		ReplayAutomaton ra = new ReplayAutomaton(context, alignments, net);

		// 2) Cut (Prune) the Automaton according to a threshold
		ra.cut(res.escTh);//TODO Get the threshold from user settings

		// 3) Extend the automaton with not reflected model behavior
		ra.extend(net, iniMark);

		// 4) Compute the Conformance metrics
		ra.conformance(res);

		//TODO Add according to users settings
		res.alignments = alignments;
		return res;
	}
	
	//Silent Conformance Checking (no UI context, and if some parameter is null it creates one by default)
	//To Andrea Buratti
	public AlignETCResult checkAlignETCSilent(PluginContext context, XLog log, Petrinet net, Marking iniMark, Marking finalMark,
			EvClassLogPetrinetConnection conLogPN, 
			PNMatchInstancesRepResult alignments, AlignETCResult res, AlignETCSettings sett) throws ConnectionCannotBeObtained, IllegalTransitionException{
		
		if(iniMark == null){
			iniMark = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
		}
		
		if(finalMark == null){
			finalMark = context.getConnectionManager()
					.getFirstConnection(FinalMarkingConnection.class, context, net)
					.getObjectWithRole(FinalMarkingConnection.MARKING);
		}
		
		if(alignments == null){
			// create parameter
			TransEvClassMapping oldMap = conLogPN.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
			XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
			CostBasedCompleteParam parameter = new CostBasedCompleteParam(logInfo.getEventClasses().getClasses(),
					oldMap.getDummyEventClass(), net.getTransitions(), 2, 5);
			parameter.getMapEvClass2Cost().remove(oldMap.getDummyEventClass());
			parameter.getMapEvClass2Cost().put(oldMap.getDummyEventClass(), 1);
			
			
			
			parameter.setGUIMode(false);
			parameter.setCreateConn(false);
			parameter.setInitialMarking(iniMark);
			parameter.setFinalMarkings(new Marking[] {finalMark});
			parameter.setMaxNumOfStates(200000);
			
			// instantiate replayer
			PNLogReplayer replayer = new PNLogReplayer();
			
			// select algorithm without ILP
			PetrinetReplayerWithoutILP replWithoutILP = new PetrinetReplayerWithoutILP();
			PNRepResult pnRepResult = null;
			try {
				pnRepResult = replayer.replayLog(null, net, log, oldMap, replWithoutILP, parameter);
			} catch (AStarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Convert to n-alignments object
			Collection<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();
			for (SyncReplayResult rep : pnRepResult) {

				//Get all the attributes of the 1-alignment result
				List<List<Object>> nodes = new ArrayList<List<Object>>();
				nodes.add(rep.getNodeInstance());

				List<List<StepTypes>> types = new ArrayList<List<StepTypes>>();
				types.add(rep.getStepTypes());

				SortedSet<Integer> traces = rep.getTraceIndex();
				boolean rel = rep.isReliable();

				//Create a n-alignment result with this attributes
				AllSyncReplayResult allRep = new AllSyncReplayResult(nodes, types, -1, rel);
				allRep.setTraceIndex(traces);//The creator not allow add the set directly
				col.add(allRep);
			}
			alignments = new PNMatchInstancesRepResult(col);
		
		}
		
		if(res == null){
			res = new AlignETCResult();
		}
		
		if(sett == null){
			sett = new AlignETCSettings(res);
		}
		
		
		return checkGenericAlignETC(context,log, net, iniMark, alignments,res,sett);
	}

	/**
	 * Cancel the future results, display a error message and return all nulls.
	 * 
	 * @param context
	 *            Context of the plug-in.
	 * @param msg
	 *            Error message.
	 * @return Return an object array of nulls;
	 */
	private AlignETCResult cancel(UIPluginContext context, String msg) {
		context.log(msg);
		context.getFutureResult(0).cancel(true);
		return null;
	}

}
