package org.cytoscape.ding.icon;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;


public class RectangleNodeShape {
	
	public static Shape getShape(float size) {
		Rectangle2D.Float rect;
		rect = new Rectangle2D.Float(0.0f,0.0f,1.0f,1.0f);	
		rect.setRect(0, 0, size, size);
		return rect;
	}
}

