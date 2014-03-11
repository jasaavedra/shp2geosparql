# By Jhonny Saavedra jhonnysaavedra@gmail.com 
# as part of a research project of the 'Instituto Geografico Agustin Codazzi'
# Geometry2RDF tool http://mayor2.dia.fi.upm.es/oeg-upm/index.php/en/technologies/151-geometry2rdf was the base for this one
# (CC BY-SA)

Was developed like a Netbeans project
a prebuild version is in the carper dist
take care of inlude the libs for new developments


========================
BUILD OUTPUT DESCRIPTION
========================

When you build an Java application project that has a main class, the IDE
automatically copies all of the JAR
files on the projects classpath to your projects dist/lib folder. The IDE
also adds each of the JAR files to the Class-Path element in the application
JAR files manifest file (MANIFEST.MF).

To run the project from the command line, go to the dist folder and
type the following:

java -jar "shp2geosparql.jar" 

To distribute this project, zip up the dist folder (including the lib folder)
and distribute the ZIP file.

Notes:

* If two JAR files on the project classpath have the same name, only the first
JAR file is copied to the lib folder.
* Only JAR files are copied to the lib folder.
If the classpath contains other types of files or folders, these files (folders)
are not copied.
* If a library on the projects classpath also has a Class-Path element
specified in the manifest,the content of the Class-Path element has to be on
the projects runtime path.
* To set a main class in a standard Java project, right-click the project node
in the Projects window and choose Properties. Then click Run and enter the
class name in the Main Class field. Alternatively, you can manually type the
class name in the manifest Main-Class element.


The script use a configuration file that should exist in a file called configuration with the next parameters. Here is an example and descripcion of them.


# yes or not to include the label of the instance in the RDF
includeLabel = yes
# yes or not to include the spatial relation among instance of the SHP in geosparql vocabulary
includeRelations = yes
# yes or not to include an alternative representation as point (only for polygons)
includePoints = yes
# rout of the shp 
shpDir = C:/shp2geosparql/shp
# name of the shp
shpName = prueba
# identificator a field in the shp
idField =  SCaCodigo
labelField = SCaNombre
# language of the laabel 
lang = es
# name of class in the ontology o vocabulary that will be used
ontologyClass = SCat
# Uri used as base of the classes of the ontology
ontologyUri = http://datos.igac.gov.co/ontologias/catastro/bogota/
# namespace to represent the URI of the ontology in the RDF
nsOntology = igac
# URI of the resources that will be used in the RDF
resourceUri = http://datos.igac.gov.co/id/catastro/bogota/
# rout in that will be keep the RDF generated 
outputFileDir = C:/shp2geosparql/rdf/scat.rdf
