package org.cytoscape.paperwing.internal.cytoscape.processing;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.WindVisualLexicon;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.data.LightingData;
import org.cytoscape.paperwing.internal.input.BoundsInputHandler;
import org.cytoscape.paperwing.internal.input.InputHandler;
import org.cytoscape.paperwing.internal.input.KeyboardMonitor;
import org.cytoscape.paperwing.internal.input.MouseMonitor;
import org.cytoscape.paperwing.internal.lighting.Light;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
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
