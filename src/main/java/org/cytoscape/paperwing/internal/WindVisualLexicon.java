package org.cytoscape.paperwing.internal;

import java.awt.Color;
import java.awt.Paint;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.ContinuousRange;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.Range;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BooleanVisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.PaintVisualProperty;

/** The visual lexicon for the Wind rendering engines */
public class WindVisualLexicon extends BasicVisualLexicon {

	/** A range from 0.0 to 1.0; useful for values such as color */
	protected static final Range<Double> ZERO_TO_ONE_DOUBLE_RANGE = new ContinuousRange<Double>(
			Double.class, 0.0, 1.0, true, true);
	
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
	
	// Light visual properties
	// =======================
	
	public static final VisualProperty<Double> LIGHT_X_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "LIGHT_X_LOCATION", "Light X Location", CyNetwork.class);
	
	public static final VisualProperty<Double> LIGHT_Y_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "LIGHT_Y_LOCATION", "Light Y Location", CyNetwork.class);
	
	public static final VisualProperty<Double> LIGHT_Z_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "LIGHT_Z_LOCATION", "Light Z Location", CyNetwork.class);
	
	public static final VisualProperty<Boolean> LIGHT_ENABLED = new BooleanVisualProperty(
			true, "LIGHT_ENABLED", "Light Enabled", CyNetwork.class);
	
	public static final VisualProperty<Paint> LIGHT_AMBIENT_COLOR = new PaintVisualProperty(
			new Color(255, 255, 255), PAINT_RANGE, "LIGHT_AMBIENT_COLOR", "Light Ambient Color", CyNetwork.class);

	public static final VisualProperty<Double> LIGHT_AMBIENT_ALPHA = new DoubleVisualProperty(
			0.0, ZERO_TO_ONE_DOUBLE_RANGE, "LIGHT_AMBIENT_ALPHA", "Light Ambient Alpha", CyNetwork.class);
	
	public static final VisualProperty<Paint> LIGHT_DIFFUSE_COLOR = new PaintVisualProperty(
			new Color(255, 255, 255), PAINT_RANGE, "LIGHT_DIFFUSE_COLOR", "Light Diffuse Color", CyNetwork.class);

	public static final VisualProperty<Double> LIGHT_DIFFUSE_ALPHA = new DoubleVisualProperty(
			0.0, ZERO_TO_ONE_DOUBLE_RANGE, "LIGHT_DIFFUSE_ALPHA", "Light Diffuse Alpha", CyNetwork.class);
	
	public static final VisualProperty<Paint> LIGHT_SPECULAR_COLOR = new PaintVisualProperty(
			new Color(255, 255, 255), PAINT_RANGE, "LIGHT_SPECULAR_COLOR", "Light Specular Color", CyNetwork.class);

	public static final VisualProperty<Double> LIGHT_SPECULAR_ALPHA = new DoubleVisualProperty(
			0.0, ZERO_TO_ONE_DOUBLE_RANGE, "LIGHT_SPECULAR_ALPHA", "Light Specular Alpha", CyNetwork.class);
	
	
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
		
		// Add light-related visual properties
		addVisualProperty(LIGHT_X_LOCATION, NETWORK);
		addVisualProperty(LIGHT_Y_LOCATION, NETWORK);
		addVisualProperty(LIGHT_Z_LOCATION, NETWORK);
		addVisualProperty(LIGHT_ENABLED, NETWORK);
		
		addVisualProperty(LIGHT_AMBIENT_COLOR, NETWORK);
		addVisualProperty(LIGHT_AMBIENT_ALPHA, NETWORK);
		addVisualProperty(LIGHT_DIFFUSE_COLOR, NETWORK);
		addVisualProperty(LIGHT_DIFFUSE_ALPHA, NETWORK);
		addVisualProperty(LIGHT_SPECULAR_COLOR, NETWORK);
		addVisualProperty(LIGHT_SPECULAR_ALPHA, NETWORK);
		
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
		
		// Add lighting-related lookup maps
		addIdentifierMapping(CyNetwork.class, "lightX", LIGHT_X_LOCATION);
		addIdentifierMapping(CyNetwork.class, "lightY", LIGHT_Y_LOCATION);
		addIdentifierMapping(CyNetwork.class, "lightZ", LIGHT_Z_LOCATION);
		addIdentifierMapping(CyNetwork.class, "lightEnabled", LIGHT_ENABLED);
		
		addIdentifierMapping(CyNetwork.class, "lightAmbientColor", LIGHT_AMBIENT_COLOR);
		addIdentifierMapping(CyNetwork.class, "lightAmbientAlpha", LIGHT_AMBIENT_ALPHA);
		addIdentifierMapping(CyNetwork.class, "lightDiffuseColor", LIGHT_DIFFUSE_COLOR);
		addIdentifierMapping(CyNetwork.class, "lightDiffuseAlpha", LIGHT_DIFFUSE_ALPHA);
		addIdentifierMapping(CyNetwork.class, "lightSpecularColor", LIGHT_SPECULAR_COLOR);
		addIdentifierMapping(CyNetwork.class, "lightSpecularAlpha", LIGHT_SPECULAR_ALPHA);
	}
}
