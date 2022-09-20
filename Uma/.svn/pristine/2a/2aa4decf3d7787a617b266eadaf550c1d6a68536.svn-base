/*****************************************************************************\
 * Copyright (c) 2008, 2009, 2010. Dirk Fahland. AGPL3.0
 * All rights reserved. 
 * 
 * ServiceTechnology.org - Uma, an Unfolding-based Model Analyzer
 * 
 * This program and the accompanying materials are made available under
 * the terms of the GNU Affero General Public License Version 3 or later,
 * which accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/agpl.txt
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
\*****************************************************************************/

package org.processmining.plugins.uma;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.annotations.TestMethod;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

import hub.top.uma.InvalidModelException;
import hub.top.uma.view.MineSimplify;
import hub.top.uma.view.MineSimplify.Configuration;

/**
 * Class to invoke Uma (an Unfolding-based Model Analyzer) to compute
 * a complete finite prefix of an unfolding of a given Petri net
 * 
 * @author Dirk Fahland
 * @email dirk.fahland@service-technology.org
 * @version Mar 23, 2010
 *
 */
@Plugin(name = "Simplify Mined Model Using Uma",
		parameterLabels = { "a log", "a Petri net", "an initial marking", "parameters", "event classifier" }, //
		returnLabels = { "Petrinet", "Marking" },
		returnTypes = { Petrinet.class, Marking.class }, 
		help = "Structurally simplify a process model such that it still fits the given event log but is less complex. Various paramters allow to balance precision and generalization during the simplification procedure. A frequency filter allows to simplfy the model further by removing rarely used parts. Assumes that the model can perfectly replay the log; if this is not the case use the 'Align Log to Model' plugin to get a log that can be replayed on the model.", 
		userAccessible = true,
		mostSignificantResult = 1)
public class Uma_SimplifyModel {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(affiliation="TU/e", author="D. Fahland", email="d.fahland@tue.nl", website = "http://service-technology.org/uma", pack="Uma")
	@PluginVariant(variantLabel = "simplify mined net", requiredParameterLabels = { 0, 1 })
	public Object[] simplifyNet(UIPluginContext context, XLog log, Petrinet net) throws ConnectionCannotBeObtained, Exception {
		Marking initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		return simplifyNet(context, log, net, initMarking);
	}
	
	// take log, net, and initial marking as input
	@UITopiaVariant(affiliation="TU/e", author="D. Fahland", email="d.fahland@tue.nl", website = "http://service-technology.org/uma", pack="Uma")	
	@PluginVariant(variantLabel = "simplify mined net", requiredParameterLabels = { 0, 1, 2 })
	public Object[] simplifyNet(UIPluginContext context, XLog log, Petrinet net, Marking initMarking) throws ConnectionCannotBeObtained, Exception {

		EvClassLogPetrinetConnection conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
		TransEvClassMapping map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		// open UI dialogue, set configuration object, and run UI-less plugin
		Configuration config =  new Configuration();
		for (Transition t : map.keySet()) {
			XEventClass ev_class = map.get(t);
			config.eventToTransition.put(ev_class.getId(), t.getLabel()); // FIXME: don't store mapping based on labels (nets with duplicate labels!)
		}
		
		XEventClassifier classifier = map.getEventClassifier();
		
		Uma_SimplifyModel_UI ui = new Uma_SimplifyModel_UI(config);
		if (ui.setParameters(context, config) != InteractionResult.CANCEL)
			return simplifyNet(context, log, net, initMarking, config, classifier);		
		else
			return ui.userCancel(context);
	}

	/**
	 * Wrapper for calling {@link #simplifyNet(PluginContext, XLog, Petrinet, Marking)} to
	 * construct a simplified net that can replay the entire log by unfolding the net,
	 * simplifying the unfolding, and folding the result back to a model.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param config
	 * 
	 * @return a simplified Petri net that can replay the entire log
	 * 
	 * @throws Exception
	 */
	@PluginVariant(variantLabel = "simplify mined net", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	public Object[] simplifyNet(PluginContext context, XLog log, Petrinet net, Marking initMarking, Configuration config, XEventClassifier classifier) throws ConnectionCannotBeObtained, Exception {
		// check connection 
		context.getConnectionManager().getFirstConnection(InitialMarkingConnection.class, context, net, initMarking);
		// load and initialize input Petri net
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);

		semantics.initialize(net.getTransitions(), initMarking);
		
		
		Map<PetrinetNode, hub.top.petrinet.Transition> transitionMap = new HashMap<PetrinetNode, hub.top.petrinet.Transition>();
		hub.top.petrinet.PetriNet originalNet = UmaPromUtil.toPNAPIFormat(net, initMarking, transitionMap);
		// hack to redirect the mapping from event ids to transition labels of the Petrinet
		// to the unique identifiers of the hub.top.petrinet.PetriNet  
		for (String key : config.eventToTransition.keySet()) {
			PetrinetNode n_for_key = null;
			for (PetrinetNode n : transitionMap.keySet()) {
				if (n.getLabel().equals(key)) {
					n_for_key = n;
					break;
				}
			}
			if (n_for_key == null || transitionMap.get(n_for_key) == null) {
				return cancel(context, "Error. Could not map "+key+" to a transition in the model.");
			}
			config.eventToTransition.put(key, transitionMap.get(n_for_key).getUniqueIdentifier());
		}

		
		LinkedList<String[]> eventLog = UmaPromUtil.toSimpleEventLog(log, classifier);

		
		MineSimplify simplify = new MineSimplify(originalNet, eventLog, config);
		simplify.prepareModel();
		
		try {
	  		simplify.run();
	  		
	  		String name = net.getLabel()+" (simplified "+toString(config)+")";
	  		
	  		hub.top.petrinet.PetriNet _simplifiedNet = simplify.getSimplifiedNet();
	  		Object[] simplifiedNet = UmaPromUtil.toPromFormat(_simplifiedNet, name);
	        
	  		// set label before result output
	  		context.getFutureResult(0).setLabel(name);
	  		context.getFutureResult(1).setLabel("Initial Marking of "+name);
	  
	  		// connect the result
	  		context.addConnection(new NetTransformationConnection(net, (Petrinet)simplifiedNet[0]));
	  		context.addConnection(new InitialMarkingConnection((Petrinet)simplifiedNet[0], (Marking)simplifiedNet[1]));
	  		
			return simplifiedNet;
			
		} catch (InvalidModelException e) {
			// the model is invalid for the unfolding algorithm
			context.log(e.getMessage());
			context.getFutureResult(0).cancel(true);
			return null ;
		}
	}
	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Uma]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return new Object[] { null, null };
	}
	
	private static String toString(Configuration conf) {
		String result = "";
		if (conf.unfold_refold) result+="u";
		if (conf.filter_threshold > 0) result+="f"+conf.filter_threshold;
		if (conf.remove_implied != Configuration.REMOVE_IMPLIED_OFF) result+="i"+conf.remove_implied;
		if (conf.abstract_chains) result+="c";
		if (conf.remove_flower_places) result+="f";
		return result;
	}
	
	@TestMethod(output="true 0.05 3 true true")
	public static String test_UMA_SimplifyModel_standardConfiguration() {
		Configuration config = new Configuration();
		return config.toString();
	}
	
	@TestMethod(filename="testresult_UMA_SimplifyModel_standardConfiguration.txt")
	public static String test_UMA_SimplifyModel_standardConfiguration_file() {
		Configuration config = new Configuration();
		return config.toString();
	}
}
