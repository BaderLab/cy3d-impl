package org.baderlab.cy3d.internal.lighting;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.data.LightingData;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * An interface providing functionality that is capable of setting up and maintaining lighting for a given {@link RenderingEngine} object.
 */
public interface LightingProcessor {
	
	/**
	 * Perform initial lighting setup.
	 * 
	 * @param gl The OpenGL context used to perform the setup.
	 * @param lightingData The {@link LightingData} object containing the current lighting information.
	 */
	public void setupLighting(GL2 gl, LightingData lightingData);
	
	/**
	 * Updates the current lighting, if necessary. This may include changing the positions of lights or turning off some lights.
	 *
	 * @param gl The OpenGL context used to update the lights.
	 * @param lightingData The {@link LightingData} object containing the current lighting information.
	 */
	public void updateLighting(GL2 gl, LightingData lightingData);
}
