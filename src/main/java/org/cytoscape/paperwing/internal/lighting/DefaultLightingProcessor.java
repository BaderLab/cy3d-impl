package org.cytoscape.paperwing.internal.lighting;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.data.LightingData;

public class DefaultLightingProcessor implements LightingProcessor {

	@Override
	public void setupLighting(GL2 gl, LightingData lightingData) {
		
		// Initialize light properties
		Light light0 = lightingData.getLight(0);
		light0.setAmbient(0.4f, 0.4f, 0.4f, 1.0f);
		light0.setDiffuse(0.57f, 0.57f, 0.57f, 1.0f);
		light0.setSpecular(0.79f, 0.79f, 0.79f, 1.0f);
		light0.setPosition(-4.0f, 4.0f, 6.0f, 1.0f);
		light0.setTurnedOn(true);
	}

	@Override
	public void updateLighting(GL2 gl, LightingData lightingData) {
		
		// Loop through all current lights
		for (int i = 0; i < LightingData.NUM_LIGHTS; i++) {
			Light light = lightingData.getLight(i);
		
			if (light.isTurnedOn()) {
				
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(light.getAmbient()));
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(light.getDiffuse()));
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(light.getSpecular()));
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(light.getPosition()));
	

				// We have that GL_LIGHTi = GL_LIGHT0 + i
				if (!gl.glIsEnabled(GL2.GL_LIGHT0 + i)) {
					gl.glEnable(GL2.GL_LIGHT0 + i);
				}
			} else {
				if (gl.glIsEnabled(GL2.GL_LIGHT0 + i)) {
					gl.glDisable(GL2.GL_LIGHT0 + i);
				}
			}
		}
	}

}
