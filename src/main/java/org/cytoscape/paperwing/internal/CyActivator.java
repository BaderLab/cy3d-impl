package org.cytoscape.paperwing.internal;

import java.util.Properties;

import org.cytoscape.paperwing.internal.cytoscape.view.WindNetworkViewFactory;
import org.cytoscape.paperwing.internal.layouts.SphericalLayoutAlgorithm;
import org.cytoscape.paperwing.internal.task.TaskFactoryListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.SubmenuTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

/**
 * CyActivator object used to import and export services from and to Cytoscape, such
 * as manager and factory objects.
 */
public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		CyNetworkViewManager cyNetworkViewManagerRef = getService(bc,
				CyNetworkViewManager.class);
		RenderingEngineManager cyRenderingEngineManagerRef = getService(bc,
				RenderingEngineManager.class);
		CyServiceRegistrar cyServiceRegistrarRef = getService(bc,
				CyServiceRegistrar.class);
		VisualMappingManager visualMappingManagerServiceRef = getService(bc, 
				VisualMappingManager.class);
		
		// TaskManager object used to execute tasks
		DialogTaskManager cyDialogTaskManager = getService(bc,
				DialogTaskManager.class);
		
		SubmenuTaskManager cySubmenuTaskManager = getService(bc,
				SubmenuTaskManager.class);
		
		// Register service to collect references to relevant task factories for the right-click context menu
		TaskFactoryListener taskFactoryListener = new TaskFactoryListener();
		registerServiceListener(bc, taskFactoryListener, "addNodeViewTaskFactory", "removeNodeViewTaskFactory", NodeViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addEdgeViewTaskFactory", "removeEdgeViewTaskFactory", EdgeViewTaskFactory.class);
		registerServiceListener(bc, taskFactoryListener, "addNetworkViewTaskFactory", "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		
		// Wind Visual Lexicon
		WindVisualLexicon windVisualLexicon = new WindVisualLexicon();
		
		Properties windVisualLexiconProps = new Properties();
		windVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		windVisualLexiconProps.setProperty("id", "wind");
		registerService(bc, windVisualLexicon, VisualLexicon.class,
				windVisualLexiconProps);

		// Wind NetworkView factory
		WindNetworkViewFactory windNetworkViewFactory =
			new WindNetworkViewFactory(cyServiceRegistrarRef, windVisualLexicon, visualMappingManagerServiceRef);
		
		Properties windNetworkViewFactoryProps = new Properties();
		windNetworkViewFactoryProps.setProperty("serviceType", 
				"factory");
		registerService(bc, windNetworkViewFactory, CyNetworkViewFactory.class, windNetworkViewFactoryProps);

		// Main RenderingEngine factory
		WindMainRenderingEngineFactory windMainRenderingEngineFactory = new WindMainRenderingEngineFactory(
				cyNetworkViewManagerRef, cyRenderingEngineManagerRef,
				windVisualLexicon, taskFactoryListener, cyDialogTaskManager, cySubmenuTaskManager, cyServiceRegistrarRef);
		
		Properties windMainRenderingEngineFactoryProps = new Properties();
		windMainRenderingEngineFactoryProps.setProperty("serviceType",
				"presentationFactory");
		windMainRenderingEngineFactoryProps.setProperty("id", "wind");
		registerService(bc, windMainRenderingEngineFactory,
				RenderingEngineFactory.class, windMainRenderingEngineFactoryProps);

		// Bird's Eye RenderingEngine factory
		WindBirdsEyeRenderingEngineFactory windBirdsEyeRenderingEngineFactory = new WindBirdsEyeRenderingEngineFactory(
				cyNetworkViewManagerRef, cyRenderingEngineManagerRef,
				windVisualLexicon, taskFactoryListener, cyDialogTaskManager, cySubmenuTaskManager, cyServiceRegistrarRef);
		
		Properties windBirdsEyeRenderingEngineFactoryProps = new Properties();
		windBirdsEyeRenderingEngineFactoryProps.setProperty("serviceType",
				"presentationFactory");
		windBirdsEyeRenderingEngineFactoryProps.setProperty("id", "windMap");
		registerService(bc, windBirdsEyeRenderingEngineFactory,
				RenderingEngineFactory.class,
				windBirdsEyeRenderingEngineFactoryProps);

		
		UndoSupport undoSupportServiceRef = getService(bc, UndoSupport.class);
		
		SphericalLayoutAlgorithm sphericalLayoutAlgorithm = new SphericalLayoutAlgorithm(undoSupportServiceRef);
		Properties sphericalLayoutAlgorithmProps = new Properties();
		sphericalLayoutAlgorithmProps.setProperty("preferredMenu","Layout.3D Layouts");
		sphericalLayoutAlgorithmProps.setProperty("preferredTaskManager","menu");
		sphericalLayoutAlgorithmProps.setProperty("title",sphericalLayoutAlgorithm.toString());
		sphericalLayoutAlgorithmProps.setProperty("menuGravity","10.2");
		
		registerService(bc,sphericalLayoutAlgorithm, CyLayoutAlgorithm.class, sphericalLayoutAlgorithmProps);
	}
}
