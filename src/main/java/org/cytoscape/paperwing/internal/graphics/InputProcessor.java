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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class InputProcessor {
	
	public static int NULL_COORDINATE = Integer.MIN_VALUE;
	
	// Relies on Cytoscape's guarantee that edge and node indices are 
	// nonnegative
	public static int NO_INDEX = -1; 
	
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData, ShapePicker shapePicker) {

		GL2 gl = graphicsData.getGlContext();
		SimpleCamera camera = graphicsData.getCamera();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		// CyNetwork model = graphicsData.getNetworkView().getModel();

		Set<Integer> selectedNodeIndices = selectionData
				.getSelectedNodeIndices();
		Set<Integer> selectedEdgeIndices = selectionData
				.getSelectedEdgeIndices();

		// Project mouse coordinates into 3d space for mouse interactions
		// --------------------------------------------------------------

		Vector3 projection = GraphicsUtility.projectScreenCoordinates(
				mouse.x(), mouse.y(), graphicsData.getScreenWidth(),
				graphicsData.getScreenHeight(), camera.getDistance(), camera);

		if (keys.hasHeld() || keys.hasNew()) {
			Set<Integer> pressed = keys.getPressed();
			Set<Integer> held = keys.getHeld();
			Set<Integer> released = keys.getReleased();

			// Display FPS
			if (pressed.contains(KeyEvent.VK_SPACE)) {
				// endTime = System.nanoTime();
				//
				// double duration = (endTime - startTime) / Math.pow(10, 9);
				// double frameRate = framesElapsed / duration;
				// System.out.println("Average fps over " + duration
				// + " seconds: " + frameRate);
				//
				// startTime = System.nanoTime();
				// framesElapsed = 0;
			}

			// Reset Camera to default
			if (pressed.contains(KeyEvent.VK_C)) {
				camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0,
						0, 0), new Vector3(0, 1, 0), 0.04, 0.002, 0.01, 0.01,
						0.4);
			}

			// Debug-related boolean
			if (pressed.contains(KeyEvent.VK_1)) {
				// latch_1 = true;
			}

			if (pressed.contains(KeyEvent.VK_P)) {
				// skipHover = !skipHover;
			}

			keys.update();
		}

		// Perform picking-related operations
		// ----------------------------------

		PickResults pickResults;
		if (graphicsData.isDisableHovering()) {
			pickResults = shapePicker.new PickResults();
		} else {
			pickResults = shapePicker.performPick(mouse.x(), mouse.y(), 2, 2, false, graphicsData);
		}

		int newHoverNodeIndex = ShapePicker.NO_INDEX;
		int newHoverEdgeIndex = ShapePicker.NO_INDEX;

		for (Integer nodeIndex : pickResults.nodeIndices) {
			newHoverNodeIndex = nodeIndex;
		}

		for (Integer edgeIndex : pickResults.edgeIndices) {
			newHoverEdgeIndex = edgeIndex;
		}

		// Make sure only 1 object is selected for single selection
		assert pickResults.nodeIndices.size() + pickResults.edgeIndices.size() <= 1;

		if (keys.getHeld().contains(KeyEvent.VK_CONTROL) || selectionData.isDragSelectMode()) {
			graphicsData.setHoverNodeIndex(ShapePicker.NO_INDEX);
			graphicsData.setHoverEdgeIndex(ShapePicker.NO_INDEX);
		} else {
			graphicsData.setHoverNodeIndex(newHoverNodeIndex);
			graphicsData.setHoverEdgeIndex(newHoverEdgeIndex);
		}

		if (mouse.hasMoved() || mouse.hasNew()) {

			// First-person camera rotation
			if (keys.getHeld().contains(KeyEvent.VK_ALT)) {
				camera.turnRight(mouse.dX());
				camera.turnDown(mouse.dY());
			}

			// Varying distance between camera and camera's target point
			if (mouse.dWheel() != 0) {
				camera.zoomOut((double) mouse.dWheel());

				if (!selectedNodeIndices.isEmpty()) {
					// TODO: Check if this is a suitable place to put this, as
					// it helps to make node dragging smoother
					Vector3 averagePosition = GraphicsUtility.findAveragePosition(selectedNodeIndices, networkView, graphicsData.getDistanceScale());
					selectionData.setSelectProjectionDistance(averagePosition.distance(camera.getPosition()));
				}
			}

			// If the left button was clicked, prepare to select nodes/edges
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {

				// If the user did not hold down shift, unselect other objects
				if (!keys.getHeld().contains(KeyEvent.VK_SHIFT)) {
					selectedNodeIndices.clear();
					selectedEdgeIndices.clear();
				}

				// Prepare to perform drag selection
				// ---------------------------------

				selectionData.setSelectTopLeftX(mouse.x());
				selectionData.setSelectTopLeftY(mouse.y());

				selectionData.setSelectBottomRightX(NULL_COORDINATE);
				selectionData.setSelectBottomRightY(NULL_COORDINATE);

				// ----------------------------------

				if (newHoverNodeIndex != NO_INDEX) {
					Integer pickedIndex = newHoverNodeIndex;

					// TODO: Possibly throw exception if the node was found to
					// be null, ie. invalid index
					if (pickedIndex != null) {

						if (selectedNodeIndices.contains(pickedIndex)) {
							selectedNodeIndices.remove(pickedIndex);
						} else {
							selectedNodeIndices.add(pickedIndex);
						}

						// System.out.println("Selected node index: " +
						// picked.getIndex());
					}
				} else if (newHoverEdgeIndex != NO_INDEX) {
					Integer pickedIndex = newHoverEdgeIndex;

					// TODO: Possibly throw exception if the edge was found to
					// be null, ie. invalid index
					if (pickedIndex != null) {

						if (selectedEdgeIndices.contains(pickedIndex)) {
							selectedEdgeIndices.remove(pickedIndex);
						} else {
							selectedEdgeIndices.add(pickedIndex);
						}

						// System.out.println("Selected node index: " +
						// picked.getIndex());
					}
				} else {

					// System.out.println("Nothing selected");
				}

			}

			// Drag selection; moving the box
			if (mouse.getHeld().contains(MouseEvent.BUTTON1)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
				selectionData.setSelectBottomRightX(mouse.x());
				selectionData.setSelectBottomRightY(mouse.y());

				/** Map-mode toggling is likely to be disabled
				if (mapMode) {
					System.out.println("Map clicked");
				}
				*/

				if (Math.abs(selectionData.getSelectTopLeftX() - selectionData.getSelectBottomRightX()) >= 1
						&& Math.abs(selectionData.getSelectTopLeftY() - selectionData.getSelectBottomRightY()) >= 1
						&& selectionData.getSelectTopLeftX() != NULL_COORDINATE
						&& selectionData.getSelectTopLeftY() != NULL_COORDINATE) {

					selectionData.setDragSelectMode(true);
				} else {
					selectionData.setDragSelectMode(false);
				}
			}

			// Drag selection; selecting contents of the box
			if (mouse.getReleased().contains(MouseEvent.BUTTON1)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)
					&& selectionData.isDragSelectMode()) {
				selectionData.setSelectBottomRightX(mouse.x());
				selectionData.setSelectBottomRightY(mouse.y());

				if (Math.abs(selectionData.getSelectTopLeftX() - selectionData.getSelectBottomRightX()) >= 1
						&& Math.abs(selectionData.getSelectTopLeftY() - selectionData.getSelectBottomRightY()) >= 1) {

					PickResults results = shapePicker.performPick(
							(selectionData.getSelectTopLeftX() + selectionData.getSelectBottomRightX()) / 2,
							(selectionData.getSelectTopLeftY() + selectionData.getSelectBottomRightY()) / 2,
							Math.abs(selectionData.getSelectTopLeftX() - selectionData.getSelectBottomRightX()),
							Math.abs(selectionData.getSelectTopLeftY() - selectionData.getSelectBottomRightY()), true,
							graphicsData);

					CyNode node;
					for (Integer nodeIndex : results.nodeIndices) {
						node = networkView.getModel().getNode(nodeIndex);

						if (node != null) {
							selectedNodeIndices.add(nodeIndex);
						} else {
							// System.out.println("Null node found for index " +
							// nodeIndex + " in drag selection, ignoring..");
						}
					}

					CyEdge edge;
					for (Integer edgeIndex : results.edgeIndices) {
						edge = networkView.getModel().getEdge(edgeIndex);

						if (edge != null) {
							selectedEdgeIndices.add(edgeIndex);
						} else {
							// System.out.println("Null edge found for index " +
							// edgeIndex + " in drag selection, ignoring..");
						}
					}
				}

				selectionData.setSelectTopLeftX(NULL_COORDINATE);
				selectionData.setSelectTopLeftY(NULL_COORDINATE);

				selectionData.setSelectBottomRightX(NULL_COORDINATE);
				selectionData.setSelectBottomRightY(NULL_COORDINATE);

				selectionData.setDragSelectMode(false);

			}

			// Drag-move selected nodes using projected cursor location
			// --------------------------------------------------------

			if (mouse.getPressed().contains(MouseEvent.BUTTON1)
					&& !selectedNodeIndices.isEmpty()) {
				// Store the result for use for mouse-difference related
				// calculations
				
				Vector3 averagePosition = GraphicsUtility.findAveragePosition(selectedNodeIndices, networkView, graphicsData.getDistanceScale());
				selectionData.setSelectProjectionDistance(averagePosition.distance(camera.getPosition()));

				// These projections are used to find displacement vectors to move the nodes
				// during dragging
				selectionData.setPreviousSelectedProjection(
						GraphicsUtility.projectMouseCoordinates(
								mouse,
								graphicsData,
								selectionData.getSelectProjectionDistance()));
				// } else if (mouse.getHeld().contains(MouseEvent.BUTTON1) &&
				// !selectedNodes.isEmpty() && previousSelectProjection != null)
				// {
			} else if (keys.getHeld().contains(KeyEvent.VK_CONTROL)
					&& mouse.getHeld().contains(MouseEvent.BUTTON1)
					&& !selectedNodeIndices.isEmpty()
					&& selectionData.getPreviousSelectedProjection() != null) {
				View<CyNode> nodeView;
				Vector3 projectionDisplacement;

				selectionData.setCurrentSelectedProjection(
						GraphicsUtility.projectMouseCoordinates(mouse, graphicsData, selectionData.getSelectProjectionDistance()));
				projectionDisplacement = selectionData.getCurrentSelectedProjection()
						.subtract(selectionData.getPreviousSelectedProjection());

				double x, y, z;
				CyNode node;
				
				for (Integer index : selectedNodeIndices) {
					// TODO: This relies on an efficient traversal of selected
					// nodes, as well
					// as efficient retrieval from the networkView object
					
					node = networkView.getModel().getNode(index);
					nodeView = networkView.getNodeView(node);
					float distanceScale = graphicsData.getDistanceScale();
					
					if (nodeView != null) {
						x = nodeView
								.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
								+ projectionDisplacement.x() * distanceScale;
						y = nodeView
								.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
								+ projectionDisplacement.y() * distanceScale;
						z = nodeView
								.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
								+ projectionDisplacement.z() * distanceScale;

						nodeView.setVisualProperty(
								RichVisualLexicon.NODE_X_LOCATION, x);
						nodeView.setVisualProperty(
								RichVisualLexicon.NODE_Y_LOCATION, y);
						nodeView.setVisualProperty(
								RichVisualLexicon.NODE_Z_LOCATION, z);

					}
				}

				selectionData.setPreviousSelectedProjection(selectionData.getCurrentSelectedProjection());

				selectionData.setSelectTopLeftX(NULL_COORDINATE);
				selectionData.setSelectTopLeftY(NULL_COORDINATE);
			}

			mouse.update();

		}
	}
	
	
	
	private void processNetworkChanges(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Create edges between nodes
		if (pressed.contains(KeyEvent.VK_J)) {
			CyNode hoverNode = networkView.getModel().getNode(
					graphicsData.getHoverNodeIndex());

			if (hoverNode != null) {

				for (Integer index : selectedNodeIndices) {
					networkView.getModel().addEdge(
							networkView.getModel().getNode(index),
							hoverNode, false);

					// TODO: Not sure if this call is needed
					networkView.updateView();
				}
			}
		}

		// Delete selected edges/nodes
		if (pressed.contains(KeyEvent.VK_DELETE)) {
			Set<CyEdge> edgesToBeRemoved = new LinkedHashSet<CyEdge>();
			Set<CyNode> nodesToBeRemoved = new LinkedHashSet<CyNode>();
			
			// Remove nodes
			CyNode nodeToBeRemoved;
			
			for (Integer index : selectedNodeIndices) {
				nodeToBeRemoved = networkView.getModel().getNode(index);
				
				if (nodeToBeRemoved != null ) {
					nodesToBeRemoved.add(nodeToBeRemoved);
					
					// TODO: Check if use of Type.ANY for any edge is correct
					// TODO: Check if this addAll method properly skips adding
					// edges already in the edgesToBeRemovedList
					edgesToBeRemoved.addAll(networkView.getModel()
							.getAdjacentEdgeList(nodeToBeRemoved,
									Type.ANY));
				}
				
			}

			// Remove edges
			CyEdge edgeToBeRemoved;
			
			for (Integer index : selectedEdgeIndices) {
				edgeToBeRemoved = networkView.getModel().getEdge(index);
				
				if (edgeToBeRemoved != null) {
					edgesToBeRemoved.add(edgeToBeRemoved);
				}
			}
			
			networkView.getModel().removeNodes(nodesToBeRemoved);
			networkView.getModel().removeEdges(edgesToBeRemoved);
			
			// TODO: Not sure if this call is needed
			networkView.updateView();
		}

		

		
	}
	
	private void processCreateNode(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
	
	private void processCreateEdge(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
	
	private void processDeleteNode(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
	
	private void processDeleteEdge(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
}
