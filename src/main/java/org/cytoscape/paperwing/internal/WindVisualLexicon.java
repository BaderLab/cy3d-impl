package org.cytoscape.paperwing.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

/** The visual lexicon for the Wind rendering engines */
public class WindVisualLexicon extends RichVisualLexicon {

	/** The root visual property */
	public static final VisualProperty<NullDataType> WIND_ROOT = new NullVisualProperty(
			"WIND_ROOT_VISUAL_PROPERTY",
			"Wind Rendering Engine Root Visual Property");
	
	public static final VisualProperty<Double> TEST_PROPERTY = new DoubleVisualProperty(
			1.0, ARBITRARY_DOUBLE_RANGE, "TEST_PROPERTY", "Wind Test Property", CyNode.class);
	
	/** Create a new WindVisualLexicon object */
	public WindVisualLexicon() {
		super(WIND_ROOT);
		
		addVisualProperty(TEST_PROPERTY, NODE);
		
		addIdentifierMapping(CyNode.class, "testProperty", TEST_PROPERTY);
		
		VisualProperty visualProperty = this.lookup(Double.class, "test");
		
		
	}

}
