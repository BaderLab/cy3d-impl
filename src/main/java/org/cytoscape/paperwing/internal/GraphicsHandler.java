package org.cytoscape.paperwing.internal;

import java.util.Map;

import org.cytoscape.model.CyTable;
import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
import org.cytoscape.paperwing.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.input.InputProcessor;
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.rendering.ReadOnlyGraphicsProcedure;
import org.cytoscape.view.model.CyNetworkView;

/**
 * A handler object that is responsible for specifying how input, calculation, and drawing are
 * handled in a {@link Graphics} object. A {@link Graphics} object relies on its GraphicsHandler
 * to provide implementations for how certain responses are handled.
 */
public interface GraphicsHandler {
		
	/**
	 * Return a {@link ShapePickingProcessor} object that is responsible for
	 * performing shape picking and selection given the state of the 
	 * network as well as the mouse. It will be given a {@link GraphicsData} object
	 * to store its results.
	 * 
	 * The {@link ShapePickingProcessor} is called first in the display loop.
	 * 
	 * @return A {@link ShapePickingProcessor} object that will perform shape picking
	 * and store the results appropriately for later use.
	 */
	public ShapePickingProcessor getShapePickingProcessor();
	
	/**
	 * Return an {@link InputProcessor} that is responsible for responding
	 * to keyboard and mouse button input given the states of the keyboard and
	 * mouse, as well as a {@link GraphicsData} object to store the interpreted
	 * input data.
	 * 
	 * The {@link InputProcessor} is called after the {@link ShapePickingProcessor}.
	 * 
	 * @return An {@link InputProcessor} that directs how input is handled.
	 */
	public InputProcessor getInputProcessor();

	/**
	 * Return an instance of a {@link ViewingCoordinator} that will be used to allow
	 * communication between the bird's eye and the main rendering windows. This communication
	 * is used to facilitate features such as being able to move the main camera by clicking
	 * on the bird's eye view.
	 * 
	 * @param graphicsData The {@link GraphicsData} object containing relevant information about
	 * the current rendering object, such as the current {@link CyNetworkView} being rendered
	 * @return An instance of a {@link ViewingCoordinator} used for coordination between
	 * the main and bird's eye cameras.
	 */
	public ViewingCoordinator getCoordinator(GraphicsData graphicsData);
	
	/**
	 * Return an instance of a {@link CoordinatorProcessor} object, which is responsible for
	 * interacting with the current {@link ViewingCoordinator} in order to extract relevant data
	 * from another {@link Graphics} object. This could be information about the position of
	 * the camera in the other {@link Graphics} object, for example.
	 * 
	 * @return An instance of a {@link CoordinatorProcessor} object used to interact with
	 * the {@link ViewingCoordinator}, which is also provided by this class.
	 */
	public CoordinatorProcessor getCoordinatorProcessor();
	
	/**
	 * Return an intance of a {@link CytoscapeDataProcessor}, which is responsible for
	 * transferring data to and from Cytoscape's data objects, such as {@link CyTable}.
	 * This could be used to update a value from a {@link CyTable}, or retrieve a value
	 * from a {@link CyTable}.
	 * 
	 * @return A {@link CytoscapeDataProcessor} object that is responsible for modifying
	 * or retrieving data belonging to the rest of Cytoscape, such as data belonging to 
	 * a {@link CyTable} object.
	 */
	public CytoscapeDataProcessor getCytoscapeDataProcessor();
	
	
	/**
	 * This method should be called before the first frame of rendering. It will initialize
	 * the rendering procedures (eg. setting up display lists) and allow them to be 
	 * executed for per-frame drawing, such as via the drawScene method.
	 * 
	 * @param graphicsData The current {@link GraphicsData} object containing information
	 * about the current state of rendering as well as the current state of the network.
	 */
	public void initializeGraphicsProcedures(GraphicsData graphicsData);
	
	
	/**
	 * This method is called to graphically render the current scene. It should be
	 * called every frame if the state of the network during each frame is desired
	 * to for visualization.
	 * 
	 * @param graphicsData The current {@link GraphicsData} object containing information
	 * about the current state of rendering as well as the current state of the network.
	 */
	public void drawScene(GraphicsData graphicsData);

	/**
	 * Sets up the lighting. Should be called before the first frame of rendering.
	 * 
	 * @param graphicsData @param graphicsData The current {@link GraphicsData} object containing information
	 * about the current state of rendering as well as the current state of the network.
	 */
	public void setupLighting(GraphicsData graphicsData);
}


