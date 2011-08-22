package org.cytoscape.paperwing.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import com.jogamp.opengl.util.FPSAnimator;

/** This class represents a WindRenderingEngine, responsible for
 * creating a rendering of a {@link CyNetwork}
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindRenderingEngine implements RenderingEngine<CyNetwork> {

	/** The networkViewManager with a list of all current network views */
	private static CyNetworkViewManager networkViewManager;
	
	/** The networkView to be rendered */
	private CyNetworkView networkView;
	
	/** The View<CyNetwork> form of the CyNetworkView to be rendered */
	private View<CyNetwork> viewModel;
	
	/** The visual lexicon to be used */
	private VisualLexicon visualLexicon;
	
	/** The animator responsible for making calls to the rendering window */
	private FPSAnimator animator;
	
	/** The Graphics object responsible for creating the graphics */
	protected Graphics graphics;
	
	/** Whether or not the current rendering engine is active */
	private boolean active;
	
	/** A pointer to itself, possibly useful for accessing protected 
	 * member variables */
	private RenderingEngine<CyNetwork> selfPointer;
	
	/** Create a new WindRenderingEngine object */
	public WindRenderingEngine(Object container, View<CyNetwork> viewModel, 
			VisualLexicon visualLexicon) {
	
		this.viewModel = viewModel;
		this.visualLexicon = visualLexicon;
		this.active = false;
		
		selfPointer = this;
		
		if (networkViewManager != null) {
			this.networkView = networkViewManager.getNetworkView(viewModel.getModel().getSUID());
		}
		
		if (this.networkView != null) {
			// System.out.println("Setting up canvas..");
			setUpCanvas(container);
		}
		
		// System.out.println("Provided visualLexicon: " + visualLexicon);
	}
	
	/** Return a listener to listen to events regarding when the graphics
	 * object is to be destroyed, and the animator stopped
	 * 
	 * @return A listener object handling certain cleanup
	 */
	public NetworkAboutToBeDestroyedListener getAboutToBeRemovedListener() {
		
		// System.out.println("getEngineRemovedListener call");
		
		return new NetworkAboutToBeDestroyedListener(){

			@Override
			public void handleEvent(NetworkAboutToBeDestroyedEvent evt) {
				// System.out.println("Rendering engine about to be removed event: " + evt.getRenderingEngine());
				// System.out.println("Current engine: " + selfPointer);
				
				if (evt.getNetwork() == networkView.getModel()) {
					System.out.println("Rendering engine about to be removed, stopping animator");
					animator.stop();
				}
			}
		};
	}
	
	/** Set the current networkViewManager */
	public static void setNetworkViewManager(CyNetworkViewManager
			networkViewManager) {
		WindRenderingEngine.networkViewManager = networkViewManager;
	}
	
	/** Return whether the rendering engine is active */
	public boolean isActive() {
		return active;
	}
	
	/** Set up the canvas by creating and placing it, along with a Graphics
	 * object, into the container
	 * 
	 * @param container A container in the GUI window used to contain
	 * the rendered results
	 */
	private void setUpCanvas(Object container) {
		if (container instanceof JComponent) {			
			JComponent component = (JComponent) container;
			Container focus = component;
			
			// Use the system's default version of OpenGL
			GLProfile profile = GLProfile.getDefault();
			GLCapabilities capabilities = new GLCapabilities(profile);

			// TODO: check if this line should be moved to graphics object
			// capabilities.setDoubleBuffered(true);
			
			// TODO: check whether to use GLCanvas or GLJPanel
			GLJPanel panel = new GLJPanel(capabilities);
			
			graphics = new Graphics(networkView, visualLexicon);

			panel.addGLEventListener(graphics);
	
			if (container instanceof JInternalFrame) {
				JInternalFrame frame = (JInternalFrame) container;
				Container pane = frame.getContentPane();
				
				focus = pane;
				graphics.trackInput(pane);
				
				pane.setLayout(new BorderLayout());
				pane.add(panel, BorderLayout.CENTER);
			} else {
				graphics.trackInput(component);
				
				component.setLayout(new BorderLayout());
				component.add(panel, BorderLayout.CENTER);
			}
			
			animator = new FPSAnimator(60);
			animator.add(panel);
			
			focus.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent event) {
					if (!event.isTemporary()) {
						//System.out.println("Animator started for: " + this);
						animator.start();
					}
				}

				@Override
				public void focusLost(FocusEvent event) {
					if (!event.isTemporary()) {
						//System.out.println("Animator stopped for: " + this);
						animator.stop();
					}
				}
			});
			
			active = true;
		}
	}

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
		return null;
		//return new BufferedImage(0, width, height);
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
		// TODO Auto-generated method stub
		
	}
}
