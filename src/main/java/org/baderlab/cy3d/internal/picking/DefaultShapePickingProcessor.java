package org.baderlab.cy3d.internal.picking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.data.PickingData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.input.KeyboardMonitor;
import org.baderlab.cy3d.internal.input.MouseMonitor;
import org.baderlab.cy3d.internal.rendering.ReadOnlyGraphicsProcedure;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class DefaultShapePickingProcessor implements ShapePickingProcessor {

	/** A constant that stands for "no type is here" */
	public static final int NO_TYPE = -1;

	/** A constant representing the type node */
	public static final int NODE_TYPE = 0;

	/** A constant representing the type edge */
	public static final int EDGE_TYPE = 1;

	/** A constant that stands for "no index is here" */
	// TODO: NO_INDEX relies on cytoscape's guarantee that node and edge indices
	// are nonnegative
	public static final int NO_INDEX = -1; // Value representing that no node
											// or edge index is being held

	private ReadOnlyGraphicsProcedure drawNodesProcedure;
	private ReadOnlyGraphicsProcedure drawEdgesProcedure;
	
	public DefaultShapePickingProcessor(ReadOnlyGraphicsProcedure drawNodesProcedure, ReadOnlyGraphicsProcedure drawEdgesProcedure) {
		this.drawNodesProcedure = drawNodesProcedure;
		this.drawEdgesProcedure = drawEdgesProcedure;
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		drawNodesProcedure.initialize(graphicsData);
		drawEdgesProcedure.initialize(graphicsData);
	}
	
	@Override
	public void processPicking(MouseMonitor mouse, KeyboardMonitor keys, GraphicsData graphicsData) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
	
		if (selectionData.isDragSelectMode()) {
			int selectionBoxCenterX = (selectionData.getSelectTopLeftX() + selectionData.getSelectBottomRightX()) / 2;
			int selectionBoxCenterY = (selectionData.getSelectTopLeftY() + selectionData.getSelectBottomRightY()) / 2;
			int selectionBoxWidth = Math.max(1, Math.abs(selectionData.getSelectTopLeftX() - selectionData.getSelectBottomRightX()));
			int selectionBoxHeight = Math.max(1, Math.abs(selectionData.getSelectTopLeftY() - selectionData.getSelectBottomRightY()));
			
			performPick(selectionBoxCenterX, selectionBoxCenterY, selectionBoxWidth, selectionBoxHeight, true, graphicsData);
		} else {
			performPick(mouse.x(), mouse.y(), 2, 2, false, graphicsData);
		}
	}
	
	
	/**
	 * Perform a picking operation on the specified region to capture 3D shapes
	 * drawn in the given region
	 * 
	 * @param gl
	 *            The {@link GL2} object used for rendering
	 * @param x
	 *            The center x location, in window coordinates
	 * @param y
	 *            The center y location, in window coordinates
	 * @param width
	 *            The width of the box used for picking
	 * @param height
	 *            The height of the box used for picking
	 * @param selectAll
	 *            Whether or not to select all shapes captured in the given
	 *            region, or only to only take the frontmost one
	 * @return The edges and nodes found in the region, as a {@link PickResults}
	 *         object
	 */
	private void performPick(int x, int y, int width, int height,
			boolean selectAll, GraphicsData graphicsData) {
		int bufferSize = 1024;

		if (selectAll) {
			bufferSize = Math.max(4096, graphicsData.getNetworkView().getAllViews().size() * 64);
		}

		GL2 gl = graphicsData.getGlContext();
		int screenHeight = graphicsData.getScreenHeight();
		int screenWidth = graphicsData.getScreenWidth();
		SimpleCamera camera = graphicsData.getCamera();
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer buffer = byteBuffer.asIntBuffer();

		IntBuffer viewport = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);

		gl.glSelectBuffer(bufferSize / 4, buffer);
		gl.glRenderMode(GL2.GL_SELECT);
		gl.glInitNames();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();

		GLU glu = new GLU();
		gl.glLoadIdentity();

		glu.gluPickMatrix(x, screenHeight - y, width, height, viewport);
		
		if (screenHeight != 0) {
			glu.gluPerspective(45.0f, (float) screenWidth / screenHeight, 0.2f, 50.0f);
		} else {
			glu.gluPerspective(45.0f, 1, 0.2f, 50.0f);
		}

		// don't think this ortho call is needed
		// gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);

		// --Begin Drawing--
		performSelectionDrawing(graphicsData);
		// --End Drawing--

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		// not sure if this is needed
		// gl.glFlush();

		int hits = gl.glRenderMode(GL2.GL_RENDER);
		
		if (selectAll) {
			parseSelectionBufferMultipleSelection(buffer, hits, graphicsData.getPickingData());
		} else {
			parseSelectionBufferSingleSelection(buffer, hits, graphicsData.getPickingData());
		}
	}
	
	
	// Render objects into the selection buffer
	private void performSelectionDrawing(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		SimpleCamera camera = graphicsData.getCamera();
		GLU glu = GLU.createGLU(gl);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		// gl.glPushMatrix();
		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		glu.gluLookAt(position.x(), position.y(), position.z(), target.x(),
				target.y(), target.z(), up.x(), up.y(), up.z());

		gl.glPushName(NODE_TYPE);
		gl.glPushName(NO_INDEX);

		// Render nodes for picking
		drawNodesProcedure.execute(graphicsData);
		
		gl.glPopName();
		gl.glPopName();

		gl.glPushName(EDGE_TYPE);
		gl.glPushName(NO_INDEX);

		// Render edges for picking
		drawEdgesProcedure.execute(graphicsData);
		
		gl.glPopName();
		gl.glPopName();
	}
	
	private void parseSelectionBufferSingleSelection(IntBuffer buffer, int hits, PickingData pickingData) {
		pickingData.setClosestPickedNodeIndex(NO_INDEX);
		pickingData.setClosestPickedEdgeIndex(NO_INDEX);
		
		int selectedIndex;
		int selectedType;

		// Current hit record is size 5 because we have
		// (numNames, minZ, maxZ, name1, name2) for
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

				if (buffer.get(i * sizeOfHitRecord + 2) <= max
						&& buffer.get(i * sizeOfHitRecord + 3) <= maxType) {

					max = buffer.get(i * sizeOfHitRecord + 2);
					maxType = buffer.get(i * sizeOfHitRecord + 3);

					// We have that name1 represents the object type
					selectedType = buffer.get(i * sizeOfHitRecord + 3);

					// name2 represents the object index
					selectedIndex = buffer.get(i * sizeOfHitRecord + 4);
				}
			}
			
			if (selectedType == NODE_TYPE) {
				pickingData.setClosestPickedNodeIndex(selectedIndex);
			} else if (selectedType == EDGE_TYPE) {
				pickingData.setClosestPickedEdgeIndex(selectedIndex);
			}
		}
	}
	
	
	private void parseSelectionBufferMultipleSelection(IntBuffer buffer, int hits, PickingData pickingData) {
		pickingData.getPickedNodeIndices().clear();
		pickingData.getPickedEdgeIndices().clear();
		
		int selectedIndex;
		int selectedType;

		// Current hit record is size 5 because we have
		// (numNames, minZ, maxZ, name1, name2) for
		// indices 0-4 respectively
		int sizeOfHitRecord = 5;

		if (hits > 0) {
			// The variable max helps keep track of the polygon that is closest
			// to the front of the screen
			int max = buffer.get(2);
			int maxType = buffer.get(3);

			selectedType = buffer.get(3);
			selectedIndex = buffer.get(4);

			// Drag-selection; select all
			for (int i = 0; i < hits; i++) {

				selectedType = buffer.get(i * sizeOfHitRecord + 3);
				selectedIndex = buffer.get(i * sizeOfHitRecord + 4);

				if (selectedType == NODE_TYPE) {
					pickingData.getPickedNodeIndices().add(selectedIndex);
				} else if (selectedType == EDGE_TYPE) {
					pickingData.getPickedEdgeIndices().add(selectedIndex);
				}
			}
		}
	}

}
