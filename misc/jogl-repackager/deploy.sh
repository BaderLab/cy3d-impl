#!/bin/bash

source artifact.properties

jogl_version='2.0-b'$jogl_build_number-`echo $jogl_build_id | sed s/'-'//g | cut -d'_' -f1`

group_id=cytoscape-temp

for i in bundles/*.jar
do
	echo mvn install:install-file -Dfile=$i -DgroupId=$group_id -DartifactId=`basename $i .jar` -Dversion=$jogl_version -Dpackaging=jar

	mvn install:install-file -Dfile=$i -DgroupId=$group_id -DartifactId=`basename $i .jar` -Dversion=$jogl_version -Dpackaging=jar
done

