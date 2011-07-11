package org.cytoscape.paperwing.internal;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedHashSet;
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
import com.jogamp.opengl.util.gl2.GLUT;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class Graphics implements GLEventListener {

	private static final float LARGE_SPHERE_RADIUS = 1.0f; // 1.5f
	private static final float SMALL_SPHERE_RADIUS = 0.102f; // 0.015f
	private static final float MINIMUM_EDGE_DRAW_DISTANCE_SQUARED = Float.MIN_NORMAL; // 0.015f
	
	private static final float EDGE_RADIUS = 0.018f;
	
	private static final float EDGE_CURVE_DISTANCE = 0.7f;
	private static final float EDGE_CURVE_FACTOR = 0.43f; //0.31f
	private static final int EDGES_PER_RADIUS = 3;
	
	/*
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private static final float DISTANCE_SCALE = 178.0f; 
	
	private static final int NODE_SLICES_DETAIL = 10; // 24, 24, 12 used to be default values for slices/stacks/slices
	private static final int NODE_STACKS_DETAIL = 10;
	private static final int EDGE_SLICES_DETAIL = 4;
	private static final int EDGE_STACKS_DETAIL = 1;

	private int nodeListIndex;
	private int edgeListIndex;
	
	private int pointerListIndex;
	
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
	
	// TODO: NO_INDEX relies on cytoscape's guarantee that node and edge indices are nonnegative
	private static final int NO_INDEX = -1; // Value representing that no node or edge index is being held
	private int hoverNodeIndex = NO_INDEX;
	private int hoverEdgeIndex = NO_INDEX;
	
	private static enum DrawStateModifier {
	    HOVERED, SELECTED, NORMAL, ENLARGED
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
	
	private boolean latch_1;
	
	private Vector3 currentSelectedProjection;
	private Vector3 previousSelectedProjection;
	private double selectProjectionDistance;

	private class PickResult {
		public int type;
		public int index;
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
		
		checkInput(gl);

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

		// gl.glRotated(direction.angle(current) * 180 / Math.PI, normal.x(),
		// normal.y(), normal.z());
		// gl.glTranslated(-camera.x(), -camera.y(), -camera.z());

		double distance = target.distance(position);
		Vector3 leftPointer = target.add(camera.getLeft().multiply(distance / 2));
		Vector3 rightPointer = target.subtract(camera.getLeft().multiply(distance / 2));
		
		gl.glPushMatrix();
		//gl.glTranslated(rightPointer.x(), rightPointer.y(), rightPointer.z());
		
		Vector3 projection = projectMouseCoordinates(camera.getDistance());
		
		gl.glTranslated(projection.x(), projection.y(), projection.z());
		gl.glColor3f(0.85f, 0.85f, 0.83f);
		gl.glCallList(pointerListIndex);
		gl.glPopMatrix();
		
		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
//				FloatBuffer.wrap(lightPosition));

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		// gl.glTranslatef(0.0f, 0.0f, -6.0f);

		gl.glColor3f(0.73f, 0.73f, 0.73f);
		drawNodes(gl);
		gl.glColor3f(0.51f, 0.51f, 0.53f);
		//gl.glColor3f(0.53f, 0.53f, 0.55f);
		//gl.glColor3f(0.73f, 0.73f, 0.73f);
		drawEdges(gl, DrawStateModifier.NORMAL);

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
			
			// Debug-related boolean
			if (pressed.contains(KeyEvent.VK_1)) {
				latch_1 = true;
			}
			
			// Roll camera clockwise
			if (held.contains(KeyEvent.VK_Z)) {
				camera.rollClockwise();
			}
			
			// Roll camera clockwise
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
		
		PickResult pickResult = performPick(gl, mouse.x(), mouse.y());
		int pickType = pickResult.type;
		int pickIndex = pickResult.index;
		
		if (pickType == NODE_TYPE) {
			hoverNodeIndex = pickIndex;
			hoverEdgeIndex = NO_INDEX;
		} else if (pickType == EDGE_TYPE) {
			hoverNodeIndex = NO_INDEX;
			hoverEdgeIndex = pickIndex;
		} else {
			// Note that if these 2 lines are removed, hovering will be "sticky" in that hovering remains unless a new object is hovered
			hoverNodeIndex = NO_INDEX;
			hoverEdgeIndex = NO_INDEX;
		}
		
		
		if (keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
			hoverNodeIndex = NO_INDEX;
			hoverEdgeIndex = NO_INDEX;
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
					selectProjectionDistance = findSelectionMidpoint().distance(camera.getPosition());
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
				
				if (pickType == NODE_TYPE) {
					CyNode picked = networkView.getModel().getNode(pickIndex);
					
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
				} else if (pickType == EDGE_TYPE) {
					CyEdge picked = networkView.getModel().getEdge(pickIndex);
					
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
			
			
			// Drag-move selected nodes using projected cursor location
			// --------------------------------------------------------
			
			if (mouse.getPressed().contains(MouseEvent.BUTTON1) && !selectedNodes.isEmpty()) {
				// Store the result for use for mouse-difference related calculations
				selectProjectionDistance = findSelectionMidpoint().distance(camera.getPosition());
				
				previousSelectedProjection = projectMouseCoordinates(selectProjectionDistance);
			// } else if (mouse.getHeld().contains(MouseEvent.BUTTON1) && !selectedNodes.isEmpty() && previousSelectProjection != null) {
			} else if ((selectedNodes.size() == 1 || keys.getHeld().contains(KeyEvent.VK_CONTROL)) && mouse.getHeld().contains(MouseEvent.BUTTON1) && !selectedNodes.isEmpty() && previousSelectedProjection != null) {
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
			}
			
			
			mouse.update();
		}
	}
	
	private Vector3 projectMouseCoordinates(double planeDistance) {
		
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
		
		double percentMouseOffsetX = (double) (mouse.x() - screenWidth) / screenWidth + 0.5;
		double percentMouseOffsetY = (double) (mouse.y() - screenHeight) / screenHeight + 0.5;
		
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
	
	private Vector3 findSelectionMidpoint() {
		if (selectedNodes.isEmpty()) {
			return null;
		}
		
		View<CyNode> nodeView;
		double x = 0;
		double y = 0;
		double z = 0;
		
		for (CyNode node : selectedNodes) {
			// TODO: This relies on an efficient traversal of selected nodes, as well
			// as efficient retrieval from the networkView object
			nodeView = networkView.getNodeView(node);
			
			x += nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION);
			y += nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION);
			z += nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION);
		}
		
		Vector3 result = new Vector3(x, y, z);
		result.divideLocal(DISTANCE_SCALE * selectedNodes.size());
		
		return result;
	}
	
	private PickResult performPick(GL2 gl, double x, double y) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1028);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer buffer = byteBuffer.asIntBuffer();
		
		IntBuffer viewport = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		
		gl.glSelectBuffer(256, buffer);
	    gl.glRenderMode(GL2.GL_SELECT);
	    gl.glInitNames();
	    
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glPushMatrix();
	    
	    GLU glu = new GLU();
	    gl.glLoadIdentity();
	
	    glu.gluPickMatrix(x, screenHeight - y, 2, 2, viewport);
	    glu.gluPerspective(45.0f, (float) screenWidth / screenHeight, 0.2f, 50.0f);
	    
	    // don't think this ortho call is needed
	    // gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
	    
	    // -Begin Drawing-
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
		
		// -End Drawing-
		
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
	    
		if (hits > 0) {
			// The variable max helps keep track of the polygon that is closest
			// to the front of the screen
	    	int max = buffer.get(2);
	    	int maxType = buffer.get(3);
	    	
	    	selectedType = buffer.get(3);
	    	selectedIndex = buffer.get(4);
	    	
	    	for (int i = 0; i < hits; i++) {
	    		
	    		// TODO: place check here so we don't go out of bounds (the Maximum Size was set at declaration of ByteBuffer)
	    		
	    		if (buffer.get(i * sizeOfHitRecord + 2) <= max && buffer.get(i * sizeOfHitRecord + 3) <= maxType) {
	    			max = buffer.get(i * sizeOfHitRecord + 2);
	    			maxType = buffer.get(i * sizeOfHitRecord + 3);
	    			
	    			selectedType = buffer.get(i * sizeOfHitRecord + 3); // We have that name1 represents the object type
	    			selectedIndex = buffer.get(i * sizeOfHitRecord + 4); // name2 represents the object index
	    		}
	    	}
	    } else {
	    	selectedType = NO_TYPE;
	    	selectedIndex = NO_INDEX;
	    }
		
		PickResult result = new PickResult();
		result.type = selectedType;
		result.index = selectedIndex;
	
	    return result;
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
	
	private void drawSingleEdge(GL2 gl, Vector3 start, Vector3 end, DrawStateModifier modifier) {
		gl.glPushMatrix();
		
		// TODO: Consider using a Vector3f object for just floats, to use translatef instead of translated
		gl.glTranslated(start.x(), start.y(), start.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 direction = end.subtract(start);
		
		Vector3 rotateAxis = current.cross(direction);

		gl.glRotated(Math.toDegrees(direction.angle(current)), rotateAxis.x(), rotateAxis.y(),
				rotateAxis.z());
		
		gl.glScalef(1.0f, 1.0f, (float) direction.magnitude());
		
		if (modifier == DrawStateModifier.ENLARGED) {
			gl.glScalef(1.6f, 1.6f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.SELECTED) {
			gl.glColor3f(0.48f, 0.65f, 0.48f);
			gl.glScalef(1.1f, 1.1f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.HOVERED) {
			gl.glColor3f(0.45f, 0.45f, 0.70f);
			gl.glCallList(edgeListIndex);
		} else {
			gl.glColor3f(0.73f, 0.73f, 0.73f);
			gl.glCallList(edgeListIndex);
		}
	
		gl.glPopMatrix();
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
	}

	private void createDisplayLists(GL2 gl) {
		nodeListIndex = gl.glGenLists(1);
		edgeListIndex = gl.glGenLists(1);
		pointerListIndex = gl.glGenLists(1);
		
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
		
		// Draw Pointer
		// ------------
		
		GLUquadric pointerQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(pointerQuadric, GLU.GLU_LINE);
		glu.gluQuadricNormals(pointerQuadric, GLU.GLU_NONE);
		
		float axisLength = 0.055f;
		float overHang = 0.0225f;
		float radius = 0.003f;
		
		gl.glNewList(pointerListIndex, GL2.GL_COMPILE);
		// glu.gluSphere(pointerQuadric, SMALL_SPHERE_RADIUS / 4, 4, 4);
		
		// gl.glColor3f(0.73f, 0.73f, 0.72f);
		
		// Draw X axis
		gl.glTranslatef(-overHang, 0.0f, 0.0f);
		gl.glRotatef(90, 0, 1, 0);
		glu.gluCylinder(edgeQuadric, radius, radius, axisLength, 4, 1);
		gl.glRotatef(-90, 0, 1, 0);
		gl.glTranslatef(overHang, 0.0f, 0.0f);
		
		// Draw Y axis
		gl.glTranslatef(0.0f, -overHang, 0.0f);
		gl.glRotatef(-90, 1, 0, 0);
		glu.gluCylinder(edgeQuadric, radius, radius, axisLength, 4, 1);
		gl.glRotatef(90, 1, 0, 0);
		gl.glTranslatef(0.0f, overHang, 0.0f);
		
		// Draw Z axis
		gl.glTranslatef(0.0f, 0.0f, -overHang);
		glu.gluCylinder(edgeQuadric, radius, radius, axisLength, 4, 1);
		gl.glTranslatef(0.0f, 0.0f, overHang);
		
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
	
	/*
	// Draw X axis gl.glTranslatef(-overhang, 0.0f, 0.0f);
	gl.glRotatef(90, 0, 1, 0); gl.glColor3f(1.0f, 0.0f, 0.0f);
	glut.glutSolidCylinder(0.005f, axisLength, 6, 3); gl.glRotatef(-90, 0, 1, 0); gl.glTranslatef(overhang, 0.0f, 0.0f);
	
	// Draw Y axis gl.glTranslatef(0.0f, -overhang, 0.0f);
	gl.glRotatef(-90, 1, 0, 0); gl.glColor3f(0.0f, 1.0f, 0.0f);
	glut.glutSolidCylinder(0.005f, axisLength, 6, 3); gl.glRotatef(90, 1, 0, 0); gl.glTranslatef(0.0f, overhang, 0.0f);
	
	// Draw Z axis gl.glTranslatef(0.0f, 0.0f, -overhang);
	gl.glColor3f(0.0f, 0.0f, 1.0f); glut.glutSolidCylinder(0.005f,
	axisLength, 6, 3); gl.glTranslatef(0.0f, 0.0f, overhang);
	*/
}
