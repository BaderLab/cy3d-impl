package org.baderlab.cy3d.internal.cytoscape.processing;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.baderlab.cy3d.internal.WindVisualLexicon;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.LightingData;
import org.baderlab.cy3d.internal.lighting.Light;
import org.cytoscape.view.model.CyNetworkView;

public class BirdsEyeCytoscapeDataProcessor implements CytoscapeDataProcessor {

	/** The list of CytoscapeDataSubprocessors used by this processor */
	private List<CytoscapeDataSubprocessor> subprocessors;
	
	public BirdsEyeCytoscapeDataProcessor() {
		subprocessors = new LinkedList<CytoscapeDataSubprocessor>();
	
		// Populate the list of subprocessors; they are called in the order added.
		subprocessors.add(new LightingCytoscapeDataSubprocessor());
	}
	
	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		
		for (CytoscapeDataSubprocessor subprocessor : subprocessors) {
			subprocessor.processCytoscapeData(graphicsData);
		}
	}
}
