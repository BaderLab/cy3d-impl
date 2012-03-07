package org.cytoscape.paperwing.internal.data;

import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.lighting.Light;

/**
 * This class is used to hold data relevant to lighting, such as the colors and states of lights.
 */
public class LightingData {
	
	/** The total number of lights. OpenGL specifies 8 as the minimum number of lights supported by hardware. */
	public static final int NUM_LIGHTS = 8;
	
	/** The array of lights present in the scene */
	private Light[] lights;
	
	/**
	 * An array of booleans that indicates whether a representation of a light should be drawn to show its position
	 */
	private boolean[] displayLight;
	
	/**
	 * Create a default {@link LightingData} object with default lights, all turned off.
	 */
	public LightingData() {
		lights = new Light[NUM_LIGHTS];
		displayLight = new boolean[NUM_LIGHTS];
		
		for (int i = 0; i < NUM_LIGHTS; i++) {
			lights[i] = new Light();
			displayLight[i] = false;
		}
	}
	
	/** 
	 * Obtain the light with the given index 
	 * 
	 * @param index The index of the desired light, ranging from 0 to LightingData.NUM_LIGHTS (default 8)
	 * @return The {@link Light} object representing the desired light.
	 */
	public Light getLight(int index) {
		return lights[index];
	}
	
	/**
	 * Obtain all the currently stored lights.
	 * 
	 * @return All the currently stored lights.
	 */
	public Light[] getLights() {
		return lights;
	}
	
	/**
	 * Check if a representation of the given light should be drawn to indicate its position.
	 * 
	 * @param index The index of the light, ranging from 0 to LightingData.NUM_LIGHTS (default 8)
	 * @return Whether or not the light at the given index should be drawn.
	 */
	public boolean displayLight(int index) {
		return displayLight[index];
	}
	
}
