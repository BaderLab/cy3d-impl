package org.baderlab.cy3d.internal;

import java.io.IOException;
import java.util.Properties;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkViewFactory;
import org.baderlab.cy3d.internal.layouts.BoxLayoutAlgorithm;
import org.baderlab.cy3d.internal.layouts.GridLayoutAlgorithm;
import org.baderlab.cy3d.internal.layouts.SphericalLayoutAlgorithm;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewLocationTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

/**
 * CyActivator object used to import and export services from and to Cytoscape, such
 * as manager and factory objects.
 */
public class CyActivator extends AbstractCyActivator {

	public void start(BundleContext bc) {
		CyNetworkViewManager cyNetworkViewManagerRef = getService(bc, CyNetworkViewManager.class);
		RenderingEngineManager cyRenderingEngineManagerRef = getService(bc, RenderingEngineManager.class);
		CyServiceRegistrar cyServiceRegistrarRef = getService(bc, CyServiceRegistrar.class);
		VisualMappingManager visualMappingManagerServiceRef = getService(bc, VisualMappingManager.class);
		UndoSupport undoSupport = getService(bc, UndoSupport.class);
		
		// TaskManager object used to execute tasks
		DialogTaskManager cyDialogTaskManager = getService(bc, DialogTaskManager.class);
		
		// Register service to collect references to relevant task factories for the right-click context menu
		TaskFactoryListener taskFactoryListener = new TaskFactoryListener();
		registerServiceListener(bc, taskFactoryListener, "addNodeViewTaskFactory", "removeNodeViewTaskFactory", NodeViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addEdgeViewTaskFactory", "removeEdgeViewTaskFactory", EdgeViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addNetworkViewTaskFactory", "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addNetworkViewLocationTaskFactory", "removeNetworkViewLocationTaskFactory", NetworkViewLocationTaskFactory.class);
		
		// Cy3D Visual Lexicon
		Cy3DVisualLexicon cy3dVisualLexicon = new Cy3DVisualLexicon();
		
		Properties cy3dVisualLexiconProps = new Properties();
		cy3dVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		cy3dVisualLexiconProps.setProperty("id", "cy3d");
		registerService(bc, cy3dVisualLexicon, VisualLexicon.class, cy3dVisualLexiconProps);

		// Cy3D NetworkView factory
		Cy3DNetworkViewFactory cy3dNetworkViewFactory =
			new Cy3DNetworkViewFactory(cyServiceRegistrarRef, cy3dVisualLexicon, visualMappingManagerServiceRef);
		
		Properties cy3dNetworkViewFactoryProps = new Properties();
		cy3dNetworkViewFactoryProps.setProperty("serviceType", "factory");
		registerService(bc, cy3dNetworkViewFactory, CyNetworkViewFactory.class, cy3dNetworkViewFactoryProps);

		
		// Main RenderingEngine factory
		Cy3DMainRenderingEngineFactory cy3dMainRenderingEngineFactory = new Cy3DMainRenderingEngineFactory(
				cyRenderingEngineManagerRef, cy3dVisualLexicon, taskFactoryListener, cyDialogTaskManager, cyServiceRegistrarRef);
		
		// Bird's Eye RenderingEngine factory
		Cy3DBirdsEyeRenderingEngineFactory cy3dBirdsEyeRenderingEngineFactory = new Cy3DBirdsEyeRenderingEngineFactory(
				cyRenderingEngineManagerRef, cy3dVisualLexicon, taskFactoryListener, cyDialogTaskManager, cyServiceRegistrarRef);

		Cy3DNetworkViewRenderer networkViewRenderer = new Cy3DNetworkViewRenderer(cy3dNetworkViewFactory, cy3dMainRenderingEngineFactory, cy3dBirdsEyeRenderingEngineFactory);
		registerService(bc, networkViewRenderer, NetworkViewRenderer.class, new Properties());
		
		SphericalLayoutAlgorithm sphericalLayoutAlgorithm = new SphericalLayoutAlgorithm(undoSupport);
		Properties sphericalLayoutAlgorithmProps = new Properties();
		sphericalLayoutAlgorithmProps.setProperty("preferredMenu","Layout.3D Layouts");
		sphericalLayoutAlgorithmProps.setProperty("preferredTaskManager","menu");
		sphericalLayoutAlgorithmProps.setProperty("title",sphericalLayoutAlgorithm.toString());
		sphericalLayoutAlgorithmProps.setProperty("menuGravity","10.5");
		registerService(bc, sphericalLayoutAlgorithm, CyLayoutAlgorithm.class, sphericalLayoutAlgorithmProps);
		
		GridLayoutAlgorithm gridLayoutAlgorithm = new GridLayoutAlgorithm(undoSupport);
		Properties gridLayoutAlgorithmProps = new Properties();
		gridLayoutAlgorithmProps.setProperty("preferredMenu","Layout.3D Layouts");
		gridLayoutAlgorithmProps.setProperty("preferredTaskManager","menu");
		gridLayoutAlgorithmProps.setProperty("title", gridLayoutAlgorithm.toString());
		gridLayoutAlgorithmProps.setProperty("menuGravity","10.49");
		registerService(bc, gridLayoutAlgorithm, CyLayoutAlgorithm.class, gridLayoutAlgorithmProps);
		
		BoxLayoutAlgorithm boxLayoutAlgorithm = new BoxLayoutAlgorithm(undoSupport);
		Properties boxLayoutAlgorithmProps = new Properties();
		boxLayoutAlgorithmProps.setProperty("preferredMenu","Layout.3D Layouts");
		boxLayoutAlgorithmProps.setProperty("preferredTaskManager","menu");
		boxLayoutAlgorithmProps.setProperty("title", boxLayoutAlgorithm.toString());
		boxLayoutAlgorithmProps.setProperty("menuGravity","10.51");
		registerService(bc, boxLayoutAlgorithm, CyLayoutAlgorithm.class, boxLayoutAlgorithmProps);
		
		
		try {
			JoglInitializer.unpackNativeLibrariesForJOGL(bc);
		} catch (IOException e) {
			// This App will be useless if Jogl can't find its libraries, so best throw an exception to OSGi to shut it down.
			throw new RuntimeException(e);
 		}
	}

	
	
}
