package org.baderlab.cy3d.internal.data;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.media.nativewindow.NativeSurface;

/**
 * Converts between window units and pixel units.
 * This is necessary to support High-DPI displays such as mac retina displays.
 * 
 * Note: This class is not thread safe (for performance reasons), do not call from multiple threads.
 * Note: This class is designed to never allocate new objects, parameters are directly modified.
 * 
 * @author mkucera
 */
public class PixelConverter {

	private NativeSurface nativeSurface;
	
	public PixelConverter(NativeSurface nativeSurface) {
		this.nativeSurface = nativeSurface;
	}
	
	public void setNativeSurface(NativeSurface nativeSurface) { 
		this.nativeSurface = nativeSurface;
	}
	
	
	// Raw array-based direct access to JOGL.

	public void convertToWindowUnits(int[] pixelUnitsAndResult) {
		nativeSurface.convertToWindowUnits(pixelUnitsAndResult);
	}
	
	public void convertToPixelUnits(int[] windowUnitsAndResult) {
		nativeSurface.convertToPixelUnits(windowUnitsAndResult);
	}
	
	/**
	 * Returns the number of OpenGL pixels per Window unit.
	 * Note: Calculates the value on every call.
	 */
	public float getPixelsPerWindowUnitRatio() {
		int[] windowUnits = new int[] {100, 100};
		nativeSurface.convertToPixelUnits(windowUnits);
		float ratio = (float) windowUnits[0] / 100.0f;
		return ratio;
	}
	
	
	// Nicer java.awt.Point based access.
	
	private int[] coords = new int[2];  // this is why this class is not thread safe
	
	public void convertToWindowUnits(Point pixelUnitsAndResult) {
		coords[0] = pixelUnitsAndResult.x;
		coords[1] = pixelUnitsAndResult.y;
		nativeSurface.convertToWindowUnits(coords);
		pixelUnitsAndResult.x = coords[0];
		pixelUnitsAndResult.y = coords[1];
	}

	public void convertToPixelUnits(Point windowUnitsAndResult) {
		coords[0] = windowUnitsAndResult.x;
		coords[1] = windowUnitsAndResult.y;
		nativeSurface.convertToPixelUnits(coords);
		windowUnitsAndResult.x = coords[0];
		windowUnitsAndResult.y = coords[1];
	}
	
	
	// Just a convenience method
	
	public void convertMouse(MouseEvent e, int[] coords) {
		// put the result in coords to use
		coords[0] = e.getX();
		coords[1] = e.getY();
		convertToPixelUnits(coords);
	}
}
