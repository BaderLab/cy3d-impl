package org.cytoscape.paperwing.internal.input;

import java.awt.event.KeyEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class NetworkChangeInputHandler implements InputHandler {
	
	private static final double NODE_PLACEMENT_DISTANCE = 2.3;
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		Set<Integer> pressed = keys.getPressed();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		processCreateNode(mouse, pressed, graphicsData);
		processCreateEdge(pressed, graphicsData);
		processDeleteSelection(pressed, graphicsData);
		
	}
	
	private static void processCreateNode(MouseMonitor mouse, Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		SimpleCamera camera = graphicsData.getCamera();
		
		// Create a new node
		if (pressed.contains(KeyEvent.VK_N)) {
			
			// Project mouse coordinates into 3d space to determine where to put the node
			Vector3 projection = GeometryToolkit.convertScreenTo3d(
					mouse.x(), mouse.y(), graphicsData.getScreenWidth(),
					graphicsData.getScreenHeight(), NODE_PLACEMENT_DISTANCE, camera);
			
			System.out.println("Node created at: " + projection);
			
			CyNode added = networkView.getModel().addNode();
			networkView.updateView();

			View<CyNode> viewAdded = networkView.getNodeView(added);

			double distanceScale = graphicsData.getDistanceScale();
			
			// TODO: Maybe throw an exception if viewAdded is null
			if (viewAdded != null) {
				viewAdded.setVisualProperty(
						RichVisualLexicon.NODE_X_LOCATION, projection.x()
								* distanceScale);
				viewAdded.setVisualProperty(
						RichVisualLexicon.NODE_Y_LOCATION, projection.y()
								* distanceScale);
				viewAdded.setVisualProperty(
						RichVisualLexicon.NODE_Z_LOCATION, projection.z()
								* distanceScale);

				// Set the node to be hovered
				// TODO: This might not be needed if the node were added
				// through some way other than the mouse
				// graphicsData.getSelectionData().setHoverNodeIndex(added.getIndex());
			}
		}
	}
	
	private static void processCreateEdge(Set<Integer> pressed, GraphicsData graphicsData) {
		
		Set<Integer> selectedNodeIndices = graphicsData.getSelectionData().getSelectedNodeIndices();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Create edges between nodes
		if (pressed.contains(KeyEvent.VK_J)) {
			CyNode hoverNode = networkView.getModel().getNode(
					graphicsData.getSelectionData().getHoverNodeIndex());

			if (hoverNode != null) {

				for (Integer index : selectedNodeIndices) {
					networkView.getModel().addEdge(
							networkView.getModel().getNode(index),
							hoverNode, false);

				}
				
				// TODO: Not sure if this call is needed
				networkView.updateView();
			}
		}
	}
	
	private static void processDeleteSelection(Set<Integer> pressed, GraphicsData graphicsData) {
		
		Set<Integer> selectedNodeIndices = graphicsData.getSelectionData().getSelectedNodeIndices();
		Set<Integer> selectedEdgeIndices = graphicsData.getSelectionData().getSelectedEdgeIndices();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Delete selected edges/nodes
		if (pressed.contains(KeyEvent.VK_DELETE)) {
			Set<CyEdge> edgesToBeRemoved = new LinkedHashSet<CyEdge>();
			Set<CyNode> nodesToBeRemoved = new LinkedHashSet<CyNode>();
			
			// Remove nodes
			CyNode nodeToBeRemoved;
			
			for (Integer index : selectedNodeIndices) {
				nodeToBeRemoved = networkView.getModel().getNode(index);
				
				if (nodeToBeRemoved != null ) {
					nodesToBeRemoved.add(nodeToBeRemoved);
					
					// TODO: Check if use of Type.ANY for any edge is correct
					// TODO: Check if this addAll method properly skips adding
					// edges already in the edgesToBeRemovedList
					edgesToBeRemoved.addAll(networkView.getModel()
							.getAdjacentEdgeList(nodeToBeRemoved,
									Type.ANY));
				}
				
			}

			// Remove edges
			CyEdge edgeToBeRemoved;
			
			for (Integer index : selectedEdgeIndices) {
				edgeToBeRemoved = networkView.getModel().getEdge(index);
				
				if (edgeToBeRemoved != null) {
					edgesToBeRemoved.add(edgeToBeRemoved);
				}
			}
			
			networkView.getModel().removeNodes(nodesToBeRemoved);
			networkView.getModel().removeEdges(edgesToBeRemoved);
			
			selectedNodeIndices.clear();
			selectedEdgeIndices.clear();
			
			// TODO: Not sure if this call is needed
			networkView.updateView();
		}
	}
}
