package org.cytoscape.paperwing.internal.layouts;

import java.util.Set;

import org.cytoscape.view.layout.AbstractLayoutAlgorithmContext;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class GridLayoutContext extends AbstractLayoutAlgorithmContext implements TunableValidator {
	
	public GridLayoutContext(boolean supportsSelectedOnly, Set<Class<?>> supportedNodeAttributes, Set<Class<?>> supportedEdgeAttributes) {
		super(supportsSelectedOnly, supportedNodeAttributes, supportedEdgeAttributes);
	}

	@Override //TODO how to validate these values?
	public ValidationState getValidationState(final Appendable errMsg) {
		return ValidationState.OK;
	}
}
