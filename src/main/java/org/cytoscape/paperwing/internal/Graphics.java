// If you are visiting this class for the first time,
// consider taking a look at the following files:
//
// src/main/resources/controls.txt -- contains information about controls
// src/main/resources/overview-todo.txt -- contains information about what 
// is to be done

package org.cytoscape.paperwing.internal;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

/** The main class for the Wind rendering engines responsible for
 * creating graphics with the use of the JOGL (Java OpenGL) library
 * 
 * @author Paperwing (Yue Dong)
 */
public class Graphics implements GLEventListener {

	/**
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private static final float DISTANCE_SCALE = 178.0f; 
	
	/** The default radius of the spherical nodes */
	private static final float SMALL_SPHERE_RADIUS = 0.102f; // 0.015f
	
	/** The minimum distance between nodes required for an edge to be drawn */
	private static final float MINIMUM_EDGE_DRAW_DISTANCE_SQUARED = 
		Float.MIN_NORMAL; // 0.015f
	
	/** The default radius of the semi-cylindrical edges */
	private static final float EDGE_RADIUS = 0.018f;
	
	/** A multiplicative curve factor for the edges */
	private static final float EDGE_CURVE_FACTOR = 0.43f; //0.31f
	
	/** A multiplicative factor for the width of the edges when reading from the
	 * visual property mappings
	 */
    private static final float EDGE_WIDTH_FACTOR = 0.17f;
	
	/** How many straight edge segments to use for approximating a curved edge,
	 * this value does not have to be static */
	private static int QUADRATIC_EDGE_SEGMENTS = 5;
	
	/** The slices detail level to use for drawing spherical nodes */
	//10, 10, 4 // 24, 24, 12 used to be default values for slices/stacks/slices
	private static int NODE_SLICES_DETAIL = 10; 
	
	/** The stacks detail level to use for drawing spherical nodes */
	private static int NODE_STACKS_DETAIL = 10;
	
	/** The slices detail level to use for drawing edges */
	private static int EDGE_SLICES_DETAIL = 4;
	
	/** The stacks detail level to use for drawing edges */
	private static int EDGE_STACKS_DETAIL = 1;

	/** The radius to use for the drag selection border's straight segments */
	private static final float SELECT_BORDER_RADIUS = 0.0027f;
	
	/** The slices detail level to use for the selection border's 
	 * straight segments */
	private static final int SELECT_BORDER_SLICES_DETAIL = 7;
	
	/** The stacks detail level to use for the selection border's 
	 * straight segments */
	private static final int SELECT_BORDER_STACKS_DETAIL = 1;
	
	/** The distance from the camera to draw the selection box */
	private static final double SELECT_BORDER_DISTANCE = 0.91;
	
	/** Controls the distance apart to draw the reticle for the mouse */
	private static final double RETICLE_DISTANCE = 0.06;
	
	/** Controls the radius of the reticle */
	private static final double RETICLE_RADIUS = 0.012;
	
	/** The length to draw each segment of the reticle */
	private static final double RETICLE_LENGTH = 0.03;
	
	
	/** The display list index for the nodes */
	private int nodeListIndex;
	
	/** The display list index for the edges */
	private int edgeListIndex;
	
	/** The display list index for the reticle */
	private int reticleListIndex;
	
	/** The display list index for the selection border segments */
	private int selectBorderListIndex;
	
	
	/** Start time used for certain timing */
	private long startTime;
	
	/** End time used for certain timing */
	private long endTime;
	
	/** Number of frames elapsed */
	private int framesElapsed = 0;
	
	
	/** The height of the screen */
	private int screenHeight;
	
	/** The width of the screen */
	private int screenWidth;
	
	
	/** The set of all currently selected nodes */
	private Set<CyNode> selectedNodes;
	
	/** The set of all currently selected edges */
	private Set<CyEdge> selectedEdges;
	
	
	
	/** The set of indices for nodes that are selected */
	private Set<Integer> selectedNodeIndices;
	
	/** The set of indices for edges that are selected */
	private Set<Integer> selectedEdgeIndices;
	
	
	
	/** The NULL coordinate which means "no coordinate" */
	private static int NULL_COORDINATE = Integer.MIN_VALUE;
	
	
	/** A flag for whether drag selection mode is currently active */
	private boolean dragSelectMode;
	
	
	
	/** The top left x position for the selection border */
	private int selectTopLeftX;
	
	/** The top left y position for the selection border */
	private int selectTopLeftY;
	
	/** The bottom right x position for the selection border */
	private int selectBottomRightX;
	
	/** The bottom right y position for the selection border */
	private int selectBottomRightY;
	

	
	/** A constant that stands for "no index is here" */
	// TODO: NO_INDEX relies on cytoscape's guarantee that node and edge indices are nonnegative
	private static final int NO_INDEX = -1; // Value representing that no node or edge index is being held
	
	/** The index of the node currently being hovered over */
	private int hoverNodeIndex;
	
	/** The index of the edge currently being hovered over */
	private int hoverEdgeIndex;
	
	
	
	/** A draw state modifier which can be used to modify the appearance
	 * of certain objects
	 */
	private static enum DrawStateModifier {
	    HOVERED, SELECTED, NORMAL, ENLARGED, SELECT_BORDER
	}
	
	/** A constant that stands for "no type is here" */
	private static final int NO_TYPE = -1;
	
	/** A constant representing the type node */
	private static final int NODE_TYPE = 0;
	
	/** A constant representing the type edge */
	private static final int EDGE_TYPE = 1;
	
	
	
	/** A monitor to keep track of keyboard events */
	private KeyboardMonitor keys;
	
	/** A monitor to keep track of mouse events */
	private MouseMonitor mouse;
	
	/** The camera to use for transformation of 3D scene */
	private SimpleCamera camera;
	
	
	
	/** The application manager for the Cytoscape application */
	private CyApplicationManager applicationManager;
	
	/** The current Cytoscape network manager */
	private CyNetworkManager networkManager;
	
	/** A view manager for network views */
	private CyNetworkViewManager networkViewManager;
	
	/** A rendering engine manager */
	private RenderingEngineManager renderingEngineManager;
	
	
	/** The network view to be rendered */
	private CyNetworkView networkView;
	
	/** The visual lexicon to use */
	private VisualLexicon visualLexicon;
	
	/** A debug boolean */
	private boolean latch_1;
	
	/** A boolean to use lower quality 3D shapes to improve framerate */
	private boolean lowerQuality = false;
	
	/** A boolean to disable real-time shape picking to improve framerate */
	private boolean skipHover = false;
	
	/** A projection of the current mouse position into 3D coordinates to be used 
	 * for mouse drag movement of certain objects */
	private Vector3 currentSelectedProjection;
	
	/** A projection of the mouse position into 3D coordinates to be used 
	 * for mouse drag movement of certain objects */
	private Vector3 previousSelectedProjection;
	
	/** The distance from the projected point to the screen */
	private double selectProjectionDistance;
	
	
	/** A class capable of storing the edge and node indices of edges and nodes
	 * that were found to be selected using the shape picking methods
	 */
	private class PickResults {
		public Set<Integer> nodeIndices = new LinkedHashSet<Integer>();
		public Set<Integer> edgeIndices = new LinkedHashSet<Integer>();
	}
	
	/** Initialize a singleton that seems to help with JOGL in some compatibility
	 * aspects
	 */
	public static void initSingleton() {
		GLProfile.initSingleton(false);
		//System.out.println("initSingleton called");
	}
	
	/** Create a new Graphics object
	 * 
	 * @param networkView The CyNetworkView object, representing the 
	 * View<CyNetwork> object that we are rendering
	 * @param visualLexicon The visual lexicon being used
	 */
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
		
		selectedNodeIndices = new HashSet<Integer>();
		selectedEdgeIndices = new HashSet<Integer>();

		hoverNodeIndex = NO_INDEX;
		hoverEdgeIndex = NO_INDEX;
		
		dragSelectMode = false;
		selectTopLeftX = NULL_COORDINATE;
		selectTopLeftY = NULL_COORDINATE;
		
		selectBottomRightX = NULL_COORDINATE;
		selectBottomRightY = NULL_COORDINATE;
	}
	
	/** Attach the KeyboardMonitor and MouseMonitors, which are listeners,
	 * to the specified component for capturing keyboard and mouse events
	 * 
	 * @param component The component to listen to events for
	 */
	public void trackInput(Component component) {
		component.addMouseListener(mouse);
		component.addMouseMotionListener(mouse);
		component.addMouseWheelListener(mouse);
		component.addFocusListener(mouse);
		
		component.addKeyListener(keys);
		component.addFocusListener(keys);
	}
	
	/** Assign the values of certain managers that were imported as OSGi services
	 * 
	 * @param applicationManager The Cytoscape application's application manager
	 * @param networkManager The network manager
	 * @param networkViewManager The network view manager
	 * @param renderingEngineManager The rendering engine manager
	 */
	public void setManagers(CyApplicationManager applicationManager,
			CyNetworkManager networkManager,
			CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager) {
		this.applicationManager = applicationManager;
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
	}

	/** Main drawing method; can be called by an {@link Animator} such as
	 * {@link FPSAnimator}, responsible for drawing the scene and advancing
	 * the frame
	 * 
	 * @param drawable The GLAutoDrawable object used for rendering
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		
		GL2 gl = drawable.getGL().getGL2();
		
		// Check input
		checkInput(gl);

		// Reset scene
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		
		GLU glu = new GLU();
		glu.gluLookAt(position.x(), position.y(), position.z(), target.x(),
				target.y(), target.z(), up.x(), up.y(), up.z());

		// Draw mouse reticle
		// ------------------
		
		if (!dragSelectMode) {
			
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
		}
		
		// Draw selection box
		// ------------------

		if (dragSelectMode) {
			
			drawSelectBox(gl, SELECT_BORDER_DISTANCE);
		}
		
		// Control light positioning
		// -------------------------
		
		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
		
		// Code below toggles the light following the camera
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
		// FloatBuffer.wrap(lightPosition));


		// Draw nodes and edges
		// --------------------
		
		drawNodes(gl);
		drawEdges(gl, DrawStateModifier.NORMAL);
		
		framesElapsed++;
	}
	
	/** Obtain input and check for changes in the keyboard and mouse buttons,
	 * as well as mouse movement. This method also handles responses
	 * to such events
	 * 
	 * @param gl The {@link GL2} object used for rendering
	 */
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
						
						//System.out.println("Selected node index: " + picked.getIndex());
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
						
						//System.out.println("Selected edge index: " + picked.getIndex());
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
							//System.out.println("Null node found for index " + nodeIndex + " in drag selection, ignoring..");
						}
					}
					
					CyEdge edge;
					for (Integer edgeIndex : results.edgeIndices) {
						edge = networkView.getModel().getEdge(edgeIndex);
						
						if (edge != null) {
							selectedEdges.add(edge);
							selectedEdgeIndices.add(edgeIndex);
						} else {
							//System.out.println("Null edge found for index " + edgeIndex + " in drag selection, ignoring..");
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
	
	/** Projects the current mouse coordinates into 3D coordinates
	 * using the camera's direction vector, the camera's location,
	 * and a given distance from the camera
	 * 
	 * @param planeDistance The distance away from the camera used
	 * to generate the 2D plane that is perpendicular to the camera
	 * @return The 3D coordinates as a Vector3 object
	 */
	private Vector3 projectMouseCoordinates(double planeDistance) {
		return projectMouseCoordinates(mouse.x(), mouse.y(), planeDistance);
	}
	
	/**
	 * Converts 2D mouse coordinates to 3D coordinates, where the coordinate 
	 * for the 3rd dimension is specified by the distance between the camera 
	 * and the plane which intersects a line passing through the eye and 
	 * the cursor location
	 * 
	 * @param x The x window coordinate of the mouse
	 * @param y The y window coordinate of the mouse
	 * @param planeDistance The distance between the camera and the 
	 * intersecting plane
	 * @return The 3D position of the mouse
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
		// Note that nearX is positive to the right
		nearPosition.addLocal(camera.getLeft().multiply(-nearX)); 
		
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
	 * @param nodes The {@link Collection} of nodes
	 * @return The average position
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
				System.out.println("Node with no view found: " + node + 
						", index: " + node.getIndex());
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		result.divideLocal(DISTANCE_SCALE * nodes.size());
		
		return result;
	}
	
	/** Perform a picking operation on the specified region to capture
	 * 3D shapes drawn in the given region
	 * 
	 * @param gl The {@link GL2} object used for rendering
	 * @param x The center x location, in window coordinates
	 * @param y The center y location, in window coordinates
	 * @param width The width of the box used for picking
	 * @param height The height of the box used for picking
	 * @param selectAll Whether or not to select all shapes captured
	 * in the given region, or only to only take the frontmost one
	 * @return The edges and nodes found in the region, as a 
	 * {@link PickResults} object
	 */
	private PickResults performPick(GL2 gl, int x, int y, int width, 
			int height, boolean selectAll) {
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
	    
	    int selectedIndex;
	    int selectedType;
	    
	    // Current hit record is size 5 because we have 
	    // (numNames, minZ, maxZ, name1, name2) for
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
		    	
		    		if (buffer.get(i * sizeOfHitRecord + 2) <= 
		    			max && buffer.get(i * sizeOfHitRecord + 3) <= maxType) {
		    			
		    			max = buffer.get(i * sizeOfHitRecord + 2);
		    			maxType = buffer.get(i * sizeOfHitRecord + 3);
		    			
		    			// We have that name1 represents the object type
		    			selectedType = buffer.get(i * sizeOfHitRecord + 3); 
		    			
		    			// name2 represents the object index		
		    			selectedIndex = buffer.get(i * sizeOfHitRecord + 4);
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

	/** Draw all the nodes onto the screen, taking into account certain visual
	 * properties
	 * 
	 * @param gl The {@link GL2} object used for rendering
	 */
	private void drawNodes(GL2 gl) {
		// Currently supporting the following visual properties
		
		// VisualProperty<Double> NODE_X_LOCATION
		// VisualProperty<Double> NODE_Y_LOCATION
		// VisualProperty<Double> NODE_Z_LOCATION
		// VisualProperty<Paint> NODE_PAINT
		// VisualProperty<Boolean> NODE_VISIBLE
		// VisualProperty<Boolean> NODE_SELECTED
		// VisualProperty<Double> NODE_WIDTH
		// VisualProperty<Double> NODE_HEIGHT
		// VisualProperty<Double> NODE_DEPTH

		// Uncertain about the following visual properties
		
		// VisualProperty<Paint> NODE_FILL_COLOR
		
		float x, y, z;
		int index;
		networkView.updateView();
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
				.floatValue() / DISTANCE_SCALE;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
				.floatValue() / DISTANCE_SCALE;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
				.floatValue() / DISTANCE_SCALE;
			
			index = nodeView.getModel().getIndex();
			gl.glLoadName(index);
			// gl.glLoadName(33);
			
			gl.glPushMatrix();
			gl.glTranslatef(x, y, z);
			
			
			gl.glScalef(nodeView.getVisualProperty(
							MinimalVisualLexicon.NODE_WIDTH).floatValue() 
							/ DISTANCE_SCALE, 
					nodeView.getVisualProperty(
							MinimalVisualLexicon.NODE_HEIGHT).floatValue() 
							/ DISTANCE_SCALE, 
					nodeView.getVisualProperty(
							RichVisualLexicon.NODE_DEPTH).floatValue() 
							/ DISTANCE_SCALE);
			
			Color color;
			
			if (selectedNodeIndices.contains(index)) {
				
				gl.glScalef(1.1f, 1.1f, 1.1f);
				
				color = (Color) nodeView.getVisualProperty(
						RichVisualLexicon.NODE_SELECTED_PAINT);
				
				gl.glColor3f(color.getRed() / 255.0f, 
						color.getGreen() / 255.0f,
						color.getBlue() / 255.0f);
				
				nodeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED, 
						true);
				
				// Default color is below
				// gl.glColor3f(0.52f, 0.70f, 0.52f);
			} else if (index == hoverNodeIndex) {
				gl.glColor3f(0.52f, 0.52f, 0.70f);
				
				nodeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED, 
						false);
			} else {
				color = (Color) nodeView.getVisualProperty(
						MinimalVisualLexicon.NODE_PAINT);

				gl.glColor3f(color.getRed() / 255.0f, 
						color.getGreen() / 255.0f,
						color.getBlue() / 255.0f);

				nodeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED, 
						false);
				
				// Default color is below
				// gl.glColor3f(0.73f, 0.73f, 0.73f);
				
			}
			
			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(MinimalVisualLexicon.NODE_VISIBLE)) {
				gl.glCallList(nodeListIndex);
			}
			
			gl.glPopMatrix();
		}
	}
	
	/** Draw all edges onto the screen, taking into account
	 * certain visual properties such as color
	 * 
	 * @param gl The {@link GL2} object used for rendering
	 * @param generalModifier A modifier to be applied to the drawn results.
	 * {@link DrawStateModifier}.ENLARGED is used for picking while using
	 * OpenGL's GL_SELECT rendering mode.
	 */
	private void drawEdges(GL2 gl, DrawStateModifier generalModifier) {
		// Indirectly supporting the following visual properties
		// VisualProperty<Paint> EDGE_PAINT
		// VisualProperty<Double> EDGE_WIDTH
		// VisualProperty<Paint> EDGE_SELECTED_PAINT
		
		// Directly supporting the following visual properties
		// VisualProperty<Boolean> EDGE_VISIBLE
		// VisualProperty<Boolean> EDGE_SELECTED
		
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
			
			// Identify this pair of nodes so we'll know if we've drawn an 
			// edge between them before
			// TODO: Check if this is a safe calculation
			pairIdentifier = ((long) Integer.MAX_VALUE + 1) * sourceIndex + targetIndex; 

			// Commenting the below will remove distinguishment between source and 
			//target nodes
			if (sourceIndex > targetIndex) {
				pairIdentifier = ((long) Integer.MAX_VALUE + 1) * targetIndex 
					+ sourceIndex;
			} else {
				pairIdentifier = ((long) Integer.MAX_VALUE + 1) * sourceIndex 
					+ targetIndex;
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
			
			// Multiplier controlling distance between curve point p1 and the 
			// straight line between the nodes p0 and p2
			int distanceMultiplier = (int) Math.sqrt(pairs.get(pairIdentifier));
			
			int radiusEdgeCount = distanceMultiplier * 2 + 1;
			
			// Multiplier controlling rotation about the p0p2 vector axis
			int rotationMultiplier = pairs.get(pairIdentifier);
			
			// Shift the square root graph one to the left and one down 
			// to get smoother curves
			//TODO: Check if sqrt is needed
			p1Offset.multiplyLocal(distanceMultiplier * EDGE_CURVE_FACTOR * 
					(Math.sqrt(direction.magnitude() + 1) - 1)); 
			
			if (distanceMultiplier % 2 == 1) {
				p1Offset = p1Offset.rotate(direction, 2 * Math.PI * 
						rotationMultiplier / radiusEdgeCount);
			} else {
				p1Offset = p1Offset.rotate(direction, 2 * Math.PI * 
						rotationMultiplier / radiusEdgeCount + Math.PI);
			}
			
			p1.addLocal(p1Offset);
			
			if (latch_1) {
				System.out.println("Source index: " + sourceIndex);
				System.out.println("Source target: " + targetIndex);
				System.out.println("pairs.get(pairIdentifier): " + 
						pairs.get(pairIdentifier));
				System.out.println("pairIdentifier: " + pairIdentifier);
			}
		
			// Load name for edge picking
			gl.glLoadName(edgeIndex);
			
			edgeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED, 
					false);
			
			DrawStateModifier modifier; 
			if (generalModifier == DrawStateModifier.ENLARGED) {
				modifier = DrawStateModifier.ENLARGED;
			} else if (selectedEdgeIndices.contains(edgeIndex)) {
				modifier = DrawStateModifier.SELECTED;
				
				edgeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED, 
						true);
			} else if (edgeIndex == hoverEdgeIndex) {
				modifier = DrawStateModifier.HOVERED;
			} else {
				modifier = DrawStateModifier.NORMAL;
			}
			
			// Draw it only if the visual property says it is visible
			if (edgeView.getVisualProperty(MinimalVisualLexicon.EDGE_VISIBLE)) {

				if (distanceMultiplier == 0) {
					drawQuadraticEdge(gl, p0, p1, p2, 1, modifier, edgeView);
				} else {
					drawQuadraticEdge(gl, p0, p1, p2, QUADRATIC_EDGE_SEGMENTS, 
							modifier, edgeView);
				}
			}
		}
		
		latch_1 = false;
	}
	
	
	/** Draws an edge shaped around a quadratic Bezier curve
	 * 
	 * @param gl {@link GL2} rendering object
	 * @param p0 The starting point for the Bezier curve, p0
	 * @param p1 The approach point, p1
	 * @param p2 Tthe end point, p2
	 * @param numSegments The number of straight-line segments used to 
	 * approximate the Bezier curve
	 * @param modifier A modifier to change the appearance of the edge object
	 */
	private void drawQuadraticEdge(GL2 gl, Vector3 p0, Vector3 p1, Vector3 p2, int numSegments, DrawStateModifier modifier, View<CyEdge> edgeView) {
		// TODO: Allow the minimum distance to be changed
		if (p0.distanceSquared(p2) < MINIMUM_EDGE_DRAW_DISTANCE_SQUARED) {
			return;
		}
		
		// Equation for Quadratic Bezier curve:
		// B(t) = (1 - t)^2P0 + 2(1 - t)tP1 + t^2P2, t in [0, 1]
		
		double parameter;
		
		Vector3 current;
		Vector3[] points = new Vector3[numSegments + 1];
		
		points[0] = new Vector3(p0);
		for (int i = 1; i < numSegments; i++) {
			// Obtain points along the Bezier curve
			parameter = (double) i / numSegments;
			
			current = p0.multiply(Math.pow(1 - parameter, 2));
			current.addLocal(p1.multiply(2 * (1 - parameter) * parameter));
			current.addLocal(p2.multiply(parameter * parameter));
			
			points[i] = new Vector3(current);
		}
		
		points[numSegments] = new Vector3(p2);
		
		for (int i = 0; i < numSegments; i++) {
			
			drawSingleEdge(gl, 
					points[i],
					points[i + 1],
					modifier,
					edgeView);
		}		
	}
	
	/** Set up matrix transformations such that the position is 
	 * equal to the location vector and the z-axis is in the direction 
	 * of the given direction
	 * 
	 * @param gl The {@link GL2} rendering object
	 * @param location The desired position
	 * @param direction The desired direction, does not have to be a 
	 * unit vector
	 * 			
	 */
	private void setUpFacingTransformation(GL2 gl, Vector3 location, Vector3 direction) {
		gl.glTranslated(location.x(), location.y(), location.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 rotateAxis = current.cross(direction);
		
		gl.glRotated(Math.toDegrees(direction.angle(current)), rotateAxis.x(), rotateAxis.y(),
				rotateAxis.z());
	}
	
	/** Draws a single edge-like object
	 * 
	 * @param gl The {@link GL2} rendering object
	 * @param start The start location
	 * @param end The end location
	 * @param modifier A modifier to vary the appearance of the output
	 */
	private void drawSingleEdge(GL2 gl, Vector3 start, Vector3 end, 
			DrawStateModifier modifier, View<CyEdge> edgeView) {
		// Directly supporting th following visual properties
		// VisualProperty<Paint> EDGE_PAINT
		// VisualProperty<Double> EDGE_WIDTH
		// VisualProperty<Paint> EDGE_SELECTED_PAINT
		
		gl.glPushMatrix();
		
		Vector3 direction = end.subtract(start);
		
		setUpFacingTransformation(gl, start, direction);
		
		// Perform a transformation to adjust length
		gl.glScalef(1.0f, 1.0f, (float) direction.magnitude());
		
		float width = edgeView.getVisualProperty(RichVisualLexicon.EDGE_WIDTH)
			.floatValue() * EDGE_WIDTH_FACTOR;
		
		// Perform a transformation to adjust width
		gl.glScalef(width, width, 1.0f);
		
		Color color;
		
		if (modifier == DrawStateModifier.NORMAL) {
			color = (Color) edgeView.getVisualProperty(
					RichVisualLexicon.EDGE_PAINT);
			
			gl.glColor3f(color.getRed() / 255.0f, 
					color.getGreen() / 255.0f,
					color.getBlue() / 255.0f);
			
			// Default color is below
			// gl.glColor3f(0.73f, 0.73f, 0.73f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.ENLARGED) {
			gl.glScalef(1.6f, 1.6f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.SELECTED) {
			color = (Color) edgeView.getVisualProperty(
					RichVisualLexicon.EDGE_SELECTED_PAINT);
			
			gl.glColor3f(color.getRed() / 255.0f, 
					color.getGreen() / 255.0f,
					color.getBlue() / 255.0f);
			
			// Default color below
			// gl.glColor3f(0.48f, 0.65f, 0.48f);
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

	/** Draw the drag selection box
	 * 
	 * @param gl The {@link GL2} rendering object
	 * @param drawDistance The distance from the camera to draw the box
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
	
	/** Draws an edge-like object for the border of the selection box; 
	 * calculations are made so that the corners of the box are sharp
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
		
		Vector3 newStart = originalStart.subtract(offset);
		Vector3 newEnd = originalEnd.add(offset);
		
		gl.glPushMatrix();
		
		Vector3 direction = newEnd.subtract(newStart);
		
		setUpFacingTransformation(gl, newStart, direction);
		
		gl.glScalef(1.0f, 1.0f, (float) direction.magnitude());
		
		
		gl.glColor3f(0.72f, 0.31f, 0.40f);
		gl.glCallList(selectBorderListIndex);

		
		gl.glPopMatrix();
	}
	
	@Override
	public void dispose(GLAutoDrawable autoDrawable) {

	}

	/** Initialize the Graphics object, performing certain
	 * OpenGL initializations
	 */
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

	/** Create the display lists that are used to draw the edges and nodes,
	 * as well as certain other objects
	 * 
	 * @param gl {@link GL2} rendering object
	 */
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
		
		// Older default values
		// float[] specularReflection = { 0.5f, 0.5f, 0.5f, 1.0f };
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
		// FloatBuffer.wrap(specularReflection));
		// gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 40);
		
		float[] specularReflection = { 0.46f, 0.46f, 0.46f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 31);
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
	
	/** Move the camera so that it zooms out on a central part of the network,
	 * but this method is not finalized yet
	 */
	public void provideCentralView() {
		camera.moveTo(new Vector3(0, 0, 0));
		
		if (findAveragePosition(networkView.getModel().getNodeList()) != null) {
			camera.moveTo(findAveragePosition(networkView.getModel().getNodeList()));
		}
		camera.moveBackward();
		camera.zoomOut(40);
	}
}
