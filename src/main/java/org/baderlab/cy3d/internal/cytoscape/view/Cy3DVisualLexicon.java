package org.baderlab.cy3d.internal.cytoscape.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NullVisualProperty;

/**
 * This VisualLexicon does not extend the BasicVisualLexicon.
 * Instead the VisualLexicon is implemented from scratch, and visual properties 
 * from BasicVisualLexicon are added to this lexicon.
 * 
 * @author mkucera
 */
public class Cy3DVisualLexicon implements VisualLexicon {

	/** The root visual property */
	public static final VisualProperty<NullDataType> ROOT = new NullVisualProperty( "CY3D_ROOT", "cy3d Rendering Engine Root Visual Property");
	
	private final BasicVisualLexicon basicLexicon;
	private final Set<VisualProperty<?>> supportedProps;
	
	
	public Cy3DVisualLexicon() {
		basicLexicon = new BasicVisualLexicon(ROOT);
		supportedProps = new HashSet<>();
		initSupportedProps();
	}
	
	private void initSupportedProps() {
		supportedProps.add(BasicVisualLexicon.NETWORK);
		supportedProps.add(BasicVisualLexicon.NODE);
		
		supportedProps.add(BasicVisualLexicon.EDGE);
		
		supportedProps.add(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT);
		supportedProps.add(BasicVisualLexicon.NETWORK_WIDTH);
		supportedProps.add(BasicVisualLexicon.NETWORK_HEIGHT);
		
		supportedProps.add(BasicVisualLexicon.NODE_X_LOCATION);
		supportedProps.add(BasicVisualLexicon.NODE_Y_LOCATION);
		supportedProps.add(BasicVisualLexicon.NODE_Z_LOCATION);
		supportedProps.add(BasicVisualLexicon.NODE_SELECTED);
		supportedProps.add(BasicVisualLexicon.NODE_FILL_COLOR);
		supportedProps.add(BasicVisualLexicon.NODE_LABEL);
		supportedProps.add(BasicVisualLexicon.NODE_VISIBLE);
		supportedProps.add(BasicVisualLexicon.NODE_SHAPE); // MKTODO this is the big one
		
		supportedProps.add(BasicVisualLexicon.NODE_SIZE);
		supportedProps.add(BasicVisualLexicon.NODE_DEPTH);
		supportedProps.add(BasicVisualLexicon.NODE_WIDTH);
		supportedProps.add(BasicVisualLexicon.NODE_HEIGHT);
		
		supportedProps.add(BasicVisualLexicon.EDGE_VISIBLE);
		supportedProps.add(BasicVisualLexicon.EDGE_LINE_TYPE); // MKTODO this is also not all supported
		supportedProps.add(BasicVisualLexicon.EDGE_SELECTED);
	}
	
	
	@Override
	public boolean isSupported(VisualProperty<?> vp) {
		return supportedProps.contains(vp) && basicLexicon.isSupported(vp);
	}
	
	@Override
	public VisualProperty<NullDataType> getRootVisualProperty() {
		return basicLexicon.getRootVisualProperty();
	}

	@Override
	public Set<VisualProperty<?>> getAllVisualProperties() {
		return basicLexicon.getAllVisualProperties();
	}

	@Override
	public VisualProperty<?> lookup(Class<?> type, String id) {
		return basicLexicon.lookup(type, id);
	}

	@Override
	public VisualLexiconNode getVisualLexiconNode(VisualProperty<?> vp) {
		return basicLexicon.getVisualLexiconNode(vp);
	}

	@Override
	public Collection<VisualProperty<?>> getAllDescendants(VisualProperty<?> prop) {
		return basicLexicon.getAllDescendants(prop);
	}

}
