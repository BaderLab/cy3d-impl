package org.cytoscape.ding.icon;

import java.awt.Shape;
import java.awt.geom.GeneralPath;


public class TriangleNodeShape {
	
	public static Shape getShape(float size) {
		GeneralPath path = new GeneralPath(); 
		path.reset();

		path.moveTo((size) / 2.0f, 0);
		path.lineTo(size, size);
		path.lineTo(0, size);

		path.closePath();

		return path;
	}
}

