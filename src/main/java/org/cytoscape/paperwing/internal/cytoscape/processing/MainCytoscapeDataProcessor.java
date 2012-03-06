package org.cytoscape.paperwing.internal.cytoscape.processing;

import java.awt.Color;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.WindVisualLexicon;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.data.LightingData;
import org.cytoscape.paperwing.internal.lighting.Light;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;

public class MainCytoscapeDataProcessor implements CytoscapeDataProcessor {

	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		
		// Perform on second frame, first frame performs initial drawing
		// TODO: Check if necessary, the ding renderer doesn't do this
//		if (graphicsData.getFramesElapsed() == 1) {
//			initializeTableSelectionState(graphicsData);
//		}
		
		processDeselectedData(graphicsData);
	}

	// Performs deselection in Cytoscape data objects, such as CyTable
	private void processDeselectedData(GraphicsData graphicsData) {
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		Set<Integer> toBeDeselectedNodeIndices = selectionData.getToBeDeselectedNodeIndices();
		Set<Integer> toBeDeselectedEdgeIndices = selectionData.getToBeDeselectedEdgeIndices();
		
		NetworkToolkit.deselectNodes(toBeDeselectedNodeIndices, networkView);
		NetworkToolkit.deselectEdges(toBeDeselectedEdgeIndices, networkView);
		toBeDeselectedNodeIndices.clear();
		toBeDeselectedEdgeIndices.clear();
		
		// Select nodes
		for (int index : selectionData.getSelectedNodeIndices()) {
			if (!NetworkToolkit.checkNodeSelected(index, networkView)) {
				NetworkToolkit.setNodeSelected(index, networkView, true);
			}
		}
		
		// Select edges
		for (int index : selectionData.getSelectedEdgeIndices()) {
			if (!NetworkToolkit.checkEdgeSelected(index, networkView)) {
				NetworkToolkit.setEdgeSelected(index, networkView, true);
			}
		}
	}
	
	// Note: For the below method, Ding does not fill in selected states, so for now the 3d renderer will not do so either.
	/**
	 *  Fills in the missing "selected" boolean values in CyTable.
	 *  
	 *  @param graphicsData The {@link GraphicsData} object containing a reference to the network view.
	 */
	private void initializeTableSelectionState(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		CyNetwork network = graphicsData.getNetworkView().getModel();
		
		for (CyNode node : network.getNodeList()) {
			NetworkToolkit.setNodeSelected(node.getIndex(), networkView, false);
		}
		
		for (CyEdge edge : network.getEdgeList()) {
			NetworkToolkit.setEdgeSelected(edge.getIndex(), networkView, false);
		}
	}
	
	/**
	 * Transfer lighting data from the visual property set
	 */
	private void updateLightingData(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		LightingData lightingData = graphicsData.getLightingData();
		
		// Try to get it working with a single light first (index 0)
		Light light = lightingData.getLight(0);
		
		// Transfer color properties
		Color ambient = (Color) networkView.getVisualProperty(WindVisualLexicon.LIGHT_AMBIENT_COLOR);
		double ambientAlpha = networkView.getVisualProperty(WindVisualLexicon.LIGHT_AMBIENT_ALPHA);
		
		Color diffuse = (Color) networkView.getVisualProperty(WindVisualLexicon.LIGHT_DIFFUSE_COLOR);
		double diffuseAlpha = networkView.getVisualProperty(WindVisualLexicon.LIGHT_DIFFUSE_ALPHA);
		
		Color specular = (Color) networkView.getVisualProperty(WindVisualLexicon.LIGHT_SPECULAR_COLOR);
		double specularAlpha = networkView.getVisualProperty(WindVisualLexicon.LIGHT_SPECULAR_ALPHA);
		
		light.setAmbient((float) ambient.getRed() / 255, 
				(float) ambient.getGreen() / 255, 
				(float) ambient.getBlue() / 255, 
				(float) ambientAlpha);
		
		light.setDiffuse((float) diffuse.getRed() / 255, 
				(float) diffuse.getGreen() / 255, 
				(float) diffuse.getBlue() / 255, 
				(float) diffuseAlpha);
		
		light.setSpecular((float) specular.getRed() / 255, 
				(float) specular.getGreen() / 255, 
				(float) specular.getBlue() / 255, 
				(float) specularAlpha);
		
		// Transfer position properties
		light.setPosition(networkView.getVisualProperty(WindVisualLexicon.LIGHT_X_LOCATION).floatValue(), 
				networkView.getVisualProperty(WindVisualLexicon.LIGHT_Y_LOCATION).floatValue(), 
				networkView.getVisualProperty(WindVisualLexicon.LIGHT_Z_LOCATION).floatValue(), 
				1.0f);
		
		// Transfer remaining properties
		light.setTurnedOn(networkView.getVisualProperty(WindVisualLexicon.LIGHT_ENABLED));
	}
	
}
