package org.baderlab.cy3d.internal.cytoscape.view;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NullVisualProperty;

/**
 * This VisualLexicon does not extend the BasicVisualLexicon.
 * Instead the VisualLexicon is implemented from scratch, and visual properties 
 * from BasicVisualLexicon are selectively added to this lexicon.
 * 
 * @author mkucera
 */
public class Cy3DVisualLexicon implements VisualLexicon {

	
	/** The root visual property */
	public static final VisualProperty<NullDataType> ROOT = new NullVisualProperty( "CY3D_ROOT", "cy3d Rendering Engine Root Visual Property");
	
	// Allows to lookup visual property by type and id
	private final Map<Class<?>, Map<String, VisualProperty<?>>> visualProperties;
	private final Map<VisualProperty<?>, VisualLexiconNode> propertyToNode;
	private final VisualLexiconNode rootNode;
	
	
	public Cy3DVisualLexicon() {
		rootNode = new VisualLexiconNode(ROOT, null);
		
		propertyToNode = new HashMap<>();
		propertyToNode.put(ROOT, rootNode);

		visualProperties = new HashMap<>();
		visualProperties.put(CyNode.class, new HashMap<String, VisualProperty<?>>());
		visualProperties.put(CyEdge.class, new HashMap<String, VisualProperty<?>>());
		visualProperties.put(CyNetwork.class, new HashMap<String, VisualProperty<?>>());
		
		initializeProperties();
	}
	
	
	private void initializeProperties() {
		addVisualProperty(BasicVisualLexicon.NETWORK, ROOT);
		
		addVisualProperty(BasicVisualLexicon.NODE, BasicVisualLexicon.NETWORK);
		addVisualProperty(BasicVisualLexicon.EDGE, BasicVisualLexicon.NETWORK);
		
		addVisualProperty(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, BasicVisualLexicon.NETWORK);
		addVisualProperty(BasicVisualLexicon.NETWORK_WIDTH, BasicVisualLexicon.NETWORK);
		addVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT, BasicVisualLexicon.NETWORK);
		
		addVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_SELECTED, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_LABEL, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_VISIBLE, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_SHAPE, BasicVisualLexicon.NODE); // MKTODO this is the big one
		
		// NOTE: There is a bug in Cytoscape, the "Properties" menu will not be enabled unless
		// at least one of the CyNode properties has children
		addVisualProperty(BasicVisualLexicon.NODE_SIZE, BasicVisualLexicon.NODE);
		addVisualProperty(BasicVisualLexicon.NODE_DEPTH, BasicVisualLexicon.NODE_SIZE);
		addVisualProperty(BasicVisualLexicon.NODE_WIDTH, BasicVisualLexicon.NODE_SIZE);
		addVisualProperty(BasicVisualLexicon.NODE_HEIGHT, BasicVisualLexicon.NODE_SIZE);
		
		addVisualProperty(BasicVisualLexicon.EDGE_VISIBLE, BasicVisualLexicon.EDGE);
		addVisualProperty(BasicVisualLexicon.EDGE_LINE_TYPE, BasicVisualLexicon.EDGE); // MKTODO this is also not all supported
		addVisualProperty(BasicVisualLexicon.EDGE_WIDTH, BasicVisualLexicon.EDGE);
		addVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, BasicVisualLexicon.EDGE);
		addVisualProperty(BasicVisualLexicon.EDGE_SELECTED, BasicVisualLexicon.EDGE);
	}
	
	
	private void addVisualProperty(final VisualProperty<?> vp, final VisualProperty<?> parent) {
		VisualLexiconNode parentNode = propertyToNode.get(parent);
		VisualLexiconNode newNode = new VisualLexiconNode(vp, parentNode);
		propertyToNode.put(vp, newNode);
		Map<String, VisualProperty<?>> map = visualProperties.get(vp.getTargetDataType());
		map.put(vp.getIdString().toLowerCase(), vp);
	}
	
	
	@Override
	public VisualProperty<NullDataType> getRootVisualProperty() {
		return ROOT;
	}

	@Override
	public Set<VisualProperty<?>> getAllVisualProperties() {
		return Collections.unmodifiableSet(propertyToNode.keySet());
	}

	@Override
	public VisualProperty<?> lookup(Class<?> type, String id) {
		if(id == null || type == null)
			return null;
		Map<String, VisualProperty<?>> map = visualProperties.get(type);
		if(map == null)
			return null;
		return map.get(id.toLowerCase());
	}

	@Override
	public VisualLexiconNode getVisualLexiconNode(VisualProperty<?> vp) {
		return propertyToNode.get(vp);
	}

	@Override
	public Collection<VisualProperty<?>> getAllDescendants(VisualProperty<?> prop) {
		VisualLexiconNode node = getVisualLexiconNode(prop);
		Set<VisualProperty<?>> properties = new HashSet<>();
		visitNode(properties, node);
		return properties;
	}
	
	private void visitNode(Set<VisualProperty<?>> properties, VisualLexiconNode node) {
		properties.add(node.getVisualProperty());
		for(VisualLexiconNode child : node.getChildren()) {
			visitNode(properties, child);
		}
	}

	@Override
	public boolean isSupported(VisualProperty<?> vp) {
		return propertyToNode.containsKey(vp);
	}

}
