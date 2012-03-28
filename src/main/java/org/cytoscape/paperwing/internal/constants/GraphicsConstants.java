package org.cytoscape.paperwing.internal.constants;

public class GraphicsConstants {
	
	/** A value representing that no valid display list is being referenced. */
	public static final int EMPTY_DISPLAY_LIST = -1;
	
	/** String id representing the renderer. */
	public static final String RENDERING_ENGINE_ID = "wind";
	
	/** 
	 * The default scaling used to convert Cytoscape coordinates to renderer coordinates. Cytoscape coordinates
	 * are divided by this value to obtain renderer coordinates.
	 */
	public static final float DEFAULT_DISTANCE_SCALE = 180.0f;
}
