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
		WindRenderingEngineFactory windRenderingEngineFactory = new WindRenderingEngineFactory(
				cyNetworkViewManagerRef, cyRenderingEngineManagerRef,
				windVisualLexicon, cyServiceRegistrarRef);
		WindMapRenderingEngineFactory windMapRenderingEngineFactory = new WindMapRenderingEngineFactory(
				cyNetworkViewManagerRef, cyRenderingEngineManagerRef,
				windVisualLexicon, cyServiceRegistrarRef);

		Properties windRenderingEngineFactoryProps = new Properties();
		windRenderingEngineFactoryProps.setProperty("serviceType",
				"presentationFactory");
		windRenderingEngineFactoryProps.setProperty("id", "wind");
		registerService(bc, windRenderingEngineFactory,
				RenderingEngineFactory.class, windRenderingEngineFactoryProps);

		Properties windMapRenderingEngineFactoryProps = new Properties();
		windMapRenderingEngineFactoryProps.setProperty("serviceType",
				"presentationFactory");
		windMapRenderingEngineFactoryProps.setProperty("id", "windMap");
		registerService(bc, windMapRenderingEngineFactory,
				RenderingEngineFactory.class,
				windMapRenderingEngineFactoryProps);

		Properties windVisualLexiconProps = new Properties();
		windVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		windVisualLexiconProps.setProperty("id", "wind");
		registerService(bc, windVisualLexicon, VisualLexicon.class,
				windVisualLexiconProps);
	}
}
