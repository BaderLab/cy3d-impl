package org.baderlab.cy3d.internal.cytoscape.view;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRange;

import com.google.common.collect.Sets;

public class DetailLevelVisualProperty extends AbstractVisualProperty<DetailLevel> {

	public static final DetailLevel DETAIL_LOW  = new DetailLevel("Low", "DETAIL_LOW");
	public static final DetailLevel DETAIL_MED  = new DetailLevel("Medium", "DETAIL_MED");
	public static final DetailLevel DETAIL_HIGH = new DetailLevel("High", "DETAIL_HIGH");
	
	public static final DiscreteRange<DetailLevel> RANGE = new DiscreteRange<>(DetailLevel.class, Sets.newHashSet(DETAIL_LOW, DETAIL_MED, DETAIL_HIGH));
	
	public DetailLevelVisualProperty(String id, String displayName, Class<? extends CyIdentifiable> targetObjectDataType) {
		super(DETAIL_MED, RANGE, id, displayName, targetObjectDataType);
	}

	@Override
	public String toSerializableString(DetailLevel value) {
		return value.getSerializableString();
	}

	@Override
	public DetailLevel parseSerializableString(String value) {
		for(DetailLevel detailLevel : RANGE.values()) {
			if (detailLevel.getSerializableString().equalsIgnoreCase(value)) {
				return detailLevel;
			}
		}
		return null;
	}

}
