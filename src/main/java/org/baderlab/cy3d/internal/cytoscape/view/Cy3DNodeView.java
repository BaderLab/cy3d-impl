package org.baderlab.cy3d.internal.cytoscape.view;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class Cy3DNodeView extends VisualPropertyKeeper<CyNode> {

	private CyNode node;
	private Long suid;
	private DefaultValueVault defaultValueVault;
	
	public Cy3DNodeView(DefaultValueVault defaultValueVault, CyNode node) {
		this.node = node;
		this.suid = SUIDFactory.getNextSUID();
		this.defaultValueVault = defaultValueVault;
	}
	
	@Override
	public Long getSUID() {
		return suid;
	}

	@Override
	public CyNode getModel() {
		return node;
	}

	@Override
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		T value = super.getVisualProperty(visualProperty);
		
		if(value == null) {
			value = defaultValueVault.getDefaultValue(visualProperty);
		}
		
		/*
		 * Its very common for nodes in 2D networks to have a zero value for BasicVisualLexicon.NODE_DEPTH.
		 * This makes the nodes look flat when rendered in 3D and causes problems
		 * with layout algorithms. The cheap solution is to override the depth to be equal to width
		 * when the depth value is zero.
		 */
		if(visualProperty.equals(BasicVisualLexicon.NODE_DEPTH) && ((Double)value).doubleValue() == 0.0) {
			return (T) getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
		}
		
		return value;
	}
	
}
