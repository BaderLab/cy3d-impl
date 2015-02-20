package org.baderlab.cy3d.internal.cytoscape.view;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NullVisualProperty;

public class Cy3DVisualLexicon extends BasicVisualLexicon {

	
	/** The root visual property */
	public static final VisualProperty<NullDataType> ROOT = new NullVisualProperty( "CY3D_ROOT", "cy3d Rendering Engine Root Visual Property");
	
	public Cy3DVisualLexicon() {
		super(ROOT);
	}

}
