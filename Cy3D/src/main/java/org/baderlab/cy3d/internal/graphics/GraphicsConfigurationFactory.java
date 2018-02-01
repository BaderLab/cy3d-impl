package org.baderlab.cy3d.internal.graphics;


/**
 * If I were using Guice I would just use a Provider.
 * 
 * Made this an enum just because it requires less code.
 * 
 * @author mkucera
 */
public enum GraphicsConfigurationFactory {
	
	MAIN_FACTORY {
		public MainGraphicsConfiguration createGraphicsConfiguration() {
			return new MainGraphicsConfiguration();
		}
	},
	
	BIRDS_EYE_FACTORY {
		public BirdsEyeGraphicsConfiguration createGraphicsConfiguration() {
			return new BirdsEyeGraphicsConfiguration();
		}
	};
	
	public abstract GraphicsConfiguration createGraphicsConfiguration();
}
