package org.baderlab.cy3d.internal.rendering;

import java.awt.Font;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.opengl.util.awt.TextRenderer;

public class TextRendererCache {
	
	private static final int CACHE_SIZE = 100;
	
	private final LoadingCache<Font,TextRenderer> cache;
	
	
	public TextRendererCache() {
		cache = CacheBuilder.newBuilder()
				.maximumSize(CACHE_SIZE)
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.build(CacheLoader.from(TextRenderer::new));
	}
	
	public TextRenderer get(Font font) {
		try {
			return cache.get(font);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
