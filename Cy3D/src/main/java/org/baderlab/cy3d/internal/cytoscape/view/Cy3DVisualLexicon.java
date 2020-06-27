package org.baderlab.cy3d.internal.cytoscape.view;

import static java.util.Arrays.asList;
import static org.baderlab.cy3d.internal.cytoscape.view.DetailLevelVisualProperty.DETAIL_HIGH;
import static org.baderlab.cy3d.internal.cytoscape.view.DetailLevelVisualProperty.DETAIL_LOW;
import static org.baderlab.cy3d.internal.cytoscape.view.DetailLevelVisualProperty.DETAIL_MED;
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
import java.util.function.Predicate;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;


public class Cy3DVisualLexicon extends BasicVisualLexicon {

	public static final String CONFIG_PROP_SELECTED_NODES = "SELECTED_NODES";
	
	private final Set<VisualProperty<?>> supportedProps = new HashSet<>();
	private final Map<VisualProperty<?>, Collection<?>> supportedValuesMap = new HashMap<>();
	
	
	public static final VisualProperty<NullDataType> ROOT = new NullVisualProperty("CY3D_ROOT", 
			"cy3d Rendering Engine Root Visual Property");
	
	public static final VisualProperty<DetailLevel> DETAIL_LEVEL = new DetailLevelVisualProperty(
			"DETAIL_LEVEL", "Detail Level", CyNetwork.class);
	
	public static final VisualProperty<Double> NETWORK_CAMERA_ORIGIN_X = new DoubleVisualProperty(0.0, 
			ARBITRARY_DOUBLE_RANGE, "NETWORK_CAMERA_ORIGIN_X", "Camera Center X", CyNetwork.class);
	
	public static final VisualProperty<Double> NETWORK_CAMERA_ORIGIN_Y = new DoubleVisualProperty(0.0, 
			ARBITRARY_DOUBLE_RANGE, "NETWORK_CAMERA_ORIGIN_Y", "Camera Center Y", CyNetwork.class);
	
	public static final VisualProperty<Double> NETWORK_CAMERA_ORIGIN_Z = new DoubleVisualProperty(0.0, 
			ARBITRARY_DOUBLE_RANGE, "NETWORK_CAMERA_ORIGIN_Z", "Camera Center Z", CyNetwork.class);
	
	
	
	public Cy3DVisualLexicon() {
		super(ROOT);
		addVisualProperty(DETAIL_LEVEL, NETWORK);
		addVisualProperty(NETWORK_CAMERA_ORIGIN_X, NETWORK);
		addVisualProperty(NETWORK_CAMERA_ORIGIN_Y, NETWORK);
		addVisualProperty(NETWORK_CAMERA_ORIGIN_Z, NETWORK);
		initSupportedProps();
	}
	
	private void initSupportedProps() {
		supportedProps.add(NETWORK);
		supportedProps.add(NODE);
		
		supportedProps.add(EDGE);
		
		supportedProps.add(NETWORK_BACKGROUND_PAINT);
		supportedProps.add(NETWORK_WIDTH);
		supportedProps.add(NETWORK_HEIGHT);
		
		supportedProps.add(NODE_X_LOCATION);
		supportedProps.add(NODE_Y_LOCATION);
		supportedProps.add(NODE_Z_LOCATION);
		supportedProps.add(NODE_SELECTED);
		supportedProps.add(NODE_FILL_COLOR);
		supportedProps.add(NODE_LABEL);
		supportedProps.add(NODE_VISIBLE);
		supportedProps.add(NODE_SHAPE);
		supportedProps.add(NODE_LABEL_COLOR);
		supportedProps.add(NODE_LABEL_FONT_SIZE);
		supportedProps.add(NODE_LABEL_FONT_FACE);
		
		supportedProps.add(NODE_SIZE);
		supportedProps.add(NODE_DEPTH);
		supportedProps.add(NODE_WIDTH);
		supportedProps.add(NODE_HEIGHT);
		
		supportedProps.add(EDGE_VISIBLE);
		supportedProps.add(EDGE_LINE_TYPE);
		supportedProps.add(EDGE_SELECTED);
		supportedProps.add(EDGE_STROKE_UNSELECTED_PAINT);
		
		supportedProps.add(DETAIL_LEVEL);
		supportedProps.add(NETWORK_CAMERA_ORIGIN_X);
		supportedProps.add(NETWORK_CAMERA_ORIGIN_Y);
		supportedProps.add(NETWORK_CAMERA_ORIGIN_Z);
		
		supportedValuesMap.put(NODE_SHAPE, asList(RECTANGLE, ELLIPSE, TRIANGLE));
		supportedValuesMap.put(EDGE_LINE_TYPE, asList(SOLID, DOT, EQUAL_DASH));
		supportedValuesMap.put(DETAIL_LEVEL, asList(DETAIL_LOW, DETAIL_MED, DETAIL_HIGH));
	}
	
	@Override
	public boolean isSupported(VisualProperty<?> vp) {
		return supportedProps.contains(vp) && super.isSupported(vp);
	}
	
	@Override
	public <T> Set<T> getSupportedValueRange(VisualProperty<T> vp) {
		if(vp.getRange() instanceof DiscreteRange) {
			DiscreteRange<T> range = (DiscreteRange<T>) vp.getRange();
			Collection<?> supportedList = supportedValuesMap.get(vp); // may be null
			if(supportedList == null) {
				return rangeToSet(range, null);
			} else {
				return rangeToSet(range, supportedList::contains);
			}
		}
		return Collections.emptySet();
	}
	
	
	private static <T> Set<T> rangeToSet(DiscreteRange<T> range, Predicate<T> filter) {
		Set<T> set = new LinkedHashSet<>();
		for(T value : range.values()) {
			if(filter == null || filter.test(value)) {
				set.add(value);
			}
		}
		return set;
	}
}
