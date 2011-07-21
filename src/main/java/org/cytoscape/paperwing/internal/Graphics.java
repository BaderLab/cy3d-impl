package org.cytoscape.paperwing.internal;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class Graphics implements GLEventListener {

	/*
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private static final float DISTANCE_SCALE = 178.0f; 
	
	private static final float LARGE_SPHERE_RADIUS = 1.0f; // 1.5f
	private static final float SMALL_SPHERE_RADIUS = 0.102f; // 0.015f
	private static final float MINIMUM_EDGE_DRAW_DISTANCE_SQUARED = Float.MIN_NORMAL; // 0.015f
	
	private static final float EDGE_RADIUS = 0.018f;
	
	private static final float EDGE_CURVE_DISTANCE = 0.7f;
	private static final float EDGE_CURVE_FACTOR = 0.43f; //0.31f
	private static final int EDGES_PER_RADIUS = 3;
	
	private static final int NODE_SLICES_DETAIL = 10; // 24, 24, 12 used to be default values for slices/stacks/slices
	private static final int NODE_STACKS_DETAIL = 10;
	private static final int EDGE_SLICES_DETAIL = 4;
	private static final int EDGE_STACKS_DETAIL = 1;

	private static final float SELECT_BORDER_RADIUS = 0.0027f;
	private static final int SELECT_BORDER_SLICES_DETAIL = 7;
	private static final int SELECT_BORDER_STACKS_DETAIL = 1;
	
	private static final double SELECT_BORDER_DISTANCE = 0.91;
	
	private static final double RETICLE_DISTANCE = 0.06;
	private static final double RETICLE_RADIUS = 0.012;
	private static final double RETICLE_LENGTH = 0.03;;
	
	private int nodeListIndex;
	private int edgeListIndex;
	
	private int reticleListIndex;
	private int selectBorderListIndex;
	
	private long startTime;
	private long endTime;
	private int framesElapsed = 0;
	private int screenHeight;
	private int screenWidth;

	private int nodeSeed = 556;
	private int edgeSeed = 556;
	
	private LinkedHashSet<CyNode> selectedNodes;
	private LinkedHashSet<CyEdge> selectedEdges;
	
	private TreeSet<Integer> selectedNodeIndices;
	private TreeSet<Integer> selectedEdgeIndices;

	private LinkedHashSet<CyNode> dragHoveredNodes;
	private LinkedHashSet<CyEdge> dragHoveredEdges;
	
	private TreeSet<Integer> dragHoveredNodeIndices;
	private TreeSet<Integer> dragHoveredEdgeIndices;
	
	private static int NULL_COORDINATE = Integer.MIN_VALUE;
	
	private boolean dragSelectMode;
	private int selectTopLeftX;
	private int selectTopLeftY;
	
	private int selectBottomRightX;
	private int selectBottomRightY;
	
	
	// TODO: NO_INDEX relies on cytoscape's guarantee that node and edge indices are nonnegative
	private static final int NO_INDEX = -1; // Value representing that no node or edge index is being held
	private int hoverNodeIndex;
	private int hoverEdgeIndex;
	
	// private LinkedHashSet<CyNode>
	
	private static enum DrawStateModifier {
	    HOVERED, SELECTED, NORMAL, ENLARGED, SELECT_BORDER, RETICLE
	}
	
	 private static final int NO_TYPE = -1;
	 private static final int NODE_TYPE = 0;
	 private static final int EDGE_TYPE = 1;
	
	private KeyboardMonitor keys;
	private MouseMonitor mouse;
	private SimpleCamera camera;
	
	private CyApplicationManager applicationManager;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private RenderingEngineManager renderingEngineManager;
	
	private CyNetworkView networkView;
	private VisualLexicon visualLexicon;
	
	private TextRenderer textRenderer;
	
	private boolean latch_1;
	
	private Vector3 currentSelectedProjection;
	private Vector3 previousSelectedProjection;
	private double selectProjectionDistance;

	private class PickResult {
		public int type;
		public int index;
	}
	
	private class PickResults {
		public LinkedHashSet<Integer> nodeIndices = new LinkedHashSet<Integer>();
		public LinkedHashSet<Integer> edgeIndices = new LinkedHashSet<Integer>();
	}
	
	public static void initSingleton() {
		GLProfile.initSingleton(false);
		System.out.println("initSingleton called");
	}
	
	public Graphics(CyNetworkView networkView, VisualLexicon visualLexicon) {
		keys = new KeyboardMonitor();
		mouse = new MouseMonitor();

		// TODO: add default constant speeds for camera movement
		camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0, 0, 0),
				new Vector3(0, 1, 0), 0.04, 0.0033, 0.01, 0.01, 0.4);
		
		this.networkView = networkView;
		this.visualLexicon = visualLexicon;
		
		selectedNodes = new LinkedHashSet<CyNode>();
		selectedEdges = new LinkedHashSet<CyEdge>();
		
		selectedNodeIndices = new TreeSet<Integer>();
		selectedEdgeIndices = new TreeSet<Integer>();

		hoverNodeIndex = NO_INDEX;
		hoverEdgeIndex = NO_INDEX;
		
		dragSelectMode = false;
		selectTopLeftX = NULL_COORDINATE;
		selectTopLeftY = NULL_COORDINATE;
		
		selectBottomRightX = NULL_COORDINATE;
		selectBottomRightY = NULL_COORDINATE;
		
		textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 36));
	}
	
	public void trackInput(Component component) {
		component.addMouseListener(mouse);
		component.addMouseMotionListener(mouse);
		component.addMouseWheelListener(mouse);
		component.addFocusListener(mouse);
		
		component.addKeyListener(keys);
		component.addFocusListener(keys);
	}
	
	public void setManagers(CyApplicationManager applicationManager,
			CyNetworkManager networkManager,
			CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager) {
		this.applicationManager = applicationManager;
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		
		//drawable.swapBuffers();
		GL2 gl = drawable.getGL().getGL2();
		
		// Check input
		checkInput(gl);

		// Reset scene
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		// System.out.println(position + " " + target + " " + up);
		
		
//		Vector3 projection = projectMouseCoordinates(camera.getDistance());
//		gl.glPushMatrix();
//		gl.glTranslated(projection.x(), projection.y(), -camera.getDistance());
//		gl.glColor3f(0.85f, 0.85f, 0.83f);
//		gl.glCallList(pointerListIndex);
//		gl.glPopMatrix();
		
		GLU glu = new GLU();
		glu.gluLookAt(position.x(), position.y(), position.z(), target.x(),
				target.y(), target.z(), up.x(), up.y(), up.z());


		// Draw mouse reticle
		// ------------------
		
		if (!dragSelectMode) {
			//gl.glPushMatrix();
			//gl.glTranslated(rightPointer.x(), rightPointer.y(), rightPointer.z());
			
			Vector3 projection = projectMouseCoordinates(camera.getDistance());
			
			Vector3 leftOffset = camera.getLeft().multiply(RETICLE_DISTANCE);
			Vector3 upOffset = camera.getUp().multiply(RETICLE_DISTANCE);
			
			Vector3 left = projection.add(leftOffset);
			Vector3 right = projection.subtract(leftOffset);
			Vector3 top = projection.add(upOffset);
			Vector3 bottom = projection.subtract(upOffset);
			
			gl.glColor3f(0.6f, 0.4f, 0.4f);
			
			gl.glPushMatrix();
			setUpFacingTransformation(gl, left, leftOffset.multiply(-1));
			gl.glCallList(reticleListIndex);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			setUpFacingTransformation(gl, right, leftOffset);
			gl.glCallList(reticleListIndex);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			setUpFacingTransformation(gl, top, upOffset.multiply(-1));
			gl.glCallList(reticleListIndex);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			setUpFacingTransformation(gl, bottom, upOffset);
			gl.glCallList(reticleListIndex);
			gl.glPopMatrix();
			
			//gl.glTranslated(projection.x(), projection.y(), projection.z());
			//gl.glColor3f(0.93f, 0.23f, 0.32f);
			
			//gl.glCallList(pointerListIndex);
			//gl.glPopMatrix();
		}
		
		// Draw selection box
		// ------------------
		
//		if (selectTopLeftX != NULL_COORDINATE && selectTopLeftY != NULL_COORDINATE 
//				&& selectBottomRightX != NULL_COORDINATE && selectBottomRightY != NULL_COORDINATE) {
		
		if (dragSelectMode) {
			
			drawSelectBox(gl, SELECT_BORDER_DISTANCE);
			
//			
//			gl.glLineWidth(3.0f);
//			gl.glBegin(GL2.GL_LINE_LOOP);
//			
//				gl.glVertex3d(topLeft.x(), topLeft.y(), topLeft.z());
//				gl.glVertex3d(bottomLeft.x(), bottomLeft.y(), bottomLeft.z());
//				gl.glVertex3d(bottomRight.x(), bottomRight.y(), bottomRight.z());
//				gl.glVertex3d(topRight.x(), topRight.y(), topRight.z());
//				
//			gl.glEnd();
//			gl.glLineWidth(1.0f);
		}
		
		// Control light positioning
		// -------------------------
		
		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
//				FloatBuffer.wrap(lightPosition));


		// Draw nodes and edges
		// --------------------
		
		gl.glColor3f(0.73f, 0.73f, 0.73f);
		drawNodes(gl);
		gl.glColor3f(0.51f, 0.51f, 0.53f);
		//gl.glColor3f(0.53f, 0.53f, 0.55f);
		//gl.glColor3f(0.73f, 0.73f, 0.73f);
		drawEdges(gl, DrawStateModifier.NORMAL);

		drawNodeNames(gl);
		
		framesElapsed++;
	}
	
	private void checkInput(GL2 gl) {
		
		// Project mouse coordinates into 3d space for mouse interactions
		// --------------------------------------------------------------
		
		Vector3 projection = projectMouseCoordinates(camera.getDistance());

		
		if (keys.hasHeld() || keys.hasNew()) {
			Set<Integer> pressed = keys.getPressed();
			Set<Integer> held = keys.getHeld();
			Set<Integer> released = keys.getReleased();
			
			// Display FPS
			if (pressed.contains(KeyEvent.VK_SPACE)) {
				endTime = System.nanoTime();

				double duration = (endTime - startTime) / Math.pow(10, 9);
				double frameRate = framesElapsed / duration;
				System.out.println("Average fps over " + duration + " seconds: "
						+ frameRate);
				
				startTime = System.nanoTime();
				framesElapsed = 0;
			}
			
			// Reset Camera to default
			if (pressed.contains(KeyEvent.VK_C)) {
				camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0, 0, 0),
						new Vector3(0, 1, 0), 0.04, 0.002, 0.01, 0.01, 0.4);
			}
			
			if (pressed.contains(KeyEvent.VK_K)) {
				generalLayout();
			}
			
			// Debug-related boolean
			if (pressed.contains(KeyEvent.VK_1)) {
				latch_1 = true;
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
				CyNode hoverNode = networkView.getModel().getNode(hoverNodeIndex);
				
				if (hoverNode != null) {
					
					for (CyNode node : selectedNodes) {
						networkView.getModel().addEdge(node, hoverNode, false);
						
						// TODO: Not sure if this call is needed
						networkView.updateView();
					};
				}
			}
			
			// Delete selected edges/nodes
			if (pressed.contains(KeyEvent.VK_DELETE)) {
				LinkedHashSet<CyEdge> edgesToBeRemoved = new LinkedHashSet<CyEdge>();
				
				for (CyNode node : selectedNodes) {
					// TODO: Check if use of Type.ANY for any edge is correct
					// TODO: Check if this addAll method properly skips adding edges already in the edgesToBeRemovedList
					edgesToBeRemoved.addAll(networkView.getModel().getAdjacentEdgeList(node, Type.ANY));
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
					viewAdded.setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, projection.x() * DISTANCE_SCALE);
					viewAdded.setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, projection.y() * DISTANCE_SCALE);
					viewAdded.setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, projection.z() * DISTANCE_SCALE);
					
					// Set the node to be hovered
					// TODO: This might not be needed if the node were added through some way other than the mouse
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
				CyNode hoverNode = networkView.getModel().getNode(hoverNodeIndex);
				
				if (hoverNode != null && selectedNodes.size() == 1) {
					View<CyNode> hoverView = networkView.getNodeView(hoverNode);
					View<CyNode> selectView = hoverView;
					for (CyNode node : selectedNodes) {selectView = networkView.getNodeView(node);};
					
					Vector3 hover = new Vector3(hoverView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
							hoverView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
							hoverView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
					
					Vector3 select = new Vector3(selectView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
							selectView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
							selectView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
					
					System.out.println("Distance: " + hover.distance(select)/DISTANCE_SCALE);
				}
			}
			
			keys.update();
		}
		
		
		// Perform picking-related operations
		// ----------------------------------
		
		PickResults pickResults = performPick(gl, mouse.x(), mouse.y(), 2, 2, false);
		
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
					// TODO: Check if this is a suitable place to put this, as it helps to make node dragging smoother
					selectProjectionDistance = findAveragePosition(selectedNodes).distance(camera.getPosition());
				}
			}
			
			
			// If the left button was clicked, prepare to select nodes/edges
			if (mouse.getPressed().contains(MouseEvent.BUTTON1) && !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
				
				// If the user did not hold down shift, unselect other objects
				if (!keys.getHeld().contains(KeyEvent.VK_SHIFT)) {
					selectedNodes.clear();
					selectedEdges.clear();
					
					selectedNodeIndices.clear();
					selectedEdgeIndices.clear();
					
					// System.out.println("Selection reset");
				}
			
				// Prepare to perform drag selection
				// ---------------------------------
				
				selectTopLeftX = mouse.x();
				selectTopLeftY = mouse.y();
				
				selectBottomRightX = NULL_COORDINATE;
				selectBottomRightY = NULL_COORDINATE;
				
				// ----------------------------------
				
				if (newHoverNodeIndex != NO_INDEX) {
					CyNode picked = networkView.getModel().getNode(newHoverNodeIndex);
					
					// TODO: Possibly throw exception if the node was found to be null, ie. invalid index
					if (picked != null) {
			
						if (selectedNodes.contains(picked)) {
							selectedNodes.remove(picked);
							selectedNodeIndices.remove(picked.getIndex());
						} else {
							selectedNodes.add(picked);
							selectedNodeIndices.add(picked.getIndex());
						}
						
						System.out.println("Selected node index: " + picked.getIndex());
					}
				} else if (newHoverEdgeIndex != NO_INDEX) {
					CyEdge picked = networkView.getModel().getEdge(newHoverEdgeIndex);
					
					// TODO: Possibly throw exception if the edge was found to be null, ie. invalid index
					if (picked != null) {
				
						if (selectedEdges.contains(picked)) {
							selectedEdges.remove(picked);
							selectedEdgeIndices.remove(picked.getIndex());
						} else {
							selectedEdges.add(picked);
							selectedEdgeIndices.add(picked.getIndex());
						}
						
						System.out.println("Selected edge index: " + picked.getIndex());
					}
				} else {
					
					// System.out.println("Nothing selected");
				}
				
			}
			
			// Drag selection; moving the box
			if (mouse.getHeld().contains(MouseEvent.BUTTON1) && !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
				selectBottomRightX = mouse.x();
				selectBottomRightY = mouse.y();
				
				if (Math.abs(selectTopLeftX - selectBottomRightX) >= 1 
						&& Math.abs(selectTopLeftY - selectBottomRightY) >= 1 &&
						selectTopLeftX != NULL_COORDINATE && 
						selectTopLeftY != NULL_COORDINATE) {
					
//					PickResults results = performPick(gl, (selectTopLeftX + selectBottomRightX)/2, 
//							(selectTopLeftY + selectBottomRightY)/2, 
//							Math.abs(selectTopLeftX - selectBottomRightX),
//							Math.abs(selectTopLeftY - selectBottomRightY), true);
					
					dragSelectMode = true;
				} else {
					dragSelectMode = false;
				}
			}
			
			// Drag selection; selecting contents of the box
			if (mouse.getReleased().contains(MouseEvent.BUTTON1) && !keys.getHeld().contains(KeyEvent.VK_CONTROL)
					&& dragSelectMode) {
				selectBottomRightX = mouse.x();
				selectBottomRightY = mouse.y();
				
				if (Math.abs(selectTopLeftX - selectBottomRightX) >= 1 
						&& Math.abs(selectTopLeftY - selectBottomRightY) >= 1) {
					
	//				System.out.println("Selection from (" + selectTopLeftX + ", " + selectTopLeftY + ") to "
	//						+ "(" + selectBottomRightX + ", " + selectBottomRightY + ")");
					
					PickResults results = performPick(gl, (selectTopLeftX + selectBottomRightX)/2, 
							(selectTopLeftY + selectBottomRightY)/2, 
							Math.abs(selectTopLeftX - selectBottomRightX),
							Math.abs(selectTopLeftY - selectBottomRightY), true);
					
					CyNode node;
					for (Integer nodeIndex : results.nodeIndices) {
						node = networkView.getModel().getNode(nodeIndex);
						
						if (node != null) {
							selectedNodes.add(node);
							selectedNodeIndices.add(nodeIndex);
						} else {
							System.out.println("Null node found for index " + nodeIndex + " in drag selection, ignoring..");
						}
					}
					
					CyEdge edge;
					for (Integer edgeIndex : results.edgeIndices) {
						edge = networkView.getModel().getEdge(edgeIndex);
						
						if (edge != null) {
							selectedEdges.add(edge);
							selectedEdgeIndices.add(edgeIndex);
						} else {
							System.out.println("Null edge found for index " + edgeIndex + " in drag selection, ignoring..");
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
			
			if (mouse.getPressed().contains(MouseEvent.BUTTON1) && !selectedNodes.isEmpty()) {
				// Store the result for use for mouse-difference related calculations
				selectProjectionDistance = findAveragePosition(selectedNodes).distance(camera.getPosition());
				
				previousSelectedProjection = projectMouseCoordinates(selectProjectionDistance);
			// } else if (mouse.getHeld().contains(MouseEvent.BUTTON1) && !selectedNodes.isEmpty() && previousSelectProjection != null) {
			} else if (keys.getHeld().contains(KeyEvent.VK_CONTROL) && mouse.getHeld().contains(MouseEvent.BUTTON1) && !selectedNodes.isEmpty() && previousSelectedProjection != null) {
				View<CyNode> nodeView;
				Vector3 projectionDisplacement;
				
				currentSelectedProjection = projectMouseCoordinates(selectProjectionDistance);
				projectionDisplacement = currentSelectedProjection.subtract(previousSelectedProjection);
				
				double x, y, z;
				
				for (CyNode node : selectedNodes) {
					// TODO: This relies on an efficient traversal of selected nodes, as well
					// as efficient retrieval from the networkView object
					nodeView = networkView.getNodeView(node);
					
					if (nodeView != null) {
						x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION) + projectionDisplacement.x() * DISTANCE_SCALE;
						y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION) + projectionDisplacement.y() * DISTANCE_SCALE;
						z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION) + projectionDisplacement.z() * DISTANCE_SCALE;
						
						nodeView.setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, x);
						nodeView.setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, y);
						nodeView.setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, z);
						
					}
				}
				
				previousSelectedProjection = currentSelectedProjection;
				
				selectTopLeftX = NULL_COORDINATE;
				selectTopLeftY = NULL_COORDINATE;
			}
			
			
			mouse.update();
		}
	}
	
	private Vector3 projectMouseCoordinates(double planeDistance) {
		return projectMouseCoordinates(mouse.x(), mouse.y(), planeDistance);
	}
	
	/**
	 * Converts 2D mouse coordinates to 3D coordinates, where the coordinate for the
	 * 3rd dimension is specified by the distance between the camera and the plane
	 * which intersects a line passing through the eye and the cursor location
	 * 
	 * @param x
	 * 			x window coordinate of the mouse
	 * @param y
	 * 			y window coordinate of the mouse
	 * @param planeDistance
	 * 			the distance between the camera and the intersecting plane
	 * @return
	 * 			the 3D position of the mouse
	 */
	private Vector3 projectMouseCoordinates(int x, int y, double planeDistance) {
		
		// Project mouse coordinates into 3d space for mouse interactions
		// --------------------------------------------------------------
		
		// Hnear = 2 * tan(fov / 2) * nearDist
		// in our case: 
		//   fov = 45 deg
		//   nearDist = 0.2
		
		double fieldOfView = Math.PI / 4;
		double nearDistance = 0.2;
		
		double nearPlaneHeight = 2 * Math.tan(fieldOfView / 2) * nearDistance;
		double nearPlaneWidth = nearPlaneHeight * screenWidth / screenHeight;
		
		double percentMouseOffsetX = (double) (x - screenWidth) / screenWidth + 0.5;
		double percentMouseOffsetY = (double) (y - screenHeight) / screenHeight + 0.5;
		
		// OpenGL has up as the positive y direction, whereas the mouse is at (0, 0) in the top left
		percentMouseOffsetY = -percentMouseOffsetY;
		
		double nearX = percentMouseOffsetX * nearPlaneWidth;
		double nearY = percentMouseOffsetY * nearPlaneHeight;
		
		// Obtain the near plane position vector
		Vector3 nearPosition;
		nearPosition = new Vector3(camera.getDirection());
		nearPosition.multiplyLocal(nearDistance);
		
		nearPosition.addLocal(camera.getPosition());
		nearPosition.addLocal(camera.getUp().multiply(nearY));
		nearPosition.addLocal(camera.getLeft().multiply(-nearX)); // Note that nearX is positive to the right
		
		// Obtain the projection direction vector
		Vector3 projectionDirection = nearPosition.subtract(camera.getPosition());
		projectionDirection.normalizeLocal();
		
		double angle = projectionDirection.angle(camera.getDirection());
		double projectionDistance = (planeDistance) / Math.cos(angle);
		
		Vector3 projection = projectionDirection.multiply(projectionDistance);
		// projection.addLocal(camera.getPosition());
		// projection.addLocal(camera.getPosition().subtract(eye));
		projection.addLocal(camera.getPosition());
		
		return projection;
	}
	
	
	/**
	 * Obtain the average position of a set of nodes, where each node has the same
	 * weight in the average
	 * 
	 * @param nodes
	 * 				the {@link Collection} of nodes
	 * @return
	 * 				the average position
	 */
	private Vector3 findAveragePosition(Collection<CyNode> nodes) {
		if (nodes.isEmpty()) {
			return null;
		}
		
		double x = 0;
		double y = 0;
		double z = 0;
		
		View<CyNode> nodeView;
		
		for (CyNode node : nodes) {
			// TODO: This relies on an efficient traversal of nodes, as well
			// as efficient retrieval from the networkView object
			nodeView = networkView.getNodeView(node);
			
			if (nodeView != null) {
				x += nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION);
			} else {
				System.out.println("Node with no view found: " + node + ", index: " + node.getIndex());
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		result.divideLocal(DISTANCE_SCALE * nodes.size());
		
		return result;
	}
	
	private PickResults performPick(GL2 gl, int x, int y, int width, int height, boolean selectAll) {
		int bufferSize = 1024;
		
		if (selectAll) {
			bufferSize = 8128;
		}
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer buffer = byteBuffer.asIntBuffer();
		
		IntBuffer viewport = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		
		gl.glSelectBuffer(bufferSize/4, buffer);
	    gl.glRenderMode(GL2.GL_SELECT);
	    gl.glInitNames();
	    
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glPushMatrix();
	    
	    GLU glu = new GLU();
	    gl.glLoadIdentity();
	
	    glu.gluPickMatrix(x, screenHeight - y, width, height, viewport);
	    glu.gluPerspective(45.0f, (float) screenWidth / screenHeight, 0.2f, 50.0f);
	    
	    // don't think this ortho call is needed
	    // gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
	    
	    // --Begin Drawing--
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    gl.glLoadIdentity();
	    
	    //gl.glPushMatrix();
		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		glu.gluLookAt(position.x(), position.y(), position.z(), target.x(),
				target.y(), target.z(), up.x(), up.y(), up.z());

		gl.glPushName(NODE_TYPE);
		gl.glPushName(NO_INDEX);
		
		// Render nodes for picking
		drawNodes(gl);
		
		gl.glPopName();
		gl.glPopName();
		
		gl.glPushName(EDGE_TYPE);
		gl.glPushName(NO_INDEX);
		
		// Render edges for picking
		drawEdges(gl, DrawStateModifier.ENLARGED);
		
		gl.glPopName();
		gl.glPopName();
		
		// --End Drawing--
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glPopMatrix();
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    
	    // not sure if this is needed
	    // gl.glFlush();

	    int hits = gl.glRenderMode(GL2.GL_RENDER);
	    
	    // System.out.println("Number of hits: " + hits);
	    int selectedIndex;
	    int selectedType;
	    
	    // Current hit record is size 5 because we have (numNames, minZ, maxZ, name1, name2) for
	    // indices 0-4 respectively
	    int sizeOfHitRecord = 5;
	    
	    PickResults results = new PickResults();
	    
		if (hits > 0) {
			// The variable max helps keep track of the polygon that is closest
			// to the front of the screen
	    	int max = buffer.get(2);
	    	int maxType = buffer.get(3);
	    	
	    	selectedType = buffer.get(3);
	    	selectedIndex = buffer.get(4);
	    	
//	    	// Check for out of bounds exceptions
//	    	if (hits * sizeOfHitRecord >= bufferSize) {
//	    		System.out.println(hits * sizeOfHitRecord + " exceeds picking buffer size " + bufferSize
//	    				+ ". Truncating extra hits..");
//	    		
//	    		// TODO: Check if this is needed
//	    		
//	    		// Perform truncation to prevent error if not enough room to store all records
//	    		hits = bufferSize / sizeOfHitRecord;
//	    		
//	    	}
	    	
	    	// Drag-selection; select all
	    	if (selectAll) {
	    		for (int i = 0; i < hits; i++) {
			    	
	    			selectedType = buffer.get(i * sizeOfHitRecord + 3);
	    			selectedIndex = buffer.get(i * sizeOfHitRecord + 4);
	    			
	    			if (selectedType == NODE_TYPE) {
						results.nodeIndices.add(selectedIndex);
					} else if (selectedType == EDGE_TYPE) {
						results.edgeIndices.add(selectedIndex);
					}
		    	}
			// Single selection
			} else {
		    	for (int i = 0; i < hits; i++) {
		    	
		    		if (buffer.get(i * sizeOfHitRecord + 2) <= max && buffer.get(i * sizeOfHitRecord + 3) <= maxType) {
		    			max = buffer.get(i * sizeOfHitRecord + 2);
		    			maxType = buffer.get(i * sizeOfHitRecord + 3);
		    			
		    			selectedType = buffer.get(i * sizeOfHitRecord + 3); // We have that name1 represents the object type
		    			selectedIndex = buffer.get(i * sizeOfHitRecord + 4); // name2 represents the object index		
		    		}
		    	}
	    	
		    	if (selectedType == NODE_TYPE) {
					results.nodeIndices.add(selectedIndex);
				} else if (selectedType == EDGE_TYPE) {
					results.edgeIndices.add(selectedIndex);
				}
			}
	    }
		
	    return results;
	}

	private void drawNodes(GL2 gl) {
		float x, y, z;
		int index;
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
			
			index = nodeView.getModel().getIndex();
			gl.glLoadName(index);
			// gl.glLoadName(33);
			
			gl.glPushMatrix();
			gl.glTranslatef(x, y, z);
			
			if (selectedNodeIndices.contains(index)) {
				gl.glColor3f(0.52f, 0.70f, 0.52f);
				gl.glScalef(1.1f, 1.1f, 1.1f);
				gl.glCallList(nodeListIndex);
			} else if (index == hoverNodeIndex) {
				gl.glColor3f(0.52f, 0.52f, 0.70f);
				gl.glCallList(nodeListIndex);
			} else {
				gl.glColor3f(0.73f, 0.73f, 0.73f);
				gl.glCallList(nodeListIndex);
			}
			
			gl.glPopMatrix();
		}
		
		// Draw the testNode
		// gl.glTranslatef(testNode.x, testNode.y, testNode.z);
		// gl.glCallList(nodeListIndex);
		// GLUT glut = new GLUT();
		// glut.glutSolidCylinder(EDGE_RADIUS, 1,
		//		EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
		// gl.glTranslatef(-testNode.x, -testNode.y, -testNode.z);
	}
	
	private void drawNodeNames(GL2 gl) {
		float x, y, z;
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
			
			gl.glLoadName(NO_INDEX);
			
			float offsetDistance = -0.05f;
			x += camera.getDirection().x() * offsetDistance;
			y += camera.getDirection().y() * offsetDistance;
			z += camera.getDirection().z() * offsetDistance;
			
			String text = "node: " + nodeView.getVisualProperty(MinimalVisualLexicon.NODE_LABEL);
			x += camera.getLeft().x() * (textRenderer.getBounds(text).getWidth() / 2);
			y += camera.getLeft().y() * (textRenderer.getBounds(text).getWidth() / 2);
			z += camera.getLeft().z() * (textRenderer.getBounds(text).getWidth() / 2);
			
			x -= camera.getUp().x() * (textRenderer.getBounds(text).getHeight() / 2);
			y -= camera.getUp().y() * (textRenderer.getBounds(text).getHeight() / 2);
			z -= camera.getUp().z() * (textRenderer.getBounds(text).getHeight() / 2);
			
			gl.glPushMatrix();
			
			setUpFacingTransformation(gl, new Vector3(x, y, z), camera.getDirection().multiply(-1));
			
			gl.glRotated(camera.getUp().angle(new Vector3(0, 0, 1)), camera.getDirection().x(), camera.getDirection().y(), camera.getDirection().z());
			
			textRenderer.begin3DRendering();
			textRenderer.draw3D(text, 0, 0, 0, 0.002f);
			textRenderer.end3DRendering();
			
			gl.glPopMatrix();
		}
		
		
		
	}

	// This method will draw all edges
	private void drawEdges(GL2 gl, DrawStateModifier generalModifier) {
		View<CyNode> sourceView;
		View<CyNode> targetView;
		
		int nodeCount = networkView.getModel().getNodeCount();
		
		int sourceIndex;
		int targetIndex;
		
		// A unique identifier (as far as this method is concerned) for each pair of nodes
		long pairIdentifier;
		
		TreeMap<Long, Integer> pairs = new TreeMap<Long, Integer>();
		
		// Points 0 and 2 represent endpoints of the quadratic Bezier curve, while
		// point 1 represents the approach point
		Vector3 p0 = new Vector3();
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();
		
		Vector3 p1Offset;
		Vector3 direction;
		
		int edgeIndex;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			
			sourceView = networkView.getNodeView(edgeView.getModel().getSource());
			targetView = networkView.getNodeView(edgeView.getModel().getTarget());
			sourceIndex = sourceView.getModel().getIndex();
			targetIndex = targetView.getModel().getIndex();
			
			edgeIndex = edgeView.getModel().getIndex();
			
			// These indices rely on CyNode's guarantee that NodeIndex < NumOfNodes
			assert sourceIndex < nodeCount;
			assert targetIndex < nodeCount;
			
			// Identify this pair of nodes so we'll know if we've drawn an edge between them before
			pairIdentifier = ((long) Integer.MAX_VALUE + 1) * sourceIndex + targetIndex; // TODO: Check if this is a safe calculation

			// Commenting the below will remove distinguishment between source and target nodes
			if (sourceIndex > targetIndex) {
				pairIdentifier = ((long) Integer.MAX_VALUE + 1) * targetIndex + sourceIndex;
			} else {
				pairIdentifier = ((long) Integer.MAX_VALUE + 1) * sourceIndex + targetIndex;
			}
			
			// Have we visited an edge between these nodes before?
			if (pairs.containsKey(pairIdentifier)) {
				pairs.put(pairIdentifier, pairs.get(pairIdentifier) + 1);
			} else {
				pairs.put(pairIdentifier, 0);
			}
		
			// Find p0, p1, p2 for the Bezier curve
			p0.set(sourceView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					sourceView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					sourceView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			p0.divideLocal(DISTANCE_SCALE);
			
			p2.set(targetView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					targetView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					targetView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			p2.divideLocal(DISTANCE_SCALE);
			
			p1 = p0.add(p2);
			p1.divideLocal(2);
			
			direction = p2.subtract(p0);
			p1Offset = direction.cross(0, 1, 0);
			p1Offset.normalizeLocal();
			
			// Multiplier controlling distance between curve point p1 and the straight line between the nodes p0 and p2
			int distanceMultiplier = (int) Math.sqrt(pairs.get(pairIdentifier));
			
			int radiusEdgeCount = distanceMultiplier * 2 + 1;
			
			// Multiplier controlling rotation about the p0p2 vector axis
			int rotationMultiplier = pairs.get(pairIdentifier);
			
			// Shift the square root graph one to the left and one down to get smoother curves
			p1Offset.multiplyLocal(distanceMultiplier * EDGE_CURVE_FACTOR * (Math.sqrt(direction.magnitude() + 1) - 1)); //TODO: Check if sqrt is needed
			
			if (distanceMultiplier % 2 == 1) {
				p1Offset = p1Offset.rotate(direction, 2 * Math.PI * rotationMultiplier / radiusEdgeCount);
			} else {
				p1Offset = p1Offset.rotate(direction, 2 * Math.PI * rotationMultiplier / radiusEdgeCount + Math.PI);
			}
			
			p1.addLocal(p1Offset);
			
			if (latch_1) {
				System.out.println("Source index: " + sourceIndex);
				System.out.println("Source target: " + targetIndex);
				System.out.println("pairs.get(pairIdentifier): " + pairs.get(pairIdentifier));
				System.out.println("pairIdentifier: " + pairIdentifier);
			}
		
			// Load name for edge picking
			gl.glLoadName(edgeIndex);
			
			DrawStateModifier modifier; 
			if (generalModifier == DrawStateModifier.ENLARGED) {
				modifier = DrawStateModifier.ENLARGED;
			} else if (selectedEdgeIndices.contains(edgeIndex)) {
				modifier = DrawStateModifier.SELECTED;
			} else if (edgeIndex == hoverEdgeIndex) {
				modifier = DrawStateModifier.HOVERED;
			} else {
				modifier = DrawStateModifier.NORMAL;
			}
			
			if (distanceMultiplier == 0) {
				drawQuadraticEdge(gl, p0, p1, p2, 1, modifier);
			} else {
				drawQuadraticEdge(gl, p0, p1, p2, 5, modifier);
			}
		}
		
		latch_1 = false;
	}
	
	
	/**
	 * Draws an edge shaped around a quadratic Bezier curve
	 * 
	 * @param gl
	 * 			{@link GL2} rendering object
	 * @param p0
	 * 			the starting point, p0
	 * @param p1
	 * 			the approach point, p1
	 * @param p2
	 * 			the end point, p2
	 * @param numSegments
	 * 			the number of straight-line segments used to approximate the Bezier curve
	 * @param modifier
	 * 			a modifier to change the appearance of the edge object
	 */
	private void drawQuadraticEdge(GL2 gl, Vector3 p0, Vector3 p1, Vector3 p2, int numSegments, DrawStateModifier modifier) {
		// TODO: Allow the minimum distance to be changed
		if (p0.distanceSquared(p2) < MINIMUM_EDGE_DRAW_DISTANCE_SQUARED) {
			return;
		}
		
		// Equation for Quadratic Bezier curve:
		// B(t) = (1 - t)^2P0 + 2(1 - t)tP1 + t^2P2, t in [0, 1]
		
		double parameter;
		
		Vector3 current;
		Vector3[] points = new Vector3[numSegments + 1];
		double[] pointAngle = new double[numSegments + 1];
		
		Vector3 lastDirection = null;
		Vector3 currentDirection;
		
		points[0] = new Vector3(p0);
		for (int i = 1; i < numSegments; i++) {
			// Obtain points along the Bezier curve
			parameter = (double) i / numSegments;
			
			current = p0.multiply(Math.pow(1 - parameter, 2));
			current.addLocal(p1.multiply(2 * (1 - parameter) * parameter));
			current.addLocal(p2.multiply(parameter * parameter));
			
			points[i] = new Vector3(current);
			
			// Obtain the angle between the ith and (i - 1)th segment, for i in the open interval (1, numSegments)
			currentDirection = points[i].subtract(points[i - 1]);
			if (lastDirection != null) {
				// Note that this loop can only find the angle at the (i - 1)th point
				pointAngle[i - 1] = lastDirection.angle(currentDirection);
			}
			lastDirection = currentDirection;
		}
		
		// Obtain the angle between the last 2 segments, if there is more than 1 segment
		if (numSegments > 1) {
			pointAngle[numSegments - 1] = p2.subtract(points[numSegments - 1]).angle(lastDirection);
		}
		points[numSegments] = new Vector3(p2);
		
		double currentAngle;
		double extend1 = 0;
		double extend2;
		Vector3 direction;
		for (int i = 0; i < numSegments; i++) {
			currentAngle = pointAngle[i + 1];
			
			// currentAngle is likely calculated from an acos operation
			assert (!Double.isNaN(currentAngle));
			
			// Extend by c * tan(theta/2)
			extend2 = EDGE_RADIUS * Math.tan(currentAngle / 2);
	
			direction = points[i + 1].subtract(points[i]);
			direction.normalizeLocal();
	
//			if (framesElapsed == 2) {
//				System.out.println("Segment: " + i);
//				System.out.println("Current segment angle: " + currentAngle);
//				System.out.println("Current extend1: " + extend1);
//				System.out.println("Current extend2: " + extend2);
//				System.out.println("Current p0: " + p0);
//				System.out.println("Current p1: " + p1);
//				System.out.println("Current p2: " + p2);
//				System.out.println("Current direction: " + direction);
//			}
			
//			drawSingleEdge(gl, 
//					points[i].subtract(direction.multiply(extend1)),
//			 		points[i + 1].add(direction.multiply(extend2)));
			
			drawSingleEdge(gl, 
					points[i],
					points[i + 1],
					modifier);

//			if (framesElapsed == 3) {
//				if (points[i].subtract(direction.multiply(extend1)).magnitude() > 100) {
//					System.out.println("points[i]: " + points[i].subtract(direction.multiply(extend1)));
//				}
//				
//				if (points[i + 1].add(direction.multiply(extend2)).magnitude() > 100) {
//					System.out.println("points[i + 1]: " + points[i + 1].add(direction.multiply(extend2)));
//					System.out.println("extend2 info, Segment: " + i);
//					System.out.println("Current segment angle: " + currentAngle);
//					System.out.println("Current extend1: " + extend1);
//					System.out.println("Current extend2: " + extend2);
//					System.out.println("Current p0: " + p0);
//					System.out.println("Current p1: " + p1);
//					System.out.println("Current p2: " + p2);
//					System.out.println("Current direction: " + direction);
//				}
//			}
			
			extend1 = extend2;
		}
		
		
	}
	
	private void drawEdgesOld(GL2 gl) {
		View<CyNode> sourceView;
		View<CyNode> targetView;
		
		float x1, x2, y1, y2, z1, z2;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {

			sourceView = networkView.getNodeView(edgeView.getModel().getSource());
			targetView = networkView.getNodeView(edgeView.getModel().getTarget());
		
			x1 = sourceView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y1 = sourceView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z1 = sourceView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
		
			x2 = targetView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y2 = targetView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z2 = targetView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
		
			// drawSingleEdge(gl, x1, y1, z1, x2, y2, z2);
		}
	}
	
	/** 
	 * Set up matrix transformations such that the position is equal to the location vector 
	 * and the z-axis is in the direction of the given direction
	 * 
	 * @param gl
	 * 			{@link GL2} rendering object
	 * @param location
	 * 			desired position
	 * @param direction
	 * 			desired direction, does not have to be a unit vector
	 * 			
	 */
	private void setUpFacingTransformation(GL2 gl, Vector3 location, Vector3 direction) {
		gl.glTranslated(location.x(), location.y(), location.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 rotateAxis = current.cross(direction);
		
		gl.glRotated(Math.toDegrees(direction.angle(current)), rotateAxis.x(), rotateAxis.y(),
				rotateAxis.z());
	}
	
	/**
	 * Draws a single edge-like graphics object
	 * 
	 * @param gl
	 * 			{@link GL2} rendering object
	 * @param start
	 * 			start location
	 * @param end
	 * 			end location
	 * @param modifier
	 * 			a modifier to vary the appearance of the output
	 */
	private void drawSingleEdge(GL2 gl, Vector3 start, Vector3 end, DrawStateModifier modifier) {
		gl.glPushMatrix();
		
		Vector3 direction = end.subtract(start);
		
		setUpFacingTransformation(gl, start, direction);
		
		gl.glScalef(1.0f, 1.0f, (float) direction.magnitude());
		
		if (modifier == DrawStateModifier.NORMAL) {
			gl.glColor3f(0.73f, 0.73f, 0.73f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.ENLARGED) {
			gl.glScalef(1.6f, 1.6f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.SELECTED) {
			gl.glColor3f(0.48f, 0.65f, 0.48f);
			gl.glScalef(1.1f, 1.1f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.HOVERED) {
			gl.glColor3f(0.45f, 0.45f, 0.70f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.SELECT_BORDER) {
			gl.glColor3f(0.72f, 0.31f, 0.40f);
			gl.glCallList(selectBorderListIndex);
		} else {
			// Invalid modifier found
		}
	
		gl.glPopMatrix();
	}

	/**
	 * Draw the drag selection box
	 * 
	 * @param gl
	 * 			{@link GL2} rendering object
	 * @param drawDistance
	 * 			the distance from the camera to draw the box
	 */
	private void drawSelectBox(GL2 gl, double drawDistance) {
		Vector3 topLeft = projectMouseCoordinates(selectTopLeftX, selectTopLeftY, drawDistance);
		Vector3 bottomLeft = projectMouseCoordinates(selectTopLeftX, selectBottomRightY, drawDistance);
		
		Vector3 topRight = projectMouseCoordinates(selectBottomRightX, selectTopLeftY, drawDistance);
		Vector3 bottomRight = projectMouseCoordinates(selectBottomRightX, selectBottomRightY, drawDistance);

		
		drawSingleSelectEdge(gl, topLeft, topRight);
		drawSingleSelectEdge(gl, topLeft, bottomLeft);
		
		drawSingleSelectEdge(gl, topRight, bottomRight);
		drawSingleSelectEdge(gl, bottomLeft, bottomRight);
		
	}
	
	/**
	 * Draws an edge-like object for the border of the selection box; calculations are made so that
	 * the corners of the box are sharp
	 * 
	 * @param gl
	 * 			{@link GL2} rendering object
	 * @param originalStart
	 * 			the intended start position for this edge, before corrections
	 * @param originalEnd
	 * 			the intended end position for this edge, before corrections
	 */
	private void drawSingleSelectEdge(GL2 gl, Vector3 originalStart, Vector3 originalEnd) {
		Vector3 offset = originalEnd.subtract(originalStart);
		offset.normalizeLocal();
		offset.multiplyLocal(SELECT_BORDER_RADIUS/2);
		
		drawSingleEdge(gl, originalStart.subtract(offset), originalEnd.add(offset), DrawStateModifier.SELECT_BORDER);
	}
	
	private void drawNodesEdges(GL2 gl) {
		gl.glCallList(edgeListIndex);
		gl.glCallList(nodeListIndex);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		initLighting(drawable);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glDepthFunc(GL.GL_LEQUAL);
		// gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

		gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());

		//generateNodes();
		//generateEdges();
		startTime = System.nanoTime();
		createDisplayLists(gl);
		
		// Correct lightning for scaling certain models
		gl.glEnable(GL2.GL_NORMALIZE);
		
		// Enable blending
		// ---------------
		
		// gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void createDisplayLists(GL2 gl) {
		nodeListIndex = gl.glGenLists(1);
		edgeListIndex = gl.glGenLists(1);
		reticleListIndex = gl.glGenLists(1);
		
		selectBorderListIndex = gl.glGenLists(1);
		
		GLUT glut = new GLUT();
		GLU glu = new GLU();

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

		// Draw Node
		// ---------
		
		gl.glNewList(nodeListIndex, GL2.GL_COMPILE);
		glu.gluSphere(quadric, SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
				NODE_STACKS_DETAIL);
		// glut.glutSolidSphere(SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
		// NODE_STACKS_DETAIL);
		gl.glEndList();
		
		// Draw Standard-Length Edge
		// -------------------------
		
		GLUquadric edgeQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(edgeQuadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(edgeQuadric, GLU.GLU_SMOOTH); // TODO: Experiment with GLU_FLAT for efficiency
		
		gl.glNewList(edgeListIndex, GL2.GL_COMPILE);
		glu.gluCylinder(edgeQuadric, EDGE_RADIUS, EDGE_RADIUS, 1.0,
				EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
		gl.glEndList();
		
		// Draw a line for the reticle
		// -----------------------------
		
		GLUquadric reticleQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(reticleQuadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(reticleQuadric, GLU.GLU_SMOOTH);
		
		float axisLength = 0.056f;
		float overHang = 0.028f;
		float radius = 0.002f;
		
		gl.glNewList(reticleListIndex, GL2.GL_COMPILE);
		// glu.gluSphere(pointerQuadric, SMALL_SPHERE_RADIUS / 4, 4, 4);
		
		glu.gluCylinder(reticleQuadric, RETICLE_RADIUS, RETICLE_RADIUS, RETICLE_LENGTH,
				SELECT_BORDER_SLICES_DETAIL, SELECT_BORDER_STACKS_DETAIL);
		
		gl.glEndList();
		
		// Draw Selection Box Border
		// -------------------------
		
		GLUquadric selectBorderQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(selectBorderQuadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(selectBorderQuadric, GLU.GLU_SMOOTH);

		gl.glNewList(selectBorderListIndex, GL2.GL_COMPILE);
		glu.gluCylinder(selectBorderQuadric, SELECT_BORDER_RADIUS, SELECT_BORDER_RADIUS, 1.0,
				SELECT_BORDER_SLICES_DETAIL, SELECT_BORDER_STACKS_DETAIL);
		gl.glEndList();
		
		
		
	}

	private void initLighting(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		float[] global = { 0.2f, 0.2f, 0.2f, 1.0f };

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global));
		gl.glShadeModel(GL2.GL_SMOOTH);

		float[] ambient = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] specular = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] position = { -4.0f, 4.0f, 6.0f, 1.0f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(ambient));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(specular));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));

		gl.glEnable(GL2.GL_LIGHT0);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		
		
		float[] specularReflection = { 0.5f, 0.5f, 0.5f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 40);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		if (height <= 0) {
			height = 1;
		}

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(45.0f, (float) width / height, 0.2f, 50.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		screenHeight = height;
		screenWidth = width;
	}
	
	public void provideCentralView() {
		camera.moveTo(findAveragePosition(networkView.getModel().getNodeList()));
		camera.moveBackward();
		camera.zoomOut(50);
		// camera.
	}
	
	public void generalLayout() {
		
		LinkedHashSet<CyNode> totalNodesToVisit = new LinkedHashSet<CyNode>();
		totalNodesToVisit.addAll(networkView.getModel().getNodeList());
		
		Vector3 currentCentralLocation = new Vector3();
		Vector3 nextCentralLocation = new Vector3();
		
		double greatestDistanceSquared = -1;
		
		// System.out.println("Initial total: " + totalNodesToVisit.size());
		
		
		do {
			
			// Find the central node
			// ---------------------
			
			// Approach: Use node with most edges
			
			LinkedHashSet<CyNode> plantedNodes = new LinkedHashSet<CyNode>();
			
			CyNetwork network = networkView.getModel();
			
			CyNode centerNode = null;
			int highestNeighborCount = -1;
			int neighbourCount;
			
			for (CyNode node : totalNodesToVisit) {
				neighbourCount = network.getNeighborList(node, Type.ANY).size();
				
				if (centerNode == null) {
					centerNode = node;
					highestNeighborCount = neighbourCount;
				} else {
					if (neighbourCount > highestNeighborCount) {
						centerNode = node;
						highestNeighborCount = neighbourCount;
					}
				}
			}
			
			// Mark as planted
			plantedNodes.add(centerNode);
			
			// Plant the center node
			// ---------------------
			
			networkView.getNodeView(centerNode).setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, currentCentralLocation.x());
			networkView.getNodeView(centerNode).setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, currentCentralLocation.y());
			networkView.getNodeView(centerNode).setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, currentCentralLocation.z());
			
			// Plant the first neighbors
			// -------------------------
			
			// Idea: 2nd and further neighbors arranged in an x degree cone 
			// facing outwards from the last edge
		
			HashMap<CyNode, Vector3> outwardDirections = new HashMap<CyNode, Vector3>();
			
			
			double nodeDistance = 1.03 * DISTANCE_SCALE;
			
			LinkedHashSet<CyNode> firstNeighbors = new LinkedHashSet<CyNode>();
			
			// Removes duplicates as well
			firstNeighbors.addAll(network.getNeighborList(centerNode, Type.ANY));
			
			int firstNeighborCount = firstNeighbors.size();
			
			double rotation = 0; 
			
			if (firstNeighborCount > 0) {
				rotation = Math.PI * 2 / firstNeighborCount;
			}
			
			Vector3 current = currentCentralLocation; // TODO: Simplify this part of the code
			Vector3 offset = new Vector3(0, 1, 0);
			offset.multiplyLocal(nodeDistance);
			
			// Pre-rotation
			// offset = offset.rotate(new Vector3(0, 0, 1), Math.random() * 2 * Math.PI);
			
			for (CyNode firstNeighbor : firstNeighbors) {
				networkView.getNodeView(firstNeighbor).setVisualProperty(RichVisualLexicon.NODE_X_LOCATION,
						offset.x() + current.x());
				networkView.getNodeView(firstNeighbor).setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, 
						offset.y() + current.y());
				networkView.getNodeView(firstNeighbor).setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, 
						offset.z() + current.z());
				
				outwardDirections.put(firstNeighbor, offset);
				
				offset = offset.rotate(new Vector3(0, 0, 1), rotation);
				
				// Mark as planted
				plantedNodes.add(firstNeighbor);
				
				// Check for greatest distance
				// ===========================
				
				Vector3 usedLocation = offset.add(current);
				
				if (usedLocation.distanceSquared(currentCentralLocation) > greatestDistanceSquared) {
					greatestDistanceSquared = usedLocation.distanceSquared(currentCentralLocation);
					
					nextCentralLocation = usedLocation.subtract(currentCentralLocation).normalize().multiply(3.4).add(usedLocation);
				}
			}
		
		
			// Plant the next neighbors
			// ------------------------
			
			double conicalAngle = 0.51;
			double outwardProjectionDistance = nodeDistance;
			
			LinkedHashSet<CyNode> currentNeighbors = firstNeighbors;
			LinkedHashSet<CyNode> nextToVisit = new LinkedHashSet<CyNode>();
			
			do {
				
				for (CyNode currentNeighbor : currentNeighbors) {
					
					LinkedHashSet<CyNode> nextNeighbors = new LinkedHashSet<CyNode>();
					
					// This will also remove redundant nodes from the getNeighborList result
					nextNeighbors.addAll(network.getNeighborList(currentNeighbor, Type.ANY));
					
					Vector3 outwardOffset = outwardDirections.get(currentNeighbor);
					outwardOffset.normalizeLocal();
					outwardOffset.multiplyLocal(outwardProjectionDistance);
					
					Vector3 perpendicularOffset = outwardOffset.cross(new Vector3(0, 0, 1));
					perpendicularOffset.normalizeLocal();
					perpendicularOffset.multiplyLocal(nodeDistance * Math.tan(conicalAngle));
					
					double conicalRotation = 0;
					
					if (nextNeighbors.size() > 0) {
						conicalRotation = 2 * Math.PI / nextNeighbors.size();
					}
					
					for (CyNode nextNeighbor : nextNeighbors) {
						
						if (!plantedNodes.contains(nextNeighbor)) {
							networkView.getNodeView(nextNeighbor).setVisualProperty(RichVisualLexicon.NODE_X_LOCATION,
									perpendicularOffset.x() + outwardOffset.x() + 
									networkView.getNodeView(currentNeighbor).
									getVisualProperty(RichVisualLexicon.NODE_X_LOCATION));
							networkView.getNodeView(nextNeighbor).setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION,
									perpendicularOffset.y() + outwardOffset.y() + 
									networkView.getNodeView(currentNeighbor).
									getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION));
							networkView.getNodeView(nextNeighbor).setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION,
									perpendicularOffset.z() + outwardOffset.z() + 
									networkView.getNodeView(currentNeighbor).
									getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
							
							outwardDirections.put(nextNeighbor, perpendicularOffset.add(outwardOffset));
							
							perpendicularOffset = perpendicularOffset.rotate(outwardOffset, conicalRotation);
							
							// Mark as planted
							plantedNodes.add(nextNeighbor);
							
							nextToVisit.add(nextNeighbor);
							
							// Check for greatest distance
							// ===========================
							
							Vector3 currentNeighborLocation = new Vector3(
									networkView.getNodeView(currentNeighbor).
									getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
									networkView.getNodeView(currentNeighbor).
									getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
									networkView.getNodeView(currentNeighbor).
									getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
									
							Vector3 usedLocation = perpendicularOffset.add(outwardOffset).add(currentNeighborLocation);
							
							if (usedLocation.distanceSquared(currentCentralLocation) > greatestDistanceSquared) {
								greatestDistanceSquared = usedLocation.distanceSquared(currentCentralLocation);
								
								nextCentralLocation = usedLocation.subtract(currentCentralLocation).normalize().multiply(3.4).add(usedLocation);
							}
						}
					}
				}
				
				currentNeighbors.clear();
				currentNeighbors.addAll(nextToVisit);
				
				nextToVisit.clear();
				
			} while (!currentNeighbors.isEmpty());
			
			// System.out.println("planted so far: " + plantedNodes.size());
			totalNodesToVisit.removeAll(plantedNodes);
			// System.out.println("total after remove: " + totalNodesToVisit.size());
			
			
			double greatestDistance = Math.sqrt(greatestDistanceSquared) * 1.5;
			
			// currentCentralLocation.set(nextCentralLocation);
			currentCentralLocation.addLocal((Math.random() * 2 * greatestDistance - greatestDistance) * 1.65, 
					(Math.random() * 2 * greatestDistance - greatestDistance) * 1.65,
					(Math.random() * 2 * greatestDistance - greatestDistance) * 0.02);
			
			if (greatestDistanceSquared < 0.2) {
				currentCentralLocation.addLocal(new Vector3(0.6, -0.6, 0));
			}
			
			greatestDistanceSquared = -1;
		} while (!totalNodesToVisit.isEmpty());
	
		camera.moveTo(findAveragePosition(networkView.getModel().getNodeList())
				.subtract(camera.getDirection().multiply(camera.getDistance())));
		camera.zoomOut(20);
	}
	
	/*
	// Draw X axis
	gl.glTranslatef(-overHang, 0.0f, 0.0f);
	gl.glRotatef(90, 0, 1, 0);
	glu.gluCylinder(reticleQuadric, radius, radius, axisLength, 4, 1);
	gl.glRotatef(-90, 0, 1, 0);
	gl.glTranslatef(overHang, 0.0f, 0.0f);
	
	// Draw Y axis
	gl.glTranslatef(0.0f, -overHang, 0.0f);
	gl.glRotatef(-90, 1, 0, 0);
	glu.gluCylinder(reticleQuadric, radius, radius, axisLength, 4, 1);
	gl.glRotatef(90, 1, 0, 0);
	gl.glTranslatef(0.0f, overHang, 0.0f);
	
	// Draw Z axis
	gl.glTranslatef(0.0f, 0.0f, -overHang);
	glu.gluCylinder(reticleQuadric, radius, radius, axisLength, 4, 1);
	gl.glTranslatef(0.0f, 0.0f, overHang);
	*/
}
