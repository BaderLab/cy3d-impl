package org.cytoscape.paperwing.internal.rendering.shapes;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer.ShapeType;

public class EdgeSegmentDrawer {
	public static enum SegmentType {
		REGULAR
	}
	
	private Map<SegmentType, Integer> segmentLists;
	
	public EdgeSegmentDrawer() {
		segmentLists = new HashMap<SegmentType, Integer>();
	}
	
	public void drawSegment (GL2 gl, SegmentType segmentType) {
		Integer segmentList = segmentLists.get(segmentType);
		
		if (segmentList != null) {
			gl.glCallList(segmentList);
		}
	}
}
