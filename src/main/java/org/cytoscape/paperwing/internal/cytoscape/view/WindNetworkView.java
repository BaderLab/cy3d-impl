package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.events.AboutToRemoveEdgesEvent;
import org.cytoscape.model.events.AboutToRemoveEdgesListener;
import org.cytoscape.model.events.AboutToRemoveNodesEvent;
import org.cytoscape.model.events.AboutToRemoveNodesListener;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class WindNetworkView extends VisualPropertyKeeper<CyNetwork> implements CyNetworkView {

	private Long suid;
	
	private CyNetwork network;
	
	private VisualLexicon visualLexicon;
	private DefaultValueVault defaultValues;
	
	// Assumes indices of nodes are unique
	private Map<Integer, View<CyNode>> nodeViews;
	private Map<Integer, View<CyEdge>> edgeViews;
	
	public WindNetworkView(CyNetwork network, VisualLexicon visualLexicon) {
		suid = SUIDFactory.getNextSUID();
		
		this.network = network;
		this.visualLexicon = visualLexicon;
		
		defaultValues = new DefaultValueVault(visualLexicon);
		nodeViews = new HashMap<Integer, View<CyNode>>();
		edgeViews = new HashMap<Integer, View<CyEdge>>();
		
		WindNodeView nodeView;
		for (CyNode node : network.getNodeList()) {
			nodeView = new WindNodeView(node, SUIDFactory.getNextSUID());
			defaultValues.initializeNode(nodeView);
			
			nodeViews.put(node.getIndex(), nodeView);
		}
		
		WindEdgeView edgeView;
		for (CyEdge edge : network.getEdgeList()) {
			edgeView = new WindEdgeView(edge, SUIDFactory.getNextSUID());
			defaultValues.initializeEdge(edgeView);
			
			edgeViews.put(edge.getIndex(), edgeView);
		}
		
		defaultValues.initializeNetwork(this);
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
		return nodeViews.get(node.getIndex());
	}

	@Override
	public Collection<View<CyNode>> getNodeViews() {
		return nodeViews.values();
	}

	@Override
	public View<CyEdge> getEdgeView(CyEdge edge) {
		return edgeViews.get(edge.getIndex());
	}

	@Override
	public Collection<View<CyEdge>> getEdgeViews() {
		return edgeViews.values();
	}

	@Override
	public Collection<View<? extends CyTableEntry>> getAllViews() {
		Collection<View<? extends CyTableEntry>> views = new HashSet<View<? extends CyTableEntry>>();
		
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
		Vector3 networkCenter = NetworkToolkit.findNetworkCenter(this, 1);
		
		// Shift the nodes to place the center of the network at the origin
		for (View<CyNode> nodeView : getNodeViews()) {
			nodeView.setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, 
					nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION) - networkCenter.x());
			nodeView.setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, 
					nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION) - networkCenter.y());
			nodeView.setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, 
					nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION) - networkCenter.z());
		}
	}

	@Override
	public void fitSelected() {
		// Obtain selected nodes
		Set<Integer> selectedNodeIndices = new HashSet<Integer>();
		
		for (View<CyNode> nodeView : getNodeViews()) {
			if (nodeView.getVisualProperty(RichVisualLexicon.NODE_SELECTED)) {
				selectedNodeIndices.add(nodeView.getModel().getIndex());
			}
		}
		
		Vector3 selectionCenter = NetworkToolkit.findCenter(selectedNodeIndices, this, 1);
	
		// Shift the nodes to place the center of the network at the center of the selected group of nodes
		for (View<CyNode> nodeView : getNodeViews()) {
			nodeView.setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, 
					nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION) - selectionCenter.x());
			nodeView.setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, 
					nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION) - selectionCenter.y());
			nodeView.setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, 
					nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION) - selectionCenter.z());
		}
	}

	@Override
	public void updateView() {
		// TODO: Check if correct place to put this; below code should ensure having a view
		// for every node/edge in the network
		
		matchNodes();
		matchEdges();
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
				if (nodeViews.get(node.getIndex()) == null) {
					
					WindNodeView nodeView = new WindNodeView(node, SUIDFactory.getNextSUID());
					defaultValues.initializeNode(nodeView);
					
					nodeViews.put(node.getIndex(), nodeView);
					
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
			int nodeIndex;
			HashSet<Integer> toBeRemovedIndices = new HashSet<Integer>();
			
			for (View<CyNode> nodeView : nodeViews.values()) {
				
				nodeIndex = nodeView.getModel().getIndex();
				
				// TODO: Currently performs check by checking if the view's node index is still valid
				if (network.getNode(nodeIndex) == null) {
					toBeRemovedIndices.add(nodeIndex);
				}
			}
			
			for (int index : toBeRemovedIndices) {
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
				if (edgeViews.get(edge.getIndex()) == null) {
					
					WindEdgeView edgeView = new WindEdgeView(edge, SUIDFactory.getNextSUID());
					defaultValues.initializeEdge(edgeView);
					
					edgeViews.put(edge.getIndex(), edgeView);
					
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
			int edgeIndex;
			HashSet<Integer> toBeRemovedIndices = new HashSet<Integer>();
			
			for (View<CyEdge> edgeView : edgeViews.values()) {
				
				edgeIndex = edgeView.getModel().getIndex();
				
				// TODO: Currently performs check by checking if the view's edge index is still valid
				if (network.getEdge(edgeIndex) == null) {
					toBeRemovedIndices.add(edgeIndex);
				}
			}
			
			for (int index : toBeRemovedIndices) {
				edgeViews.remove(index);
			}
		}
	}

	@Override
	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> visualProperty,
			V defaultValue) {
		
		defaultValues.modifyDefaultValue(visualProperty, defaultValue);
	}

//	@Override
//	public void handleEvent(AboutToRemoveNodesEvent e) {
//		if (e.getSource() == network) {
//			for (CyNode node : e.getNodes()) {
//				nodeViews.remove(node.getIndex());
//			}
//		}
//	}
//	
//	@Override
//	public void handleEvent(AboutToRemoveEdgesEvent e) {
//		if (e.getSource() == network) {
//			for (CyEdge edge : e.getEdges()) {
//				edgeViews.remove(edge.getIndex());
//			}
//		}
//	}
//
//	@Override
//	public void handleEvent(AddedNodesEvent e) {
//		if (e.getSource() == network) {
//			WindNodeView nodeView;
//			
//			for (CyNode node : e.getPayloadCollection()) {
//				nodeView = new WindNodeView(node, SUIDFactory.getNextSUID());
//				defaultValues.initializeNode(nodeView);
//				
//				nodeViews.put(node.getIndex(), nodeView);
//			}
//		}
//	}
//
//	@Override
//	public void handleEvent(AddedEdgesEvent e) {
//		if (e.getSource() == network) {
//			WindEdgeView edgeView;
//			
//			for (CyEdge edge : e.getPayloadCollection()) {
//				edgeView = new WindEdgeView(edge, SUIDFactory.getNextSUID());
//				defaultValues.initializeEdge(edgeView);
//				
//				edgeViews.put(edge.getIndex(), edgeView);
//			}
//		}
//	}

	

}
