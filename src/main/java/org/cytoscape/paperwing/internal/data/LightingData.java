package org.cytoscape.paperwing.internal.data;

import org.cytoscape.paperwing.internal.lighting.Light;

/**
 * This class is used to hold data relevant to lighting, such as the colors and states of lights.
 */
public class LightingData {
	
	/** The total number of lights. OpenGL specifies 8 as the minimum number of lights supported by hardware. */
	private static int NUM_LIGHTS = 8;
	
	/** The array of lights present in the scene */
	private Light[] lights;
	
	/** Obtain the light with the given index */
	public Light getLight(int index) {
		return lights[index];
	}
	
	/**
	 * Create a default {@link LightingData} object with default lights, all turned off.
	 */
	public LightingData() {
		lights = new Light[NUM_LIGHTS];
		for (int i = 0; i < NUM_LIGHTS; i++) {
			lights[i] = new Light();
		}
	}
}
