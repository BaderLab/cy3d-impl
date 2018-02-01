// If you are visiting this class for the first time,
// consider taking a look at the following files:
//
// src/main/resources/controls.txt -- contains information about controls
// src/main/resources/overview-todo.txt -- contains information about what 
// is to be done

package org.baderlab.cy3d.internal.graphics;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JComponent;

import org.baderlab.cy3d.internal.camera.CameraPosition;
import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.PixelConverter;
import org.baderlab.cy3d.internal.eventbus.EventBusProvider;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import com.google.common.eventbus.EventBus;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * This class represents a Cy3D rendering object, directly called
 * by the display thread to update the rendered scene for every frame.
 * 
 * Its behavior is governed by its {@link GraphicsConfiguration} object, 
 * which will determine if and how it handles the following:
 * - keyboard and mouse input
 * - calculation related to rendering
 * - rendering of the network
 * - communication with other {@link RenderEventListener} objects, such as in the case
 * of a bird's eye rendering object and a main window rendering object communication
 * with each other.
 * 
 */
public class RenderEventListener implements GLEventListener {
	
	// Contains the "global" state used by one renderer.
	private final GraphicsData graphicsData;
	private final GraphicsConfiguration configuration;
	
	
	public RenderEventListener(
			Cy3DNetworkView networkView, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,
			TaskFactoryListener taskFactoryListener, 
			DialogTaskManager taskManager,
			JComponent component,
			JComponent inputComponent) {
		
		this.configuration = checkNotNull(configuration);
		EventBus eventBus = eventBusProvider.getEventBus(networkView);
		
		graphicsData = new GraphicsData(networkView, visualLexicon, eventBus, component, inputComponent);
		graphicsData.setTaskFactoryListener(taskFactoryListener);
		graphicsData.setTaskManager(taskManager);
	}
	
	
	/** 
	 * Initialize the Graphics object, performing certain OpenGL initializations.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		graphicsData.setGlContext(gl);
		graphicsData.setPixelConverter(new PixelConverter(drawable.getNativeSurface()));
		
		configuration.initialize(graphicsData);
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);

		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());

		// Correct lightning for scaling certain models
		gl.glEnable(GL2.GL_NORMALIZE);

		// force render of first frame
		graphicsData.getNetworkView().updateView();
	}

	// MKTODO this should be moved into the GraphicsConfiguration

	
	
	/** Main drawing method; can be called by an {@link Animator} such as
	 * {@link FPSAnimator}, responsible for drawing the scene and advancing
	 * the frame
	 * 
	 * @param drawable The GLAutoDrawable object used for rendering
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		graphicsData.setGlContext(gl);
		
		// Re-calculate the viewing volume
		CameraPosition camera = graphicsData.getCamera();
		graphicsData.getViewingVolume().calculateViewingVolume(
				camera.getPosition(), 
				camera.getDirection(), 
				camera.getUp(), 
				GraphicsData.NEAR_Z, 
				GraphicsData.FAR_Z, 
				GraphicsData.VERTICAL_VOF, 
				GeometryToolkit.findHorizontalFieldOfView(GraphicsData.DISTANCE_SCALE, 
						graphicsData.getScreenWidth(), graphicsData.getScreenHeight()));
		
		
		// Doesn't really need to be split into two methods, but it allows GrapicsConfigurations to 
		// only override update() and leave the drawing to AbstractGraphicsConfiguration.
		
		configuration.update();
		configuration.drawScene();
		
		int errorCode = gl.glGetError();
		if(errorCode != GL2.GL_NO_ERROR) {
			System.err.println("Error Code: " + errorCode);
		}
	}


	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		if (height <= 0) {
			height = 1;
		}

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(GraphicsData.VERTICAL_VOF, (float) width / height, GraphicsData.NEAR_Z, GraphicsData.FAR_Z);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		graphicsData.setScreenHeight(height);
		graphicsData.setScreenWidth(width);
		
		// MKTODO I think this is required to move between monitors, confirm this assumption
		graphicsData.getPixelConverter().setNativeSurface(drawable.getNativeSurface());
	}
	
	
	@Override
	public void dispose(GLAutoDrawable autoDrawable) {
		configuration.dispose();
	}
	
	@Override
	public String toString() {
		return "RenderEventListener(" + configuration + ")";
	}
}
