#!/bin/bash

path1=/Users/yuedong/Documents/workspace/paperwing-impl
plugin=target/paperwing-impl-1.0-SNAPSHOT.jar 
path2=/Users/yuedong/Documents/workspace/gui-distribution/distribution/target/cytoscape-3.0.0-M3-SNAPSHOT/cytoscape-3.0.0-M3-SNAPSHOT/bundles/plugins

if test $# -eq 0; then
	
	echo Building plugin..
	cd $path1
	mvn -o clean install
	if test $? -ne 0; then
		echo Build failed
		exit 1
	fi
	
	echo Moving plugin to directory..
	cp $plugin $path2
	if test $? -ne 0; then
		echo Copy failed
		exit 1
	fi
fi

echo Running Cytoscape..
cd /Users/yuedong/Documents/workspace/gui-distribution/distribution/target/cytoscape-3.0.0-M3-SNAPSHOT/cytoscape-3.0.0-M3-SNAPSHOT
sh cytoscape.sh


