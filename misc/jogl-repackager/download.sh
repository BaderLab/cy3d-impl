#!/bin/bash

source artifact.properties

types='linux-amd64 linux-i586 macosx-universal solaris-amd64 solaris-i586 windows-amd64 windows-i586'
jogl_version='2.0-b'$jogl_build_number-`echo $jogl_build_id | sed s/'-'//g | cut -d'_' -f1`
gluegen_version='2.0-b'$gluegen_build_number-`echo $gluegen_build_id | sed s/'-'//g | cut -d'_' -f1`

echo Downloading JOGL version: $jogl_version

for i in $types
do
	url='https://jogamp.org/deployment/autobuilds/rc/jogl-b'${jogl_build_number}-${jogl_build_id}/
	filename=jogl-$jogl_version-$i'.7z'
	
	echo Obtaining: $url$filename
	curl $url$filename -o downloads/$filename --create-dirs
done

echo Downloading Gluegen version: $gluegen_version

for i in $types
do
	url='https://jogamp.org/deployment/autobuilds/rc/gluegen-b'${gluegen_build_number}-${gluegen_build_id}/
	filename=gluegen-$gluegen_version-$i'.7z'
	
	echo Obtaining: $url$filename
	curl $url$filename -o downloads/$filename --create-dirs
done


