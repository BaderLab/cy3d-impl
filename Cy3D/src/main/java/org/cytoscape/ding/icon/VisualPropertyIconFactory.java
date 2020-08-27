package org.cytoscape.ding.icon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;

/*
 * #%L
 * Cytoscape Ding View/Presentation Impl (ding-presentation-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2020 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/**
 * Static factory for icons.
 */
public final class VisualPropertyIconFactory {	
	
	private static final float DEF_SHAPE_SIZE = 32;
	
	private static final Map<NodeShape,Shape> SHAPES = new HashMap<>();
	static {
		SHAPES.put(NodeShapeVisualProperty.RECTANGLE, RectangleNodeShape.getShape(DEF_SHAPE_SIZE));
		SHAPES.put(NodeShapeVisualProperty.ELLIPSE,   EllipseNodeShape.getShape(DEF_SHAPE_SIZE));
		SHAPES.put(NodeShapeVisualProperty.TRIANGLE,  TriangleNodeShape.getShape(DEF_SHAPE_SIZE));
	}
	
	public static <V> Icon createIcon(V value, int w, int h) {
		if (value == null)
			return null;
		
		if (value instanceof Color) {
			return new ColorIcon((Color) value, w, h, value.toString());
		} else if (value instanceof NodeShape) {
			Shape shape = SHAPES.get(value);
			String text = ((NodeShape)value).getDisplayName();
			if(shape == null) {
				return new TextIcon(value, w, h, text);
			} else {
				return new NodeIcon(shape, w, h, text);
			}
		} else if (value instanceof Font) {
			return new FontFaceIcon((Font) value, w, h, "");
		} else {
			// If not found, use return value of toString() as icon.
			return new TextIcon(value, w, h, value.toString());
		}
	}
	
}
