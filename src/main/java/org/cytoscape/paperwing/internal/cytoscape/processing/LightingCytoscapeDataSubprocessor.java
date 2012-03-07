package org.cytoscape.paperwing.internal.cytoscape.processing;

import java.awt.Color;

import org.cytoscape.paperwing.internal.WindVisualLexicon;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.LightingData;
import org.cytoscape.paperwing.internal.lighting.Light;
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
		Color ambient = (Color) networkView.getVisualProperty(WindVisualLexicon.LIGHT_AMBIENT_COLOR);
		double ambientAlpha = networkView.getVisualProperty(WindVisualLexicon.LIGHT_AMBIENT_ALPHA);
		
		Color diffuse = (Color) networkView.getVisualProperty(WindVisualLexicon.LIGHT_DIFFUSE_COLOR);
		double diffuseAlpha = networkView.getVisualProperty(WindVisualLexicon.LIGHT_DIFFUSE_ALPHA);
		
		Color specular = (Color) networkView.getVisualProperty(WindVisualLexicon.LIGHT_SPECULAR_COLOR);
		double specularAlpha = networkView.getVisualProperty(WindVisualLexicon.LIGHT_SPECULAR_ALPHA);
		
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
		light.setPosition(networkView.getVisualProperty(WindVisualLexicon.LIGHT_X_LOCATION).floatValue(), 
				networkView.getVisualProperty(WindVisualLexicon.LIGHT_Y_LOCATION).floatValue(), 
				networkView.getVisualProperty(WindVisualLexicon.LIGHT_Z_LOCATION).floatValue(), 
				1.0f);
		
		// Transfer remaining properties
		light.setTurnedOn(networkView.getVisualProperty(WindVisualLexicon.LIGHT_ENABLED));
	}
}
