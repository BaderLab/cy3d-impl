package org.baderlab.cy3d.internal.graphics;


/**
 * Render events are fired by the animator at up to 60 frames/events per second.
 * If there has not been any input (or animation) handled between render events
 * then there is no reason to actually render the image because it will be the
 * same as the existing image.
 * 
 * This is much simpler than trying to control the FPSAnimator thread through 
 * its start() and stop() methods.
 * 
 * (Note, an alternative is to ask the GraphicsData object if any of its state
 * has changed. That actually makes more sense because its possible some inputs
 * might not actually update the scene, and its possible that the state might be
 * update by some means other than mouse/keyboard input. However, that is a very 
 * large object with several sub-objects that would all need to be tracked for 
 * changes. For now it seems simpler to directly track the input instead.)
 * 
 * @author mkucera
 */
public interface RenderUpdateFlag {
	
	boolean needToRender();
	
	void reset();

	
	public static final RenderUpdateFlag ALWAYS_RENDER = new RenderUpdateFlag() {
		
		@Override
		public void reset() {
		}
		
		@Override
		public boolean needToRender() {
			return true;
		}
		
	};
	
}
