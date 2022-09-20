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

import hub.top.uma.DNodeBP;
import hub.top.uma.DNodeSys;
import hub.top.uma.InvalidModelException;
import hub.top.uma.Options;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.annotations.TestMethod;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.UnfoldingNetConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * Class to invoke Uma (an Unfolding-based Model Analyzer) to compute
 * a complete finite prefix of an unfolding of a given Petri net
 * 
 * @author Dirk Fahland
 * @email dirk.fahland@service-technology.org
 * @version Mar 23, 2010
 *
 */
@Plugin(name = "Analyze Model Using Uma",
		parameterLabels = { "a Petri net", "an initial marking", "parameters" }, //
		returnLabels = { "complete prefix of the net's unfolding" },
		returnTypes = Petrinet.class,
		help = "Compute a finite complete prefix of the Petri net (also known as McMillan prefix) which represents the complete true concurrency behavior of the model in finite form.", 
		userAccessible = true,
		mostSignificantResult = 1)
public class Uma_UnfoldPN {

	// take net as input and guess initial marking
	@UITopiaVariant(affiliation="Humboldt-Universit&#228;t zu Berlin", author="D. Fahland", email="dirk.fahland@service-technology.org", website = "http://service-technology.org/uma", pack="Uma")
	@PluginVariant(variantLabel = "unfold Petri net to its complete finite prefix", requiredParameterLabels = { 0 })
	public Petrinet unfoldPetrinet(UIPluginContext context, Petrinet net) throws ConnectionCannotBeObtained, Exception {
		// retrieve associated initial marking
		Marking initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		// and call default UI plugin (without parameters)
		return unfoldPetrinet(context, net, initMarking);
	}
	
	// take net and initial marking as input
	@UITopiaVariant(affiliation="Humboldt-Universit&#228;t zu Berlin", author="D. Fahland", email="dirk.fahland@service-technology.org", website = "http://service-technology.org/uma", pack="Uma")
	@PluginVariant(variantLabel = "unfold Petri net to its complete finite prefix", requiredParameterLabels = { 0, 1 })
	public Petrinet unfoldPetrinet(UIPluginContext context, Petrinet net, Marking initMarking) throws ConnectionCannotBeObtained, Exception {
		// open UI dialogue, set configuration object, and run UI-less plugin
		UnfoldingConfiguration config =  new UnfoldingConfiguration();
		Uma_UnfoldPN_UI ui = new Uma_UnfoldPN_UI(config);
		if (ui.setParameters(context, config) != InteractionResult.CANCEL)
			return unfoldPetrinet(context, net, initMarking, config);		
		else
			return ui.userCancel(context);
	}
	
	// take net and initial marking as input
	@PluginVariant(variantLabel = "unfold Petri net to its complete finite prefix (k = 1)", requiredParameterLabels = { 0, 1, 2 })
	public Petrinet unfoldPetrinet(PluginContext context, Petrinet net, Marking initMarking, UnfoldingConfiguration config) throws ConnectionCannotBeObtained, Exception {
		
		assert config != null;
		
		// ui-less plugin variant, all run information set in configuration object
		context.getConnectionManager().getFirstConnection(InitialMarkingConnection.class, context, net, initMarking);

		if (config.mode == UnfoldingConfiguration.BUILD_PREFIX) {
			return buildPrefix(context, net, initMarking, config);
		} else if (config.mode == UnfoldingConfiguration.CHECK_SOUNDNESS_FC) {
			return checkSoundnessFC(context, net, initMarking);
		}
		
		context.log("Uma did not do any work.");
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
	/**
	 * Parameter class to configure the unfolding algorithm either
	 * by command-line or by user interface.
	 * 
	 * @author dfahland
	 */
	public static class UnfoldingConfiguration {
		public static final int BUILD_PREFIX = 1;
		public static final int CHECK_SOUNDNESS_FC = 2;
		
		public int bound = 1;
		public int mode = BUILD_PREFIX;
		
		/**
		 * @return default parameters for building a complete finite prefix
		 * of a 1-bounded system
		 */
		public static UnfoldingConfiguration getDefault_unfolding() {
			return new UnfoldingConfiguration();
		}

		/**
		 * @return default parameters for checking soundness of a free-choice
		 * Petri net
		 */
		public static UnfoldingConfiguration getDefault_soundnessFC() {
			UnfoldingConfiguration param = new UnfoldingConfiguration();
			param.mode = CHECK_SOUNDNESS_FC;
			return param;
		}
	}

	/**
	 * build and return complete finite prefix of the given net
	 * 
	 * @param context
	 * @param net
	 * @param initMarking
	 * @return
	 * @throws Exception
	 */
	private Petrinet buildPrefix(PluginContext context, Petrinet net, Marking initMarking, UnfoldingConfiguration param) throws Exception {
		
		// load and initialize input Petri net
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		semantics.initialize(net.getTransitions(), initMarking);

		try {
			DNodeSys sys = new DNodeSys_PtNet(net, initMarking);
			
			Options o = new Options(sys);
			o.configure_buildOnly();
			o.configure_PetriNet();
			o.configure_setBound(param.bound);
			DNodeBP buildPrefix = new DNodeBP(sys, o);
			
			boolean interrupted = false;
			while (buildPrefix.step() > 0) {
				if (context.getProgress().isCancelled()) {
					interrupted = true;
					break;
				}
			}
	
			// Step 2. construct net unfolding from the coverability graph
			//Object [] result = constructPrefix(net, initMarking, covGraph, startStateSet);
			Petrinet result = DNode2Petrinet.process(buildPrefix, false);
	
			if (!interrupted) {
				// set label before result output
				context.getFutureResult(0).setLabel("Complete prefix of the unfolding of " + net.getLabel());
			} else {
				// set label before result output
				context.getFutureResult(0).setLabel("Incomplete prefix of the unfolding of " + net.getLabel()+" (cancelled by user)");
			}
			
			// connect the result
			context.addConnection(new UnfoldingNetConnection(net, initMarking, semantics, result));
		
			return result;
		} catch (InvalidModelException e) {
			// the model is invalid for the unfolding algorithm
			context.log(e.getMessage());
			context.getFutureResult(0).cancel(true);
			return null ;
		}
	}
	
	/**
	 * build prefix and check for soundness of free-choice net
	 * 
	 * @param context
	 * @param net
	 * @param initMarking
	 * @return
	 * @throws Exception
	 */
	private Petrinet checkSoundnessFC(PluginContext context, Petrinet net, Marking initMarking) throws Exception {
		
		// load and initialize input Petri net
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		semantics.initialize(net.getTransitions(), initMarking);

		try {
			DNodeSys sys = new DNodeSys_PtNet(net, initMarking);
			
			Options o = new Options(sys);
			o.configure_checkSoundness();
			o.configure_PetriNet();
			DNodeBP buildPrefix = new DNodeBP(sys, o);
			
			boolean interrupted = false;
			while (buildPrefix.step() > 0) {
				if (context.getProgress().isCancelled()) {
					interrupted = true;
					break;
				}
			}
			
			Petrinet result;
			
			if (!interrupted) {
				buildPrefix.findDeadConditions(true);
				
	            boolean hasDeadlock = buildPrefix.hasDeadCondition();
	            boolean isUnsafe = !buildPrefix.isSafe();
	            
	            if (hasDeadlock || isUnsafe) {
	            	// return constructed prefix of unsound net
					result = DNode2Petrinet.process(buildPrefix, false);
					
					// set label before result output
					context.getFutureResult(0).setLabel("Counterexample to unsoundness of " + net.getLabel());
	            } else {
	            	Petrinet netResult = PetrinetFactory.newPetrinet("net is sound");
	            	
	            	Place alpha = netResult.addPlace("alpha");
	            	Place omega = netResult.addPlace("omega");
	            	Transition sound = netResult.addTransition("sound");
	            	netResult.addArc(alpha, sound);
	            	netResult.addArc(sound, omega);
	            	result = netResult;
	            	
					// set label before result output
					context.getFutureResult(0).setLabel("Sound: " + net.getLabel());
	            }
			} else {
            	// return constructed prefix of unsound net
				result = DNode2Petrinet.process(buildPrefix, false);
				
				// set label before result output
				context.getFutureResult(0).setLabel("Incomplete prefix of the unfolding of " + net.getLabel()+" (cancelled by user)");
			}
			
			// connect the result
			context.addConnection(new UnfoldingNetConnection(net, initMarking, semantics, result));
		
			return result;
		} catch (InvalidModelException e) {
			// the model is invalid for the unfolding algorithm
			context.log(e.getMessage());
			context.getFutureResult(0).cancel(true);
			return null ;
		}
	}
	
	@TestMethod(output="1 1")
	public static String test_UMA_UnfoldPN_standardParametersUnfold() {
		UnfoldingConfiguration param = UnfoldingConfiguration.getDefault_unfolding();
		return param.bound+" "+param.mode;
	}
	
	@TestMethod(output="1 2")
	public static String test_UMA_UnfoldPN_standardParametersSoundnessFC() {
		UnfoldingConfiguration param = UnfoldingConfiguration.getDefault_soundnessFC();
		return param.bound+" "+param.mode;
	}

}
