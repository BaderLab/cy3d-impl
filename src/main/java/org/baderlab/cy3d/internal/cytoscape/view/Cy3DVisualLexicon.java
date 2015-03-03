package org.baderlab.cy3d.internal.cytoscape.view;

import static java.util.Arrays.asList;
import static org.cytoscape.view.presentation.property.LineTypeVisualProperty.DOT;
import static org.cytoscape.view.presentation.property.LineTypeVisualProperty.EQUAL_DASH;
import static org.cytoscape.view.presentation.property.LineTypeVisualProperty.SOLID;
import static org.cytoscape.view.presentation.property.NodeShapeVisualProperty.ELLIPSE;
import static org.cytoscape.view.presentation.property.NodeShapeVisualProperty.RECTANGLE;
import static org.cytoscape.view.presentation.property.NodeShapeVisualProperty.TRIANGLE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.NullDataType;
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
public class Cy3DVisualLexicon extends BasicVisualLexicon {

	/** The root visual property */
	public static final VisualProperty<NullDataType> ROOT = new NullVisualProperty( "CY3D_ROOT", "cy3d Rendering Engine Root Visual Property");
	
	private final Set<VisualProperty<?>> supportedProps;
	private final Map<VisualProperty<?>, Collection<?>> supportedValuesMap;
	
	
	public Cy3DVisualLexicon() {
		super(ROOT);
		supportedProps = new HashSet<>();
		supportedValuesMap = new HashMap<>();
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
		supportedProps.add(BasicVisualLexicon.NODE_SHAPE);
		
		supportedProps.add(BasicVisualLexicon.NODE_SIZE);
		supportedProps.add(BasicVisualLexicon.NODE_DEPTH);
		supportedProps.add(BasicVisualLexicon.NODE_WIDTH);
		supportedProps.add(BasicVisualLexicon.NODE_HEIGHT);
		
		supportedProps.add(BasicVisualLexicon.EDGE_VISIBLE);
		supportedProps.add(BasicVisualLexicon.EDGE_LINE_TYPE);
		supportedProps.add(BasicVisualLexicon.EDGE_SELECTED);
		
		supportedValuesMap.put(NODE_SHAPE, asList(RECTANGLE, ELLIPSE, TRIANGLE));
		supportedValuesMap.put(EDGE_LINE_TYPE, asList(SOLID, DOT, EQUAL_DASH));
	}
	
	@Override
	public boolean isSupported(VisualProperty<?> vp) {
		return supportedProps.contains(vp) && super.isSupported(vp);
	}
	
	@Override
	public <T> Set<T> getSupportedValueRange(VisualProperty<T> vp) {
		if (vp.getRange() instanceof DiscreteRange) {
			final DiscreteRange<T> range = (DiscreteRange<T>) vp.getRange();
			final Collection<?> supportedList = supportedValuesMap.get(vp);
			
			if (supportedList != null) {
				final Set<T> set = new LinkedHashSet<>();
				
				for (T value : range.values()) {
					if (supportedList.contains(value))
						set.add(value);
				}
				
				return set;
			}
		}
		
		return Collections.emptySet();
	}
}
