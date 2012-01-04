package org.cytoscape.paperwing.internal.tools;

import javax.media.opengl.GL2;

public class RenderColor {
	private static final double DEFAULT_ALPHA = 1.0;
	
	private double red;
	private double green;
	private double blue;
	private double alpha;
	
	public RenderColor(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public RenderColor(double red, double green, double blue) {
		this(red, green, blue, DEFAULT_ALPHA);
	}
	
	public RenderColor() {
		this(1, 1, 1, 1);
	}
	
	public double getRed() {
		return red;
	}
	
	public double getGreen() {
		return green;
	}
	
	public double getBlue() {
		return blue;
	}
	
	public double getAlpha() {
		return alpha;
	}

	public static void setNonAlphaColors(GL2 gl, RenderColor color) {
		gl.glColor3d(color.getRed(), color.getGreen(), color.getBlue());
	}
}
