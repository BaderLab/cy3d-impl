package org.cytoscape.ding.icon;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;


public class EllipseNodeShape {
	
	public static Shape getShape(float size) {
		Ellipse2D.Float ellipse = new Ellipse2D.Float(0.0f,0.0f,1.0f,1.0f);
		ellipse.setFrame(0, 0, size, size);
		return ellipse;
	}
	
}

