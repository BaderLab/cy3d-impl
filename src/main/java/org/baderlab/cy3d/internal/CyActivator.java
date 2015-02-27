package org.baderlab.cy3d.internal;

import static org.cytoscape.work.ServiceProperties.*;

import java.io.IOException;
import java.util.Properties;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkViewFactory;
import org.baderlab.cy3d.internal.cytoscape.view.Cy3DVisualLexicon;
import org.baderlab.cy3d.internal.eventbus.EventBusProvider;
import org.baderlab.cy3d.internal.graphics.GraphicsConfigurationFactory;
import org.baderlab.cy3d.internal.layouts.BoxLayoutAlgorithm;
import org.baderlab.cy3d.internal.layouts.CenterLayoutAlgorithm;
import org.baderlab.cy3d.internal.layouts.CyLayoutAlgorithmAdapter;
import org.baderlab.cy3d.internal.layouts.FlattenLayoutAlgorithm;
import org.baderlab.cy3d.internal.layouts.GridLayoutAlgorithm;
import org.baderlab.cy3d.internal.layouts.SphericalLayoutAlgorithm;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewLocationTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

/**
 * CyActivator object used to import and export services from and to Cytoscape, such
 * as manager and factory objects.
 */
public class CyActivator extends AbstractCyActivator {

	public void start(BundleContext bc) {
		CySwingApplication application = getService(bc, CySwingApplication.class);
		OpenBrowser openBrowser = getService(bc, OpenBrowser.class);
		RenderingEngineManager renderingEngineManager = getService(bc, RenderingEngineManager.class);
		VisualMappingManager visualMappingManagerService = getService(bc, VisualMappingManager.class);
		UndoSupport undoSupport = getService(bc, UndoSupport.class);
		CyLayoutAlgorithmManager layoutAlgorithmManager =  getService(bc, CyLayoutAlgorithmManager.class);
		TunableSetter tunableSetter = getService(bc, TunableSetter.class);
		
		// TaskManager object used to execute tasks
		DialogTaskManager dialogTaskManager = getService(bc, DialogTaskManager.class);
		
		// Register service to collect references to relevant task factories for the right-click context menu
		TaskFactoryListener taskFactoryListener = new TaskFactoryListener();
		registerServiceListener(bc, taskFactoryListener, "addNodeViewTaskFactory", "removeNodeViewTaskFactory", NodeViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addEdgeViewTaskFactory", "removeEdgeViewTaskFactory", EdgeViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addNetworkViewTaskFactory", "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addNetworkViewLocationTaskFactory", "removeNetworkViewLocationTaskFactory", NetworkViewLocationTaskFactory.class);
		
		// Cy3D Visual Lexicon
		VisualLexicon cy3dVisualLexicon = new Cy3DVisualLexicon();
		Properties cy3dVisualLexiconProps = new Properties();
		cy3dVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		cy3dVisualLexiconProps.setProperty("id", "cy3d");
		registerService(bc, cy3dVisualLexicon, VisualLexicon.class, cy3dVisualLexiconProps);

		// Cy3D NetworkView factory
		EventBusProvider eventBusProvider = new EventBusProvider();
		Cy3DNetworkViewFactory cy3dNetworkViewFactory = new Cy3DNetworkViewFactory(cy3dVisualLexicon, visualMappingManagerService, eventBusProvider);
		Properties cy3dNetworkViewFactoryProps = new Properties();
		cy3dNetworkViewFactoryProps.setProperty("serviceType", "factory");
		registerService(bc, cy3dNetworkViewFactory, CyNetworkViewFactory.class, cy3dNetworkViewFactoryProps);

		
		// Main RenderingEngine factory
		GraphicsConfigurationFactory mainFactory = GraphicsConfigurationFactory.MAIN_FACTORY;
		Cy3DRenderingEngineFactory cy3dMainRenderingEngineFactory = new Cy3DRenderingEngineFactory(
				renderingEngineManager, cy3dVisualLexicon, taskFactoryListener, dialogTaskManager, eventBusProvider, mainFactory);
		
		// Bird's Eye RenderingEngine factory
		GraphicsConfigurationFactory birdsEyeFactory = GraphicsConfigurationFactory.BIRDS_EYE_FACTORY;
		Cy3DRenderingEngineFactory cy3dBirdsEyeRenderingEngineFactory = new Cy3DRenderingEngineFactory(
				renderingEngineManager, cy3dVisualLexicon, taskFactoryListener, dialogTaskManager, eventBusProvider, birdsEyeFactory);

		
		// NetworkViewRenderer, this is the main entry point that Cytoscape will call into
		Cy3DNetworkViewRenderer networkViewRenderer = new Cy3DNetworkViewRenderer(cy3dNetworkViewFactory, cy3dMainRenderingEngineFactory, cy3dBirdsEyeRenderingEngineFactory);
		registerService(bc, networkViewRenderer, NetworkViewRenderer.class, new Properties());
		
		
		// Layout algorithms
		CyLayoutAlgorithm frAlgorithm = layoutAlgorithmManager.getLayout("fruchterman-rheingold");
		CyLayoutAlgorithmAdapter fr3DAlgorithm = new CyLayoutAlgorithmAdapter(frAlgorithm, tunableSetter, "fruchterman-rheingold-3D", "3D Force directed (BioLayout)");
		
		registerLayoutAlgorithms(bc,
				fr3DAlgorithm,
				new SphericalLayoutAlgorithm(undoSupport),
				new GridLayoutAlgorithm(undoSupport),
				new BoxLayoutAlgorithm(undoSupport),
				new FlattenLayoutAlgorithm(undoSupport),
				new CenterLayoutAlgorithm(undoSupport)
		);
		
		// About dialog
		AboutDialogAction aboutDialogAction = new AboutDialogAction(application, openBrowser);
		aboutDialogAction.setPreferredMenu("Apps.Cy3D");
		registerAllServices(bc, aboutDialogAction, new Properties());
		
		
		// Special handling for JOGL library
		try {
			JoglInitializer.unpackNativeLibrariesForJOGL(bc);
		} catch (IOException e) {
			// This App will be useless if Jogl can't find its libraries, so best throw an exception to OSGi to shut it down.
			throw new RuntimeException(e);
 		}
	}

	
	private void registerLayoutAlgorithms(BundleContext bc, CyLayoutAlgorithm... algorithms) {
		for(int i = 0; i < algorithms.length; i++) {
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(TITLE, algorithms[i].toString());
			props.setProperty(MENU_GRAVITY, "30." + (i+1));
			if(i == 0)
				props.setProperty(INSERT_SEPARATOR_BEFORE, "true");
			if(i == algorithms.length-1)
				props.setProperty(INSERT_SEPARATOR_AFTER, "true");
			
			registerService(bc, algorithms[i], CyLayoutAlgorithm.class, props);
		}
	}
	
}
