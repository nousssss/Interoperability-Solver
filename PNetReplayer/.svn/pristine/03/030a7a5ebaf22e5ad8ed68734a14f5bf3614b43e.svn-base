/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.exporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * @author aadrians Mar 14, 2012
 * 
 */
@Plugin(name = "Export result report as CSV (.csv)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"PNMatchInstancesRepResult", "File" }, userAccessible = true)
@UIExportPlugin(description = "Export result report as CSV (.csv)", extension = "csv")
public class ExportPNMatchInstancesRepResult {
	private NumberFormat nf = NumberFormat.getInstance();
	private NumberFormat nfInt = NumberFormat.getInstance();

	@PluginVariant(variantLabel = "Export result report as CSV (.csv)", requiredParameterLabels = { 0, 1 })
	public void exportRepResult2File(UIPluginContext context, PNMatchInstancesRepResult res, File file) {
		System.gc();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		nfInt.setMinimumFractionDigits(0);
		nfInt.setMaximumFractionDigits(0);

		exportToFile(context, res, file);
	}

	private void exportToFile(UIPluginContext context, PNMatchInstancesRepResult res, File file) {
		// export  to file
		BufferedWriter bw = null;
		try {
			// get the log
			PNMatchInstancesRepResultConnection conn = context.getConnectionManager().getFirstConnection(
					PNMatchInstancesRepResultConnection.class, context, res);
			XLog log = conn.getObjectWithRole(PNMatchInstancesRepResultConnection.LOG);
			XConceptExtension ce = XConceptExtension.instance();

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			char commaSeparator = ',';
			bw.write("Num." + commaSeparator + "Case type" + commaSeparator + "Case ID" + commaSeparator
					+ "Trace Index" + commaSeparator + "IsReliable");

			int caseType = 0;
			int rowNumber = 1;
			Iterator<AllSyncReplayResult> it = res.iterator();

			// the first iteration also set up the index
			String[] keys = null;
			if (it.hasNext()) {
				Map<String, Double> info = it.next().getInfo();
				if (info != null) {
					Collection<String> keyset = Collections.unmodifiableCollection(info.keySet());
					keys = keyset.toArray(new String[keyset.size()]);

					for (int i = 0; i < keys.length; i++) {
						bw.write(commaSeparator);
						bw.write(keys[i]);
					}
				}
			}
			
			bw.write(commaSeparator + "#Representatives" + commaSeparator + "#Represented");
			bw.newLine();

			StringBuilder sb = new StringBuilder();
			for (AllSyncReplayResult r : res) {

				// create stats for same case type
				sb.append(commaSeparator);
				sb.append(r.isReliable() ? "Yes" : "No");
				sb.append(commaSeparator);
				Map<String, Double> info = r.getInfo();
				if (info != null) {
					for (int j = 0; j < keys.length; j++) {
						sb.append("\"");
						sb.append(info.get(keys[j]));
						sb.append("\"");
						sb.append(commaSeparator);
					}
				}
				String endStr = sb.toString();

				for (int caseID : r.getTraceIndex()) {
					bw.write(String.valueOf(rowNumber++));
					bw.write(commaSeparator);
					bw.write(String.valueOf(caseType));
					bw.write(commaSeparator);
					bw.write("\"");
					bw.write(ce.extractName(log.get(caseID)));
					bw.write("\"");
					bw.write(commaSeparator);
					bw.write(String.valueOf(caseID));
					bw.write(endStr);
					
					if ((r.getInfoObject() != null)&&(r.getInfoObject().get(InfoObjectConst.NUMREPRESENTEDALIGNMENT) != null)){
						@SuppressWarnings("unchecked")
						List<Integer> numAlignment = (List<Integer>) r.getInfoObject().get(InfoObjectConst.NUMREPRESENTEDALIGNMENT);
						bw.write("\"");
						bw.write(String.valueOf(numAlignment.size()));
						bw.write("\"");
						bw.write(commaSeparator);
						bw.write("\"");
						bw.write(numAlignment.toString());
						bw.write("\"");
					} else {
						bw.write("\"n/a\",\"n/a\"");
					}
					bw.newLine();
				}
				sb.delete(0, sb.length());
				caseType++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConnectionCannotBeObtained e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception exc) {
					// do nothing
				}
			}
		}

	}
}
