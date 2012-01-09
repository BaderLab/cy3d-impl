package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class WindNetworkView extends VisualPropertyKeeper<CyNetwork> 
		implements CyNetworkView {

	@Override
	public CyNetwork getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSUID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View<CyNode> getNodeView(CyNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<View<CyNode>> getNodeViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View<CyEdge> getEdgeView(CyEdge edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<View<CyEdge>> getEdgeViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<View<? extends CyTableEntry>> getAllViews() {
		// TODO Auto-generated method stub
		return null;
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
	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> vp,
			V defaultValue) {
		// TODO Auto-generated method stub
		
	}

}
