#!/bin/bash

mkdir -p extracted_jars
mkdir -p extracted_native_jars

mkdir -p combined_jars
mkdir -p bundles

# open archives
for i in downloads/*.7z
do
	echo Extracting: $i
	 7z x -y -o'extract' $i >/dev/null
done

# create list of files for comparison
# pushd extract; for i in *; do pushd $i; find . >files; popd; done; popd

# move non native library jars
for i in `find extract | grep '\.jar$' | grep -v natives`
do
	mv $i extracted_jars/`basename $i`
done

# extract jars
for i in extracted_jars/*.jar
do
	echo Unzipping: $i
	unzip -o $i -d extracted_jars/classes >/dev/null
done

# combine into single jar
pushd extracted_jars/classes/
jar cf jogl.jar *
popd
mv extracted_jars/classes/jogl.jar combined_jars/jogl.jar

# repackage jar as OSGi bundle
java -jar bnd.jar wrap combined_jars/jogl.jar

# move it to bundles folder
mv combined_jars/jogl.bar bundles/jogl.jar


# move native jars
for i in `find extract | grep '\.jar$' | grep natives`
do
	mv $i extracted_native_jars/`basename $i`
done

# add to single jar
pushd extracted_native_jars
jar cf jogl-natives.jar *
popd
mv extracted_native_jars/jogl-natives.jar combined_jars/jogl-natives.jar

# generate properties file for combined natives jar
echo "Fragment-Host=jogl" >combined_jars/jogl-natives.jar.properties

# repackage jar as OSGi bundle
java -jar bnd.jar wrap -properties combined_jars/jogl-natives.jar.properties combined_jars/jogl-natives.jar

# move it to bundles folder
mv combined_jars/jogl-natives.bar bundles/jogl-natives.jar
