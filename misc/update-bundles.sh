#!/bin/bash

bundle1=/Users/yuedong/Documents/workspace/presentation-impl
bundle2=/Users/yuedong/Documents/workspace/swing-application-impl
bundle3=/Users/yuedong/Documents/workspace/vizmap-gui-impl
output=/dev/null

echo Building $bundle1
cd $bundle1
mvn install

echo Building $bundle2
cd $bundle2
mvn install

echo Building $bundle3
cd $bundle3
mvn install

echo Clearing startlevel-3..
rm /Users/yuedong/Documents/workspace/gui-distribution/distribution/target/cytoscape-3.0.0-M3-SNAPSHOT/cytoscape-3.0.0-M3-SNAPSHOT/bundles/startlevel-3/*

echo Bulding Cytoscape..
cd /Users/yuedong/Documents/workspace/gui-distribution
mvn install