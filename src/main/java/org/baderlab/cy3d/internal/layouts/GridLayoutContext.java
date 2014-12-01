package org.baderlab.cy3d.internal.layouts;

import org.cytoscape.work.TunableValidator;

public class GridLayoutContext implements TunableValidator {
	

	@Override //TODO how to validate these values?
	public ValidationState getValidationState(final Appendable errMsg) {
		return ValidationState.OK;
	}
}
