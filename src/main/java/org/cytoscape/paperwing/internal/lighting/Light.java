package org.cytoscape.paperwing.internal.lighting;

import org.cytoscape.paperwing.internal.geometric.Vector3;

/**
 * This class represents a light described by the Phong illumination model
 */
public class Light {
	
	/** An array containing the color of the ambient component of the light in RGBA format */
	private float[] ambient;
	
	/** An array containing the color of the diffuse component of the light in RGBA format */
	private float[] diffuse;
	
	/** An array containing the color of the specular component of the light in RGBA format */
	private float[] specular;
	
	/** The position of the light in 3D space */
	private Vector3 position;
	
	public Light(float[] ambient, float[] diffuse, float[] specular, Vector3 position) {
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
		
		this.position = new Vector3(position);
		
		this.ambient = new float[4]; 
		this.diffuse = new float[4];
		this.specular = new float[4];
		
		// Copy light properties
		for (int i = 0; i < 4; i++) {
			this.ambient[i] = ambient[i];
			this.diffuse[i] = diffuse[i];
			this.specular[i] = specular[i];
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
	public Vector3 getPosition() {
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
	public void setPosition(Vector3 position) {
		this.position.set(position);
	}
}
