package org.cytoscape.paperwing.internal.cytoscape.view;

public class VisualPropertyValueHolder<V> {
	private V value;
	
	private boolean isValueLocked;
	
	public VisualPropertyValueHolder(V value) {
		this.value = value;
	}
	
	public V getValue() {
		return value;
	}

	public void setValueLocked(boolean isValueLocked) {
		this.isValueLocked = isValueLocked;
	}

	public boolean isValueLocked() {
		return isValueLocked;
	}
}
