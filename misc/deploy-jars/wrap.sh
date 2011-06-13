
for i in `ls | grep .jar | grep -v aQute`
do
	java -jar biz.aQute.bnd.jar wrap $i
done

rm deploy.sh
version=2.7.1

for i in `ls | grep .bar | grep -v aQute`
do
	#$version=`java -jar biz.aQute.bnd.jar print ${i/.bar/.jar} | grep Implementation-Version | sed s/Implementation-Version/""/g | sed s/" "/""/g`

	echo mvn install:install-file -Dfile=$i -DgroupId=cytoscape-temp -DartifactId=${i%.bar} -Dversion=$version -Dpackaging=jar >>deploy.sh
	
	#echo \<dependency\>
	#echo "\t"\<groupId\>cytoscape-temp\</groupId\>
	#echo "\t"\<artifactId\>${i%.bar}\</artifactId\>
	#echo "\t"\<version\>$version\</version\>
	#echo \</dependency\>
done

