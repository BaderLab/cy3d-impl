package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

public class WindNetworkView extends VisualPropertyKeeper<CyNetwork> 
		implements CyNetworkView {

	private long suid;
	
	private CyNetwork network;
	
	private VisualLexicon visualLexicon;
	private DefaultValueVault defaultValues;
	
	// Assumes indices of nodes are unique
	private HashMap<Integer, View<CyNode>> nodeViews;
	private HashMap<Integer, View<CyEdge>> edgeViews;
	
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
		}
		
		WindEdgeView edgeView;
		for (CyEdge edge : network.getEdgeList()) {
			edgeView = new WindEdgeView(edge, SUIDFactory.getNextSUID());
			defaultValues.initializeEdge(edgeView);
		}
		
		defaultValues.initializeNetwork(this);
	}
	
	@Override
	public CyNetwork getModel() {
		return network;
	}

	@Override
	public long getSUID() {
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
		Collection<View<? extends CyTableEntry>> views = new HashSet();
		
		// Return views for Node, Edge, Network
		views.addAll(getNodeViews());
		views.addAll(getEdgeViews());
		views.add(this);
		
		return views;
	}

	@Override
	public void fitContent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fitSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> visualProperty,
			V defaultValue) {
		
		defaultValues.modifyDefaultValue(visualProperty, defaultValue);
	}

}
