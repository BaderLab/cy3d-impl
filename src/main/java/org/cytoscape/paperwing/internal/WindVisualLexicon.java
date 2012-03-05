package org.cytoscape.paperwing.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BooleanVisualProperty;
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
	
	public static final VisualProperty<Double> CAMERA_X_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "CAMERA_X_LOCATION", "Camera X Location", CyNetwork.class);
	
	public static final VisualProperty<Double> CAMERA_Y_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "CAMERA_Y_LOCATION", "Camera Y Location", CyNetwork.class);
	
	public static final VisualProperty<Double> CAMERA_Z_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "CAMERA_Z_LOCATION", "Camera Z Location", CyNetwork.class);
	
	public static final VisualProperty<Double> CAMERA_PITCH_ANGLE = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "CAMERA_PITCH_ANGLE", "Camera Pitch Angle", CyNetwork.class);
	
	public static final VisualProperty<Double> CAMERA_YAW_ANGLE = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "CAMERA_YAW_ANGLE", "Camera Yaw Angle", CyNetwork.class);
	
	public static final VisualProperty<Double> CAMERA_ROLL_ANGLE = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "CAMERA_ROLL_ANGLE", "Camera Roll Angle", CyNetwork.class);
	
	public static final VisualProperty<Boolean> SHOW_NODE_LABELS = new BooleanVisualProperty(
			true, "SHOW_NODE_LABELS", "Show Node Labels", CyNetwork.class);
	
	public static final VisualProperty<Boolean> SHOW_EDGE_LABELS = new BooleanVisualProperty(
			true, "SHOW_EDGE_LABELS", "Show Edge Labels", CyNetwork.class);
	
	public static final VisualProperty<Double> LIGHT_X_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "LIGHT_X_LOCATION", "Light X Location", CyNetwork.class);
	
	public static final VisualProperty<Double> LIGHT_Y_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "LIGHT_Y_LOCATION", "Light Y Location", CyNetwork.class);
	
	public static final VisualProperty<Double> LIGHT_Z_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "LIGHT_Z_LOCATION", "Light Z Location", CyNetwork.class);
	
	public static final VisualProperty<Boolean> LIGHT_ENABLED = new BooleanVisualProperty(
			true, "LIGHT_ENABLED", "Light Enablede", CyNetwork.class);
	
	/** Create a new WindVisualLexicon object */
	public WindVisualLexicon() {
		super(WIND_ROOT);
		
		addVisualProperty(CAMERA_X_LOCATION, NETWORK);
		addVisualProperty(CAMERA_Y_LOCATION, NETWORK);
		addVisualProperty(CAMERA_Z_LOCATION, NETWORK);
		
		addVisualProperty(CAMERA_PITCH_ANGLE, NETWORK);
		addVisualProperty(CAMERA_YAW_ANGLE, NETWORK);
		addVisualProperty(CAMERA_ROLL_ANGLE, NETWORK);
		
		addVisualProperty(SHOW_NODE_LABELS, NETWORK);
		addVisualProperty(SHOW_EDGE_LABELS, NETWORK);
		
		createLookupMap();
	}
	
	private void createLookupMap() {
		addIdentifierMapping(CyNode.class, "nodePitch", NODE_PITCH);
		addIdentifierMapping(CyNode.class, "nodeYaw", NODE_YAW);
		addIdentifierMapping(CyNode.class, "nodeRoll", NODE_ROLL);
		
		addIdentifierMapping(CyNetwork.class, "cameraX", CAMERA_X_LOCATION);
		addIdentifierMapping(CyNetwork.class, "cameraY", CAMERA_Y_LOCATION);
		addIdentifierMapping(CyNetwork.class, "cameraZ", CAMERA_Z_LOCATION);
		
		addIdentifierMapping(CyNetwork.class, "cameraPitch", CAMERA_PITCH_ANGLE);
		addIdentifierMapping(CyNetwork.class, "cameraYaw", CAMERA_YAW_ANGLE);
		addIdentifierMapping(CyNetwork.class, "cameraRoll", CAMERA_ROLL_ANGLE);
		
		addIdentifierMapping(CyNetwork.class, "showNodeLabels", SHOW_NODE_LABELS);
		addIdentifierMapping(CyNetwork.class, "showEdgeLabels", SHOW_EDGE_LABELS);
	}
}
