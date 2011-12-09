package org.cytoscape.paperwing.internal.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.paperwing.internal.Graphics.PickResults;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class InputProcessor {
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {

		SimpleCamera camera = graphicsData.getCamera();

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
				endTime = System.nanoTime();

				double duration = (endTime - startTime) / Math.pow(10, 9);
				double frameRate = framesElapsed / duration;
				System.out.println("Average fps over " + duration
						+ " seconds: " + frameRate);

				startTime = System.nanoTime();
				framesElapsed = 0;
			}

			// Reset Camera to default
			if (pressed.contains(KeyEvent.VK_C)) {
				camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0,
						0, 0), new Vector3(0, 1, 0), 0.04, 0.002, 0.01, 0.01,
						0.4);
			}

			// Debug-related boolean
			if (pressed.contains(KeyEvent.VK_1)) {
				latch_1 = true;
			}

			if (pressed.contains(KeyEvent.VK_L)) {
				if (!lowerQuality) {
					lowerQuality = true;

					QUADRATIC_EDGE_SEGMENTS = 3;
					NODE_SLICES_DETAIL = 6;
					NODE_STACKS_DETAIL = 6;
					EDGE_SLICES_DETAIL = 3;
					EDGE_STACKS_DETAIL = 1;

					createDisplayLists(gl);
				} else {
					lowerQuality = false;

					QUADRATIC_EDGE_SEGMENTS = 5;
					NODE_SLICES_DETAIL = 10;
					NODE_STACKS_DETAIL = 10;
					EDGE_SLICES_DETAIL = 4;
					EDGE_STACKS_DETAIL = 1;

					createDisplayLists(gl);
				}
			}

			if (pressed.contains(KeyEvent.VK_P)) {
				skipHover = !skipHover;
			}

			// Roll camera clockwise
			if (held.contains(KeyEvent.VK_Z)) {
				camera.rollClockwise();
			}

			// Roll camera counter-clockwise
			if (held.contains(KeyEvent.VK_X)) {
				camera.rollCounterClockwise();
			}

			// Create edges between nodes
			if (pressed.contains(KeyEvent.VK_J)) {
				CyNode hoverNode = networkView.getModel().getNode(
						hoverNodeIndex);

				if (hoverNode != null) {

					for (CyNode node : selectedNodes) {
						networkView.getModel().addEdge(node, hoverNode, false);

						// TODO: Not sure if this call is needed
						networkView.updateView();
					}
					;
				}
			}

			// Delete selected edges/nodes
			if (pressed.contains(KeyEvent.VK_DELETE)) {
				LinkedHashSet<CyEdge> edgesToBeRemoved = new LinkedHashSet<CyEdge>();

				for (CyNode node : selectedNodes) {
					// TODO: Check if use of Type.ANY for any edge is correct
					// TODO: Check if this addAll method properly skips adding
					// edges already in the edgesToBeRemovedList
					edgesToBeRemoved.addAll(networkView.getModel()
							.getAdjacentEdgeList(node, Type.ANY));
				}

				if (!networkView.getModel().removeNodes(selectedNodes)) {
					// do nothing
				} else {
					// Remove edges attached to the node
					networkView.getModel().removeEdges(edgesToBeRemoved);
				}

				// Remove selected edges
				networkView.getModel().removeEdges(selectedEdges);

				// TODO: Not sure if this call is needed
				networkView.updateView();
			}

			// If shift is pressed, perform orbit camera movement
			if (held.contains(KeyEvent.VK_SHIFT)) {

				if (held.contains(KeyEvent.VK_LEFT)) {
					camera.orbitLeft();
				}

				if (held.contains(KeyEvent.VK_RIGHT)) {
					camera.orbitRight();
				}

				if (held.contains(KeyEvent.VK_UP)) {
					camera.orbitUp();
				}

				if (held.contains(KeyEvent.VK_DOWN)) {
					camera.orbitDown();
				}

				// Otherwise, turn camera in a first-person like fashion
			} else {

				if (held.contains(KeyEvent.VK_LEFT)) {
					camera.turnLeft(4);
				}

				if (held.contains(KeyEvent.VK_RIGHT)) {
					camera.turnRight(4);
				}

				if (held.contains(KeyEvent.VK_UP)) {
					camera.turnUp(4);
				}

				if (held.contains(KeyEvent.VK_DOWN)) {
					camera.turnDown(4);
				}

			}

			// Create a new node
			if (pressed.contains(KeyEvent.VK_N)) {
				CyNode added = networkView.getModel().addNode();
				networkView.updateView();

				View<CyNode> viewAdded = networkView.getNodeView(added);

				// TODO: Maybe throw an exception if viewAdded is null
				if (viewAdded != null) {
					viewAdded.setVisualProperty(
							RichVisualLexicon.NODE_X_LOCATION, projection.x()
									* DISTANCE_SCALE);
					viewAdded.setVisualProperty(
							RichVisualLexicon.NODE_Y_LOCATION, projection.y()
									* DISTANCE_SCALE);
					viewAdded.setVisualProperty(
							RichVisualLexicon.NODE_Z_LOCATION, projection.z()
									* DISTANCE_SCALE);

					// Set the node to be hovered
					// TODO: This might not be needed if the node were added
					// through some way other than the mouse
					hoverNodeIndex = added.getIndex();
				}
			}

			// Camera translational movement
			// -----------------------------

			if (held.contains(KeyEvent.VK_W)) {
				camera.moveUp();
			}

			if (held.contains(KeyEvent.VK_S)) {
				camera.moveDown();
			}

			if (held.contains(KeyEvent.VK_A)) {
				camera.moveLeft();
			}

			if (held.contains(KeyEvent.VK_D)) {
				camera.moveRight();
			}

			if (held.contains(KeyEvent.VK_Q)) {
				camera.moveBackward();
			}

			if (held.contains(KeyEvent.VK_E)) {
				camera.moveForward();
			}

			// Debug - display distance between 2 nodes
			if (pressed.contains(KeyEvent.VK_O)) {
				CyNode hoverNode = networkView.getModel().getNode(
						hoverNodeIndex);

				if (hoverNode != null && selectedNodes.size() == 1) {
					View<CyNode> hoverView = networkView.getNodeView(hoverNode);
					View<CyNode> selectView = hoverView;
					for (CyNode node : selectedNodes) {
						selectView = networkView.getNodeView(node);
					}
					;

					Vector3 hover = new Vector3(
							hoverView
									.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
							hoverView
									.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
							hoverView
									.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));

					Vector3 select = new Vector3(
							selectView
									.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
							selectView
									.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
							selectView
									.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));

					System.out.println("Distance: " + hover.distance(select)
							/ DISTANCE_SCALE);
				}
			}

			keys.update();
		}

		// Perform picking-related operations
		// ----------------------------------

		PickResults pickResults;
		if (skipHover) {
			pickResults = new PickResults();
		} else {
			pickResults = performPick(gl, mouse.x(), mouse.y(), 2, 2, false);
		}

		int newHoverNodeIndex = NO_INDEX;
		int newHoverEdgeIndex = NO_INDEX;

		for (Integer nodeIndex : pickResults.nodeIndices) {
			newHoverNodeIndex = nodeIndex;
		}

		for (Integer edgeIndex : pickResults.edgeIndices) {
			newHoverEdgeIndex = edgeIndex;
		}

		// Make sure only 1 object is selected for single selection
		assert pickResults.nodeIndices.size() + pickResults.edgeIndices.size() <= 1;

		if (keys.getHeld().contains(KeyEvent.VK_CONTROL) || dragSelectMode) {
			hoverNodeIndex = NO_INDEX;
			hoverEdgeIndex = NO_INDEX;
		} else {
			hoverNodeIndex = newHoverNodeIndex;
			hoverEdgeIndex = newHoverEdgeIndex;
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

				if (!selectedNodes.isEmpty()) {
					// TODO: Check if this is a suitable place to put this, as
					// it helps to make node dragging smoother
					selectProjectionDistance = findAveragePosition(
							selectedNodes).distance(camera.getPosition());
				}
			}

			// If the left button was clicked, prepare to select nodes/edges
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {

				// If the user did not hold down shift, unselect other objects
				if (!keys.getHeld().contains(KeyEvent.VK_SHIFT)) {
					selectedNodes.clear();
					selectedEdges.clear();

					selectedNodeIndices.clear();
					selectedEdgeIndices.clear();
				}

				// Prepare to perform drag selection
				// ---------------------------------

				selectTopLeftX = mouse.x();
				selectTopLeftY = mouse.y();

				selectBottomRightX = NULL_COORDINATE;
				selectBottomRightY = NULL_COORDINATE;

				// ----------------------------------

				if (newHoverNodeIndex != NO_INDEX) {
					CyNode picked = networkView.getModel().getNode(
							newHoverNodeIndex);

					// TODO: Possibly throw exception if the node was found to
					// be null, ie. invalid index
					if (picked != null) {

						if (selectedNodes.contains(picked)) {
							selectedNodes.remove(picked);
							selectedNodeIndices.remove(picked.getIndex());
						} else {
							selectedNodes.add(picked);
							selectedNodeIndices.add(picked.getIndex());
						}

						// System.out.println("Selected node index: " +
						// picked.getIndex());
					}
				} else if (newHoverEdgeIndex != NO_INDEX) {
					CyEdge picked = networkView.getModel().getEdge(
							newHoverEdgeIndex);

					// TODO: Possibly throw exception if the edge was found to
					// be null, ie. invalid index
					if (picked != null) {

						if (selectedEdges.contains(picked)) {
							selectedEdges.remove(picked);
							selectedEdgeIndices.remove(picked.getIndex());
						} else {
							selectedEdges.add(picked);
							selectedEdgeIndices.add(picked.getIndex());
						}

						// System.out.println("Selected edge index: " +
						// picked.getIndex());
					}
				} else {

					// System.out.println("Nothing selected");
				}

			}

			// Drag selection; moving the box
			if (mouse.getHeld().contains(MouseEvent.BUTTON1)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
				selectBottomRightX = mouse.x();
				selectBottomRightY = mouse.y();

				if (mapMode) {
					System.out.println("Map clicked");
				}

				if (Math.abs(selectTopLeftX - selectBottomRightX) >= 1
						&& Math.abs(selectTopLeftY - selectBottomRightY) >= 1
						&& selectTopLeftX != NULL_COORDINATE
						&& selectTopLeftY != NULL_COORDINATE) {

					dragSelectMode = true;
				} else {
					dragSelectMode = false;
				}
			}

			// Drag selection; selecting contents of the box
			if (mouse.getReleased().contains(MouseEvent.BUTTON1)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)
					&& dragSelectMode) {
				selectBottomRightX = mouse.x();
				selectBottomRightY = mouse.y();

				if (Math.abs(selectTopLeftX - selectBottomRightX) >= 1
						&& Math.abs(selectTopLeftY - selectBottomRightY) >= 1) {

					PickResults results = performPick(gl,
							(selectTopLeftX + selectBottomRightX) / 2,
							(selectTopLeftY + selectBottomRightY) / 2,
							Math.abs(selectTopLeftX - selectBottomRightX),
							Math.abs(selectTopLeftY - selectBottomRightY), true);

					CyNode node;
					for (Integer nodeIndex : results.nodeIndices) {
						node = networkView.getModel().getNode(nodeIndex);

						if (node != null) {
							selectedNodes.add(node);
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
							selectedEdges.add(edge);
							selectedEdgeIndices.add(edgeIndex);
						} else {
							// System.out.println("Null edge found for index " +
							// edgeIndex + " in drag selection, ignoring..");
						}
					}
				}

				selectTopLeftX = NULL_COORDINATE;
				selectTopLeftY = NULL_COORDINATE;

				selectBottomRightX = NULL_COORDINATE;
				selectBottomRightY = NULL_COORDINATE;

				dragSelectMode = false;

			}

			// Drag-move selected nodes using projected cursor location
			// --------------------------------------------------------

			if (mouse.getPressed().contains(MouseEvent.BUTTON1)
					&& !selectedNodes.isEmpty()) {
				// Store the result for use for mouse-difference related
				// calculations
				selectProjectionDistance = findAveragePosition(selectedNodes)
						.distance(camera.getPosition());

				previousSelectedProjection = projectMouseCoordinates(selectProjectionDistance);
				// } else if (mouse.getHeld().contains(MouseEvent.BUTTON1) &&
				// !selectedNodes.isEmpty() && previousSelectProjection != null)
				// {
			} else if (keys.getHeld().contains(KeyEvent.VK_CONTROL)
					&& mouse.getHeld().contains(MouseEvent.BUTTON1)
					&& !selectedNodes.isEmpty()
					&& previousSelectedProjection != null) {
				View<CyNode> nodeView;
				Vector3 projectionDisplacement;

				currentSelectedProjection = projectMouseCoordinates(selectProjectionDistance);
				projectionDisplacement = currentSelectedProjection
						.subtract(previousSelectedProjection);

				double x, y, z;

				for (CyNode node : selectedNodes) {
					// TODO: This relies on an efficient traversal of selected
					// nodes, as well
					// as efficient retrieval from the networkView object
					nodeView = networkView.getNodeView(node);

					if (nodeView != null) {
						x = nodeView
								.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
								+ projectionDisplacement.x() * DISTANCE_SCALE;
						y = nodeView
								.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
								+ projectionDisplacement.y() * DISTANCE_SCALE;
						z = nodeView
								.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
								+ projectionDisplacement.z() * DISTANCE_SCALE;

						nodeView.setVisualProperty(
								RichVisualLexicon.NODE_X_LOCATION, x);
						nodeView.setVisualProperty(
								RichVisualLexicon.NODE_Y_LOCATION, y);
						nodeView.setVisualProperty(
								RichVisualLexicon.NODE_Z_LOCATION, z);

					}
				}

				previousSelectedProjection = currentSelectedProjection;

				selectTopLeftX = NULL_COORDINATE;
				selectTopLeftY = NULL_COORDINATE;
			}

			mouse.update();

		}
	}
}
