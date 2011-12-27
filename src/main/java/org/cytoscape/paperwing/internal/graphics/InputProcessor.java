package org.cytoscape.paperwing.internal.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.media.opengl.GL2;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.paperwing.internal.graphics.ShapePicker.PickResults;
import org.cytoscape.paperwing.internal.input.CameraInputHandler;
import org.cytoscape.paperwing.internal.input.InputHandler;
import org.cytoscape.paperwing.internal.input.NetworkChangeInputHandler;
import org.cytoscape.paperwing.internal.input.SelectionInputHandler;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public interface InputProcessor {
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData);

}
