package org.baderlab.cy3d.internal.cytoscape.processing;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.baderlab.cy3d.internal.WindVisualLexicon;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.data.LightingData;
import org.baderlab.cy3d.internal.input.BoundsInputHandler;
import org.baderlab.cy3d.internal.input.InputHandler;
import org.baderlab.cy3d.internal.input.KeyboardMonitor;
import org.baderlab.cy3d.internal.input.MouseMonitor;
import org.baderlab.cy3d.internal.lighting.Light;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

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
