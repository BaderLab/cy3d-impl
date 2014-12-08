package org.baderlab.cy3d.internal.data;

import javax.media.nativewindow.NativeSurface;

public class PixelConverter {

	private NativeSurface nativeSurface;
	
	public PixelConverter(NativeSurface nativeSurface) {
		this.nativeSurface = nativeSurface;
	}

	public void setNativeSurface(NativeSurface nativeSurface) {
		this.nativeSurface = nativeSurface;
	}
	
	public int[] convertToWindowUnits(int[] pixelUnitsAndResult) {
		return nativeSurface.convertToWindowUnits(pixelUnitsAndResult);
	}
	
	public int[] convertToPixelUnits(int[] windowUnitsAndResult) {
		return nativeSurface.convertToPixelUnits(windowUnitsAndResult);
	}
}
