package org.baderlab.cy3d.internal.cytoscape.view;

import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.baderlab.cy3d.internal.Cy3DNetworkViewRenderer;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

public class Cy3DNetworkView extends VisualPropertyKeeper<CyNetwork> implements CyNetworkView {

	private Long suid;
	
	private CyNetwork network;
	
	private VisualLexicon visualLexicon;
	private DefaultValueVault defaultValues;
	private VisualMappingManager visualMappingManager;
	
	/**
	 * The camera associated with the main network viewing window used to
	 * perform operations such as fitting all nodes onto the screen
	 */
	private SimpleCamera networkCamera = null;
	
	/**
	 * The container associated with the network view, used to request
	 * input focus after actions such as the user clicking a button on the 
	 * toolbar
	 */
	private Component container = null;
	
	// Assumes indices of nodes are unique
	private Map<Long, View<CyNode>> nodeViews;
	private Map<Long, View<CyEdge>> edgeViews;
	
	/** A boolean that keeps track if fitContent() has been called to ensure the network has been centered at least once. */
	private boolean networkCentered;
	
	public Cy3DNetworkView(CyNetwork network, VisualLexicon visualLexicon, VisualMappingManager visualMappingManager) {
		suid = SUIDFactory.getNextSUID();
		
		this.network = network;
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;
		
		defaultValues = new DefaultValueVault(visualLexicon);
		nodeViews = new HashMap<>();
		edgeViews = new HashMap<>();
		
		Cy3DNodeView nodeView;
		for (CyNode node : network.getNodeList()) {
			nodeView = new Cy3DNodeView(defaultValues, node);
			
			nodeViews.put(node.getSUID(), nodeView);
		}
		
		Cy3DEdgeView edgeView;
		for (CyEdge edge : network.getEdgeList()) {
			edgeView = new Cy3DEdgeView(defaultValues, edge);
			
			edgeViews.put(edge.getSUID(), edgeView);
		}
		
		networkCentered = false;
	}
	
	@Override
	public CyNetwork getModel() {
		return network;
	}

	@Override
	public Long getSUID() {
		return suid;
	}

	@Override
	public View<CyNode> getNodeView(CyNode node) {
		return nodeViews.get(node.getSUID());
	}

	@Override
	public Collection<View<CyNode>> getNodeViews() {
		return nodeViews.values();
	}

	@Override
	public View<CyEdge> getEdgeView(CyEdge edge) {
		return edgeViews.get(edge.getSUID());
	}

	@Override
	public Collection<View<CyEdge>> getEdgeViews() {
		return edgeViews.values();
	}

	@Override
	public Collection<View<? extends CyIdentifiable>> getAllViews() {
		Collection<View<? extends CyIdentifiable>> views = new HashSet<View<? extends CyIdentifiable>>();
		
		// Return views for Node, Edge, Network
		views.addAll(getNodeViews());
		views.addAll(getEdgeViews());
		views.add(this);
		
		return views;
	}

	/**
	 * Center the network
	 */
	@Override
	public void fitContent() {
		fitNodesInView();
		
		// Request focus for the network view to be ready for keyboard input
		requestNetworkFocus();
	}

	@Override
	public void fitSelected() {
		// Obtain selected nodes
		Set<View<CyNode>> selectedNodeViews = new HashSet<View<CyNode>>();
		
		for (View<CyNode> nodeView : getNodeViews()) {
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_SELECTED)) {
				selectedNodeViews.add(nodeView);
			}
		}
		
		if (selectedNodeViews.isEmpty()) {
			return;
		}
		
		if (networkCamera != null) {
			NetworkToolkit.fitInView(networkCamera, selectedNodeViews, 180.0, 2.3, 1.8);
		}
		
		// Request focus for the network view to be ready for keyboard input
		requestNetworkFocus();
	}

	@Override
	public void updateView() {
		// TODO: Check if correct place to put this; below code should ensure having a view
		// for every node/edge in the network
		
		matchNodes();
		matchEdges();
		
		// Match the current network view to the currently applied visual style
//		updateToMatchVisualStyle();
		
//		// Render at least 1 more frame to reflect changes in network
//		if (animatorController != null) {
//			animatorController.startAnimator();
//		}
		
		// Center the network if it hasn't been centered yet
		if (!networkCentered) {
			fitNodesInView();
		}
		
		// Request focus after the network has been updated, such as via clicking a toolbar button,
		// in order to be ready to receive keyboard and mouse input
		requestNetworkFocus();
	}
	
	// Checks if there is a discrepancy between number of nodes and nodeViews, attempts
	// to fix discrepancy by removing extra views and adding missing views
	// TODO: Currently considers the set of views to be OK if node and nodeView counts match,
	// does not check if there is an actual 1:1 relationship
	private void matchNodes() {
		int nodeCountDifference = network.getNodeCount() - nodeViews.size();
		
		// Check if nodes have been added to the network
		if (nodeCountDifference > 0) {
			for (CyNode node : network.getNodeList()) {
				
				// Found a node without a view?
				if (nodeViews.get(node.getSUID()) == null) {
					
					Cy3DNodeView nodeView = new Cy3DNodeView(defaultValues, node);
					
					nodeViews.put(node.getSUID(), nodeView);
					
					nodeCountDifference--;
				}
			}
			
			// Did we fail to match every node with a node view?
			if (nodeCountDifference != 0) {
				
				// TODO: Use exception
				System.out.println("WindNetworkView.matchNodes(): node count mismatch by " + nodeCountDifference);
			}
		// Check if nodes have been removed from the network
		} else if (nodeCountDifference < 0) {
			long nodeIndex;
			HashSet<Long> toBeRemovedIndices = new HashSet<>();
			
			for (View<CyNode> nodeView : nodeViews.values()) {
				
				nodeIndex = nodeView.getModel().getSUID();
				
				// TODO: Currently performs check by checking if the view's node index is still valid
				if (network.getNode(nodeIndex) == null) {
					toBeRemovedIndices.add(nodeIndex);
				}
			}
			
			for (Long index : toBeRemovedIndices) {
				nodeViews.remove(index);
			}
		}
	}
	
	private void matchEdges() {
		int edgeCountDifference = network.getEdgeCount() - edgeViews.size();
		
		// Check if nodes have been added to the network
		if (edgeCountDifference > 0) {
			for (CyEdge edge : network.getEdgeList()) {
				
				// Found a edge without a view?
				if (edgeViews.get(edge.getSUID()) == null) {
					
					Cy3DEdgeView edgeView = new Cy3DEdgeView(defaultValues, edge);
					
					edgeViews.put(edge.getSUID(), edgeView);
					
					edgeCountDifference--;
				}
			}
			
			// Did we fail to match every edge with a edge view?
			if (edgeCountDifference != 0) {
				
				// TODO: Use exception
				System.out.println("WindNetworkView.matchEdges(): edge count mismatch by " + edgeCountDifference);
			}
		// Check if edges have been removed from the network
		} else if (edgeCountDifference < 0) {
			Long edgeIndex;
			HashSet<Long> toBeRemovedIndices = new HashSet<>();
			
			for (View<CyEdge> edgeView : edgeViews.values()) {
				
				edgeIndex = edgeView.getModel().getSUID();
				
				// TODO: Currently performs check by checking if the view's edge index is still valid
				if (network.getEdge(edgeIndex) == null) {
					toBeRemovedIndices.add(edgeIndex);
				}
			}
			
			for (Long index : toBeRemovedIndices) {
				edgeViews.remove(index);
			}
		}
	}

	private void updateToMatchVisualStyle() {
		
		// TODO: Make the set declared below a private member field, formalize the set of node or edge specific visual properties
		// that do not need to be matched with visual style changes, such as 3D position.
		
		// These visual properties are object-specific such as x, y, z coordinates
		// and do not need to be updated according to the visual style
		Set<VisualProperty<?>> exemptProperties = new HashSet<VisualProperty<?>>();
		exemptProperties.add(BasicVisualLexicon.NODE_X_LOCATION);
		exemptProperties.add(BasicVisualLexicon.NODE_Y_LOCATION);
		exemptProperties.add(BasicVisualLexicon.NODE_Z_LOCATION);
		
		// Update visual properties according to the current visual style
		VisualStyle visualStyle = visualMappingManager.getVisualStyle(this);
		
		for (View<? extends CyIdentifiable> view : getAllViews()) {
			for (VisualProperty<?> visualProperty : visualLexicon.getAllVisualProperties()) {
				if (view.getVisualProperty(visualProperty) != null 
						&& visualStyle.getDefaultValue(visualProperty) != null
						&& visualStyle.getVisualMappingFunction(visualProperty) == null
						&& !exemptProperties.contains(visualProperty)) {
					view.setVisualProperty(visualProperty, (Object) visualStyle.getDefaultValue(visualProperty));
				}
			}
		}
	}
	
	@Override
	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> visualProperty, V defaultValue) {
		
		defaultValues.modifyDefaultValue(visualProperty, defaultValue);
	}

	@Override
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		T value = super.getVisualProperty(visualProperty);
		
		if (value != null) {
			// If we were given an explicit value, return it
			return value;
		} else {
			// Otherwise, return the default value
			return defaultValues.getDefaultValue(visualProperty);
		}
	}


	public void setNetworkCamera(SimpleCamera networkCamera) {
		this.networkCamera = networkCamera;
	}

	public SimpleCamera getNetworkCamera() {
		return networkCamera;
	}

	public void setContainer(Component container) {
		this.container = container;
	}

	public Component getContainer() {
		return container;
	}
	
	/**
	 * Attempts to adjust the view to show all nodes by using the network camera.
	 */
	private void fitNodesInView() {
		if (networkCamera != null) {
			NetworkToolkit.fitInView(networkCamera, nodeViews.values(), 180.0, 1.9, 2.0);
			networkCentered = true;
		}
	}
	
	/**
	 * Requests focus for this network view so that it is ready to accept mouse and keyboard input.
	 */
	private void requestNetworkFocus() {
		if (container != null) {
			container.requestFocus();
		}
	}

	@Override
	public void dispose() {
		// MKTODO Auto-generated method stub
		
	}

	@Override
	public String getRendererId() {
		return Cy3DNetworkViewRenderer.ID;
	}


}
