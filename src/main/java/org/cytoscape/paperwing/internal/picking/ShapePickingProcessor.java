package org.cytoscape.paperwing.internal.picking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.input.KeyboardMonitor;
import org.cytoscape.paperwing.internal.input.MouseMonitor;
import org.cytoscape.paperwing.internal.picking.ShapePicker.PickResults;
import org.cytoscape.paperwing.internal.utility.SimpleCamera;

import com.jogamp.newt.event.MouseEvent;

// Read-only from GraphicsData and SelectionData, writes to PickingData
public interface ShapePickingProcessor {
	
	public void initialize(GraphicsData graphicsData);
	
	public void processPicking(MouseMonitor mouse, KeyboardMonitor keys, GraphicsData graphicsData);

}
