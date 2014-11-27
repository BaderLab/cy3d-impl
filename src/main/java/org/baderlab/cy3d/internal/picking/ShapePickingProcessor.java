package org.baderlab.cy3d.internal.picking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.input.KeyboardMonitor;
import org.baderlab.cy3d.internal.input.MouseMonitor;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

import com.jogamp.newt.event.MouseEvent;

// Read-only from GraphicsData and SelectionData, writes to PickingData
public interface ShapePickingProcessor {
	
	public void initialize(GraphicsData graphicsData);
	
	public void processPicking(MouseMonitor mouse, KeyboardMonitor keys, GraphicsData graphicsData);

}
