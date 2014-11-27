package org.baderlab.cy3d.internal.cytoscape.view;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

// This class assumes that the VisualProperty's getIdString() method returns a unique
// value amongst the VisualProperty objects
public abstract class VisualPropertyKeeper<S> implements View<S>{
	private Map<String, VisualPropertyValueHolder<?>> valueHolders;
		
	public VisualPropertyKeeper() {
		valueHolders = new HashMap<String, VisualPropertyValueHolder<?>>();
	}
	
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		VisualPropertyValueHolder<?> valueHolder = valueHolders.get(visualProperty.getIdString());
		
		if (valueHolder != null) {
			return (T) valueHolder.getValue();
		} else {
			return null;
		}
	}

	public <T, V extends T> void setVisualProperty(VisualProperty<? extends T> visualProperty, V value) {
		VisualPropertyValueHolder<V> valueHolder = new VisualPropertyValueHolder<V>(value);
		
		valueHolders.put(visualProperty.getIdString(), valueHolder);
	}
	
	public <V> void setVisualProperty(String propertyID, V value) {
		VisualPropertyValueHolder<V> valueHolder = new VisualPropertyValueHolder<V>(value);
		
		valueHolders.put(propertyID, valueHolder);
	}

	@Override
	public <T, V extends T> void setLockedValue(VisualProperty<? extends T> visualProperty,
			V value) {
		setVisualProperty(visualProperty, value);
		
		valueHolders.get(visualProperty.getIdString()).setValueLocked(true);
	}

	@Override
	public boolean isValueLocked(VisualProperty<?> visualProperty) {
		if (valueHolders.get(visualProperty.getIdString()) != null
				&& valueHolders.get(visualProperty.getIdString()).isValueLocked()) {
			
			return true;
		} else {
			// TODO: Currently returns false even when visualProperty not found, check if should update
			return false;
		}
	}

	@Override
	public void clearValueLock(VisualProperty<?> visualProperty) {
		// TODO: Doesn't throw an exception if visualProperty not found
		if (valueHolders.get(visualProperty.getIdString()) != null) {
			
			valueHolders.get(visualProperty.getIdString()).setValueLocked(false);
		}
	}
}
