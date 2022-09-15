package org.processmining.plugins.alignetc.result;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.alignetc.connection.AlignETCResultConnection;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Visualization of Align ETCResult.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(name = "AlignETCResult Visualizer", 
		returnLabels = { "Visualized Align ETCResult" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = "AlignETCResult")
@Visualizer
public class AlignETCResultVisualization {
		
		@PluginVariant(	requiredParameterLabels = { 0 }, 
						variantLabel = "Default Visualization")
		public JComponent open(PluginContext context, AlignETCResult res) {
			
			//Create the General Pane
			SlickerTabbedPane pane = SlickerFactory.instance()
			.createTabbedPane("Align ETConformance Results", SlickerColors.COLOR_BG_1,
					SlickerColors.COLOR_FG,SlickerColors.COLOR_FG);
			
			//Results Tab
			pane.addTab("Results", new ResultsPaneUI(res));
			
			//Alignments Tab
			if(res.alignments != null){ 
				pane.addTab("Alignments", new AlignmentsPaneUI(context,res));
			
				
//				//TODO Remove
//				double lmgood = 0;
//				double lmnogood = 0;
//				double l = 0;
//				double minvi = 0;
//				double mreal = 0;
//				PNMatchInstancesRepResult allAlignments = res.alignments;
//				for(AllSyncReplayResult casesAlignments: allAlignments){	
//					int numReplaysForCurr = casesAlignments.getNodeInstanceLst().size();
//					double lmgoodC = 0;
//					double lmnogoodC = 0;
//					double lC = 0;
//					double minviC = 0;
//					double mrealC = 0;
//					for(int iAlign=0; iAlign< casesAlignments.getNodeInstanceLst().size(); iAlign++){
//						for(StepTypes type: casesAlignments.getStepTypesLst().get(iAlign)){
//							switch(type){
//								case L : lC++;
//									break;
//								case LMGOOD : lmgoodC++;
//									break;
//								case LMNOGOOD : lmnogoodC++;
//									break;
//								case MINVI : minviC++;
//									break;
//								case MREAL : mrealC++;
//									break;
//								
//							}
//						}
//					}
//					l += (lC / numReplaysForCurr);
//					lmgood += (lmgoodC / numReplaysForCurr);
//					lmnogood += (lmnogoodC / numReplaysForCurr);
//					minvi += (minviC / numReplaysForCurr);
//					mreal += (mrealC / numReplaysForCurr);
//				}
//				System.out.println("L:"+l+" LMGOOD:"+lmgood+" LMNOGOOD:"+lmnogood+" MINVI:"+minvi+" MREAL:"+mreal);
				
				PNMatchInstancesRepResult allAlignments = res.alignments;
				XLog log=null;
				// AA: commented because no codec is necessary
//				PNCodec codec = null;
				try {
					log = context.getConnectionManager()
					.getFirstConnection(AlignETCResultConnection.class, context, allAlignments) 
							.getObjectWithRole(AlignETCResultConnection.LOG);
					// AA: commented because no codec is necessary	
//					codec = (PNCodec) context.getConnectionManager()
//					.getFirstConnection(PNCodecConnection.class, context, net) 
//					.getObjectWithRole(PNCodecConnection.PNCODEC);
					
					
				} catch (ConnectionCannotBeObtained e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				double ltotal=0;
				double lbad = 0;
				int notAlign = 0;
				
				for(AllSyncReplayResult casesAlignments: allAlignments){	
					double numCasesForCurr = casesAlignments.getTraceIndex().size();
					double numReplaysForCurr = casesAlignments.getNodeInstanceLst().size();
					double lcurrent = 0;

					
					for(Integer traceID: casesAlignments.getTraceIndex()){
						lcurrent += log.get(traceID).size();
					}
					
					if(numReplaysForCurr == 0){
						lbad += lcurrent;
						ltotal += lcurrent;
						notAlign += casesAlignments.getTraceIndex().size();
					}
					else{
						double lbadcurrent = 0;
						for(int iAlign=0; iAlign< casesAlignments.getNodeInstanceLst().size(); iAlign++){
							for(StepTypes type: casesAlignments.getStepTypesLst().get(iAlign)){
								if(type == StepTypes.L){
									lbadcurrent++;
								}
							}
						}
						lbad += (lbadcurrent / numReplaysForCurr) * numCasesForCurr;
						ltotal +=lcurrent;
					}

				}
				double lgood = (ltotal-lbad);
				
				System.out.println();
				System.out.println("L_TOTAL:"+ltotal+" L_GOOD:"+lgood+" L_BAD:"+lbad+" CasesNotAlign:"+notAlign+" %Behav:"+((lgood *100)/(ltotal))+"%");
				
			}	
			//Automaton Tab
			//if(res.ra != null) 
				//pane.addTab("Replay Automaton", new ReplayAutomatonPaneUI(res));
			
			//Write the results by console
			writeResults(res);
			
			return pane;
		}

		private void writeResults(AlignETCResult res) {
			System.out.println();
			System.out.println("-------------------------------------------------");
			System.out.println("ap: "+rond(res.ap, 4));
			System.out.println("gamma: "+rond(res.escTh, 4));
			System.out.println("-------------------------------------------------");
			System.out.println();
			
		}
		
		/**
		 * Function to round a double with the given number of decimals.
		 * @param n Number to be rounded.
		 * @param decimals Number of decimals wanted.
		 * @return The rounded number with the given number of decimals.
		 */
		private double rond(double n, int decimals){
			return Math.rint(n*(Math.pow(10,decimals)))/Math.pow(10,decimals);
		}
	
}
