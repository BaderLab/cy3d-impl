package org.baderlab.cy3d.internal.input;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.baderlab.cy3d.internal.tools.RenderToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

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
			
//			System.out.println("Input window coordinates: " + mouse.x() + ", " + mouse.y());
			
			CyNode added = networkView.getModel().addNode();
			networkView.updateView();

			View<CyNode> viewAdded = networkView.getNodeView(added);

			double distanceScale = graphicsData.getDistanceScale();
			
			// TODO: Maybe throw an exception if viewAdded is null
			if (viewAdded != null) {
				viewAdded.setVisualProperty(
						BasicVisualLexicon.NODE_X_LOCATION, projection.x()
								* distanceScale);
				viewAdded.setVisualProperty(
						BasicVisualLexicon.NODE_Y_LOCATION, projection.y()
								* distanceScale);
				viewAdded.setVisualProperty(
						BasicVisualLexicon.NODE_Z_LOCATION, projection.z()
								* distanceScale);

				// Set the node to be hovered
				// TODO: This might not be needed if the node were added
				// through some way other than the mouse
				// graphicsData.getSelectionData().setHoverNodeIndex(added.getIndex());
			}
			
//			System.out.println("Node created, window coordinates: " + RenderToolkit.convert3dToScreen(graphicsData.getGlContext(), projection));
		}
	}
	
	private static void processCreateEdge(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
		
		// Create edges between nodes
		if (pressed.contains(KeyEvent.VK_J)) {
			CyNode hoverNode = networkView.getModel().getNode(
					graphicsData.getSelectionData().getHoverNodeIndex());

			if (hoverNode != null) {

				for (CyNode node : selectedNodes) {
					networkView.getModel().addEdge(
							node,
							hoverNode, false);

				}
				
				// TODO: Not sure if this call is needed
				networkView.updateView();
			}
		}
	}
	
	private static void processDeleteSelection(Set<Integer> pressed, GraphicsData graphicsData) {
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
		List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(networkView.getModel(), "selected", true);
		
		// Delete selected edges/nodes
		if (pressed.contains(KeyEvent.VK_DELETE)) {
			Set<CyEdge> edgesToBeRemoved = new LinkedHashSet<CyEdge>();
			Set<CyNode> nodesToBeRemoved = new LinkedHashSet<CyNode>();
			
			// Prepare to remove nodes
			for (CyNode node : selectedNodes) {

				nodesToBeRemoved.add(node);

				edgesToBeRemoved.addAll(networkView.getModel()
						.getAdjacentEdgeList(node,
								Type.ANY));
			}

			// Prepare to remove edges
			for (CyEdge edge : selectedEdges) {
				edgesToBeRemoved.add(edge);
			}
			
			// Remove the node and edge entries from the CyTable
			NetworkToolkit.deselectEdges(edgesToBeRemoved, networkView);
			NetworkToolkit.deselectNodes(nodesToBeRemoved, networkView);
			
			networkView.getModel().removeEdges(edgesToBeRemoved);
			networkView.getModel().removeNodes(nodesToBeRemoved);
			
			// TODO: Not sure if this call is needed
			networkView.updateView();
		}
	}
}
