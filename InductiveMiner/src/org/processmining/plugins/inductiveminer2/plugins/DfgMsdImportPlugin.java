package org.processmining.plugins.inductiveminer2.plugins;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsdImpl;

@Plugin(name = "Import a minimum self-distance graph", parameterLabels = { "Filename" }, returnLabels = {
		"Minimum self-distance graph" }, returnTypes = { DfgMsd.class })
@UIImportPlugin(description = "Directly follows + minimum self distance files", extensions = { "dfgmsd" })
public class DfgMsdImportPlugin extends AbstractImportPlugin {

	private static final int BUFFER_SIZE = 8192 * 4;
	private static final String CHARSET = Charset.defaultCharset().name();

	public DfgMsd importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {

		//read the file
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = input.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();

		//try 2
		DfgMsd dfg2 = readFile(new ByteArrayInputStream(baos.toByteArray()));

		if (dfg2 != null) {
			return dfg2;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "Invalid directly follows model/minimum self-distance graph file",
						"Invalid file", JOptionPane.ERROR_MESSAGE);
			}
		});
		context.getFutureResult(0).cancel(false);
		return null;
	}

	public static DfgMsd readFile(InputStream input) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(input, CHARSET), BUFFER_SIZE);

		//read activity names
		int nrOfActivities = Integer.parseInt(r.readLine());
		String[] activities = new String[nrOfActivities];
		for (int i = 0; i < nrOfActivities; i++) {
			activities[i] = r.readLine();
		}

		DfgMsd dfg = new DfgMsdImpl(activities);
		for (int i = 0; i < nrOfActivities; i++) {
			dfg.addActivity(i);
		}

		//read start activities
		{
			int nrOfStartActivities = Integer.parseInt(r.readLine());
			for (int i = 0; i < nrOfStartActivities; i++) {
				String line = r.readLine();
				int xAt = line.indexOf('x');
				int activityIndex = Integer.parseInt(line.substring(0, xAt));
				long cardinality = Long.parseLong(line.substring(xAt + 1, line.length()));

				dfg.getStartActivities().add(activityIndex, cardinality);
			}
		}

		//read end activities
		{
			int nrOfEndActivities = Integer.parseInt(r.readLine());
			for (int i = 0; i < nrOfEndActivities; i++) {
				String line = r.readLine();
				int xAt = line.indexOf('x');
				int activityIndex = Integer.parseInt(line.substring(0, xAt));
				long cardinality = Long.parseLong(line.substring(xAt + 1, line.length()));

				dfg.getEndActivities().add(activityIndex, cardinality);
			}
		}

		//read dfg-edges
		{
			int nrOfEdges = Integer.parseInt(r.readLine());
			for (int i = 0; i < nrOfEdges; i++) {
				String line = r.readLine();
				int eAt = line.indexOf('>');
				int xAt = line.indexOf('x');
				int source = Integer.parseInt(line.substring(0, eAt));
				int target = Integer.parseInt(line.substring(eAt + 1, xAt));
				long cardinality = Long.parseLong(line.substring(xAt + 1, line.length()));

				dfg.getDirectlyFollowsGraph().addEdge(source, target, cardinality);
			}
		}

		//read msd-edges
		String msdNrOfEdges = r.readLine();
		if (msdNrOfEdges != null) {
			int nrOfEdges = Integer.parseInt(msdNrOfEdges);
			for (int i = 0; i < nrOfEdges; i++) {
				String line = r.readLine();
				int eAt = line.indexOf('>');
				int xAt = line.indexOf('x');
				int source = Integer.parseInt(line.substring(0, eAt));
				int target = Integer.parseInt(line.substring(eAt + 1, xAt));
				long cardinality = Long.parseLong(line.substring(xAt + 1, line.length()));

				dfg.getMinimumSelfDistanceGraph().addEdge(source, target, cardinality);
			}
		}

		return dfg;
	}
}
