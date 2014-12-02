package org.baderlab.cy3d.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.swing.DialogTaskManager;

import com.jogamp.opengl.util.FPSAnimator;

/** This class represents a Cy3DRenderingEngine, responsible for
 * creating a rendering of a {@link CyNetwork}
 * 
 * @author Paperwing (Yue Dong)
 */
public abstract class Cy3DRenderingEngine implements RenderingEngine<CyNetwork> {

	/** The networkView to be rendered */
	private CyNetworkView networkView;
	
	/** The View<CyNetwork> form of the CyNetworkView to be rendered */
	private View<CyNetwork> viewModel;
	
	/** The visual lexicon to be used */
	private VisualLexicon visualLexicon;
	
	/** The animator responsible for making calls to the rendering window */
	private FPSAnimator animator;
	
	/** The Graphics object responsible for creating the graphics */
	private Graphics graphics;
	
	/** Whether or not the current rendering engine is active */
	private boolean active;
	
	private GLJPanel panel;
	
	private CyServiceRegistrar serviceRegistrar;
	private NetworkViewAboutToBeDestroyedListener networkViewDestroyedListener;
	private SetCurrentRenderingEngineListener setCurrentRenderingEngineListener;
	
	/** Create a new WindRenderingEngine object */
	public Cy3DRenderingEngine(Object container, View<CyNetwork> viewModel, 
			VisualLexicon visualLexicon) {
	
		this.viewModel = viewModel;
		this.visualLexicon = visualLexicon;
		this.active = false;

//		setUpCanvas(container);
	}
	
	// Needs to be called before setUpCanvas
//	public void setUpNetworkView(CyNetworkViewManager networkViewManager) {
//		if (networkViewManager != null) {
//			this.networkView = networkViewManager.getNetworkView(viewModel.getModel());
//		}
//	}
	
	/** Set up the canvas by creating and placing it, along with a Graphics
	 * object, into the container
	 * 
	 * @param container A container in the GUI window used to contain
	 * the rendered results
	 */
	public void setUpCanvas(Object container) {
		
		// TODO: Addition of this line prevents an "ERROR: JarContent: Unable to read bytes." when Cytoscape shuts down, why?
//		ShutdownType shutdownType = ShutdownType.COMPLETE;
		
		// TODO: The current presentation API seems to require this cast, check
		// if there's a way around it
		this.networkView = (CyNetworkView) viewModel;
		
		if (networkView != null) {
			
			if (container instanceof JComponent) {
				
				JComponent component = (JComponent) container;
				Container focus = component;
				
				// Use the system's default version of OpenGL
				GLProfile profile = GLProfile.getDefault();
				GLCapabilities capabilities = new GLCapabilities(profile);
				capabilities.setHardwareAccelerated(true);
				// TODO: check if this line should be moved to graphics object
				capabilities.setDoubleBuffered(true);
				
				// TODO: check whether to use GLCanvas or GLJPanel
				panel = new GLJPanel(capabilities);

				// TODO: check if negative effects produced by this
				panel.setIgnoreRepaint(true);
				// panel.setDoubleBuffered(true);
				
				graphics = getGraphicsInstance(networkView, visualLexicon);

				panel.addGLEventListener(graphics);

				if (container instanceof JInternalFrame) {
					JInternalFrame frame = (JInternalFrame) container;
					Container pane = frame.getContentPane();
					
					focus = pane;
					graphics.trackInput(pane);
					
					pane.setLayout(new BorderLayout());
					pane.add(panel, BorderLayout.CENTER);
				} else {
					focus = component;
					graphics.trackInput(component);
					
					component.setLayout(new BorderLayout());
					component.add(panel, BorderLayout.CENTER);
				}
				
				animator = new FPSAnimator(60);
				animator.add(panel);
				animator.start();
				
				addStopAnimatorListener(component);
				graphics.setAnimatorControl(animator);
				
				active = true;
			}
		}
	}
	
	public void setupTaskFactories(TaskFactoryListener taskFactoryListener, 
			DialogTaskManager taskManager) {
		graphics.setupTaskFactories(taskFactoryListener, taskManager);
	}
	
	// Adds a listener to the component containing the GLJPanel to stop the animator
	// if the GLJPanel is about to be removed
	private void addStopAnimatorListener(JComponent container) {
		container.addContainerListener(new ContainerListener() {

			@Override
			public void componentAdded(ContainerEvent event) {
			}

			@Override
			public void componentRemoved(ContainerEvent event) {
				if (event.getChild() == panel
						&& animator != null) {
					animator.stop();
				}
			}
		});
	}
	
	
	
	public void setUpListeners(CyServiceRegistrar serviceRegistrar) {
		this.serviceRegistrar = serviceRegistrar;
		
		// NetworkViewDestroyedEvent listener
		if (networkViewDestroyedListener == null) {
			networkViewDestroyedListener = getAboutToBeRemovedListener();

			serviceRegistrar.registerService(
					networkViewDestroyedListener,
					NetworkViewAboutToBeDestroyedListener.class,
					new Properties());
		}
		
		if (setCurrentRenderingEngineListener == null) {
			final RenderingEngine<CyNetwork> renderingEngine = this; 
			
			setCurrentRenderingEngineListener = getSetCurrentRenderingEngineListener(animator);
			if (setCurrentRenderingEngineListener != null) {
				serviceRegistrar.registerService(setCurrentRenderingEngineListener,
						SetCurrentRenderingEngineListener.class,
						new Properties());
			}
		}
	}
	
	
	/** Return a listener to listen to events regarding when the graphics
	 * object is to be destroyed, and the animator stopped
	 * 
	 * @return A listener object handling certain cleanup
	 */
	private NetworkViewAboutToBeDestroyedListener getAboutToBeRemovedListener() {
		
		// System.out.println("getEngineRemovedListener call");
		
		return new NetworkViewAboutToBeDestroyedListener(){

			@Override
			public void handleEvent(NetworkViewAboutToBeDestroyedEvent evt) {
				// System.out.println("Rendering engine about to be removed event: " + evt.getRenderingEngine());
				// System.out.println("Current engine: " + selfPointer);
				
				if (evt.getNetworkView() == networkView) {
					animator.stop();
					
					serviceRegistrar.unregisterService(networkViewDestroyedListener, NetworkViewAboutToBeDestroyedListener.class);
					
					if (setCurrentRenderingEngineListener != null) {
						serviceRegistrar.unregisterService(setCurrentRenderingEngineListener, SetCurrentRenderingEngineListener.class);
					}
				}
			}
		};
	}
	
	protected abstract SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(FPSAnimator animator);
	
	/** Return whether the rendering engine is active */
	public boolean isActive() {
		return active;
	}
	
	protected abstract Graphics getGraphicsInstance(CyNetworkView networkView, VisualLexicon visualLexicon);
	
	@Override
	public View<CyNetwork> getViewModel() {
		return viewModel;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Printable createPrintable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(int width, int height) {
		// TODO Auto-generated method stub
		
		// create image to return
		Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);		

		Dimension panelSize = panel.getSize();
		
		panel.setSize(width, height);
		panel.paint(image.getGraphics());
		panel.setSize(panelSize);
		
		return image;
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int width,
			int height) {
		// TODO Auto-generated method stub
		return null;
		//return new ImageIcon();
	}

	@Override
	public void printCanvas(java.awt.Graphics printCanvas) {
	}
	
	@Override
	public String getRendererId() {
		return Cy3DNetworkViewRenderer.ID;
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
