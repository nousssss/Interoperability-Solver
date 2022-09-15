package org.processmining.plugins.alignetc.result;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.alignetc.connection.AlignETCResultConnection;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.replayresult.visualization.ProcessInstanceConformanceView;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class AlignmentsPaneUI extends JPanel{
	
	private static final long serialVersionUID = 465646082443367376L;

	public AlignmentsPaneUI(PluginContext context,AlignETCResult res ){
		
		super();
		
		PNMatchInstancesRepResult allAlignments = res.alignments;
		
		//Get the connection between Alignment nodes and Transitions
		PetrinetGraph net;
		// AA: commented because no codec is necessary
//		PNCodec codec = null;
		try {
			net = context.getConnectionManager()
			.getFirstConnection(AlignETCResultConnection.class, context, allAlignments) 
					.getObjectWithRole(AlignETCResultConnection.PN);
			// AA: commented because no codec is necessary	
//			codec = (PNCodec) context.getConnectionManager()
//			.getFirstConnection(PNCodecConnection.class, context, net) 
//			.getObjectWithRole(PNCodecConnection.PNCODEC);
			
			
		} catch (ConnectionCannotBeObtained e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		// add panel below
		TableLayout bgPanelLayout = new TableLayout(
				new double[][] { { TableLayout.FILL, 300 }, { TableLayout.FILL} });
		this.setLayout(bgPanelLayout);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(new Color(30, 30, 30));

		// add util
		SlickerFactory factory = SlickerFactory.instance();
		
		//create aligments panel
		JPanel caseView = new JPanel();
		caseView.setBorder(BorderFactory.createEmptyBorder());
		caseView.setBackground(new Color(30, 30, 30));
		TableLayout layout = new TableLayout(
				new double[][] { { TableLayout.FILL, 75, TableLayout.PREFERRED, 100 }, {} });
		caseView.setLayout(layout);
		
		int row = 0;
		
		for(AllSyncReplayResult casesAlignments: allAlignments){
			
			for(int iAlign=0; iAlign< casesAlignments.getNodeInstanceLst().size(); iAlign++){
				
				layout.insertRow(row, TableLayout.PREFERRED);
				
				if(iAlign==0){
					// create combobox
					Object[] caseIDs = casesAlignments.getCaseIDs().toArray();
					for (int i = 0; i < caseIDs.length; i++) {
						if (caseIDs[i] == null) {
							caseIDs[i] = "NONE";
						}
					}

					// create label for combobox
					JLabel lbl1 = factory.createLabel(caseIDs.length + " case(s) :");
					lbl1.setForeground(Color.WHITE);
					caseView.add(lbl1, "0," + row + " r, c");
					JComboBox combo = factory.createComboBox(caseIDs);
					combo.setPreferredSize(new Dimension(200, combo.getPreferredSize().height));
					combo.setMinimumSize(new Dimension(200, combo.getPreferredSize().height));
					combo.setMaximumSize(new Dimension(200, combo.getPreferredSize().height));
					caseView.add(combo, "1," + row + " l, c");
				}
				
				List<Object> alignment = casesAlignments.getNodeInstanceLst().get(iAlign); 
				List<StepTypes> alignmentTypes = casesAlignments.getStepTypesLst().get(iAlign);
				
				// reformat node instance list
				List<Object> alignmentReformated = new LinkedList<Object>();
				for (Object obj : alignment) {
					if (obj instanceof String) {
						alignmentReformated.add(obj);
						// AA: commented because no codec is necessary
//					} else if (codec != null) {
//						alignmentReformated.add(codec.decode((Short) obj).getLabel());
					} else if (obj instanceof Transition) {
						alignmentReformated.add(((Transition) obj).getLabel());
					} else {
						alignmentReformated.add(obj.toString());
					}
				}
				
				ProcessInstanceConformanceView confView = new ProcessInstanceConformanceView("Replayed", alignmentReformated, alignmentTypes);
				caseView.add(confView, "2," + row + " l, c");
				
				
				
				row++;
			}
		}
		
		JScrollPane scp = new JScrollPane(caseView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scp.setBackground(new Color(30, 30, 30));
		scp.setPreferredSize(new Dimension(1000, 160));
		scp.setMaximumSize(new Dimension(1000, 320));
		scp.setBorder(BorderFactory.createEmptyBorder());
		this.add(scp,  "0,0");
	}

}
