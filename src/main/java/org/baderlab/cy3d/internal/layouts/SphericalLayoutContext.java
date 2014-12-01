package org.baderlab.cy3d.internal.layouts;

import org.cytoscape.work.TunableValidator;

public class SphericalLayoutContext implements TunableValidator {
	

	@Override //TODO how to validate these values?
	public ValidationState getValidationState(final Appendable errMsg) {
		return ValidationState.OK;
	}
}
