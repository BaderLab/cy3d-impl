package org.cytoscape.paperwing.internal;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class WindVisualLexicon extends RichVisualLexicon {

	public static final VisualProperty<NullDataType> WIND_ROOT = new NullVisualProperty(
			"WIND_ROOT_VISUAL_PROPERTY",
			"Wind Rendering Engine Root Visual Property");
	
	public WindVisualLexicon() {
		super(WIND_ROOT);
	}

}
