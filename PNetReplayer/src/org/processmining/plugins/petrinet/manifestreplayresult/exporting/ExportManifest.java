/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayresult.exporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.Arrays;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians Feb 28, 2012
 * 
 */
@Plugin(name = "Export manifest report as CSV (.csv)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Manifest", "File" }, userAccessible = true)
@UIExportPlugin(description = "Export manifest report as CSV (.csv)", extension = "csv")
public class ExportManifest { 
	// local constants
	final int SVALUES = 0;
	final int MVALUES = 1;
	final int MINVALUES = 2;
	final int MAXVALUES = 3;
	final int FREQ = 4;

	final int RAWFITNESSCOST = Manifest.RAWFITNESSCOST * 5;
	final int MOVELOGFITNESS = Manifest.MOVELOGFITNESS * 5;
	final int MOVEMODELFITNESS = Manifest.MOVEMODELFITNESS * 5;
	final int TRACEFITNESS = Manifest.TRACEFITNESS * 5;
	final int NUMSTATEGENERATED = Manifest.NUMSTATEGENERATED * 5;
	final int TIME = Manifest.TIME * 5;

	@PluginVariant(variantLabel = "Export manifest report as CSV (.csv)", requiredParameterLabels = { 0, 1 })
	public void exportManifest2File(UIPluginContext context, Manifest manifest, File file) {
		System.gc();

		// export  to file
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writeManifest(manifest, bw);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}

	private void writeManifest(Manifest manifest, BufferedWriter bw) throws IOException {
		/**
		 * reference to object
		 */
		XLog log = manifest.getLog();
		PetrinetGraph net = manifest.getNet();

		/**
		 * stats related variable [sValues][mValues][minValues][maxValues][freq]
		 * 
		 * the stats are calculated for 6 elements: 1. [RAWFITNESSCOST] 2.
		 * [MOVELOGFITNESS] 3. [MOVEMODELFITNESS] 4. [TRACEFITNESS] 5.
		 * [NUMSTATEGENERATED] 6. [TIME]
		 */
		double[] stats = new double[6 * 5];
		Arrays.fill(stats, 0, stats.length, Double.NaN);

		int numReliableCases = 0;
		int numPerfectCases = 0;

		// utilities
		String newLineChar = System.getProperty("line.separator");
		char commaSeparator = ',';
		XConceptExtension ce = XConceptExtension.instance();
		NumberFormat nfDouble = NumberFormat.getInstance();
		NumberFormat nfInteger = NumberFormat.getInstance();

		nfDouble.setMinimumFractionDigits(2);
		nfDouble.setMaximumFractionDigits(2);
		nfInteger.setMaximumFractionDigits(0);

		// start writing to string
		bw.write("Result of replaying ");
		bw.write(net.getLabel());
		bw.write(" on ");
		bw.write(ce.extractName(log));
		bw.write(newLineChar);
		bw.write(newLineChar);

		bw.write("Index" + commaSeparator + "Case ID" + commaSeparator + "Raw Cost Fitness" + commaSeparator
				+ "Move Log Fitness" + commaSeparator + "Move Model Fitness" + commaSeparator + "Trace Fitness"
				+ commaSeparator + "Num. States" + commaSeparator + "Computation Time");
		bw.write(newLineChar);

		// iterate all cases
		int[] casePtr = manifest.getCasePointers();
		for (int i = 0; i < casePtr.length; i++) {
			if (casePtr[i] >= 0) {
				// write index 
				bw.write(String.valueOf(i + 1));
				bw.write(commaSeparator);

				// case ID
				XTrace trace = log.get(i);
				bw.write(ce.extractName(trace));
				bw.write(commaSeparator);

				// raw cost fitness
				double tmp = manifest.getRawCostFitness(i);
				if (tmp == Manifest.NOSTATS) {
					bw.write("Not available");
				} else {
					bw.write(nfInteger.format(tmp));
					if (Double.compare(0.0000, tmp) == 0) {
						numPerfectCases++;
					}
					updateStats(stats, RAWFITNESSCOST, tmp);
				}
				bw.write(commaSeparator);

				// move log fitness
				tmp = manifest.getMoveLogFitness(i);
				if (tmp == Manifest.NOSTATS) {
					bw.write("Not available");
				} else {
					bw.write("\"");
					bw.write(nfDouble.format(tmp));
					bw.write("\"");
					updateStats(stats, MOVELOGFITNESS, tmp);
				}
				bw.write(commaSeparator);

				// move model fitness
				tmp = manifest.getMoveModelFitness(i);
				if (tmp == Manifest.NOSTATS) {
					bw.write("Not available");
				} else {
					bw.write("\"");
					bw.write(nfDouble.format(tmp));
					bw.write("\"");
					updateStats(stats, MOVEMODELFITNESS, tmp);
				}
				bw.write(commaSeparator);

				// trace fitness
				tmp = manifest.getTraceFitness(i);
				if (tmp == Manifest.NOSTATS) {
					bw.write("Not available");
				} else {
					bw.write("\"");
					bw.write(nfDouble.format(tmp));
					bw.write("\"");
					updateStats(stats, TRACEFITNESS, tmp);
				}
				bw.write(commaSeparator);

				// generated states
				tmp = manifest.getNumStates(i);
				if (tmp == Manifest.NOSTATS) {
					bw.write("Not available");
				} else {
					bw.write("\"");
					bw.write(nfInteger.format(tmp));
					bw.write("\"");
					updateStats(stats, NUMSTATEGENERATED, tmp);
				}
				bw.write(commaSeparator);

				// computation time 
				tmp = manifest.getComputationTime(i);
				if (tmp == Manifest.NOSTATS) {
					bw.write("Not available");
				} else {
					bw.write("\"");
					bw.write(nfDouble.format(tmp));
					bw.write("\"");
					updateStats(stats, TIME, tmp);
				}
				bw.write(newLineChar);

				// last, update case stats
				numReliableCases++;
			}
		}

		bw.write(newLineChar);
		bw.write(newLineChar);
		bw.write("Property");
		bw.write(commaSeparator);
		bw.write("Average");
		bw.write(commaSeparator);
		bw.write("Minimum");
		bw.write(commaSeparator);
		bw.write("Maximum");
		bw.write(commaSeparator);
		bw.write("Std. Deviation");
		bw.write(newLineChar);

		for (int i = 0; i <= Manifest.TIME; i++) {
			// if the manifest is 0
			if (i == Manifest.RAWFITNESSCOST){
				bw.write("Raw fitness");
			} else if (i == Manifest.MOVELOGFITNESS){
				bw.write("Move log fitness");				
			} else if (i == Manifest.MOVEMODELFITNESS){
				bw.write("Move model fitness");
			} else if (i == Manifest.NUMSTATEGENERATED){
				bw.write("Num. explored states");
			} else if (i == Manifest.TRACEFITNESS){
				bw.write("Trace Fitness");
			} else if (i == Manifest.TIME){
				bw.write("Computation time (ms)");
			}
			bw.write(commaSeparator);
			bw.write("\"");
			bw.write(nfDouble.format(stats[(i * 5) + MVALUES]));
			bw.write("\"");
			bw.write(commaSeparator);
			bw.write("\"");
			bw.write(nfDouble.format(stats[(i * 5) + MINVALUES]));
			bw.write("\"");
			bw.write(commaSeparator);
			bw.write("\"");
			bw.write(nfDouble.format(stats[(i * 5) + MAXVALUES]));
			bw.write("\"");
			bw.write(commaSeparator);
			bw.write("\"");
			if (Double.compare(stats[(i * 5) + FREQ], Double.NaN) != 0){
				bw.write(nfDouble.format(Math.sqrt(stats[(i * 5) + SVALUES] / (stats[(i * 5) + FREQ]-1))));
			} else {
				bw.write(nfDouble.format(Double.NaN));
			}
			bw.write("\"");
			bw.write(newLineChar);
		}
	}

	private void updateStats(double[] stats, int statType, double value) {
		if (Double.compare(stats[FREQ + statType], Double.NaN) == 0) {
			stats[SVALUES + statType] = 0;
			stats[MVALUES + statType] = value;
			stats[MAXVALUES + statType] = value;
			stats[MINVALUES + statType] = value;
			stats[FREQ + statType] = 1;
		} else {
			stats[FREQ + statType]++;
			double oldMVal = stats[MVALUES + statType];
			stats[MVALUES + statType] += ((value - oldMVal) / stats[FREQ + statType]);
			stats[SVALUES + statType] += ((value - oldMVal) * (value - stats[MVALUES + statType]));

			if (Double.compare(stats[MAXVALUES + statType], value) < 0) {
				stats[MAXVALUES + statType] = value;
			}

			if (Double.compare(stats[MINVALUES + statType], value) > 0) {
				stats[MINVALUES + statType] = value;
			}

		}
	}
}
