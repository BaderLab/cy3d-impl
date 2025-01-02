package org.baderlab.cy3d.internal.tools;

import com.jogamp.opengl.GL2;

public class RenderColor {
	private static final double DEFAULT_ALPHA = 1.0;
	
	private double red;
	private double green;
	private double blue;
	private double alpha;
	
	public static void setNonAlphaColors(GL2 gl, RenderColor color) {
		gl.glColor3d(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public RenderColor(double red, double green, double blue, double alpha) {
		set(red, green, blue, alpha);
	}
	
	public RenderColor(double red, double green, double blue) {
		this(red, green, blue, DEFAULT_ALPHA);
	}
	
	public RenderColor(RenderColor other) {
		this.red = other.red;
		this.green = other.green;
		this.blue = other.blue;
		this.alpha = other.alpha;
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
	
	public void set(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public void set(double red, double green, double blue) {
		set(red, green, blue, this.alpha);
	}

	// Does not allow colors to escape range [0, 1]
	public void multiplyNonAlphaColors(double multiplier) {
		if (multiplier < 0) {
			return;
		}
		
		set(Math.min(red * multiplier, 1), 
				Math.min(green * multiplier, 1), 
				Math.min(blue * multiplier, 1));
	}
	
	public void multiplyRed(double multiplier, double minimum, double maximum) {
		if (multiplier < 0) {
			return;
		}
		
		red = Math.max(red, minimum);
		red = Math.min(red * multiplier, maximum);
		red = Math.min(red, 1);
	}
	
	public void multiplyGreen(double multiplier, double minimum, double maximum) {
		if (multiplier < 0) {
			return;
		}
		
		green = Math.max(green, minimum);
		green = Math.min(green * multiplier, maximum);
		green = Math.min(green, 1);
	}
	
	public void multiplyBlue(double multiplier, double minimum, double maximum) {
		if (multiplier < 0) {
			return;
		}
		
		blue = Math.max(blue, minimum);
		blue = Math.min(blue * multiplier, maximum);
		blue = Math.min(blue, 1);
	}
	
}
