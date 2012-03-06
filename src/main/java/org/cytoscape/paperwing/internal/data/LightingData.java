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
	
	/** The previous projected position of lights used for drag-moving lights */
	private Vector3[] lightPreviousProjections;
	
	/** The current projected position of lights used for drag-moving lights */
	private Vector3[] lightCurrentProjections;

	/** The projected distance of a light 
	
	/**
	 * Create a default {@link LightingData} object with default lights, all turned off.
	 */
	public LightingData() {
		lights = new Light[NUM_LIGHTS];
		
		lightPreviousProjections = new Vector3[NUM_LIGHTS];
		lightCurrentProjections = new Vector3[NUM_LIGHTS];
		
		for (int i = 0; i < NUM_LIGHTS; i++) {
			lights[i] = new Light();
			
			lightPreviousProjections[i] = new Vector3();
			lightCurrentProjections[i] = new Vector3();
		}
		
	}
	
	/** 
	 * Obtain the light with the given index 
	 * 
	 * @param index The index of the desired light, ranging from 0 to LightingData.NUM_LIGHTS (default 8)
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
	
	
	
	
}
