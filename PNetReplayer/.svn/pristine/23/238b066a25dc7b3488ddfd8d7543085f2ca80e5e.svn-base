/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.IPNManifestReplayAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.PNManifestReplayerILPAlgorithm;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians
 * Feb 27, 2012
 *
 */
public class ChooseAlgorithmPanel extends JPanel {
	private static final long serialVersionUID = -6102982650312287271L;
	private static final String NONE = "No Algorithm Available";

	// algorithm selection 
	private JComboBox algorithmCbBox;
	private JLabel algorithmInfo;
	
	public ChooseAlgorithmPanel(PetrinetGraph net, XLog log, Marking initialMarking, Marking[] finalMarkings){
		// shared objects 
		SlickerFactory factory = SlickerFactory.instance();

		// initialize algorithms
		List<IPNManifestReplayAlgorithm> algorithms = new ArrayList<IPNManifestReplayAlgorithm>(1);
		
		// check ILP algorithm
		PNManifestReplayerILPAlgorithm ilpAlg = new PNManifestReplayerILPAlgorithm();
		if (ilpAlg.isReqWOParameterSatisfied(net, log, initialMarking, finalMarkings)){
			algorithms.add(ilpAlg);
		} 
		
		// check ILP algorithm with Petrinet pattern
//		PNManifestReplayerILPPNetPatternAlgorithm netAlg = new PNManifestReplayerILPPNetPatternAlgorithm();
//		if (netAlg.isReqWOParameterSatisfied(net, log, initialMarking, finalMarkings)){
//			algorithms.add(netAlg);
//		} 
		
		// create selection panel
		Object[] algorithmsArr = algorithms.toArray();
		if ((algorithmsArr == null)||(algorithmsArr.length == 0)){
			algorithmsArr = new Object[1];
			algorithmsArr[0] = NONE;
		}
		algorithmCbBox = factory.createComboBox(algorithmsArr);
		algorithmCbBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmInfo.setText(((IPNManifestReplayAlgorithm) algorithmCbBox.getSelectedItem()).getHTMLInfo());
			}
		});
		algorithmCbBox.setPreferredSize(new Dimension(500, 25));
		algorithmCbBox.setMinimumSize(new Dimension(500, 25));

		// init info 		
		algorithmInfo = factory.createLabel("");
		algorithmInfo = new JLabel();
		algorithmInfo.setPreferredSize(new Dimension(600, 200));		

		// initiate text
		if (algorithmCbBox.getSelectedItem() instanceof IPNManifestReplayAlgorithm){
			algorithmInfo.setText(((IPNManifestReplayAlgorithm) algorithmCbBox.getSelectedItem()).getHTMLInfo());			
		} else {
			algorithmInfo.setText("");
		}
		
		// set layout
		double size[][] = new double [][]{{100, 500},{25, TableLayout.FILL}};
		setLayout(new TableLayout(size));
		
		JLabel lbl = new JLabel("Select algorithm");
		
		add(lbl, "0,0,r,c");
		add(algorithmCbBox, "1,0,l,c");
		add(algorithmInfo, "0,1,1,1, c, t");
	}

	/**
	 * Get selected algorithm, otherwise return null
	 * @return
	 */
	public IPNManifestReplayAlgorithm getSelectedAlgorithm() {
		if (algorithmCbBox.getSelectedItem() instanceof IPNManifestReplayAlgorithm){
			return (IPNManifestReplayAlgorithm) algorithmCbBox.getSelectedItem();
		}
		return null;
	}
}
