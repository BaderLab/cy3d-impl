package org.baderlab.cy3d.internal.cytoscape.processing;

import java.awt.Color;

import org.baderlab.cy3d.internal.Cy3DVisualLexicon;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.LightingData;
import org.baderlab.cy3d.internal.lighting.Light;
import org.cytoscape.view.model.CyNetworkView;

/**
 * This {@link CytoscapeDataSubprocessor} is used to obtain updated lighting data from the network view's 
 * visual property set.
 */
public class LightingCytoscapeDataSubprocessor implements CytoscapeDataSubprocessor {

	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		updateLightingData(graphicsData);
	}
	
	/**
	 * Transfer lighting data from the visual property set
	 */
	private void updateLightingData(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		LightingData lightingData = graphicsData.getLightingData();
		
		// Try to get it working with a single light first (index 0)
		Light light = lightingData.getLight(0);
		
		// Transfer color properties
		Color ambient = (Color) networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_AMBIENT_COLOR);
		double ambientAlpha = networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_AMBIENT_ALPHA);
		
		Color diffuse = (Color) networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_DIFFUSE_COLOR);
		double diffuseAlpha = networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_DIFFUSE_ALPHA);
		
		Color specular = (Color) networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_SPECULAR_COLOR);
		double specularAlpha = networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_SPECULAR_ALPHA);
		
		light.setAmbient((float) ambient.getRed() / 255, 
				(float) ambient.getGreen() / 255, 
				(float) ambient.getBlue() / 255, 
				(float) ambientAlpha);
		
		light.setDiffuse((float) diffuse.getRed() / 255, 
				(float) diffuse.getGreen() / 255, 
				(float) diffuse.getBlue() / 255, 
				(float) diffuseAlpha);
		
		light.setSpecular((float) specular.getRed() / 255, 
				(float) specular.getGreen() / 255, 
				(float) specular.getBlue() / 255, 
				(float) specularAlpha);
		
		// Transfer position properties
		light.setPosition(networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_X_LOCATION).floatValue() / GraphicsData.DISTANCE_SCALE,
				networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_Y_LOCATION).floatValue() / GraphicsData.DISTANCE_SCALE, 
				networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_Z_LOCATION).floatValue() / GraphicsData.DISTANCE_SCALE, 
				1.0f);
		
		// Transfer remaining properties
		light.setTurnedOn(networkView.getVisualProperty(Cy3DVisualLexicon.LIGHT_ENABLED));
	}
}
