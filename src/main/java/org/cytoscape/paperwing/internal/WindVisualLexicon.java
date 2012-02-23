package org.cytoscape.paperwing.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/** The visual lexicon for the Wind rendering engines */
public class WindVisualLexicon extends BasicVisualLexicon {

	/** The root visual property */
	public static final VisualProperty<NullDataType> WIND_ROOT = new NullVisualProperty(
			"WIND_ROOT_VISUAL_PROPERTY",
			"Wind Rendering Engine Root Visual Property");
	
	public static final VisualProperty<Double> TEST_PROPERTY = new DoubleVisualProperty(
			1.0, ARBITRARY_DOUBLE_RANGE, "TEST_PROPERTY", "Wind Test Property", CyNode.class);
	
	public static final VisualProperty<Double> NODE_PITCH = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NODE_PITCH", "Node Model Pitch", CyNode.class);
	
	public static final VisualProperty<Double> NODE_YAW = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NODE_YAW", "Node Model Yaw", CyNode.class);
	
	public static final VisualProperty<Double> NODE_ROLL = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NODE_ROLL", "Node Model Roll", CyNode.class);
	
	
	/** Create a new WindVisualLexicon object */
	public WindVisualLexicon() {
		super(WIND_ROOT);
		
		addVisualProperty(TEST_PROPERTY, NODE);
		
		addIdentifierMapping(CyNode.class, "testProperty", TEST_PROPERTY);
		
		addIdentifierMapping(CyNode.class, "nodePitch", NODE_PITCH);
		addIdentifierMapping(CyNode.class, "nodeYaw", NODE_YAW);
		addIdentifierMapping(CyNode.class, "nodeRoll", NODE_ROLL);
		
		// VisualProperty visualProperty = this.lookup(Double.class, "test");
		
		
	}

}
