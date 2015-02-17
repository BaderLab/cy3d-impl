package org.baderlab.cy3d.internal.cytoscape.view;

public class VisualPropertyValue<V> {
	
	private final V value;
	private boolean isValueLocked;
	
	public VisualPropertyValue(V value) {
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
