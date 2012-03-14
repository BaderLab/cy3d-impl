package org.cytoscape.paperwing.internal.lighting;

import org.cytoscape.paperwing.internal.geometric.Vector3;

/**
 * This class represents a light described by its position and its ambient, diffuse, and specular light components.
 */
public class Light {

	/** Default value for a color */
	private static float DEFAULT_COLOR_VALUE = 1.0f;
	
	/** Default alpha value for light colors */
	private static float DEFAULT_ALPHA_VALUE = 1.0f;
	
	/** An array containing the color of the ambient component of the light in RGBA format */
	private float[] ambient;
	
	/** An array containing the color of the diffuse component of the light in RGBA format */
	private float[] diffuse;
	
	/** An array containing the color of the specular component of the light in RGBA format */
	private float[] specular;
	
	/** An array containing the position of the lights in homogenous coordinates, format (x, y, z, w) */
	private float[] position;
	
	/** A boolean indicating whether the light is turned on. */
	private boolean turnedOn;
	
	/**
	 * Creates a light with its position at the origin and white values for all colors
	 */
	public Light() {
		turnedOn = false;
		
		ambient = new float[4]; 
		diffuse = new float[4];
		specular = new float[4];
		position = new float[4];
		
		for (int i = 0; i < 3; i++) {
			ambient[i] = DEFAULT_COLOR_VALUE;
		    diffuse[i] = DEFAULT_COLOR_VALUE;
			specular[i] = DEFAULT_COLOR_VALUE;
			position[i] = 0;
		}
		
		ambient[3] = DEFAULT_ALPHA_VALUE;
	    diffuse[3] = DEFAULT_ALPHA_VALUE;
		specular[3] = DEFAULT_ALPHA_VALUE;
		position[3] = 1;
	}
	
	/**
	 * Creates a new light object with the specified ambient, diffuse, and specular components.
	 * 
	 * @param ambient An array containing the color and alpha of the ambient light component, in the form (r, g, b, a)
	 * @param diffuse An array containing the color and alpha of the diffuse light component, in the form (r, g, b, a)
	 * @param specular An array containing the color and alpha of the specular light component, in the form (r, g, b, a)
	 * @param position An array containing the position of the light in homogenous coordinates, in the form (x, y, z, w)
	 */
	public Light(float[] ambient, float[] diffuse, float[] specular, float[] position) {
		// Require 4 parameters for ambient, diffuse, and specular lighting
		if (ambient.length < 4) {
			throw new IllegalArgumentException("Array for light ambient property has less than 4 elements.");
		}
		
		if (diffuse.length < 4) {
			throw new IllegalArgumentException("Array for light diffuse property has less than 4 elements.");
		}
		
		if (specular.length < 4) {
			throw new IllegalArgumentException("Array for light specular property has less than 4 elements.");
		}
		
		if (position.length < 4) {
			throw new IllegalArgumentException("Array containing light position in homogenous coordinates has less than 4 elements.");
		}

		turnedOn = false;
		
		this.ambient = new float[4]; 
		this.diffuse = new float[4];
		this.specular = new float[4];
		this.position = new float[4];
		
		// Copy light properties
		for (int i = 0; i < 4; i++) {
			this.ambient[i] = ambient[i];
			this.diffuse[i] = diffuse[i];
			this.specular[i] = specular[i];
			this.position[i] = position[i];
		}
	}
	
	/**
	 * Return the RGBA values for the ambient light property.
	 * 
	 * @return A size 4 array containing the RGBA values for the ambient light property.
	 */
	public float[] getAmbient() {
		return ambient;
	}
	
	/**
	 * Return the RGBA values for the diffuse light property.
	 * 
	 * @return A size 4 array containing the RGBA values for the diffuse light property.
	 */
	public float[] getDiffuse() {
		return diffuse;
	}
	
	/**
	 * Return the RGBA values for the specular light property.
	 * 
	 * @return A size 4 array containing the RGBA values for the specular light property.
	 */
	public float[] getSpecular() {
		return specular;
	}
	
	/**
	 * Return the position of the light.
	 * 
	 * @return A {@link Vector3} containing the 3D coordinates of the light.
	 */
	public float[] getPosition() {
		return position;
	}
	
	/**
	 * Set the properties of the light's ambient component.
	 * 
	 * @param red The amount of red, ranging from 0.0 to 1.0.
	 * @param green The amount of green, ranging from 0.0 to 1.0.
	 * @param blue The amount of blue, ranging from 0.0 to 1.0.
	 * @param alpha The alpha value for the light, which contributes to light intensity.
	 */
	public void setAmbient(float red, float green, float blue, float alpha) {
		ambient[0] = red;
		ambient[1] = green;
		ambient[2] = blue;
		ambient[3] = alpha;
	}
	
	/**
	 * Set the properties of the light's diffuse component.
	 * 
	 * @param red The amount of red, ranging from 0.0 to 1.0.
	 * @param green The amount of green, ranging from 0.0 to 1.0.
	 * @param blue The amount of blue, ranging from 0.0 to 1.0.
	 * @param alpha The alpha value for the light, which contributes to light intensity.
	 */
	public void setDiffuse(float red, float green, float blue, float alpha) {
		diffuse[0] = red;
		diffuse[1] = green;
		diffuse[2] = blue;
		diffuse[3] = alpha;
	}
	
	/**
	 * Set the properties of the light's specular component.
	 * 
	 * @param red The amount of red, ranging from 0.0 to 1.0.
	 * @param green The amount of green, ranging from 0.0 to 1.0.
	 * @param blue The amount of blue, ranging from 0.0 to 1.0.
	 * @param alpha The alpha value for the light, which contributes to light intensity.
	 */
	public void setSpecular(float red, float green, float blue, float alpha) {
		specular[0] = red;
		specular[1] = green;
		specular[2] = blue;
		specular[3] = alpha;
	}
	
	/**
	 * Set the position of the light to a new value.
	 * 
	 * @param position The new position represented by a {@link Vector3} object.
	 */
	public void setPosition(float x, float y, float z, float w) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
		position[3] = w;
	}
	
	/**
	 * Check whether the light is considered to be turned on.
	 * 
	 * @return <code>true</code> if the light is considered to be on, <code>false</code> otherwise.
	 */
	public boolean isTurnedOn() {
		return turnedOn;
	}
	
	/**
	 * Sets the on/off state associated with the light.
	 * 
	 * @param turnedOn Whether the light is considered as on or off.
	 */
	public void setTurnedOn(boolean turnedOn) {
		this.turnedOn = turnedOn;
	}
}
