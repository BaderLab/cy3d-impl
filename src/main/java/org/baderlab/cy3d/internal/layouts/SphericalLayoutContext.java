package org.baderlab.cy3d.internal.layouts;

import java.util.Set;

import org.cytoscape.view.layout.AbstractLayoutAlgorithmContext;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class SphericalLayoutContext extends AbstractLayoutAlgorithmContext implements TunableValidator {
	
	public SphericalLayoutContext(boolean supportsSelectedOnly, Set<Class<?>> supportedNodeAttributes, Set<Class<?>> supportedEdgeAttributes) {
		super(supportsSelectedOnly, supportedNodeAttributes, supportedEdgeAttributes);
	}

	@Override //TODO how to validate these values?
	public ValidationState getValidationState(final Appendable errMsg) {
		return ValidationState.OK;
	}
}
