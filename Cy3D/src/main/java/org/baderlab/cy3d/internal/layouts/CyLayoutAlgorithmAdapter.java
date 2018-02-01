package org.baderlab.cy3d.internal.layouts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableSetter;

/**
 * Wrapper for cytoscape layout algorithms that support 3D, activates the tunable
 * property that puts the layout into 3D mode.
 * 
 * @author mkucera
 */
public class CyLayoutAlgorithmAdapter implements CyLayoutAlgorithm {
	
	private static final String LAYOUT_3D_PROPERTY = "layout3D";
	
	private final String computerName;
	private final String humanName;
	private final CyLayoutAlgorithm delegate;
	private final TunableSetter tunableSetter;
	
	private Object defaultContext;
	
	
	public CyLayoutAlgorithmAdapter(CyLayoutAlgorithm delegate, TunableSetter tunableSetter, String computerName, String humanName) {
		this.tunableSetter = tunableSetter;
		this.delegate = delegate;
		this.computerName = computerName;
		this.humanName = humanName;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object layoutContext, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return delegate.createTaskIterator(networkView, layoutContext, nodesToLayOut, layoutAttribute);
	}

	@Override
	public boolean isReady(CyNetworkView networkView, Object layoutContext, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return delegate.isReady(networkView, layoutContext, nodesToLayOut, layoutAttribute);
	}

	@Override
	public Object createLayoutContext() {
		Object context = delegate.createLayoutContext();
		Map<String,Object> tunableValues = new HashMap<>();
		tunableValues.put(LAYOUT_3D_PROPERTY, true);
		tunableSetter.applyTunables(context, tunableValues);
		return context;
	}

	@Override
	public Object getDefaultLayoutContext() {
		if (defaultContext == null)
			defaultContext = createLayoutContext();
		return defaultContext;
	}

	@Override
	public Set<Class<?>> getSupportedNodeAttributeTypes() {
		return delegate.getSupportedNodeAttributeTypes();
	}

	@Override
	public Set<Class<?>> getSupportedEdgeAttributeTypes() {
		return delegate.getSupportedEdgeAttributeTypes();
	}

	@Override
	public boolean getSupportsSelectedOnly() {
		return delegate.getSupportsSelectedOnly();
	}

	/**
	 * Overrides the display string of the delegate.
	 */
	public String toString() {
		return humanName;
	}
	
	/**
	 * Overrides the ID of the delegate;
	 */
	@Override
	public String getName() {
		return computerName;
	}
	

}
