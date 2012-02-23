#!/bin/bash

mkdir -p extracted_jars
mkdir -p extracted_natives

# open archives
for i in downloads/*.7z
do
	 7z x -y -o'extract' $i
done

# move jars
for i in `find extract | grep '\.jar$'`
do
	mv $i extracted_jars/`basename $i`
done

# move native libraries
for i in `find extract | grep '\.dll$\|\.so\|\.jnilib'`
do
	mv $i extracted_natives/`basename $i`
done


