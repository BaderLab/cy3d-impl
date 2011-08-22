package org.cytoscape.paperwing.internal;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

/** The visual lexicon for the Wind rendering engines */
public class WindVisualLexicon extends RichVisualLexicon {

	/** The root visual property */
	public static final VisualProperty<NullDataType> WIND_ROOT = new NullVisualProperty(
			"WIND_ROOT_VISUAL_PROPERTY",
			"Wind Rendering Engine Root Visual Property");
	
	/** Create a new WindVisualLexicon object */
	public WindVisualLexicon() {
		super(WIND_ROOT);
	}

}
