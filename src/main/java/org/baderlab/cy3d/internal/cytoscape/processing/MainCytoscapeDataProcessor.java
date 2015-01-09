package org.baderlab.cy3d.internal.cytoscape.processing;

import java.util.LinkedList;
import java.util.List;

import org.baderlab.cy3d.internal.data.GraphicsData;

public class MainCytoscapeDataProcessor implements CytoscapeDataProcessor {

	/** The list of CytoscapeDataSubprocessors used by this processor */
	private List<CytoscapeDataSubprocessor> subprocessors;
	
	public MainCytoscapeDataProcessor() {
		subprocessors = new LinkedList<CytoscapeDataSubprocessor>();
	
		// Populate the list of subprocessors; they are called in the order added.
		subprocessors.add(new TableSelectionCytoscapeDataSubprocessor());
		subprocessors.add(new LightingCytoscapeDataSubprocessor());
		subprocessors.add(new CameraCytoscapeDataSubprocessor());
	}
	
	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		
		for (CytoscapeDataSubprocessor subprocessor : subprocessors) {
			subprocessor.processCytoscapeData(graphicsData);
		}
	}
}
