#!/bin/bash

mkdir -p extracted_jars

mkdir -p combined_jars
mkdir -p bundles

mkdir -p extracted_natives

# open archives
for i in downloads/*.7z
do
	echo Extracting: $i
	 7z x -y -o'extract' $i >/dev/null
done

# create list of files for comparison
# pushd extract; for i in *; do pushd $i; find . >files; popd; done; popd

# process jogl jar
# ================

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
echo Combining to single jar..
pushd extracted_jars/classes/
jar cf jogl.jar *
popd
mv extracted_jars/classes/jogl.jar combined_jars/jogl.jar

# repackage jar as OSGi bundle
java -jar bnd.jar wrap combined_jars/jogl.jar

# move it to bundles folder
mv combined_jars/jogl.bar bundles/jogl.jar


# native library jar
# ==================

# move native jars
for i in `find extract | grep '\.jar$' | grep natives`
do
	mv $i extracted_natives/`basename $i`
done

# move native library files
for i in extract/*
do
	dest_dir=extracted_natives/`echo $i | cut -d'-' -f5,6`
	mkdir -p $dest_dir

	for j in `find $i/lib -type f`
	do
		cp $j $dest_dir
	done
done

# add to single jar
pushd extracted_natives
jar cf jogl-natives.jar *
popd
mv extracted_natives/jogl-natives.jar combined_jars/jogl-natives.jar

# generate properties file for combined natives jar
properties_file=combined_jars/jogl-natives.jar.properties
rm $properties_file

echo -n "Bundle-NativeCode: " >>$properties_file

# linux x32 properties
for i in `find extracted_natives/linux-i586 -type f`
do
	echo linux-i586/`basename $i`\; >>$properties_file
done

echo osname=linux\; >>$properties_file
echo processor=x86, >>$properties_file

# linux x64 properties
for i in `find extracted_natives/linux-amd64 -type f`
do
	echo linux-amd64/`basename $i`\; >>$properties_file
done

echo osname=linux\; >>$properties_file
echo processor=x86-64, >>$properties_file

# solaris x32 properties
for i in `find extracted_natives/solaris-i586 -type f`
do
	echo solaris-i586/`basename $i`\; >>$properties_file
done

echo osname=solaris\; >>$properties_file
echo processor=x86, >>$properties_file

# solaris x64 properties
for i in `find extracted_natives/solaris-amd64 -type f`
do
	echo solaris-amd64/`basename $i`\; >>$properties_file
done

echo osname=solaris\; >>$properties_file
echo processor=x86-64, >>$properties_file

# windows x64 properties
for i in `find extracted_natives/windows-amd64 -type f`
do
	echo windows-amd64/`basename $i`\; >>$properties_file
done

echo osname=win32\; >>$properties_file
echo processor=x86-64, >>$properties_file

# windows x32 properties
for i in `find extracted_natives/windows-i586 -type f`
do
	echo windows-i586/`basename $i`\; >>$properties_file
done

echo osname=win32\; >>$properties_file
echo processor=x86, >>$properties_file

# mac properties
for i in `find extracted_natives/macosx-universal -type f`
do
	echo macosx-universal/`basename $i`\; >>$properties_file
done

echo osname=mac os x\; >>$properties_file
echo processor=x86\; >>$properties_file
echo processor=ppc >>$properties_file

echo Fragment-Host=jogl >>$properties_file

# repackage jar as OSGi bundle
java -jar bnd.jar wrap -properties $properties_file combined_jars/jogl-natives.jar

# move it to bundles folder
mv combined_jars/jogl-natives.bar bundles/jogl-natives.jar
