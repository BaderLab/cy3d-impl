package org.cytoscape.paperwing.internal;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.osgi.framework.BundleContext;

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

		WindVisualLexicon windVisualLexicon = new WindVisualLexicon();
		WindMainRenderingEngineFactory windMainRenderingEngineFactory = new WindMainRenderingEngineFactory(
				cyNetworkViewManagerRef, cyRenderingEngineManagerRef,
				windVisualLexicon, cyServiceRegistrarRef);
		WindBirdsEyeRenderingEngineFactory windBirdsEyeRenderingEngineFactory = new WindBirdsEyeRenderingEngineFactory(
				cyNetworkViewManagerRef, cyRenderingEngineManagerRef,
				windVisualLexicon, cyServiceRegistrarRef);

		Properties windMainRenderingEngineFactoryProps = new Properties();
		windMainRenderingEngineFactoryProps.setProperty("serviceType",
				"presentationFactory");
		windMainRenderingEngineFactoryProps.setProperty("id", "wind");
		registerService(bc, windMainRenderingEngineFactory,
				RenderingEngineFactory.class, windMainRenderingEngineFactoryProps);

		Properties windBirdsEyeRenderingEngineFactoryProps = new Properties();
		windBirdsEyeRenderingEngineFactoryProps.setProperty("serviceType",
				"presentationFactory");
		windBirdsEyeRenderingEngineFactoryProps.setProperty("id", "windMap");
		registerService(bc, windBirdsEyeRenderingEngineFactory,
				RenderingEngineFactory.class,
				windBirdsEyeRenderingEngineFactoryProps);

		Properties windVisualLexiconProps = new Properties();
		windVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		windVisualLexiconProps.setProperty("id", "wind");
		registerService(bc, windVisualLexicon, VisualLexicon.class,
				windVisualLexiconProps);
	}
}
