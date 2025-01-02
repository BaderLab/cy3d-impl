package org.baderlab.cy3d.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import com.google.common.collect.ImmutableSet;
import com.jogamp.common.util.IOUtil;
import com.jogamp.common.util.JarUtil;

/**
 * For JOGL to find the platform native libraries they must be unpacked from this 
 * bundle jar to the file system. Then we must tell JOGL where to find them.
 */
public class JoglInitializer {
	
	private static final boolean DEBUG = true;
	
	private static final String JOGL_VERSION = "2.5.0";
	
	private static final String GLUEGEN_JAR = "gluegen-rt-" + JOGL_VERSION + ".jar";
	private static final String JOGL_JAR    = "jogl-all-" + JOGL_VERSION + ".jar";

	private JoglInitializer() {}
	
	
	public static void unpackNativeLibrariesForJOGL(BundleContext bc) throws IOException {
		// Get the URLs for the JOGL jar files that are contained in the bundle.
		List<URL> jarFileURLs = getJarFileURLs(bc);
		
		if(DEBUG)
			System.out.println("JoglInitializer.jarFileURLs: " + jarFileURLs);
		
		// Copy the jar files to the persistent storage area, return a map of file paths.
		Map<String,String> pathMap = copyJarFiles(jarFileURLs, bc);
		
		if(DEBUG)
			System.out.println("JoglInitializer.pathMap: " + pathMap);
		
		// Create a URL resolver object and register it with JOGL.
		JarUtil.Resolver resolver = createJarResolver(pathMap);
		JarUtil.setResolver(resolver);
	}
	
	
	@SuppressWarnings("unchecked")
	private static List<URL> getJarFileURLs(BundleContext bc) {
		List<URL> jars = new ArrayList<>();
		jars.addAll(Collections.list(bc.getBundle().findEntries("/", "gluegen*.jar", false)));
		jars.addAll(Collections.list(bc.getBundle().findEntries("/", "jogl*.jar", false)));
		return jars;
	}
	
	
	/**
	 * Copies the JOGL Jar files to the OSGi persistent storage area for this bundle.
	 * Returns a map that provides the locations of the files.
	 */
	private static Map<String,String> copyJarFiles(List<URL> jarFileURLs, BundleContext bc) throws IOException {
		Map<String, String> pathMap = new HashMap<>();
		
		for(URL url : jarFileURLs) {
			String fileName = url.getPath();
			if(fileName.startsWith("/"))
				fileName = fileName.substring(1);
			File outFile = bc.getDataFile(fileName);
			URLConnection inConnection = url.openConnection();
			copyURLToFile(inConnection, outFile);
			
			if(GLUEGEN_JAR.equals(fileName))
				pathMap.put(GLUEGEN_JAR, outFile.getAbsolutePath());
			if(JOGL_JAR.equals(fileName))
				pathMap.put(JOGL_JAR, outFile.getAbsolutePath());
		}
		
		return pathMap;
	}
	
	
	private static void copyURLToFile(URLConnection urlConnection, File targetFile) throws IOException {
		InputStream in = urlConnection.getInputStream();
		OutputStream out = new FileOutputStream(targetFile);
		byte[] buff = new byte[1024];
		int bytesRead;
		while((bytesRead = in.read(buff)) != -1) {
			out.write(buff, 0, bytesRead);
		}
		
		IOUtil.close(in, false);
		IOUtil.close(out, false);
	}
	
	
	private static JarUtil.Resolver createJarResolver(final Map<String,String> pathMap) {
		return new JarUtil.Resolver() {
			
			final Set<String> gluegenPrefixes = ImmutableSet.of("/com/jogamp/common", 
					                                            "/com/jogamp/gluegen", 
					                                            "/jogamp/common");
			
			private boolean isGluegen(String path) {
				for(String prefix : gluegenPrefixes) {
					if(path.startsWith(prefix)) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public URL resolve(URL joglUrl) {
				String qualifiedClassName = joglUrl.getPath();
				String jarToUse = isGluegen(qualifiedClassName) ? GLUEGEN_JAR : JOGL_JAR;
				String localFilePath = pathMap.get(jarToUse);
				
				try {
					String encodedLocalPath = Paths.get(localFilePath).toUri().toURL().getPath();
					String urlString = "jar:file:" + encodedLocalPath + "!" + qualifiedClassName;
					URL newUrl = new URL(urlString);
					
					if(DEBUG)
						System.out.println("JoglInitializer.createJarResolver: " + joglUrl + " -> " + newUrl);
					
					return newUrl;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return joglUrl;
				}
			}
			
		};
	}
}
